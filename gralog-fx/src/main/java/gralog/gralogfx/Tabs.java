/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx;

import gralog.structure.Structure;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import java.util.List;
import java.util.ArrayList;

/**
 * A tab pane for structures.
 */
public class Tabs {

    private final TabPane tabPane;
    private final ObjectInspector objectInspector;

    private final Runnable onChangeTabHandler;

    /**
     * @param onChangeTab Handler to be called when a new tab is selected or a
     * tab is closed.
     */
    public Tabs(Runnable onChangeTab) {
        onChangeTabHandler = onChangeTab;

        tabPane = new TabPane();
        tabPane.getSelectionModel().selectedItemProperty()
            .addListener(e -> onChangeTab());

        objectInspector = new ObjectInspector();
    }

    /**
     * @return The underlying Node object.
     */
    public Node getTabPane() {
        return tabPane;
    }

    /**
     * @return The underlying ObjectInspector object. This object shows the
     * properties of the currently selected object (vertex or edge).
     */
    public ObjectInspector getObjectInspector() {
        return objectInspector;
    }

    /**
     * Adds a new tab containing the given structure.
     *
     * @param name The name of the new tab
     * @param structure The new structure
     */
    public void addTab(String name, Structure structure) {
        Tab t = new Tab(name);
        StructurePane structurePane = new StructurePane(structure);
        t.setContent(structurePane);

        tabPane.getTabs().add(t);
        tabPane.getSelectionModel().select(t);
        structurePane.draw();
        structurePane.setOnSelectionChanged(e -> onChangeStructurePane(structurePane));

        onChangeTabHandler.run();
    }

    /**
     * Sets the name of the current tab. Does nothing if no tab exists.
     *
     * @param name The new name of the current tab
     */
    public void setCurrentTabName(String name) {
        Tab tab = getCurrentTab();
        if (tab != null)
            tab.setText(name);
    }

    /**
     * Returns the StructurePane object managed by the current tab or null if no
     * tab exists.
     *
     * @return The current structure pane
     */
    public StructurePane getCurrentStructurePane() {
        Tab tab = getCurrentTab();
        if (tab != null)
            return (StructurePane) tab.getContent();
        return null;
    }

    public List<Structure> getAllStructures(){
        List<Structure> structures = new ArrayList<Structure>();
        for (Tab tab : tabPane.getTabs()){
            StructurePane currentStructurePane = (StructurePane) tab.getContent();
            structures.add(currentStructurePane.getStructure());
            System.out.println("current structure: " + currentStructurePane.getStructure());
        }
        return structures;
    }

    private Tab getCurrentTab() {
        return tabPane.getSelectionModel().getSelectedItem();
    }

    private void onChangeTab() {
        onChangeStructurePane(getCurrentStructurePane());
    }

    private void onChangeStructurePane(StructurePane sender) {
        try {
            Set<Object> selection = null;
            if (sender != null) {
                selection = sender.highlights.getSelection();
                sender.requestRedraw();
            }
            if (selection != null && selection.size() == 1)
                objectInspector.setObject(selection.iterator().next(), sender);
            else
                objectInspector.setObject(null, sender);

            onChangeTabHandler.run();
        } catch (Exception ex) {
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait(ex);
        }
    }
}
