/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx;

import gralog.algorithm.AlgorithmManager;
import gralog.generator.GeneratorManager;
import gralog.structure.Structure;
import gralog.structure.StructureManager;
import java.util.function.Consumer;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 * The main menu of the main window.
 */
public class MainMenu {

    /**
     * The handlers for the menu actions. Null handlers disable the
     * corresponding menu entry permanently.
     */
    public static class Handlers {

        Runnable onOpen, onSave, onDirectInput, onClose, onLoadPlugin, onExit;
        Runnable onUndo, onRedo, onCut, onCopy, onPaste, onDelete;
        Runnable onAboutGralog, onAboutGraph;

        Consumer<String> onNew, onGenerate, onRunAlgorithm;
    }

    private final MenuBar menu;
    private final Menu menuFile;
    private final Menu menuFileNew;
    private final MenuItem menuFileSave;
    private final Menu menuFileGenerators;
    private final Menu menuAlgo;
    private final Handlers handlers;

    private Structure currentStructure;

    private static MenuItem createMenuItem(String label, Runnable handler) {
        MenuItem item = new MenuItem(label);
        if (handler != null)
            item.setOnAction(e -> handler.run());
        else
            item.setDisable(true);
        return item;
    }

    private static MenuItem createMenuItem(String label, Consumer<String> handler) {
        MenuItem item = new MenuItem(label);
        if (handler != null)
            item.setOnAction(e -> handler.accept(label));
        else
            item.setDisable(true);
        return item;
    }

    MainMenu(Handlers handlers) {
        this.handlers = handlers;

        menu = new MenuBar();

        // File Menu
        menuFile = new Menu("File");
        menuFileNew = new Menu("New");
        updateStructures();
        menuFileGenerators = new Menu("Generators");
        updateGenerators();

        // "Save" is initially disabled because we do not have a structure.
        menuFileSave = createMenuItem("Save graph as...", handlers.onSave);
        menuFileSave.setDisable(true);
        menuFile.getItems().addAll(
            menuFileNew, menuFileGenerators,
            createMenuItem("Open graph...", handlers.onOpen),
            createMenuItem("Direct input...", handlers.onDirectInput),
            menuFileSave,
            createMenuItem("Close graph", handlers.onClose),
            new SeparatorMenuItem(),
            createMenuItem("Load plugin...", handlers.onLoadPlugin),
            new SeparatorMenuItem(),
            createMenuItem("Exit", handlers.onExit));

        // Edit Menu
        Menu menuEdit = new Menu("Edit");
        menuEdit.getItems().addAll(
            createMenuItem("Undo", handlers.onUndo),
            createMenuItem("Redo", handlers.onRedo),
            createMenuItem("Cut", handlers.onCut),
            createMenuItem("Copy", handlers.onCopy),
            createMenuItem("Paste", handlers.onPaste),
            createMenuItem("Delete", handlers.onDelete));

        // Algorithm Menu
        menuAlgo = new Menu("Algorithms");

        // Help Menu
        Menu menuHelp = new Menu("Help");
        menuHelp.getItems().addAll(
            createMenuItem("About Gralog", handlers.onAboutGralog),
            createMenuItem("About the current graph", handlers.onAboutGraph));

        menu.getMenus().addAll(menuFile, menuEdit, menuAlgo, menuHelp);
    }

    /**
     * @return The underlying MenuBar instance.
     */
    public MenuBar getMenuBar() {
        return menu;
    }

    /**
     * Update the "New structure", "Generate" and "Run algorithm" submenus. You
     * probably want to call this function whenever the list of available
     * structures/generators/algorithms changes, for example after loading a
     * plugin.
     */
    public void update() {
        updateStructures();
        updateGenerators();
        updateAlgorithms();
    }

    protected final void updateStructures() {
        menuFileNew.getItems().clear();
        for (String str : StructureManager.getStructureClasses())
            menuFileNew.getItems().add(
                createMenuItem(str, handlers.onNew));
    }

    protected final void updateGenerators() {
        menuFileGenerators.getItems().clear();
        for (String str : GeneratorManager.getGeneratorClasses())
            menuFileGenerators.getItems().add(
                createMenuItem(str, handlers.onGenerate));
    }

    protected final void updateAlgorithms() {
        menuAlgo.getItems().clear();

        if (currentStructure == null)
            return;

        for (String str : AlgorithmManager.getAlgorithms(currentStructure.getClass()))
            menuAlgo.getItems().add(
                createMenuItem(str, handlers.onRunAlgorithm));
    }

    /**
     * Tells the menu about the current structure. If the argument is null,
     * certain menu items such as "Save" become disabled. The current structure
     * also affects the available algorithms, which get updated automatically.
     *
     * @param s The current structure. May be null.
     */
    public void setCurrentStructure(Structure s) {
        this.currentStructure = s;
        menuFileSave.setDisable(s == null);
        updateAlgorithms();
    }
}
