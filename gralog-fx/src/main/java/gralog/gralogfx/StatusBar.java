/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx;

import gralog.structure.Structure;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * The status bar of the main window.
 */
public class StatusBar {

    private final HBox statusBar;
    private final Label message;
    private final Label graphType;

    StatusBar() {
        statusBar = new HBox();
        message = new Label("");
        graphType = new Label("");
        Region buttonBarSpacer = new Region();
        HBox.setHgrow(buttonBarSpacer, Priority.ALWAYS);
        statusBar.getChildren().addAll(message, buttonBarSpacer, graphType);
    }

    /**
     * @return The underlying Node object.
     */
    public Node getStatusBar() {
        return statusBar;
    }

    /**
     * Sets the status message.
     *
     * @param message The new status message
     */
    public void setStatus(String message) {
        this.message.setText(message);
    }

    /**
     * Tells the status bar about the current structure. The status bar displays
     * the type of the current structure if it is not null.
     *
     * @param structure The current structure. May be null.
     */
    public void setCurrentStructure(Structure structure) {
        if (structure == null)
            graphType.setText("");
        else
            graphType.setText(structure.getClass().getSimpleName());
    }
}
