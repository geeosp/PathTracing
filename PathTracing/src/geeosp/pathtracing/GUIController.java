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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

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
    
    private Renderer renderer;
    
    
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
}
