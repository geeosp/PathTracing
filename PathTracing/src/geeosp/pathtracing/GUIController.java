/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import javafx.stage.Stage;
/**
 * FXML Controller class
 *
 * @author Geovane
 */
public class GUIController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private  TextField tfWidth;
    @FXML
    private TextField tfHeight;
    @FXML
    private TextField tfConsole;
    @FXML
    private TextField tfRays;
    
    @FXML
    private ImageView ivImage;
    
    @FXML
    private BorderPane paneRoot;
    
    @FXML
    private AnchorPane formPane;
    
    
    private Renderer renderer;
    private Stage stage;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert ivImage!=null;
        System.out.println("init");
        }    
    
    public Pane getRootPane(){
        
        return this.paneRoot;
    }
    
    
    
    public void setRenderer(Renderer render){
        this.renderer = render;
    }
    
    public void startRender(){
      
        int width = Integer.parseInt(tfWidth.getText());
        int height = Integer.parseInt(tfHeight.getText());
        int rays = Integer.parseInt(tfRays.getText());
        this.renderer.startRender(width, height, rays, ivImage, tfConsole);
        ivImage.setFitHeight(height);
        ivImage.setFitWidth(width);
        resize();
        
        
    }
    
    public void setStage(Stage stage){
        this.stage = stage;
        resize();
    }
    private void resize(){
        this.stage.setMinHeight(ivImage.getFitHeight()+140);
        
        this.stage.setMinWidth(ivImage.getFitWidth()+formPane.getWidth()+50);
       // this.stage.setWidth(this.stage.getMinWidth());
        //this.stage.setHeight(this.stage.getMinHeight());
    }
    
}
