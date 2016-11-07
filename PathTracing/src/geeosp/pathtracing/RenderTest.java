/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing;

import geeosp.pathtracing.scene.RenderScene;
import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author geeo
 */
public class RenderTest extends Application {

    @Override
    public void start(Stage primaryStage1) throws IOException, InterruptedException {
        int errors = 0;

        RenderScene renderScene = RenderScene.load();
        //System.out.println(renderScene);
        ImageView imageView = new ImageView();
        imageView.setFitWidth(renderScene.getSizeWidth());
        imageView.setFitHeight(renderScene.getSizeHeight());
        Pane pane = new Pane(imageView);
        Scene scene = new Scene(pane, renderScene.getSizeWidth(), renderScene.getSizeHeight());
       
        
        
        primaryStage1.setScene(scene);
        primaryStage1.show();
        primaryStage1.setResizable(false);
        Renderer renderer = new Renderer(renderScene, imageView, new PathTracingAlgorithm());
        renderer.startRender();

       // System.out.println("Erros: " + errors);
        /*
         */
        
        
        

    }

    public static void main(String[] args) {
        launch(args);
    }
}
