/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author geeo
 */
public class RenderTest extends Application {
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Render");
        FXMLLoader loader = new FXMLLoader(RenderFrameController.class.getResource("RenderFrame.fxml"));
        
        
        Pane myPane = (Pane) loader.load();
        Scene myScene = new Scene(myPane);
        primaryStage.setScene(myScene);
        
       RenderFrameController guiController =loader.<RenderFrameController>getController();
        Renderer renderer = new Renderer();
        
        guiController.setRender(renderer);
        
        primaryStage.show();
       int width = 12      ;
        int height =    4;
       int rays = 100;
       int threads = 1;
        try {
            guiController.startRendering(width,height, rays, threads);
        } catch (InterruptedException ex) {
            Logger.getLogger(RenderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
      public static void main(String[] args) {
        launch(args);
    }
}
