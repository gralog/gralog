/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx;

import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

/**
 * The mode buttons in the main window. You need to tell the mode buttons about
 * the StructurePane object they apply to so that the buttons can show and
 * change the mode of this StructurePane.
 */
public class ModeButtons {

    private static final String INACTIVE_BUTTON_STYLE = "";
    private static final String ACTIVE_BUTTON_STYLE = "-fx-base: #0000FF;";

    private final Button buttonSelectMode;
    private final Button buttonVertexMode;
    private final Button buttonEdgeMode;

    private final HBox buttonBar;
    private StructurePane structurePane;

    private final Consumer<String> onModeUpdate;

    /**
     * @param onModeUpdate This function will be called whenever the mode needs
     * to be displayed. For example, this function could show the mode in the
     * status bar. May be null.
     */
    public ModeButtons(Consumer<String> onModeUpdate) {
        if (onModeUpdate != null)
            this.onModeUpdate = onModeUpdate;
        else
            this.onModeUpdate = (s) -> {
            };

        buttonBar = new HBox(UIConstants.HBOX_SPACING);
        buttonSelectMode = new Button("Select");
        buttonSelectMode.tooltipProperty().setValue(new Tooltip("Shortcut: s"));
        buttonVertexMode = new Button("New vertex");
        buttonVertexMode.tooltipProperty().setValue(new Tooltip("Shortcut: v"));
        buttonEdgeMode = new Button("New edge");
        buttonEdgeMode.tooltipProperty().setValue(new Tooltip("Shortcut: e"));
        buttonBar.getChildren().addAll(buttonSelectMode, buttonVertexMode, buttonEdgeMode);

        checkMode();
    }

    /**
     * @return The underlying Node object.
     */
    public Node getButtonBar() {
        return buttonBar;
    }

    /**
     * Sets the current structure pane. After this method, the buttons will
     * reflect the state of the new structure pane.
     *
     * @param pane The new structure pane. May be null if there is no current
     * structure pane.
     */
    public void setCurrentStructurePane(StructurePane pane) {
        if (structurePane != pane) {
            structurePane = pane;
            checkMode();
        }
    }


    private void checkMode() {
        buttonSelectMode.setStyle(INACTIVE_BUTTON_STYLE);
        buttonVertexMode.setStyle(INACTIVE_BUTTON_STYLE);
        buttonEdgeMode.setStyle(INACTIVE_BUTTON_STYLE);
        if (structurePane == null)
            return;
        switch (structurePane.getMouseMode()) {
            case SELECT_MODE:
                buttonSelectMode.setStyle(ACTIVE_BUTTON_STYLE);
                onModeUpdate.accept("Selection mode");
                break;
            case VERTEX_MODE:
                buttonVertexMode.setStyle(ACTIVE_BUTTON_STYLE);
                onModeUpdate.accept("Vertex creation mode");
                break;
            case EDGE_MODE:
                buttonEdgeMode.setStyle(ACTIVE_BUTTON_STYLE);
                onModeUpdate.accept("Edge creation mode");
                break;
        }
    }
}
