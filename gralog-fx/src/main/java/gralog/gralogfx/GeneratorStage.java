/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx;

import gralog.algorithm.AlgorithmParameters;
import gralog.generator.*;

import gralog.gralogfx.panels.ObjectInspector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import java.util.ArrayList;


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

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> this.close());
        runButton = new Button("Run");
        runButton.setPrefWidth(UIConstants.SUBMIT_BUTTON_WIDTH);
        runButton.setDefaultButton(true);
        runButton.setOnAction(e -> {
            this.dialogResult = true;
            this.close();
        });
        hBox = new HBox(UIConstants.HBOX_SPACING);
        hBox.getChildren().addAll(runButton, cancelButton);
        infoButton = null;
        GeneratorDescription descr = gen.getDescription();
        String url = descr.url();
        if (url != null && !url.trim().equals("")) {
            infoButton = new Button("Info");
            infoButton.setOnAction(e -> app.getHostServices().showDocument(url));
            hBox.getChildren().add(infoButton);
        }

        objectInspector = new ObjectInspector();
        try {
            ArrayList<Object> myList = new ArrayList<Object>();
            myList.add(params);
            objectInspector.setObject(myList, (b) -> runButton.setDisable(!b));
        } catch (Exception ex) {
        }

        root = new BorderPane();
        root.setCenter(objectInspector);
        root.setBottom(hBox);
        scene = new Scene(root);

        this.setScene(scene);
        this.setTitle("Generate " + gen.getDescription().name());
        this.initModality(Modality.APPLICATION_MODAL);

        this.addEventHandler(WindowEvent.WINDOW_HIDDEN, (e) -> objectInspector.onClose());
    }
}
