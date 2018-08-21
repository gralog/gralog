/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx;
//test

import gralog.gralogfx.input.MultipleKeyCombination;
import gralog.gralogfx.panels.*;
import javafx.event.EventHandler;

import gralog.plugins.*;
import gralog.structure.*;
import gralog.importfilter.*;
import gralog.exportfilter.*;
import gralog.generator.*;
import gralog.algorithm.*;
import gralog.gralogfx.piping.Piping;
import gralog.gralogfx.piping.Piping.MessageToConsoleFlag;
// import java.util.concurrent.CountDownLatch;

import gralog.gralogfx.events.RedrawOnProgress;
import gralog.gralogfx.views.ViewManager;
import gralog.preferences.Preferences;
import gralog.gralogfx.windows.ChooseFileForPipingWindow;

import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
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

import org.dockfx.*;
/**
 * The gralog main window.
 */
public class MainWindow extends Application {

    public static MainWindow Main_Application;

    // preference key identifiers
    private static final String PREF_LAST_USED_STRUCTURE = "lastUsedStructure";

    private Stage stage;
    private BorderPane root;
    private MainMenu menu;
    private Tabs tabs;
    private StatusBar statusBar;
    // private Piping pipeline;
    private List<Piping> pipelines;

    private HBox rightBox;

    private Console mainConsole;
    private PluginControlPanel pluginControlPanel;

    private boolean pipingUnderway = false;



    //argument configs
    private boolean dontAskWhenClosing = false; // <==> argument 'dawc'

    public MainWindow() {

        Main_Application = this;


        MainMenu.Handlers handlers = new MainMenu.Handlers();
        handlers.onNew = this::onNew;
        handlers.onGenerate = this::onGenerate;
        handlers.onOpen = this::onOpen;
        handlers.onSave = this::onSave;
        handlers.onSaveAs = this::onSaveAs;
        handlers.onDirectInput = this::onDirectInput;
        handlers.onLoadPluginFromSpecifiedFilepath = this::onLoadPluginFromSpecifiedFilepath;
        handlers.onLoadPluginWithPromptForFile = this::onLoadPluginWithPromptForFile;
        handlers.onLoadLastPlugin = this::onLoadLastPlugin;
        handlers.onExit = () -> stage.getOnCloseRequest().handle(null);
        handlers.onRunAlgorithm = this::onRunAlgorithm;

        // pipeline = new Piping();
        pipelines = new ArrayList<Piping>();
        //controls
        handlers.onAlignHorizontally = () -> {
            if(tabs.getCurrentStructurePane() != null){
                tabs.getCurrentStructurePane().alignHorizontallyMean();
            }
        };

        handlers.onAlignVertically = () -> {
            if(tabs.getCurrentStructurePane() != null){
                tabs.getCurrentStructurePane().alignVerticallyMean();
            }
        };


        handlers.onAboutGralog = () -> (new AboutStage(this)).showAndWait();
        handlers.onAboutGraph = () -> {
            Structure structure = getCurrentStructure();
            if (structure == null)
                return;
            String url = structure.getDescription().url();
            if (url != null && !url.trim().equals(""))
                this.getHostServices().showDocument(url);
        };

        statusBar = new StatusBar();

        menu = new MainMenu(handlers);

        VBox topPane = new VBox();
        topPane.getChildren().addAll(menu.getMenuBar());

        rightBox = new HBox();
        //inspectorSplit = new SplitPane();



        tabs = new Tabs(this::onSwitchCurrentStructure);
        String lastOpenedFilePath = Preferences.getString(PREF_LAST_USED_STRUCTURE, "");
        if(lastOpenedFilePath.isEmpty()){
            tabs.initializeTab();
        }else{
            doOpenFile(new File(lastOpenedFilePath));
        }


        mainConsole = new Console(tabs, this::profferTextToMainWindow);

        ObjectInspector objectInspector = new ObjectInspector(tabs);
        //put lambdas here for controlling stuff
        


        Runnable play = new Runnable(){
            public void run(){
                Piping currentPiping = MainWindow.this.tabs.getCurrentStructurePane().getPiping();
                if (!MainWindow.this.tabs.getCurrentStructurePane().handlePlay()){
                    Platform.runLater(
                        () -> {
                            Alert alert = new Alert(AlertType.INFORMATION);
                            alert.setTitle("No External Process");
                            alert.setHeaderText(null);
                            alert.setContentText("You have not yet started a piping thread in this window, as such you cannot resume piping");
                            alert.showAndWait();
                        }
                    );
                }

            }
        };
        

        Runnable skip = new Runnable(){
            public void run(){
                System.out.println("skip");
                Piping currentPiping = MainWindow.this.tabs.getCurrentStructurePane().getPiping();
                if (currentPiping != null){
                    MainWindow.this.tabs.getCurrentStructurePane().handleSkip();
                }else{
                    Platform.runLater(
                        () -> {
                            Alert alert = new Alert(AlertType.INFORMATION);
                            alert.setTitle("No External Process");
                            alert.setHeaderText(null);
                            alert.setContentText("You have not yet started a piping thread in this window, as such there is nothing to skip");
                            alert.showAndWait();
                        }
                    );
                }
            }
        };

        Runnable pause = new Runnable(){
            public void run(){
                if (!MainWindow.this.tabs.getCurrentStructurePane().handleSpontaneousPause()){
                }else{
                    Platform.runLater(
                        () -> {
                            Alert alert = new Alert(AlertType.INFORMATION);
                            alert.setTitle("No External Process");
                            alert.setHeaderText(null);
                            alert.setContentText("You have not yet started a piping thread in this window, as such there is nothing to pause");
                            alert.showAndWait();
                        }
                    );
                }
            }
        };

        Runnable stop = new Runnable(){
            public void run(){
                if (!MainWindow.this.tabs.getCurrentStructurePane().handleSpontaneousStop()){
                    Platform.runLater(
                        () -> {
                            Alert alert = new Alert(AlertType.INFORMATION);
                            alert.setTitle("No External Process");
                            alert.setHeaderText(null);
                            alert.setContentText("You have not yet started a piping thread in this window, as such there is nothing to pause");
                            alert.showAndWait();
                        }
                    );
                }
            }
        };
        


        DockPane mainDockPane = new DockPane();
        DockNode structureNode = new DockNode(tabs.getTabPane());

        //  pluginControlPanel stuff commented out because
        // it is not properly integrated with multiple piping
        this.pluginControlPanel = new PluginControlPanel();
        this.pluginControlPanel.setOnPlay(play);
        this.pluginControlPanel.setOnStep(skip);
        this.pluginControlPanel.setOnPause(pause);
        this.pluginControlPanel.setOnStop(stop);
        // END
        

        DockNode objDock = new DockNode(objectInspector, "Object Inspector", null);
        DockNode pluginDock = new DockNode(this.pluginControlPanel, "Algorithm Control", null);
        DockNode consoleDock = new DockNode(mainConsole, "Console", null);

        structureNode.dock(mainDockPane, DockPos.CENTER);
        structureNode.setMaxHeight(Double.MAX_VALUE);
        structureNode.setPrefWidth(Double.MAX_VALUE);
        structureNode.setPrefHeight(Double.MAX_VALUE);
        structureNode.setDockTitleBar(null);



        objDock.dock(mainDockPane, DockPos.RIGHT);
        objDock.setPrefHeight(250);
        objDock.setPrefWidth(270);
        objDock.setMinWidth(270);


        pluginDock.dock(mainDockPane, DockPos.BOTTOM, objDock);
        pluginDock.setPrefHeight(70);
        pluginDock.setMinHeight(70);


        consoleDock.dock(mainDockPane, DockPos.BOTTOM, pluginDock);
        consoleDock.setPrefWidth(200);
        consoleDock.setMinHeight(200);
        consoleDock.setMaxHeight(Double.MAX_VALUE);

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        root = new BorderPane();
        root.setTop(topPane);
        root.setCenter(mainDockPane);
        root.setBottom(statusBar.getStatusBar());
    }

    public void foo(){
        System.out.println("foo");
    }

    public void handlePlannedConsoleInput(){}
    public void onLoadLastPlugin() {
        onLoadPlugin(getLastFileName());
    }

    public void onLoadPluginWithPromptForFile() {
        
        ChooseFileForPipingWindow chooseFileForPipingWindow = new ChooseFileForPipingWindow();
        chooseFileForPipingWindow.setOnHiding(new EventHandler<WindowEvent>() {

             @Override
             public void handle(WindowEvent event) {
                 Platform.runLater(new Runnable() {

                     @Override
                    public void run() {
                        String fileName = chooseFileForPipingWindow.fileName;
                        if (fileName != null){
                            MainWindow.this.onLoadPlugin(fileName);
                        }else{
                            System.out.println("twas the nullteenth of april");
                        }
                    }
                });
            }
        });

    }

    public void onLoadPluginFromSpecifiedFilepath(){
        this.onLoadPlugin(getSpecifiedFileName());
    }

    public void onLoadPlugin(String fileName) {
        Preferences.setFile("MainWindow_lastPipingFile",new File(fileName));
        // FileChooser fileChooser = new FileChooser();
        // fileChooser.setInitialDirectory(new File(getLastDirectory()));
        // fileChooser.setTitle("Load Plugins");
        // fileChooser.getExtensionFilters().add(
        //     new FileChooser.ExtensionFilter("Jar Files (*.jar)", "*.jar")
        // );
        // List<File> list = fileChooser.showOpenMultipleDialog(stage);
        // if (list != null && !list.isEmpty()) {
        //     setLastDirectory(list.get(0));
        //     for (File file : list)
        //         doLoadPlugin(file.getAbsolutePath());
        // }

        // try {
        //     String s = null;
        //     System.out.println(s.substring(0,1));
        // } catch (Exception ex) {
        //     ExceptionBox exbox = new ExceptionBox();
        //     Exception hello = new Exception("hello world");
        //     exbox.showAndWait(hello);
        // }

        try{
//<<<<<<< HEAD
            // this.waitForPause = new CountDownLatch(1);



            if (this.tabs.getCurrentStructurePane() == null){
                Platform.runLater(
                    () -> {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("No Structure Pane");
                        alert.setHeaderText(null);
                        alert.setContentText("Youmust have an open graph to pipe it bro");
                        alert.showAndWait();
                    }
                );

                return;
            }


            System.out.println("with filename: " + fileName);

            Piping newPiping = this.tabs.getCurrentStructurePane().makeANewPiping(fileName,this::initGraph,this::sendOutsideMessageToConsole);
            this.pipelines.add(newPiping);
            
            

            // pipeline.run(this::initGraph);
            // if (!externalProcessInitResponse.equals("useCurrentGraph")){
            //     System.out.println("trying to make a grpah with type : " + externalProcessInitResponse);
            //     Structure temp = StructureManager.instantiateStructure(externalProcessInitResponse);
            //     System.out.println("intsantiaed structuer with id: ");
            //     System.out.println(temp.getId());
            //     tabs.addTab("new " + externalProcessInitResponse,temp);

            //     tabs.getAllStructures();
                
            //     pipeline.run(temp,tabs.getCurrentStructurePane());
            // }else{
            //     pipeline.run(getCurrentStructure(),tabs.getCurrentStructurePane());

            // }


        } catch(Exception e){
            e.printStackTrace();
        }
    }


    public String getSpecifiedFileName(){


        String fileName = Preferences.getFile("MainWindow_pipingFile", "/home/michelle/gralog/gralog/gralog-layout/DFS.py").getPath();
        return fileName;
    }

    public String getLastFileName(){

        String fileName = Preferences.getFile("MainWindow_lastPipingFile", "/Users/f002nb9/Documents/f002nb9/kroozing/gralog/gralog-fx/src/main/java/gralog/gralogfx/piping/FelixTest.py").getPath();

        return fileName;
    }

    public StructurePane initGraph(String graphType,Piping pipelineThatCalled){
        System.out.println("here in initgraph!!!!" + graphType);
        if (!graphType.equals("useCurrentGraph")){
            System.out.println("trying to make a grpah with type : " + graphType);
            System.out.println("previosly current structure pane was : " + this.tabs.getCurrentStructurePane());
            Structure temp;
            try{
                temp = StructureManager.instantiateStructure(graphType);
            }catch(Exception e){
                e.printStackTrace();
                System.out.println("well that's an L" + graphType);
                return null;
            }
         
            tabs.addTab("new " + graphType,temp);

            tabs.getCurrentStructurePane().setPiping(pipelineThatCalled);

            System.out.println("postwardly current structure pane is : " + this.tabs.getCurrentStructurePane());
            
            return tabs.getCurrentStructurePane();
        }else{
            return tabs.getCurrentStructurePane();

        }
    }

    public Boolean profferTextToMainWindow(String text){
        Piping currentPiping = this.tabs.getCurrentStructurePane().getPiping();
        if (currentPiping != null && currentPiping.getPipingState() == Piping.State.WaitingForConsoleInput){
            return currentPiping.profferConsoleInput(text);
        }
        return false;
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
        menu.update();
        this.setStatus("");
    }

    public void onNew(String structureName) throws Exception {
        System.out.println("instantiating structrure called: " + structureName);
        Structure structure = StructureManager.instantiateStructure(structureName);
        tabs.addTab(structureName, structure);
        setStatus("created a " + structureName + "...");
    }

    public void sendOutsideMessageToConsole(String msg, MessageToConsoleFlag flag){
        this.mainConsole.outsideMessage(msg,flag);
    }


    // public void 

    public void onSave() {
        onSave(getCurrentStructure(), this);
    }
    public static void onSave(Structure structure, Application app){
        try {
            if(structure == null){
                ExceptionBox exbox = new ExceptionBox();
                exbox.showAndWait("You want to save an empty structure");
                return;
            }

            if(structure.hasFileReference()){
                structure.getFileReference();
                File file = new File(structure.getFileReference());
                if (file != null) {
                    setLastDirectory(file);
                    // has the user selected the native file-type or an export-filter?
                    String extension = file.getName(); // unclean way of getting file extension
                    int idx = extension.lastIndexOf('.');
                    extension = idx > 0 ? extension.substring(idx + 1) : "";

                    ExportFilter exportFilter = ExportFilterManager
                            .instantiateExportFilterByExtension(structure.getClass(), extension);
                    if (exportFilter != null) {
                        // configure export filter
                        ExportFilterParameters params = exportFilter.getParameters(structure);
                        if (params != null) {
                            ExportFilterStage exportStage = new ExportFilterStage(exportFilter, params, app);
                            exportStage.showAndWait();
                            if (!exportStage.dialogResult)
                                return;
                        }
                        exportFilter.exportGraph(structure, file.getAbsolutePath(), params);
                    } else {
                        structure.writeToFile(file.getAbsolutePath());
                    }
                    System.out.println("Saving " + file.getName() + " to: \t" + file.getPath());
                }
            }
        } catch (Exception ex) {
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait(ex);
        }
    }

    public void onSaveAs() {
        onSaveAs(getCurrentStructure(), stage, tabs, this);
    }
    public static void onSaveAs(Structure structure, Stage stage, Tabs tabs, Application app) {
        try {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(getLastDirectory()));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Graph Markup Language (*.graphml)", "*.graphml")
            );

            // add export-filters to list of extensions
            for (String format : ExportFilterManager.getExportFilters(structure.getClass())) {
                ExportFilterDescription descr = ExportFilterManager.getExportFilterDescription(structure.getClass(), format);
                ExtensionFilter filter = new FileChooser.ExtensionFilter(
                        descr.name() + " (*." + descr.fileExtension() + ")",
                        "*." + descr.fileExtension());
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

                ExportFilter exportFilter = ExportFilterManager
                        .instantiateExportFilterByExtension(structure.getClass(), extension);
                if (exportFilter != null) {
                    // configure export filter
                    ExportFilterParameters params = exportFilter.getParameters(structure);
                    if (params != null) {
                        ExportFilterStage exportStage = new ExportFilterStage(exportFilter, params, app);
                        exportStage.showAndWait();
                        if (!exportStage.dialogResult)
                            return;
                    }
                    exportFilter.exportGraph(structure, file.getAbsolutePath(), params);
                } else {
                    structure.writeToFile(file.getAbsolutePath());
                }
                tabs.setCurrentTabName(file.getName());
            }
        } catch (Exception ex) {
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait(ex);
        }
    }

    public void onOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(getLastDirectory()));
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All (*.*)", "*.*"),
            new FileChooser.ExtensionFilter("Graph Markup Language (*.graphml)", "*.graphml")
        );

        // add export-filters to list of extensions
        for (String format : ImportFilterManager.getImportFilterClasses()) {
            ImportFilterDescription descr = ImportFilterManager.getImportFilterDescription(format);
            ExtensionFilter filter = new FileChooser.ExtensionFilter(
                descr.name() + " (*." + descr.fileExtension() + ")",
                "*." + descr.fileExtension());
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

    public void onDirectInput() throws Exception {
        DirectInputStage directinputstage = new DirectInputStage(this);
        directinputstage.showAndWait();
        Structure s = directinputstage.dialogResult;
        if (s != null)
            tabs.addTab("", s);
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

            if (structure != null){
                structure.setFileReference(true, file.getAbsolutePath());
                tabs.addTab(file.getName(), structure);
            }
        } catch (Exception ex) {
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait(ex);
        }
        this.setStatus("");
    }

    public void setStatus(String status) {
        statusBar.setStatus(status);
    }

    /**
     * Handler to generate a new graph.
     *
     * @param generatorName The name of the structure to generate.
     */
    public void onGenerate(String generatorName) {
        try {
            // prepare
            Generator gen = GeneratorManager.instantiateGenerator(generatorName);
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
            tabs.addTab(gen.getDescription().name(), genResult);

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
                tabs.addTab("Algorithm result", (Structure) algoResult);
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

    public void onRunAlgorithm(String algorithmName) {
        try {
            // Prepare
            StructurePane structurePane = tabs.getCurrentStructurePane();
            Structure structure = structurePane.structure;
            Algorithm algo = AlgorithmManager.instantiateAlgorithm(structure.getClass(), algorithmName);

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
            this.setStatus("Running Algorithm \"" + algorithmName + "\"...");
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
            Preferences.getInteger(getClass(), "main-window-width", 1200),
            Preferences.getInteger(getClass(), "main-window-height", 700));

        scene.getStylesheets().add("stylesheet.css");
        System.out.println("sheets: " + scene.getStylesheets());

        scene.getStylesheets().add(DockPane.class.getResource("default.css").toExternalForm());
        this.stage = primaryStage;
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(400);
        primaryStage.setTitle("Gralog");
        primaryStage.setScene(scene);
        primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN, e -> windowShown());

        //TODO: implement hot keys here


        scene.setOnKeyPressed(event -> {


            
        });

        // Remember the size of the window.
        primaryStage.setOnCloseRequest((e) -> {
            Preferences.setInteger(getClass(), "main-window-width", (int) scene.getWidth());
            Preferences.setInteger(getClass(), "main-window-height", (int) scene.getHeight());


            if(dontAskWhenClosing){
                primaryStage.hide();
                Platform.exit();
            }else{
                tabs.requestClose(() -> {
                    primaryStage.hide();
                    Platform.exit();
                });
            }

            for (Piping p : this.pipelines){
                if (p != null){
                    p.killSelf();

                }
            }

            e.consume();

        });
        primaryStage.show();

        MultipleKeyCombination.setupMultipleKeyCombination(scene);


        // Setting up arguments
        Parameters p = getParameters();
        for(String arg : p.getRaw()){
            if(arg.equals("dawc")){
                dontAskWhenClosing = true;
            }
        }
    }
    @Override
    public void stop(){
        System.exit(0);
    }

    public void windowShown() {
        // Load Config
        NodeList children = null;
        String configFileDir = null;
        try {
            File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            File configFile = new File(jarFile.getParentFile().getAbsolutePath() +
                    File.separator + "config.xml");
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

    private Structure getCurrentStructure() {
        StructurePane pane = tabs.getCurrentStructurePane();
        return pane == null ? null : pane.structure;
    }


    /**
     * Whenever the tab switches structure panes, notify all relevant entities
     */
    private void onSwitchCurrentStructure() {
        StructurePane pane = tabs.getCurrentStructurePane();
        Structure structure = getCurrentStructure();

        rightBox.setVisible(structure != null);
        menu.setCurrentStructurePane(pane);
        statusBar.setCurrentStructure(structure);
    }

    private static String getLastDirectory() {
        final String defaultDir = System.getProperty("user.home");
        String dir = Preferences.getString(MainWindow.class, "lastdirectory", defaultDir);
        if (Files.exists(Paths.get(dir)))
            return dir;
        return defaultDir;
    }

    private static void setLastDirectory(File lastFile) {
        setLastDirectory(Paths.get(lastFile.getAbsolutePath()).getParent().toString());
    }

    private static void setLastDirectory(String lastDirectory) {
        Preferences.setString(MainWindow.class, "lastdirectory", lastDirectory);
    }
}
