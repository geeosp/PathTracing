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

        public int width;
        public int height;
        public int rays;
        public String configPath;
        public String modelsPath;

        public ImageView imageViewGui;
        public TextField textFieldConsole;
        public WritableImage writeImage;

        public RenderBundle(int width, int height, int rays, ImageView ivImage, TextField tfConsole) {
            this.width = width;
            this.height = height;
            this.rays = rays;
            this.imageViewGui = ivImage;
            this.writeImage = new WritableImage(width, height);
            this.textFieldConsole = tfConsole;

        }

    }
