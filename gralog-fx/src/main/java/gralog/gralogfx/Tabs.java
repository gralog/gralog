/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx;

import gralog.gralogfx.panels.GralogWindow;
import gralog.preferences.Configuration;
import gralog.preferences.Preferences;
import gralog.structure.DirectedGraph;
import gralog.structure.Highlights;
import gralog.structure.Structure;

import java.util.*;

import javafx.event.Event;
import javafx.event.EventHandler;
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

    private final ArrayList<StructurePane> panes = new ArrayList<>();
    private final ArrayList<Tab> tabsArray = new ArrayList<>();


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
        StructurePane structurePane = new StructurePane(structure, new Configuration(Preferences.getProperties()));
        t.setContent(structurePane);
        t.setOnClosed(e -> {
            panes.remove(structurePane);
            tabsArray.remove(t);
        });

        panes.add(structurePane);
        tabsArray.add(t);

        tabPane.getTabs().add(t);
        tabPane.getSelectionModel().select(t);
        structurePane.draw();

        structurePane.setOnHighlightsChanged(this::onHighlightsChange);
        structurePane.setOnStructureChanged(this::onStructureChange);

        onChangeTabHandler.run();
    }

    /**
     * Closes the tab at index i
     * @param index
     */
    public void closeTab(int index){
        EventHandler<Event> e = tabsArray.get(index).getOnClosed();
        tabPane.getTabs().remove(tabsArray.get(index));
        e.handle(null); // removes entry from all relevant arrays
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
        if(getCurrentStructurePane() != null){
            this.requestRedraw();
            onStructureChange(getCurrentStructurePane().structure);
        }
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
        if(getCurrentStructurePane() != null){
            getCurrentStructurePane().requestRedraw();
        }
    }

    /**
     * Requests to close all structure panes. If all structures
     * were successfully closed, the Runnable afterClose will
     * be ran.
     */
    public void requestClose(Runnable afterClose){
        if(panes.isEmpty()){
            afterClose.run();
        }
        requestClosePerPaneProxy(afterClose);
    }


    /**
     * Closes the first tab (tab at index 0) and propagates the closing signal on to the
     * next tab recursively. Since closing a tab at i=0 means that all other tabs will move
     * up, all tabs will get closed in a sequential manner. Each tab
     * will be able to close itself AFTER the tab before was done properly closing.
     * @param afterClose This runnable will execute after all tabs were correctly closed.
     */
    private void requestClosePerPaneProxy(Runnable afterClose){
        if(panes.isEmpty()){
            afterClose.run();
        }else{
            panes.get(0).requestClose(() -> {
                closeTab(0);
                requestClosePerPaneProxy(afterClose);
            });
        }
    }
    /**
     * Requests to close all structure panes.
     */
    public void requestClose(){
        requestClose(() -> {});
    }


}
