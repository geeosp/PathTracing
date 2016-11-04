/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing;

import geeosp.pathtracing.scene.RenderScene;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;

/**
 *
 * @author Geovane
 */
public class Renderer {

    private final Object lock = new Object();

    private RunningState runningState;
    private Thread[] renderThreads;
    private AtomicInteger pixelsRendered;
    private AtomicInteger threadsFinished;
    private long time;
    private RenderBundle renderBundle;
    int currentProgress;
    RenderScene scene;
    private double[][][] pixels;
    private RenderAlgorithm algorithm;

    public Renderer() {
        this.runningState = RunningState.Stopped;
    }

    Renderer(RenderScene renderScene, ImageView imageView, RenderAlgorithm algorithm) {
        this.scene = renderScene;
        this.renderBundle = new RenderBundle(renderScene.getSizeWidth(), renderScene.getSizeHeight(), renderScene.getNpaths(), imageView);
        this.renderThreads = new RenderThread[renderScene.getNthreads()];
        this.pixelsRendered = new AtomicInteger(0);
        this.pixels = new double[renderScene.getSizeWidth()][renderScene.getSizeHeight()][4];
        this.threadsFinished = new AtomicInteger(renderScene.getNthreads());
        this.algorithm = algorithm;
    }

    public int getProgress() {
        return 100 * pixelsRendered.intValue() / (scene.getSizeWidth() * scene.getSizeHeight());

    }

    public RunningState getRunningState() {
        return runningState;
    }

    public void stopRender() {
        System.out.println("Stop");
        runningState = RunningState.Stopped;
    }

    public boolean isRunning() {
        return runningState == RunningState.Running;
    }

    void startRender() throws IOException, InterruptedException {
        System.out.println("Starting Rendering");

        time = System.currentTimeMillis();
        this.runningState = RunningState.Running;
        for (int i = 0; i < scene.getSizeWidth(); i++) {
            for (int j = 0; j < scene.getSizeHeight(); j++) {
                savePixel(i, j, scene.getBackgroundColor(), false);
                //    this.renderBundle.writeImage.getPixelWriter().setColor(i, j, Color.BLACK);
            }
        }
        if (scene.getNthreads() > 1) {
            for (int i = 0; i < renderThreads.length; i++) {
                renderThreads[i] = new RenderThread(i, renderThreads.length, this.renderBundle);
                renderThreads[i].setDaemon(false);
                renderThreads[i].start();
            }
        } else {
            startRenderNoThreads();
        }

    }

    void startRenderNoThreads() throws IOException, InterruptedException {
        //System.out.println("Starting Rendering");

        this.runningState = RunningState.Running;
        for (int i = 0; i < scene.getSizeWidth(); i++) {
            for (int j = 0; j < scene.getSizeHeight(); j++) {
                savePixel(i, j, scene.getBackgroundColor(), false);
            }
        }
        float t = 1.f / scene.getSizeHeight();
        for (int i = 0; i < scene.getSizeWidth(); i++) {
            for (int j = 0; j < scene.getSizeHeight(); j++) {
                double[] color;
                color = algorithm.calulatePixel(i, j, scene);
                savePixel(i, j, color, true);
            }
        }
        time = System.currentTimeMillis() - time;
        try {
            saveFile(scene.getSizeWidth(), scene.getSizeHeight(), this.renderBundle.writeImage);
        } catch (IOException ex) {
            Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.err.println("Finished: " + (time / 1000.0) + " seconds");

    }

    synchronized void savePixel(int x, int y, double[] color, boolean update) {

        pixels[x][y] = color;
            renderBundle.writeImage.getPixelWriter().setColor(x, pixels[0].length - y - 1, new Color(pixels[x][y][0], pixels[x][y][1], pixels[x][y][2], pixels[x][y][3]));
        if (update) {
            pixelsRendered.incrementAndGet();
            if (currentProgress != getProgress()) {
                //       System.out.println("Progress: " + currentProgress);
                renderBundle.imageViewGui.setImage(renderBundle.writeImage);
                currentProgress = getProgress();
            }
        }
    }

    public static void saveFile(int width, int height, WritableImage wImage) throws IOException {
        File dir = new File("out");
        dir.mkdir();
        String name = "out/" + width + "px_" + height + "px_";
        //FileChooser fileChooser = new FileChooser();
        File file = new File(name + ".png");
        if (file != null) {
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(wImage, null);
            ImageIO.write(renderedImage, "png", file);

        }
    }

    public enum RunningState {
        Running,
        Stopped
    }

    class RenderThread extends Thread {

        private int index;
        private int max;
        private final RenderBundle renderBundle;

        public RenderBundle getRenderBundle() {
            return renderBundle;
        }

        public RenderThread(int index, int max, RenderBundle renderBundle) {
            this.index = index;
            this.max = max;
            this.renderBundle = renderBundle;
        }

        public void run() {
            float t = 1.f / scene.getSizeHeight();
            int stepWidth = (int) Math.floor(scene.getSizeWidth() / max);

            for (int i = index * stepWidth; i <= (1 + index) * stepWidth - 1; i++) {
                for (int j = 0; j < scene.getSizeHeight(); j++) {
                    double[] color = algorithm.calulatePixel(i, j, scene);
                    savePixel(i, j, color, true);
                }
            }
            if (threadsFinished.decrementAndGet() == 0) {
                time = System.currentTimeMillis() - time;
                try {
                    saveFile(scene.getSizeWidth(), scene.getSizeHeight(), this.renderBundle.writeImage);
                } catch (IOException ex) {
                    Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
                }
                runningState = RunningState.Stopped;
                System.err.println("Finished: " + (time / 1000.0) + " seconds");

            }
        }
    }
}
