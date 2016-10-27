/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing;

import javafx.application.Application;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import javafx.stage.Stage;

/**
 *
 * @author Geovane
 */
public class PathTracing extends Application {
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("FXML TableView Example");
        GUIController guiController = new GUIController();
        Renderer renderer = new Renderer();
        guiController.setRenderer(renderer);
        Pane myPane = (Pane) FXMLLoader.load(guiController.getClass().getResource("GUI.fxml"));
        Scene myScene = new Scene(myPane);
        primaryStage.setScene(myScene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
