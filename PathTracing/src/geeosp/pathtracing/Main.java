/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing;

import renderers.RenderAlgorithm;
import geeosp.pathtracing.scene.RenderScene;
import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import renderers.*;

/**
 *
 * @author geeo
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage1) throws IOException, InterruptedException {
        int errors = 0;
        ArrayList<RenderAlgorithm> renderers = new ArrayList<RenderAlgorithm>();

      renderers.add(new PathTracingRenderer());
      //   renderers.add(new DistanceRenderer());
     //renderers.add(new NormalRendererAlgorithm());
       //renderers.add(new LightRenderer());
        //AlgebTest.test();
        
        
        RenderScene renderScene = RenderScene.load();
        for (int i = 0; i < renderers.size(); i++) {
            Stage stage;
            if (i == 0) {
                stage = primaryStage1;
                stage.setOnCloseRequest(e -> {
                    Platform.exit();
                    System.exit(0);
                });
            } else {
                stage = new Stage();

            }
            int size = 450;
            ImageView imageView = new ImageView();
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            Pane pane = new Pane(imageView);
            Scene scene = new Scene(pane, size, size);
            stage.setScene(scene);
            stage.show();
            stage.setResizable(false);
            Renderer renderer = new Renderer(renderScene, imageView, renderers.get(i));
            renderer.startRender();

        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
