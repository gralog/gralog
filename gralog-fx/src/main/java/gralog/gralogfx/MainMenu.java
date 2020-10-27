/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx;

import gralog.algorithm.AlgorithmManager;
import gralog.generator.GeneratorManager;
import gralog.gralogfx.windows.PreferenceWindow;
import gralog.gralogfx.windows.ChooseFileForPipingWindow;
import gralog.preferences.MenuPrefVariable;
import gralog.structure.Structure;
import gralog.structure.StructureManager;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import org.apache.commons.io.FilenameUtils;

import java.lang.reflect.Field;

import static gralog.gralogfx.MainWindow.getLastFileName;

/**
 * The main menu of the main window.
 */
public class MainMenu {

    /**
     * A handler which can be called when a menu item is activated.
     */
    @FunctionalInterface
    public interface MenuAction {

        void run() throws Exception;
    }

    /**
     * A handler which can be called when a menu item is activated and which
     * receives the name of the menu item as a parameter.
     */
    @FunctionalInterface
    public interface MenuStringAction {

        void accept(String menuItemName) throws Exception;
    }

    /**
     * The handlers for the menu actions. Null handlers disable the
     * corresponding menu entry permanently. If a handler throws an exception,
     * the exception will be shown to the user.
     */
    public static class Handlers {

        MenuAction onOpen, onSaveAs, onExportTikz, onSave, onDirectInput, onLoadPluginWithPromptForFile,onLoadPluginFromSpecifiedFilepath, onLoadLastPlugin, onExit;
        MenuAction onUndo, onRedo, onCut, onCopy, onPaste, onDelete;
        MenuAction onAlignHorizontally, onAlignVertically;
        MenuAction onAboutGralog, onAboutGraph;
        MenuAction onCloseTab;

        MenuStringAction onNew, onGenerate, onRunAlgorithm;
    }

    private final MenuBar menu;

    // We remember some menu items because we want to disable them when there
    // is no structure available.
    private MenuItem menuFileSave;
    private MenuItem menuFileSaveAs;
    private MenuItem menuExportTikz;
    private MenuItem loadPluginFromSpecifiedFilepath;
    private static MenuItem loadLastPlugin;
    private MenuItem loadPluginWithPromptForFile;
    

    // We need to keep track of the following submenus because they are
    // dynamically generated.
    private Menu menuFileNew;
    private Menu menuFileGenerators;

    private Menu menuStructurePane;
    private Menu menuAlgorithms;

    private final Handlers handlers;


    private Structure currentStructure;
    private StructurePane currentPane;

    private static void handleException(Exception exception) {
        (new ExceptionBox()).showAndWait(exception);
    }

    private static MenuItem createMenuItem(String label, MenuAction handler) {
        return createMenuItem(label, handler, null);
    }
    private static MenuItem createMenuItem(String label, MenuAction handler, KeyCodeCombination hotkey) {
        MenuItem item = new MenuItem(label);
        if (handler != null)
            item.setOnAction(e -> {
                try {
                    handler.run();
                } catch (Exception exception) {
                    handleException(exception);
                }
            });
        else
            item.setDisable(true);

        item.setAccelerator(hotkey);
        return item;
    }

    private static MenuItem createMenuItem(String label, MenuStringAction handler) {
        MenuItem item = new MenuItem(label);
        if (handler != null)
            item.setOnAction(e -> {
                try {
                    handler.accept(label);
                } catch (Exception exception) {
                    handleException(exception);
                }
            });
        else
            item.setDisable(true);

        return item;
    }

    private Menu createFileMenu() {
        Menu menuFile = new Menu("File");
        final KeyCodeCombination newStructure, generate, save, saveAs, exportTikz, open, loadLast, load, quitGralog, tabClose;
        newStructure = new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN);
        generate     = new KeyCodeCombination(KeyCode.G, KeyCombination.SHORTCUT_DOWN);
        save         = new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN);
        saveAs       = new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN);
        exportTikz   = new KeyCodeCombination(KeyCode.E, KeyCombination.SHORTCUT_DOWN);
        open         = new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN);
        loadLast     = new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN);
        load         = new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN);
        quitGralog   = new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN);

        menuFileNew = new Menu("New");
        menuFileGenerators = new Menu("Generators");

        // "Save", "SaveAs", "ExportTikz" is initially disabled because we do not have a structure.
        menuFileSave = createMenuItem("Save graph", handlers.onSave,save);
        menuFileSave.setDisable(true);

        menuFileSaveAs = createMenuItem("Save graph as...", handlers.onSaveAs,saveAs);
        menuFileSaveAs.setDisable(true);

        menuExportTikz = createMenuItem("Exprot to TikZ", handlers.onExportTikz, exportTikz);
        menuExportTikz.setDisable(true);

        String lastExternalProgramFileName = getLastFileName(); // returns "" if no such file exists
        if (lastExternalProgramFileName == "") {
            loadLastPlugin = createMenuItem("Load last program", handlers.onLoadLastPlugin, loadLast);
            loadLastPlugin.setDisable(true);
        }
        else
            loadLastPlugin = createMenuItem("Load last program (" + FilenameUtils.getBaseName(lastExternalProgramFileName) + ")" ,
                    handlers.onLoadLastPlugin,loadLast);



        menuFile.getItems().addAll(
            menuFileNew, menuFileGenerators, new SeparatorMenuItem(),
            createMenuItem("Open graph...", handlers.onOpen,open),
            createMenuItem("Direct input...", handlers.onDirectInput),
                menuFileSave,
                menuFileSaveAs,
                menuExportTikz,
            new SeparatorMenuItem(),
//            createMenuItem("Load spec'd program", handlers.onLoadPluginFromSpecifiedFilepath),
            createMenuItem("Load external program...", handlers.onLoadPluginWithPromptForFile,load),
            loadLastPlugin,

            new SeparatorMenuItem(),
            createMenuItem("Preferences", PreferenceWindow::new),
            new SeparatorMenuItem(),
            createMenuItem("Exit", handlers.onExit,quitGralog));
        if (lastExternalProgramFileName == "")
            loadLastPlugin.setDisable(true);
        return menuFile;
    }

    private Menu createEditMenu() {
        Menu menuEdit = new Menu("Edit");
        final String os = System.getProperty("os.name").toUpperCase();
        final KeyCodeCombination undo, redo;
        if(os.contains("MAC")) {
            undo = new KeyCodeCombination(KeyCode.Z, KeyCombination.META_DOWN);
            redo = new KeyCodeCombination(KeyCode.Y, KeyCombination.META_DOWN);
        }else {
            undo = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
            redo = new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN);
        }
        menuEdit.getItems().addAll(
                createMenuItem("Undo", handlers.onUndo, undo),
                createMenuItem("Redo", handlers.onRedo, redo),
                new SeparatorMenuItem(),
                createMenuItem("Cut", handlers.onCut),
                createMenuItem("Copy", handlers.onCopy),
                createMenuItem("Paste", handlers.onPaste),
                createMenuItem("Delete", handlers.onDelete),
                new SeparatorMenuItem(),
                createMenuItem("Align Horizontally", handlers.onAlignHorizontally,
                        new KeyCodeCombination(KeyCode.H, KeyCombination.ALT_DOWN)),
                createMenuItem("Align Vertically", handlers.onAlignVertically,
                        new KeyCodeCombination(KeyCode.V, KeyCombination.ALT_DOWN)),
                new SeparatorMenuItem(),
                createMenuItem("Close Tab", handlers.onCloseTab,
                		new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN)) 

        );
        return menuEdit;
    }

    private Menu createAlgorithmMenu() {
        menuAlgorithms = new Menu("Algorithms");
        return menuAlgorithms;
    }

    private Menu createStructurePaneMenu() {
        menuStructurePane = new Menu("Structure");
        return menuStructurePane;
    }

    private Menu createHelpMenu() {
        Menu menuHelp = new Menu("Help");
        menuHelp.getItems().addAll(
            createMenuItem("About Gralog", handlers.onAboutGralog),
            createMenuItem("About the current graph", handlers.onAboutGraph));
        return menuHelp;
    }

    MainMenu(Handlers handlers) {
        this.handlers = handlers;

        menu = new MenuBar();
        menu.setUseSystemMenuBar(true);
        menu.getMenus().addAll(
            createFileMenu(),
            createEditMenu(),
            createStructurePaneMenu(),
            createAlgorithmMenu(),
            createHelpMenu());

        updateStructures();
        updateGenerators();
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

    private void updateStructures() {
        menuFileNew.getItems().clear();
        for (String str : StructureManager.getStructureClasses())
            menuFileNew.getItems().add(
                createMenuItem(str, handlers.onNew));
    }

    private void updateGenerators() {
        menuFileGenerators.getItems().clear();
        for (String str : GeneratorManager.getGeneratorClasses())
            menuFileGenerators.getItems().add(
                createMenuItem(str, handlers.onGenerate));
    }

    private void updateAlgorithms() {
        menuAlgorithms.getItems().clear();

        if (currentStructure == null) {
            return;
        }

        for (String str : AlgorithmManager.getAlgorithms(currentStructure.getClass()))
            menuAlgorithms.getItems().add(
                createMenuItem(str, handlers.onRunAlgorithm));
    }

    private void updateStructureVariables() {
        menuStructurePane.getItems().clear();

        if(currentPane == null) {
            return;
        }

        for(int i = 0; i < StructurePane.menuVariables.length; i++) {
            MenuPrefVariable ff = StructurePane.menuVariables[i];
            Field f = StructurePane.menuVariableFields[i];

            menuStructurePane.getItems().add(
                    createMenuItem(ff.name(), () -> dummy(f, currentPane))
            );
        }
    }

    private void dummy(Field f, StructurePane ref) {
        //TODO: use this to start a process (like a window) that lets the user change the field
        System.out.printf(">> Request to change variable [%s] of Structure [%s]\n",
                f.getName(),
                ref.structure.getClass().getSimpleName());
        System.out.flush();
    }
    /**
     * Tells the menu about the current structure. If the argument is null,
     * certain menu items such as "Save" become disabled. The current structure
     * also affects the available algorithms, which get updated automatically.
     *
     * @param pane The current structure pane. May be null.
     */
    public void setCurrentStructurePane(StructurePane pane) {
        Structure s = pane == null ? null : pane.structure;
        this.currentPane = pane;
        this.currentStructure = s;
        menuFileSave.setDisable(s == null || !s.hasFileReference());
        menuFileSaveAs.setDisable(s == null);
        menuExportTikz.setDisable(s == null);

        updateAlgorithms();
        updateStructureVariables();
    }

    public static void  enableLoadsLastPlugin(){
        loadLastPlugin.setDisable(false);
        loadLastPlugin.setText("Load lasy program (" + FilenameUtils.getBaseName(getLastFileName()) + ")" );
    }
}
