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
    public void start(Stage primaryStage1) throws IOException {
        int errors = 0;
        for (int i = 0; i < 1; i++) {
            try {
                int width = 1280;
                int height = 720;
                int rays = 100;
                int threads = 4;
                Stage primaryStage = new Stage();
                primaryStage.setTitle("Render");
                FXMLLoader loader = new FXMLLoader(RenderFrameController.class.getResource("RenderFrame.fxml"));

                Pane myPane = (Pane) loader.load();
                Scene myScene = new Scene(myPane,width, height);
                primaryStage.setScene(myScene);

                RenderFrameController guiController = loader.<RenderFrameController>getController();
                Renderer renderer = new Renderer();

                guiController.setRender(renderer);

                primaryStage.show();
                try {
                    guiController.startRendering(width, height, rays, threads);
                } catch (InterruptedException ex) {
                    Logger.getLogger(RenderTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (Exception e) {
                errors++;
            }
        }
            System.out.println("Erros: "+errors);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
