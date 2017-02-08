/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx;

import gralog.plugins.*;
import gralog.structure.*;
import gralog.importfilter.*;
import gralog.exportfilter.*;
import gralog.generator.*;
import gralog.algorithm.*;

import gralog.gralogfx.events.RedrawOnProgress;
import gralog.gralogfx.views.ViewManager;
import gralog.preferences.Preferences;

import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.Alert.AlertType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 */
public class MainWindow extends Application {

    Stage stage;
    BorderPane root;
    MenuBar menu;
    Menu menuFile;
    Menu menuFileNew;
    MenuItem menuFileSave;
    Menu menuFileGenerators;
    Menu menuAlgo;
    VBox topPane;
    TabPane tabPane;
    ObjectInspector objectInspector;
    Label statusBarMessage;

    Button buttonSelectMode, buttonVertexMode, buttonEdgeMode;

    private static MenuItem createMenuItem(String label, Runnable handler) {
        MenuItem item = new MenuItem(label);
        item.setOnAction(e -> handler.run());
        return item;
    }

    private static MenuItem createDisabledMenuItem(String label) {
        MenuItem item = new MenuItem(label);
        item.setDisable(true);
        return item;
    }

    public MainWindow() {
        stage = null;

        // Tab Panel
        tabPane = new TabPane();
        tabPane.getSelectionModel().selectedItemProperty().addListener(e -> {
            updateAlgorithms();
            updateSelection();
        });
        //tabPane.setFocusTraversable(true);

        // Menu
        menu = new MenuBar();

        // File Menu
        menuFile = new Menu("File");
        menuFileNew = new Menu("New");
        updateStructures();
        menuFileGenerators = new Menu("Generators");
        updateGenerators();
        // "Save" is initially disabled because we do not have a structure.
        menuFileSave = createMenuItem("Save graph as...", this::menuFileSaveActivated);
        menuFileSave.setDisable(true);
        menuFile.getItems().addAll(
            menuFileNew, menuFileGenerators,
            createMenuItem("Open graph...", this::menuFileOpenActivated),
            createMenuItem("Direct input...", this::menuFileDirectInputActivated),
            menuFileSave,
            createMenuItem("Close graph", this::menuFileCloseActivated),
            new SeparatorMenuItem(),
            createMenuItem("Load plugin...", this::menuFilePluginActivated),
            new SeparatorMenuItem(),
            createMenuItem("Exit", this::menuFileExitActivated));

        // Edit Menu
        Menu menuEdit = new Menu("Edit");
        menuEdit.getItems().addAll(
            createDisabledMenuItem("Undo"),
            createDisabledMenuItem("Redo"),
            createDisabledMenuItem("Cut"),
            createDisabledMenuItem("Copy"),
            createDisabledMenuItem("Paste"),
            createDisabledMenuItem("Delete"));

        // Algorithm Menu
        menuAlgo = new Menu("Algorithms");

        // Help Menu
        Menu menuHelp = new Menu("Help");
        menuHelp.getItems().addAll(
            createMenuItem(
                "About Gralog", () -> {
                    AboutStage aboutstage = new AboutStage(this);
                    aboutstage.showAndWait();
                }),
            createMenuItem(
                "About the current graph", () -> {
                    StructurePane structurePane = this.getCurrentStructurePane();
                    if (structurePane == null)
                        return;
                    try {
                        StructureDescription descr = structurePane.structure.getDescription();
                        String url = descr.url();
                        if (url != null && !url.trim().equals(""))
                            this.getHostServices().showDocument(url);
                    } catch (Exception ex) {
                        ExceptionBox exbox = new ExceptionBox();
                        exbox.showAndWait(ex);
                    }
                }));

        menu.getMenus().addAll(menuFile, menuEdit, menuAlgo, menuHelp);

        // Button Bar
        HBox buttonBar = new HBox(UIConstants.HBOX_SPACING);
        buttonSelectMode = new Button("Select");
        buttonSelectMode.setOnAction(e -> setSelectMode());
        buttonSelectMode.tooltipProperty().setValue(new Tooltip("Shortcut: s"));
        buttonVertexMode = new Button("New vertex");
        buttonVertexMode.setOnAction(e -> setVertexCreationMode());
        buttonVertexMode.tooltipProperty().setValue(new Tooltip("Shortcut: v"));
        buttonEdgeMode = new Button("New edge");
        buttonEdgeMode.setOnAction(e -> setEdgeCreationMode());
        buttonEdgeMode.tooltipProperty().setValue(new Tooltip("Shortcut: e"));
        buttonBar.getChildren().addAll(buttonSelectMode, buttonVertexMode, buttonEdgeMode);
        topPane = new VBox();
        topPane.getChildren().addAll(menu, buttonBar);

        // Object Inspector
        objectInspector = new ObjectInspector();

        // Status Bar
        HBox statusBar = new HBox();
        statusBarMessage = new Label("");
        statusBar.getChildren().add(statusBarMessage);

        root = new BorderPane();
        //root.setFocusTraversable(true);
        root.setTop(topPane);
        root.setCenter(tabPane);
        root.setRight(objectInspector);
        root.setBottom(statusBar);
    }

    public void menuFilePluginActivated() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(getLastDirectory()));
        fileChooser.setTitle("Load Plugins");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Jar Files (*.jar)", "*.jar")
        );
        List<File> list = fileChooser.showOpenMultipleDialog(stage);
        if (list != null && !list.isEmpty()) {
            setLastDirectory(list.get(0));
            for (File file : list)
                doLoadPlugin(file.getAbsolutePath());
        }
    }

    public void doLoadPlugin(String filename) {
        try {
            this.setStatus("Loading Plugin " + filename + "...");
            PluginManager.loadPlugin(filename);
            ViewManager.loadPlugin(filename);
        } catch (Exception ex) {
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait(ex);
        }
        updateStructures();
        updateGenerators();
        updateAlgorithms();
        this.setStatus("");
    }

    public void menuFileNewActivated(String str) {
        try {
            Structure structure = StructureManager.instantiateStructure(str);
            addTab(str, structure);
            setStatus("created a " + str + "...");
        } catch (Exception ex) {
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait(ex);
        }
    }

    public void menuFileSaveActivated() {
        try {
            Structure structure = getCurrentStructurePane().structure;

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(getLastDirectory()));
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Graph Markup Language (*.graphml)", "*.graphml")
            );

            // add export-filters to list of extensions
            for (String format : ExportFilterManager.getExportFilters(structure.getClass())) {
                ExportFilterDescription descr = ExportFilterManager.getExportFilterDescription(structure.getClass(), format);
                ExtensionFilter filter = new FileChooser.ExtensionFilter(descr.name() + " (*." + descr.fileExtension() + ")", "*." + descr.fileExtension());
                fileChooser.getExtensionFilters().add(filter);
            }

            fileChooser.setTitle("Save File");
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                setLastDirectory(file);
                // has the user selected the native file-type or an export-filter?
                String extension = file.getName(); // unclean way of getting file extension
                int idx = extension.lastIndexOf('.');
                extension = idx > 0 ? extension.substring(idx + 1) : "";

                ExportFilter exportFilter = ExportFilterManager.instantiateExportFilterByExtension(structure.getClass(), extension);
                if (exportFilter != null) {
                    // configure export filter
                    ExportFilterParameters params = exportFilter.getParameters(structure);
                    if (params != null) {
                        ExportFilterStage exportStage = new ExportFilterStage(exportFilter, params, this);
                        exportStage.showAndWait();
                        if (!exportStage.dialogResult)
                            return;
                    }
                    exportFilter.exportGraph(structure, file.getAbsolutePath(), params);
                } else {
                    structure.writeToFile(file.getAbsolutePath());
                }
                setCurrentStructureTitle(file.getName());
            }
        } catch (Exception ex) {
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait(ex);
        }
    }

    public void menuFileOpenActivated() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(getLastDirectory()));
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All (*.*)", "*.*"),
            new FileChooser.ExtensionFilter("Graph Markup Language (*.graphml)", "*.graphml")
        );

        // add export-filters to list of extensions
        for (String format : ImportFilterManager.getImportFilterClasses()) {
            ImportFilterDescription descr = ImportFilterManager.getImportFilterDescription(format);
            ExtensionFilter filter = new FileChooser.ExtensionFilter(descr.name() + " (*." + descr.fileExtension() + ")", "*." + descr.fileExtension());
            fileChooser.getExtensionFilters().add(filter);
        }

        fileChooser.setInitialFileName("*.*");
        fileChooser.setTitle("Open File");
        List<File> list = fileChooser.showOpenMultipleDialog(stage);
        if (list != null) {
            setLastDirectory(list.get(0));
            for (File file : list)
                doOpenFile(file);
        }
    }

    public void menuFileDirectInputActivated() {
        try {
            DirectInputStage directinputstage = new DirectInputStage(this);
            directinputstage.showAndWait();
            Structure s = directinputstage.dialogResult;
            if (s != null)
                this.addTab("", s);
        } catch (Exception ex) {
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait(ex);
        }
    }

    public void doOpenFile(File file) {
        try {
            // unclean way of getting file extension
            int idx = file.getName().lastIndexOf('.');
            String extension = idx > 0 ? file.getName().substring(idx + 1) : "";

            ImportFilter importFilter = ImportFilterManager.instantiateImportFilterByExtension(extension);
            this.setStatus("Loading File " + file.getAbsolutePath() + "...");
            Structure structure;

            if (importFilter != null) {
                ImportFilterParameters params = importFilter.getParameters();
                if (params != null) {
                    ImportFilterStage importStage = new ImportFilterStage(importFilter, params, this);
                    importStage.showAndWait();
                    if (!importStage.dialogResult) {
                        this.setStatus("");
                        return;
                    }
                }
                structure = importFilter.importGraph(file.getAbsolutePath(), params);
            } else {
                structure = Structure.loadFromFile(file.getAbsolutePath());
            }

            if (structure != null)
                addTab(file.getName(), structure);
        } catch (Exception ex) {
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait(ex);
        }
        this.setStatus("");
    }

    public void menuFileExitActivated() {
        try {
            stage.close();
        } catch (Exception ex) {
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait(ex);
        }
    }

    public void menuFileCloseActivated() {
        try {
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            if (tab != null) {
                tabPane.getTabs().remove(tab);
                currentStructureChanged();
            }
        } catch (Exception ex) {
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait(ex);
        }
    }

    public void setStatus(String status) {
        statusBarMessage.setText(status);
    }

    public void addTab(String text, Structure structure) {
        Tab t = new Tab(text);
        StructurePane structurePane = new StructurePane(structure);
        t.setContent(structurePane);

        tabPane.getTabs().add(t);
        tabPane.getSelectionModel().select(t);
        structurePane.draw();
        structurePane.setOnSelectionChanged(e -> updateSelection(structurePane));

        currentStructureChanged();
    }

    protected final void updateStructures() {
        menuFileNew.getItems().clear();
        for (String str : StructureManager.getStructureClasses()) {
            MenuItem item = new MenuItem(str);
            item.setOnAction(e -> menuFileNewActivated(str));
            menuFileNew.getItems().add(item);
        }
    }

    protected final void updateGenerators() {
        menuFileGenerators.getItems().clear();
        for (String str : GeneratorManager.getGeneratorClasses()) {
            MenuItem item = new MenuItem(str);
            item.setOnAction(e -> menuFileGeneratorActivated(str));
            menuFileGenerators.getItems().add(item);
        }
    }

    protected final void updateAlgorithms() {
        menuAlgo.getItems().clear();
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        if (tab == null)
            return;
        if (!(tab.getContent() instanceof StructurePane))
            return;
        StructurePane structurePane = (StructurePane) tab.getContent();
        Structure structure = structurePane.structure;

        for (String str : AlgorithmManager.getAlgorithms(structure.getClass())) {
            MenuItem item = new MenuItem(str);
            item.setOnAction(e -> menuAlgorithmActivated(str));
            menuAlgo.getItems().add(item);
        }
    }

    protected void updateSelection() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        StructurePane structurePane = null;

        if (tab != null)
            if (tab.getContent() instanceof StructurePane)
                structurePane = (StructurePane) tab.getContent();

        updateSelection(structurePane);
        checkMode();
    }

    protected void updateSelection(StructurePane sender) {
        try {
            Set<Object> selection = null;
            if (sender != null) {
                selection = sender.highlights.getSelection();
                sender.requestRedraw();
            }
            if (selection != null && selection.size() == 1)
                objectInspector.setObject(selection.iterator().next(), sender);
            currentStructureChanged();
        } catch (Exception ex) {
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait(ex);
        }
    }

    public void menuFileGeneratorActivated(String str) {
        try {
            // prepare
            Generator gen = GeneratorManager.instantiateGenerator(str);
            AlgorithmParameters params = gen.getParameters();
            if (params != null) {
                GeneratorStage genstage = new GeneratorStage(gen, params, this);
                genstage.showAndWait();
                if (!genstage.dialogResult)
                    return;
            }

            // run
            Structure genResult = gen.generate(params);
            if (genResult == null)
                return;
            this.addTab(gen.getDescription().name(), genResult);

        } catch (Exception ex) {
            this.setStatus("");
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait(ex);
        }
    }

    public void algorithmCompleted(StructurePane structurePane,
        AlgorithmThread algoThread) {
        try {
            if (algoThread.exception != null)
                throw algoThread.exception;

            Object algoResult = algoThread.result;
            this.setStatus("");

            // Show result if it is not null.
            if (algoResult == null) {
                structurePane.requestRedraw();
                return;
            }

            if (algoResult instanceof Structure) {
                this.addTab("Algorithm result", (Structure) algoResult);
            } else if (algoResult instanceof Set) {
                boolean isSelection = true;
                for (Object o : (Set) algoResult)
                    if (!(o instanceof Vertex)
                        && !(o instanceof Edge)
                        && !(o instanceof EdgeIntermediatePoint))
                        isSelection = false;
                if (isSelection) {
                    structurePane.selectAll((Set) algoResult);
                    structurePane.requestRedraw();
                }
            } else if (algoResult instanceof String) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Algorithm Result");
                alert.setHeaderText(null);
                alert.setContentText((String) algoResult);
                alert.showAndWait();
            } else {
                AlgorithmResultStage resultStage = new AlgorithmResultStage(
                    algoThread.algo,
                    algoThread.structure,
                    algoThread.params,
                    structurePane,
                    algoResult
                );
                resultStage.show();
            }
        } catch (Exception ex) {
            this.setStatus("");
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait(ex);
        }
    }

    public void menuAlgorithmActivated(String str) {
        try {
            // Prepare
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            StructurePane structurePane = (StructurePane) tab.getContent();
            Structure structure = structurePane.structure;
            Algorithm algo = AlgorithmManager.instantiateAlgorithm(structure.getClass(), str);

            AlgorithmParameters params = algo.getParameters(structure);
            if (params != null) {
                AlgorithmStage algostage = new AlgorithmStage(algo, structure, params, this);
                algostage.showAndWait();
                if (!algostage.dialogResult)
                    return;
            }

            // Run
            AlgorithmThread algoThread = new AlgorithmThread(
                algo, structure, params, structurePane.highlights.getSelection(),
                new RedrawOnProgress(structurePane, 1d / 60d));
            algoThread.setOnThreadComplete(t -> Platform.runLater(() -> {
                algorithmCompleted(structurePane, t);
            }));
            this.setStatus("Running Algorithm \"" + str + "\"...");
            algoThread.start();
        } catch (InvocationTargetException ex) {
            this.setStatus("");
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait((Exception) ex.getCause());
        } catch (Exception ex) {
            this.setStatus("");
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait(ex);
        }
    }

    // PROGRAM STARTUP
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            PluginManager.initialize();
            ViewManager.initialize();
            launch(args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(
            root,
            Preferences.getInteger(getClass(), "main-window-width", 1000),
            Preferences.getInteger(getClass(), "main-window-height", 800));
        this.stage = primaryStage;
        primaryStage.setTitle("Gralog");
        primaryStage.setScene(scene);
        primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN, e -> windowShown());
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case S:
                    setSelectMode();
                    break;
                case V:
                    setVertexCreationMode();
                    break;
                case E:
                    setEdgeCreationMode();
                    break;
            }
        });
        // Remember the size of the window.
        primaryStage.setOnCloseRequest((e) -> {
            Preferences.setInteger(getClass(), "main-window-width", (int) scene.getWidth());
            Preferences.setInteger(getClass(), "main-window-height", (int) scene.getHeight());
        });
        primaryStage.show();
    }

    public void windowShown() {
        // Load Config
        NodeList children = null;
        String configFileDir = null;
        try {
            File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            File configFile = new File(jarFile.getParentFile().getAbsolutePath() + File.separator + "config.xml");
            configFileDir = configFile.getParent();

            if (configFile.exists()) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                try (FileInputStream input = new FileInputStream(configFile)) {
                    Document doc = dBuilder.parse(input);
                    doc.getDocumentElement().normalize();
                    children = doc.getDocumentElement().getChildNodes();
                }
            }
        } catch (IOException | URISyntaxException | ParserConfigurationException | SAXException ex) {
        }

        // Load Plugins from Config
        if (children != null)
            for (int i = 0; i < children.getLength(); ++i) {
                Node childNode = children.item(i);
                if (childNode.getNodeType() != Node.ELEMENT_NODE)
                    continue;
                Element child = (Element) childNode;
                if (!child.getTagName().equals("plugin"))
                    continue;

                doLoadPlugin(configFileDir + File.separator + child.getAttribute("location"));
            }

        // Load Plugins from Parameters
        Application.Parameters params = this.getParameters();
        for (String s : params.getUnnamed())
            if (s.endsWith(".jar"))
                doLoadPlugin(s);

        // Open Files from Config
        if (children != null)
            for (int i = 0; i < children.getLength(); ++i) {
                Node childNode = children.item(i);
                if (childNode.getNodeType() != Node.ELEMENT_NODE)
                    continue;
                Element child = (Element) childNode;
                if (!child.getTagName().equals("file"))
                    continue;

                doOpenFile(new File(child.getAttribute("location")));
            }

        // Open Files from Parameters
        for (String s : params.getUnnamed())
            if (!s.endsWith(".jar"))
                doOpenFile(new File(s));
    }

    private void setCurrentStructureTitle(String title) {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        if (tab != null)
            tab.setText(title);
    }

    private StructurePane getCurrentStructurePane() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        if (tab == null)
            return null;
        return (StructurePane) tab.getContent();
    }

    private void currentStructureChanged() {
        menuFileSave.setDisable(getCurrentStructurePane() == null);
    }

    private void checkMode() {
        buttonSelectMode.setStyle("");
        buttonVertexMode.setStyle("");
        buttonEdgeMode.setStyle("");
        StructurePane pane = getCurrentStructurePane();
        if (pane != null) {
            switch (pane.getMouseMode()) {
                case SELECT_MODE:
                    buttonSelectMode.setStyle("-fx-base: #0000FF;");
                    setStatus("Selection mode");
                    break;
                case VERTEX_MODE:
                    buttonVertexMode.setStyle("-fx-base: #0000FF;");
                    setStatus("Vertex creation mode");
                    break;
                case EDGE_MODE:
                    buttonEdgeMode.setStyle("-fx-base: #0000FF;");
                    setStatus("Edge creation mode");
                    break;
            }
        }
    }

    private void setSelectMode() {
        StructurePane pane = getCurrentStructurePane();
        if (pane != null)
            pane.setSelectMode();
        checkMode();
    }

    private void setVertexCreationMode() {
        StructurePane pane = getCurrentStructurePane();
        if (pane != null)
            pane.setVertexCreationMode();
        checkMode();
    }

    private void setEdgeCreationMode() {
        StructurePane pane = getCurrentStructurePane();
        if (pane != null)
            pane.setEdgeCreationMode();
        checkMode();
    }

    private String getLastDirectory() {
        final String defaultDir = System.getProperty("user.home");
        String dir = Preferences.getString(this.getClass(), "lastdirectory", defaultDir);
        if (Files.exists(Paths.get(dir)))
            return dir;
        return defaultDir;
    }

    private void setLastDirectory(File lastFile) {
        setLastDirectory(Paths.get(lastFile.getAbsolutePath()).getParent().toString());
    }

    private void setLastDirectory(String lastDirectory) {
        Preferences.setString(this.getClass(), "lastdirectory", lastDirectory);
    }
}
