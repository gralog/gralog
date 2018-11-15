/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx;

import gralog.algorithm.Algorithm;
import gralog.algorithm.AlgorithmParameters;
import gralog.gralogfx.panels.ObjectInspector;
import gralog.structure.Structure;
import java.util.ArrayList;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;

/**
 *
 */
public class AlgorithmResultStage extends Stage {

    Scene scene;
    BorderPane root;
    ObjectInspector objectInspector;
    Button closeButton;

    Algorithm algo;
    Structure structure;
    StructurePane structurePane;
    AlgorithmParameters params;
    Object result;

    public AlgorithmResultStage(Algorithm algo, Structure structure,
        AlgorithmParameters params, StructurePane structurePane,
        Object result) throws Exception {
        this.algo = algo;
        this.structure = structure;
        this.params = params;
        this.result = result;
        this.structurePane = structurePane;

        objectInspector = new ObjectInspector();

        try {
            ArrayList<Object> myList = new ArrayList<Object>();
            myList.add(result);
            objectInspector.setObject(myList);
            // objectInspector.setObject(result, structurePane);
        } catch (Exception ex) {
        }
        closeButton = new Button("Close");
        closeButton.setOnAction(e -> this.close());

        root = new BorderPane();

        root.setCenter(objectInspector);
        root.setBottom(closeButton);
        scene = new Scene(root, 640, 480);

        this.setScene(scene);
        this.setTitle("Algorithm Result");

        this.addEventHandler(WindowEvent.WINDOW_HIDDEN, (e) -> objectInspector.onClose());
    }
}
