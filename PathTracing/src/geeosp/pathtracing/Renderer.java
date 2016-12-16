/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing;

import renderers.RenderAlgorithm;
import geeosp.pathtracing.scene.RenderScene;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author Geovane
 */
public class Renderer {

    private final Object lock = new Object();
    private static long lastimeupdated = System.currentTimeMillis();
    private RunningState runningState;
    private Thread[] renderThreads;
    private AtomicInteger pixelsRendered;
    private AtomicInteger threadsFinished;
    private long time;
    private RenderBundle renderBundle;
    int currentProgress;
    RenderScene scene;
    private double[][][] mappedPixels;
    private double[][][] rawPixels;
    private RenderAlgorithm algorithm;

    public Renderer() {
        this.runningState = RunningState.Stopped;
    }

    Renderer(RenderScene renderScene, ImageView imageView, RenderAlgorithm algorithm) {
        this.scene = renderScene;
        this.renderBundle = new RenderBundle(renderScene.getSizeWidth(), renderScene.getSizeHeight(), renderScene.getNpaths(), imageView);
        this.renderThreads = new RenderThread[Math.max(1, renderScene.getNthreads())];
        this.pixelsRendered = new AtomicInteger(0);
        this.mappedPixels = new double[renderScene.getSizeWidth()][renderScene.getSizeHeight()][4];
        this.rawPixels = new double[renderScene.getSizeWidth()][renderScene.getSizeHeight()][4];
        this.threadsFinished = new AtomicInteger(renderScene.getNthreads());
        this.algorithm = algorithm;
        algorithm.set(renderScene);
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
                //   savePixel(i, j, scene.getBackgroundColor(), false);
                //       mappedPixels[i][j] = scene.getBackgroundColor();
                //    this.renderBundle.writeImage.getPixelWriter().setColor(i, j, Color.BLACK);
            }
        }
        UpdateViewThread updateViewThread = new UpdateViewThread(renderBundle);
        updateViewThread.setDaemon(false);
        updateViewThread.start();
        for (int i = 0; i < renderThreads.length; i++) {
            renderThreads[i] = new RenderThread(i, renderThreads.length, this.renderBundle);
            renderThreads[i].setDaemon(false);
            renderThreads[i].start();
        }

    }

    public double[] toneMapp(double[] color, double tm) {
        double[] c = new double[color.length];
        for (int i = 0; i < 3; i++) {
            color[i] = color[i] / (color[i] + tm);
        }
        return color;
    }

    public void renderAfterFinish() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

                Stage stage;

                stage = new Stage();

                int size = 450;
                ImageView imageView = new ImageView();
                imageView.setFitWidth(size);
                imageView.setFitHeight(size);
                Pane pane = new Pane(imageView);
                Scene scene = new Scene(pane, size, size);
                stage.setScene(scene);
                stage.show();
                stage.setResizable(false);
                WritableImage writableImage = new WritableImage(rawPixels.length, rawPixels[0].length);
                double max = 0.00001;
                for (int x = 0; x < rawPixels.length; x++) {
                    for (int y = 0; y < rawPixels[0].length; y++) {
                        for (int i = 0; i < 4; i++) {
                            if (rawPixels[x][y][i] > max) {
                                max = rawPixels[x][y][i];
                            }
                        }
                    }
                }
                for (int x = 0; x < rawPixels.length; x++) {
                    for (int y = 0; y < rawPixels[0].length; y++) {
                        for (int i = 0; i < 4; i++) {
                            if (rawPixels[x][y][i] > max) {
                                rawPixels[x][y][i] = rawPixels[x][y][i] / max;
                            }
                        }
                    }
                }

                for (int x = 0; x < rawPixels.length; x++) {
                    for (int y = 0; y < rawPixels[0].length; y++) {
                        writableImage.getPixelWriter().setColor(x, rawPixels[0].length - y - 1, new Color(rawPixels[x][y][0], rawPixels[x][y][1], rawPixels[x][y][2], rawPixels[x][y][3]));

                    }
                }
                imageView.setImage(writableImage);

            }

        });
    }

    public static void saveFile(int width, int height, WritableImage wImage) throws IOException {
        File dir = new File("out");
        dir.mkdir();
        String name = "out/_" + System.currentTimeMillis();//

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

    class UpdateViewThread extends Thread {

        RenderBundle bundle;

        public UpdateViewThread(RenderBundle bundle) {
            this.bundle = bundle;
        }

        @Override
        public void run() {
            while (runningState == RunningState.Running) {
                // try {
                //     sleep(20);
                if (currentProgress != getProgress()) {
                    //   System.out.println("Progress: " + currentProgress);
                    ImageView iv = renderBundle.imageViewGui;
                    if (iv != null) {
                        WritableImage wi = renderBundle.writeImage;
                        if (wi != null) {
                            //  if (System.currentTimeMillis() - lastimeupdated > 500) {

                            try {
                                iv.setImage(wi);
                            } catch (Exception e) {
                                System.out.println("erro");
                            }
                            //   }
                        }
                    }

                    currentProgress = getProgress();
                }
                //  } catch (InterruptedException ex) {
                //      Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
                //  }
            }
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
            float t = 1.f / scene.getSizeHeight();
            int stepWidth = (int) Math.floor(scene.getSizeWidth() / max);

            for (int i = index * stepWidth; i <= (1 + index) * stepWidth - 1; i++) {
                for (int j = 0; j < scene.getSizeHeight(); j++) {
                    double[] color = new double[4];
                    color = algorithm.calulatePixel(i, j, scene);
                    try {
                        this.savePixelConcurrent(i, j, color, true);
                    } catch (IllegalArgumentException e) {
                        System.err.println("pixel: " + i + ", " + j + " " + e.getMessage());
                    }
                }
            }
            if (threadsFinished.decrementAndGet() == 0) {
                time = System.currentTimeMillis() - time;
                try {
                    renderAfterFinish();
                    saveFile(scene.getSizeWidth(), scene.getSizeHeight(), this.renderBundle.writeImage);
                } catch (IOException ex) {
                    Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
                }
                runningState = RunningState.Stopped;
                System.err.println("Finished: " + (time / 1000.0) + " seconds");

            }
        }

        synchronized void savePixelConcurrent(int x, int y, double[] color, boolean update) {
            //System.err.println(Algeb.VectorToString(color));
            rawPixels[x][y] = new double[]{color[0], color[1], color[2], color[3]};
            double[] c = toneMapp(color, scene.getTonemapping());
            mappedPixels[x][y] = c;

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    renderBundle.writeImage.getPixelWriter().setColor(x, mappedPixels[0].length - y - 1, new Color(mappedPixels[x][y][0], mappedPixels[x][y][1], mappedPixels[x][y][2], mappedPixels[x][y][3]));
                }
            });
            if (update) {

                pixelsRendered.incrementAndGet();

            }
        }

    }
}
