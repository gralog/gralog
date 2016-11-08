/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.gralogfx;

import gralog.algorithm.AlgorithmParameters;
import gralog.generator.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;

/**
 *
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
    AlgorithmParameters params;
    boolean dialogResult;

    public GeneratorStage(Generator gen, AlgorithmParameters params,
            Application app) throws Exception {
        this.gen = gen;
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
        runButton = new Button("Run");
        runButton.setOnAction(e -> {
            this.dialogResult = true;
            this.close();
        });
        hBox = new HBox();
        hBox.getChildren().addAll(cancelButton, runButton);
        infoButton = null;
        GeneratorDescription descr = gen.getDescription();
        String url = descr.url();
        if (url != null && !url.trim().equals("")) {
            infoButton = new Button("Info");
            infoButton.setOnAction(e -> app.getHostServices().showDocument(url));
            hBox.getChildren().add(infoButton);
        }

        root = new BorderPane();
        root.setCenter(objectInspector);
        root.setBottom(hBox);
        scene = new Scene(root);

        this.setScene(scene);
        this.setTitle("Generate " + gen.getDescription().name());
        this.initModality(Modality.APPLICATION_MODAL);
    }
}
