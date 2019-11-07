/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
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
 */
public class AboutStage extends Stage {

    Scene scene;
    BorderPane root;
    WebView webView;
    WebEngine webEngine;
    Button okButton;

    public AboutStage(Application host) {
        okButton = new Button("OK");
        okButton.setOnAction(e -> this.close());

        webView = new WebView();
        webEngine = webView.getEngine();
        webEngine.loadContent(
            "<b>Gralog</b><br/>"
            + "Graphs, Algorithms, Logic and Games<br/>"
            + "©2015-2019 Technische Universität Berlin<br/>"
            + "Lehrstuhl für Logik und Semantik"
        );

        root = new BorderPane();
        root.setCenter(webView);
        root.setBottom(okButton);
        scene = new Scene(root, 350, 200);

        this.setScene(scene);
        this.setTitle("About Gralog");
        this.initModality(Modality.APPLICATION_MODAL);
    }
}
