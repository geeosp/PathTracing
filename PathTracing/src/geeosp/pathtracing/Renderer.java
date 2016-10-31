/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing;

import geeosp.pathtracing.scene.RenderScene;
import geeosp.pathtracing.scene.SceneLoader;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.TextField;
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

    Renderer(int width, int height, int rays, int threads, ImageView imageView) {
        set(width, height, rays, threads, imageView);

    }

    public enum RunningState {
        Running,
        Stopped
    }
    private RunningState runningState;
    private Thread[] renderThreads;
    private AtomicInteger pixelsRendered;
    private AtomicInteger threadsFinished;

    private RenderBundle renderBundle;
    private double[][][] pixels;

    public int getProgress() {
        return 100 * pixelsRendered.intValue() / (renderBundle.height * renderBundle.width);

    }

    public RunningState getRunningState() {
        return runningState;
    }

    public Renderer() {
        this.runningState = RunningState.Stopped;
    }

    public void stopRender() {
        System.out.println("Stop");
        runningState = RunningState.Stopped;
    }

    public boolean isRunning() {
        return runningState == RunningState.Running;
    }

    public void set(int width, int height, int rays, int numThreads, ImageView ivImage, TextField tfConsole) {
        this.renderBundle = new RenderBundle(width, height, rays, ivImage, tfConsole);
        this.renderThreads = new RenderThread[numThreads];
        this.pixelsRendered = new AtomicInteger(0);
        this.pixels = new double[width][height][4];
        RenderScene scene = SceneLoader.load();
        System.out.println(scene);
        this.threadsFinished = new AtomicInteger(numThreads);
    }

    public void set(int width, int height, int rays, int numThreads, ImageView ivImage) {
        this.renderBundle = new RenderBundle(width, height, rays, ivImage);
        this.renderThreads = new RenderThread[numThreads];
        this.pixelsRendered = new AtomicInteger(0);
        this.pixels = new double[width][height][4];
        RenderScene scene = SceneLoader.load();
        System.out.println(scene);
        this.threadsFinished = new AtomicInteger(numThreads);

    }

    void startRender() throws IOException, InterruptedException {
        System.out.println("Starting Rendering");

        this.runningState = RunningState.Running;
        for (int i = 0; i < this.renderBundle.width; i++) {
            for (int j = 0; j < this.renderBundle.height; j++) {
                savePixel(i, j, new double[]{1, 1, 1, 1}, false);
                //    this.renderBundle.writeImage.getPixelWriter().setColor(i, j, Color.BLACK);
            }
        }

        for (int i = 0; i < renderThreads.length; i++) {
            renderThreads[i] = new RenderThread(i, renderThreads.length, this.renderBundle);
            renderThreads[i].setDaemon(false);
            renderThreads[i].start();
        }

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
            float t = 1.f / renderBundle.width;
            int stepWidth = (int) Math.floor(renderBundle.width / max);

            for (int i = index * stepWidth; i <= (1 + index) * stepWidth - 1; i++) {
                for (int j = 0; j < renderBundle.height; j++) {
                    double[] color = new double[]{Math.max(.4 - i * t, 0), Math.max(i * t - .5, 0), .5, 1};
                    //calculate color here;

                    savePixel(i, j, color, true);
                }
            }
            if (threadsFinished.decrementAndGet() == 0) {
                try {
                    saveFile(this.renderBundle.width, this.renderBundle.height, this.renderBundle.writeImage);
                } catch (IOException ex) {
                    Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
                }
                runningState = RunningState.Stopped;
                System.out.println("Finished");
            }
        }
    }

    synchronized void savePixel(int x, int y, double[] color, boolean update) {

        pixels[x][y] = color;
        renderBundle.writeImage.getPixelWriter().setColor(x, y, new Color(color[0], color[1], color[2], color[3]));
        if (update) {
            pixelsRendered.incrementAndGet();
            System.out.println("Progress: " + getProgress());
            renderBundle.imageViewGui.setImage(renderBundle.writeImage);
        }
    }

    public void saveFile(int width, int height, WritableImage wImage) throws IOException {
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
}
