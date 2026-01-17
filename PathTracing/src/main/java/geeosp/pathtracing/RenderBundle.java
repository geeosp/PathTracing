/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing;

import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

/**
 *
 * @author geeo
 */
class RenderBundle {


    public final ImageView imageViewGui;
    public final WritableImage writeImage;

    public RenderBundle(int width, int height, int rays, ImageView ivImage, TextField tfConsole) {
        
        this.imageViewGui = ivImage;
        this.writeImage = new WritableImage(width, height);

    }

    public RenderBundle(int width, int height, int rays, ImageView ivImage) {
     
        this.imageViewGui = ivImage;
        this.writeImage = new WritableImage(width, height);
    }

}
