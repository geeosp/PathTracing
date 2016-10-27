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

    public void stopRender() {
        System.out.println("Stop not working");
    }

    void startRender(int width, int height, int rays, ImageView ivImage, TextField tfConsole) throws IOException {
        System.out.println("StartRendering");

        
        WritableImage wImage = new WritableImage(width, height);
        ivImage.setImage(wImage);
        PixelWriter pixelWriter = wImage.getPixelWriter();
        
        
        
        float t = 1.f/width;
        for (int j = 0; j < wImage.getHeight(); j++) {
            for (int i = 0; i < wImage.getWidth(); i++) {
              
                Color color = Color.color(i*t,1.0-i*t, i*t);
                pixelWriter.setColor(i, j, color);
        ivImage.setImage(wImage);
            }

        }
        
        saveFile(width, height, wImage);
        
    

        
        
        
        

    }
    
public void saveFile(int width, int height, WritableImage wImage){
            String name = ""+width+"px_"+height+"px_";
    FileChooser fileChooser = new FileChooser();
                 
    /*
                //Set extension filter
                FileChooser.ExtensionFilter extFilter = 
                        new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
                fileChooser.getExtensionFilters().add(extFilter);
               
                //Show save file dialog
                File file = fileChooser.showSaveDialog(new Stage());
      */
    File file = new File(name+".png");
                if(file != null){
                    try {
                      
                        RenderedImage renderedImage = SwingFXUtils.fromFXImage(wImage, null);
                        ImageIO.write(renderedImage, "png", file);
                    } catch (IOException ex) {
                      //  Logger.getLogger(JavaFX_DrawOnCanvas.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
    }
}
