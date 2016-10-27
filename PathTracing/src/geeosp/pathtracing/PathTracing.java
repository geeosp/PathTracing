/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing;

import javafx.application.Application;
import java.io.IOException;
import javafx.fxml.FXML;
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
        FXMLLoader loader = new FXMLLoader(GUIController.class.getResource("GUI.fxml"));
        Pane myPane = (Pane) loader.load();
        Scene myScene = new Scene(myPane);
        primaryStage.setScene(myScene);
        
       GUIController guiController =loader.<GUIController>getController();
        Renderer renderer = new Renderer();
        guiController.setRenderer(renderer);
        
        primaryStage.show();
        primaryStage.setMinHeight(primaryStage.getHeight()+50);
        primaryStage.setMinWidth(primaryStage.getWidth()+50);
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
