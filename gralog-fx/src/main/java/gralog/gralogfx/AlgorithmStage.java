/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx;

import gralog.gralogfx.panels.ObjectInspector;
import gralog.structure.*;
import gralog.algorithm.*;

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
public class AlgorithmStage extends Stage {

    Scene scene;
    BorderPane root;
    ObjectInspector objectInspector;
    HBox hBox;
    Button runButton;
    Button cancelButton;
    Button infoButton;

    Algorithm algo;
    AlgorithmParameters params;
    Structure structure;
    boolean dialogResult;

    public AlgorithmStage(Algorithm algo, Structure structure,
        AlgorithmParameters params, Application app) throws Exception {
        this.algo = algo;
        this.structure = structure;
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
        AlgorithmDescription descr = algo.getDescription();
        String url = descr.url();
        if (url != null && !url.trim().equals("")) {
            infoButton = new Button("Info");
            infoButton.setOnAction(e -> app.getHostServices().showDocument(url));
            hBox.getChildren().add(infoButton);
        }

        objectInspector = new ObjectInspector();
        try {
            // objectInspector.setObject(params, null, (b) -> runButton.setDisable(!b));
            ArrayList<Object> myList = new ArrayList<Object>();
            myList.add(params);
            objectInspector.setObject(myList,(b) -> runButton.setDisable(!b));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        root = new BorderPane();
        root.setCenter(objectInspector);
        root.setBottom(hBox);
        scene = new Scene(root);
        this.setScene(scene);
        this.setTitle("Run Algorithm");
        this.initModality(Modality.APPLICATION_MODAL);

        this.addEventHandler(WindowEvent.WINDOW_HIDDEN, (e) -> objectInspector.onClose());
    }
}
