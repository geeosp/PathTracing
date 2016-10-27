/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing;

import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 *
 * @author Geovane
 */
public class Renderer {

    public void stopRender() {
        System.out.println("Stop not working");
    }

    void startRender(int width, int height, int rays, ImageView ivImage, TextField tfConsole) {
        System.out.println("StartRendering");

        WritableImage wImage = new WritableImage(width, height);
        ivImage.setImage(wImage);
        PixelWriter pixelWriter = wImage.getPixelWriter();
        
        
        
        float t = 1.f/width;
        for (int j = 0; j < wImage.getHeight(); j++) {
            for (int i = 0; i < wImage.getWidth(); i++) {
              
                Color color = Color.color(i*t,1.0-i*t, i*t);
                pixelWriter.setColor(i, j, color);
            }

        }
        
        
        
        
        ivImage.setImage(wImage);
        

    }

}
