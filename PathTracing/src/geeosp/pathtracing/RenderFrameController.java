/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author Geovane
 */
public class RenderFrameController implements Initializable {

    private Renderer renderer;

    void setRender(Renderer renderer) {
        this.renderer = renderer;
//     throw new Unsuppo0rtedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void startRendering(int width, int height, int rays, int threads) throws IOException, InterruptedException {
        ivRender.setFitHeight(height);
        ivRender.setFitWidth(width);
        rootPane.setMinSize(width, height);
        rootPane.setPrefSize(width, height);
        
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                //
                renderer.set(width, height, rays, threads, ivRender, tfOutput);
                renderer.startRender();
                return null;
            }
        };
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
       
 //       t.join();
       // ivRender.setImage(renderer.getwImage());

    }

    /**
     * Initializes the controller class.
     */
    @FXML
    private ImageView ivRender;

    @FXML
    private TextField tfOutput;
    @FXML
    private StackPane rootPane;

    public ImageView getIvRender() {
        return ivRender;
    }

    public TextField getTfOutput() {
        return tfOutput;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
       /*
        rootPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ivRender.setFitWidth(newValue.intValue());
                rootPane.setMinHeight(ivRender.getFitHeight() + tfOutput.getHeight()+50);
            }
        });
        */
    }
    
}
