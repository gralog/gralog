/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.gralogfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

/**
 *
 * @author viktor
 */
public class AboutStage extends Stage {
    Application host;
    Scene scene;
    BorderPane root;
    WebView webView;
    WebEngine webEngine;
    Button okButton;
    
    public AboutStage(Application host)
    {
        this.host = host;
        okButton = new Button("OK");
        okButton.setOnAction(e -> this.close());
        
        webView = new WebView();
        webEngine = webView.getEngine();
        webEngine.loadContent(
            "<b>GrALoG FX</b><br/>"
          + "Graphs, Algorithms, Logic and Games<br/>"
          + "©2015 Technische Universität Berlin<br/>"
          + "Lehrstuhl für Logik und Semantik"
        );
        
        root = new BorderPane();
        root.setCenter(webView);
        root.setBottom(okButton);
        scene = new Scene(root, 350, 200);

        this.setScene(scene);
        this.setTitle("About GrALoG");
        this.initModality(Modality.APPLICATION_MODAL);
    }
}
