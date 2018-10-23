/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx;

import gralog.gralogfx.panels.ObjectInspector;
import gralog.importfilter.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

/**
 *
 */
public class ImportFilterStage extends Stage {

    Scene scene;
    BorderPane root;
    ObjectInspector objectInspector;
    HBox hBox;
    Button runButton;
    Button cancelButton;
    Button infoButton;

    ImportFilter importfilter;
    ImportFilterParameters params;
    boolean dialogResult;

    public ImportFilterStage(ImportFilter importfilter,
        ImportFilterParameters params, Application app) throws Exception {
        this.importfilter = importfilter;
        this.params = params;
        this.dialogResult = false;

        objectInspector = new ObjectInspector();
        try {
            objectInspector.setObject(params, null);
        } catch (Exception ex) {
        }
        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> this.close());
        runButton = new Button("Import");
        runButton.setPrefWidth(UIConstants.SUBMIT_BUTTON_WIDTH);
        runButton.setDefaultButton(true);
        runButton.setOnAction(e -> {
            this.dialogResult = true;
            this.close();
        });
        hBox = new HBox(UIConstants.HBOX_SPACING);
        infoButton = null;
        ImportFilterDescription descr = importfilter.getDescription();
        String url = descr.url();
        if (url != null && !url.trim().equals("")) {
            infoButton = new Button("Info");
            infoButton.setOnAction(e -> app.getHostServices().showDocument(url));
            hBox.getChildren().addAll(runButton, cancelButton, infoButton);
        } else {
            hBox.getChildren().addAll(runButton, cancelButton);
        }

        root = new BorderPane();
        root.setCenter(objectInspector);
        root.setBottom(hBox);
        scene = new Scene(root, 350, 200);

        this.setScene(scene);
        this.setTitle("Import from " + descr.name());
        this.initModality(Modality.APPLICATION_MODAL);

        this.addEventHandler(WindowEvent.WINDOW_HIDDEN, (e) -> objectInspector.onClose());
    }
}
