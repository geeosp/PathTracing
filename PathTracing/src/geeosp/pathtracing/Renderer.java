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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

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
    private int numThreads = 4;

    public RunningState getRunningState() {
        return runningState;
    }

    public Renderer() {
        this.runningState = RunningState.Stopped;
        this.renderThreads = new RenderThread[numThreads];
    }

    public void stopRender() {
        System.out.println("Stop");

    }

    void toogleRender(int width, int height, int rays, ImageView ivImage, TextField tfConsole) throws IOException {
        switch (runningState) {
            case Running:
                stopRender();
                this.runningState = RunningState.Stopped;

                break;
            case Stopped:
                this.runningState = RunningState.Running;
                 {
                    try {
                        startRender(width, height, rays, ivImage, tfConsole);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;

        }
    }

    void startRender(int width, int height, int rays, ImageView ivImage, TextField tfConsole) throws IOException, InterruptedException {
        System.out.println("StartRendering");
        WritableImage wImage = new WritableImage(width, height);

        for (int i = 0; i < numThreads; i++) {
            renderThreads[i] = new RenderThread(i,renderThreads.length, new RenderBundle( width, height, rays, ivImage, wImage, tfConsole));
            renderThreads[i].start();
        }
        for(int i =0;i<numThreads;i++)
            renderThreads[i].join();

        ivImage.setImage(wImage);
        
        
        
        PixelWriter pixelWriter = wImage.getPixelWriter();

        saveFile(width, height, wImage);
        runningState= RunningState.Stopped;
    }

    class RenderBundle{
        public int width;
        public int height;
        public int rays;
        public ImageView ivImage;
        public TextField tfConsole;
        public WritableImage wImage;

        public RenderBundle(int width, int height, int rays, ImageView ivImage,  WritableImage wImage,TextField tfConsole) {
            this.width = width;
            this.height = height;
            this.rays = rays;
            this.ivImage = ivImage;
            this.wImage = wImage;
            this.tfConsole = tfConsole;
        }

        
        
    }
    
    class RenderThread extends Thread {
        private int index;
        private int max;
        private RenderBundle renderBundle;
        

        private RenderThread(int index, int max, RenderBundle renderBundle) {
this.index=index;
        this.max = max;
        this.renderBundle = renderBundle;        }

        
        
        

        public void run() {
            float t = 1.f / renderBundle.width;
            int  stepWidth = (int)Math.floor(renderBundle.width/max);
            
            for(int i = index*stepWidth;i<=(1+index)*stepWidth-1;i++){
                for(int j=0;j<renderBundle.height;j++){
                    Color color = Color.color(i * t, 1.0 - i * t, i * t);
                    savePixel(i, j, color);
                }
            }
            
            
            
           
        }
         public synchronized void savePixel(int posX, int posY, Color color){
                   renderBundle.wImage.getPixelWriter().setColor(posX, posY, color);
                   renderBundle.ivImage.setImage(renderBundle.wImage);
               
         }
    }

    public void saveFile(int width, int height, WritableImage wImage) {
        String name = "" + width + "px_" + height + "px_";
        FileChooser fileChooser = new FileChooser();

        /*
                //Set extension filter
                FileChooser.ExtensionFilter extFilter = 
                        new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
                fileChooser.getExtensionFilters().add(extFilter);
               
                //Show save file dialog
                File file = fileChooser.showSaveDialog(new Stage());
         */
        File file = new File(name + ".png");
        if (file != null) {
            try {

                RenderedImage renderedImage = SwingFXUtils.fromFXImage(wImage, null);
                ImageIO.write(renderedImage, "png", file);
            } catch (IOException ex) {
                //  Logger.getLogger(JavaFX_DrawOnCanvas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
