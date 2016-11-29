/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing;

import geeosp.pathtracing.scene.RenderScene;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
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
        Algeb.test();
        RenderScene renderScene = RenderScene.load();
        //System.out.println(renderScene);
        ImageView imageView = new ImageView();
        imageView.setFitWidth(640);
        imageView.setFitHeight(640);
        Pane pane = new Pane(imageView);
        Scene scene = new Scene(pane, 640, 640);

        primaryStage1.setScene(scene);
        primaryStage1.show();
        primaryStage1.setResizable(false);
        primaryStage1.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        Renderer renderer = new Renderer(renderScene, imageView, new PathTracingAlgorithm());
        //Renderer renderer = new Renderer(renderScene, imageView, new NormalRendererAlgorithm());

        renderer.startRender();

        // System.out.println("Erros: " + errors);
        /*
         */
    }

    public static void main(String[] args) {
        launch(args);
    }
}
