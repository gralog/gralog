/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.gralogfx;

import gralog.plugins.*;
import gralog.structure.*;
import gralog.generator.*;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;

/**
 *
 * @author viktor
 */
public class GeneratorStage extends Stage {
    Scene scene;
    BorderPane root;
    ObjectInspector objectInspector;
    HBox hBox;
    Button runButton;
    Button cancelButton;
    Button infoButton;
    
    Generator gen;
    GeneratorParameters params;
    boolean DialogResult;
    
    public GeneratorStage(Generator gen, GeneratorParameters params, Application app) throws Exception
    {
        this.gen = gen;
        this.params = params;
        this.DialogResult = false;
        
        objectInspector = new ObjectInspector();
        try {
            objectInspector.SetObject(params);
        } catch(Exception ex) {
        }
        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> this.close());
        runButton = new Button("Run");
        runButton.setOnAction(e -> {this.DialogResult = true; this.close();});
        hBox = new HBox();
        hBox.getChildren().addAll(cancelButton,runButton);
        infoButton = null;
        GeneratorDescription descr = gen.getDescription();
        String url = descr.url();
        if(url != null && !url.trim().equals(""))
        {
            infoButton = new Button("Info");
            infoButton.setOnAction(e -> app.getHostServices().showDocument(url));
            hBox.getChildren().add(infoButton);
        }
        
        root = new BorderPane();
        root.setCenter(objectInspector);
        root.setBottom(hBox);
        scene = new Scene(root, 350, 200);

        this.setScene(scene);
        this.setTitle("Run Algorithm");
        this.initModality(Modality.APPLICATION_MODAL);
    }
    
    public boolean ShowAndWait()
    {
        this.showAndWait();
        return DialogResult;
    }
}
