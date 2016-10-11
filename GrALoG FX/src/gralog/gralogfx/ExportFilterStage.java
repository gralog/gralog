/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.gralogfx;

import gralog.plugins.*;
import gralog.structure.*;
import gralog.exportfilter.*;

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
 */
public class ExportFilterStage extends Stage {

    Scene scene;
    BorderPane root;
    ObjectInspector objectInspector;
    HBox hBox;
    Button runButton;
    Button cancelButton;
    Button infoButton;

    ExportFilter exportfilter;
    ExportFilterParameters params;
    boolean dialogResult;

    public ExportFilterStage(ExportFilter exportfilter,
            ExportFilterParameters params, Application app) throws Exception {
        this.exportfilter = exportfilter;
        this.params = params;
        this.dialogResult = false;

        objectInspector = new ObjectInspector();
        try {
            objectInspector.setObject(params, null);
        }
        catch (Exception ex) {
        }
        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> this.close());
        runButton = new Button("Export");
        runButton.setOnAction(e -> {
            this.dialogResult = true;
            this.close();
        });
        hBox = new HBox();
        infoButton = null;
        ExportFilterDescription descr = exportfilter.getDescription();
        String url = descr.url();
        if (url != null && !url.trim().equals("")) {
            infoButton = new Button("Info");
            infoButton.setOnAction(e -> app.getHostServices().showDocument(url));
            hBox.getChildren().addAll(infoButton, cancelButton, runButton);
        }
        else
            hBox.getChildren().addAll(cancelButton, runButton);

        root = new BorderPane();
        root.setCenter(objectInspector);
        root.setBottom(hBox);
        scene = new Scene(root, 350, 200);

        this.setScene(scene);
        this.setTitle("Export to " + descr.name());
        this.initModality(Modality.APPLICATION_MODAL);
    }
}
