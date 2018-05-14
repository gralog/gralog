/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx;

import gralog.gralogfx.panels.GralogWindow;
import gralog.structure.DirectedGraph;
import gralog.structure.Highlights;
import gralog.structure.Structure;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * A tab pane for structures.
 */
public class Tabs {

    private final TabPane tabPane;

    private final Runnable onChangeTabHandler;

    private final Set<GralogWindow> subscribers = new HashSet<>();

    /**
     * @param onChangeTab Handler to be called when a new tab is selected or a
     * tab is closed.
     */
    public Tabs(Runnable onChangeTab) {
        onChangeTabHandler = onChangeTab;

        tabPane = new TabPane();

        tabPane.getSelectionModel().selectedItemProperty()
            .addListener(e -> onChangeTab());
    }

    public void initializeTab(){
        addTab("Unnamed", new DirectedGraph());
    }
    /**
     * @return The underlying Node object.
     */
    public Node getTabPane() {
        return tabPane;
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

        structurePane.setOnHighlightsChanged(this::onHighlightsChange);
        structurePane.setOnStructureChanged(this::onStructureChange);

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

    private Tab getCurrentTab() {
        return tabPane.getSelectionModel().getSelectedItem();
    }

    private void onChangeTab() {
        onStructureChange(getCurrentStructurePane().structure);
    }

    private void onHighlightsChange(Highlights highlights){
        for(GralogWindow window : subscribers){
            window.notifyHighlightChange(highlights);
        }
    }
    private void onStructureChange(Structure structure){
        for(GralogWindow window : subscribers){
            window.notifyStructureChange(structure);
        }
    }

    public void subscribe(GralogWindow win){
        subscribers.add(win);
    }
    public void unsubscribe(GralogWindow win){
        subscribers.add(win);
    }

    public void requestRedraw(){
        getCurrentStructurePane().requestRedraw();
    }

}
