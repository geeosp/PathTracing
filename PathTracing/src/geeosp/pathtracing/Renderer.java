/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import sun.java2d.pipe.RenderBuffer;

/**
 *
 * @author Geovane
 */
public class Renderer {

    public enum RunningState {
        Running,
        Stopped
    }
    private RunningState runningState;
    private Thread[] renderThreads;
    private AtomicInteger pixelsRendered;
    RenderBundle renderBundle;

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
        this.pixelsRendered=new AtomicInteger(0);
    }

    void startRender() throws IOException, InterruptedException {
        System.out.println("Starting Rendering");
        this.runningState = RunningState.Running;
        for (int i = 0; i < this.renderBundle.width; i++) {
            for (int j = 0; j < this.renderBundle.height; j++) {
                this.renderBundle.writeImage.getPixelWriter().setColor(i, j, Color.BLACK);
            }
        }

        for (int i = 0; i < renderThreads.length; i++) {
            renderThreads[i] = new RenderThread(i, renderThreads.length, this.renderBundle);
            renderThreads[i].setDaemon(true);
            renderThreads[i].start();
        }
        UpdateThread updateThread = new UpdateThread(this.renderBundle.textFieldConsole, this.renderBundle.imageViewGui);
        updateThread.setDaemon(true);
        updateThread.start();
       /*
        for (int i = 0; i < this.renderThreads.length; i++) {

            try {
                renderThreads[i].join();
            } catch (InterruptedException ex) {
                Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
*/
       updateThread.join();
        saveFile(this.renderBundle.width, this.renderBundle.height, this.renderBundle.writeImage);
        runningState = RunningState.Stopped;
        System.out.println("Finished");

    }

    class UpdateThread extends Thread {

        TextField tfConsole;
        ImageView ivImage;

        public UpdateThread(TextField tfConsole, ImageView ivImage) {
            this.tfConsole = tfConsole;
            this.ivImage = ivImage;
        }

        @Override
        public void run() {
            while (isRunning()) {

                try {
                    sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
//                System.out.println("updatingImage");
            renderBundle.imageViewGui.setImage(renderBundle.writeImage);
                tfConsole.setText("" + getProgress());
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
            float t = 1.f / renderBundle.width;
            int stepWidth = (int) Math.floor(renderBundle.width / max);

            for (int i = index * stepWidth; i <= (1 + index) * stepWidth - 1; i++) {
                for (int j = 0; j < renderBundle.height; j++) {
                    Color color = Color.color(i * t, 1.0 - i * t, i * t);
                    savePixel(i, j, color);
                }

            }
        }

        void savePixel(int posX, int posY, Color color) {
            renderBundle.writeImage.getPixelWriter().setColor(posX, posY, color);
            pixelsRendered.incrementAndGet();
            //renderBundle.imageViewGui.setImage(renderBundle.writeImage);

        }
    }

    public void saveFile(int width, int height, WritableImage wImage) {
        File dir = new File("out");
        dir.mkdir();
        String name = "out/" + width + "px_" + height + "px_";
        //FileChooser fileChooser = new FileChooser();
        File file = new File(name + ".jpg");
        if (file != null) {
            try {

                RenderedImage renderedImage = SwingFXUtils.fromFXImage(wImage, null);
                ImageIO.write(renderedImage, "jpg", file);

            } catch (IOException ex) {
                //  Logger.getLogger(JavaFX_DrawOnCanvas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
