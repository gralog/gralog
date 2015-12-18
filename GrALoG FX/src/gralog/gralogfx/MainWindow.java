
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.gralogfx;

import gralog.plugins.*;
import gralog.structure.*;
import gralog.importfilter.*;
import gralog.exportfilter.*;
import gralog.generator.*;
import gralog.algorithm.*;
import gralog.events.VertexEvent;

import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URI;

import javafx.application.Application;
import javafx.stage.WindowEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.Alert.AlertType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author viktor
 */
public class MainWindow extends Application {

    Stage stage;
    BorderPane root;
    MenuBar menu;
        Menu menuFile;
            MenuItem menuFilePlugin;
            Menu menuFileNew;
            Menu menuFileGenerators;
            MenuItem menuFileOpen;
            MenuItem menuFileSave;
            MenuItem menuFileClose;
            MenuItem menuFileExit;
        Menu menuEdit;
            MenuItem menuEditUndo;
            MenuItem menuEditRedo;
            MenuItem menuEditCut;
            MenuItem menuEditCopy;
            MenuItem menuEditPaste;
            MenuItem menuEditDelete;
        Menu menuAlgo;
        Menu menuHelp;
            MenuItem menuHelpAbout;
            MenuItem menuHelpInfo;
    VBox topPane;
        HBox buttonBar;
    TabPane tabPane;
    ObjectInspector objectInspector;
    HBox statusBar;
    Label statusBarMessage;
            
    public MainWindow()
    {
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
                menuFilePlugin = new MenuItem("Load Plugin");
                    menuFilePlugin.setOnAction(e -> menuFilePluginActivated());
                menuFileNew = new Menu("New");
                updateStructures();
                menuFileGenerators = new Menu("Generators");
                updateGenerators();
                menuFileOpen = new MenuItem("Open");
                    menuFileOpen.setOnAction(e -> menuFileOpenActivated());
                menuFileSave = new MenuItem("Save");
                    menuFileSave.setOnAction(e -> menuFileSaveActivated());
                menuFileClose = new MenuItem("Close");
                menuFileExit = new MenuItem("Exit");
            menuFile.getItems().addAll(menuFilePlugin, menuFileNew, menuFileGenerators,
                                       menuFileOpen, menuFileSave, menuFileClose,
                                       menuFileExit);
            
            // Edit Menu
            menuEdit = new Menu("Edit");
                menuEditUndo = new MenuItem("Undo");
                menuEditRedo = new MenuItem("Redo");
                menuEditCut = new MenuItem("Cut");
                menuEditCopy = new MenuItem("Copy");
                menuEditPaste = new MenuItem("Paste");
                menuEditDelete = new MenuItem("Delete");
            menuEdit.getItems().addAll(menuEditUndo, menuEditRedo, menuEditCut,
                    menuEditCopy,menuEditPaste,menuEditDelete);
            
            // Algorithm Menu
            menuAlgo = new Menu("Algorithms");
                
            // Help Menu
            menuHelp = new Menu("Help");
                menuHelpAbout = new MenuItem("About GrALoG");
                menuHelpAbout.setOnAction(e -> {
                    AboutStage aboutstage = new AboutStage(this);
                    aboutstage.showAndWait();
                });
                menuHelpInfo = new MenuItem("Info");
                menuHelpInfo.setOnAction(e -> {
                    Tab tab = tabPane.getSelectionModel().getSelectedItem();
                    if(tab == null)
                        return;
                    StructurePane structurePane = (StructurePane)tab.getContent();
                    Structure structure = structurePane.structure;
                    try {
                        StructureDescription descr = structure.getDescription();
                        String url = descr.url();
                        if(url != null && !url.trim().equals(""))
                            this.getHostServices().showDocument(url);
                    } catch(Exception ex) {
                        ExceptionBox exbox = new ExceptionBox();
                        exbox.showAndWait(ex);
                    }
                });
            menuHelp.getItems().addAll(menuHelpAbout, menuHelpInfo);
            
        menu.getMenus().addAll(menuFile, menuEdit, menuAlgo, menuHelp);

        // Button Bar
        buttonBar = new HBox();
        Button buttonSelectMode = new Button();
        buttonSelectMode.setText("S");
        buttonSelectMode.setOnAction( e -> {
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            if(tab == null)
                return;
            StructurePane structurePane = (StructurePane)tab.getContent();
            structurePane.SetSelectMode();
        });
        Button buttonVertexMode = new Button();
        buttonVertexMode.setText("V");
        buttonVertexMode.setOnAction(e -> {
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            if(tab == null)
                return;
            StructurePane structurePane = (StructurePane)tab.getContent();
            structurePane.SetVertexCreationMode();
        });
        Button buttonEdgeMode = new Button();
        buttonEdgeMode.setText("E");
        buttonEdgeMode.setOnAction(e -> {
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            if(tab == null)
                return;
            StructurePane structurePane = (StructurePane)tab.getContent();
            structurePane.SetEdgeCreationMode();
        });
        buttonBar.getChildren().addAll(buttonSelectMode,buttonVertexMode,buttonEdgeMode);
        topPane = new VBox();
        topPane.getChildren().addAll(menu,buttonBar);
                
        
        // Object Inspector
        objectInspector = new ObjectInspector();

        // Status Bar
        statusBar = new HBox();
        statusBarMessage = new Label("");
        statusBar.getChildren().add(statusBarMessage);
        
        // 
        root = new BorderPane();
        //root.setFocusTraversable(true);
        root.setTop(topPane);
        root.setCenter(tabPane);
        root.setRight(objectInspector);
        root.setBottom(statusBar);
    }
    
    
    
    
    
    
  
    
    public void menuFilePluginActivated() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Plugins");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Jar Files (*.jar)", "*.jar")
        );
        List<File> list = fileChooser.showOpenMultipleDialog(stage);
        if (list != null)
            for (File file : list)
                doLoadPlugin(file.getAbsolutePath());
    }
    
    public void doLoadPlugin(String filename)
    {
        try {
            this.setStatus("Loading Plugin " + filename + "...");
            PluginManager.LoadPlugin(filename);
        } catch(Exception ex) {
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
            Structure structure = StructureManager.InstantiateStructure(str);
            addTab(str, structure);
            setStatus("created a " + str + "...");
        } catch(Exception ex) {
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait(ex);
        }
    }
    
    public void menuFileSaveActivated() {
        try {
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            StructurePane structurePane = (StructurePane)tab.getContent();
            Structure structure = structurePane.structure;
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files (*.*)", "*.*"),
                new FileChooser.ExtensionFilter("Graph Markup Language (*.graphml)", "*.graphml")
            );
            
            // add export-filters to list of extensions
            for(String format : ExportFilterManager.getExportFilters(structure.getClass())){
                ExportFilterDescription descr = ExportFilterManager.getExportFilterDescription(structure.getClass(),format);
                ExtensionFilter filter = new FileChooser.ExtensionFilter(descr.name() + " (*." + descr.fileextension() + ")", "*." + descr.fileextension());
                fileChooser.getExtensionFilters().add(filter);
            }
            
            fileChooser.setInitialFileName("*.*");
            fileChooser.setTitle("Save File");
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                
                // has the user selected the native file-type or an export-filter?
                ExportFilter exportFilter = null;
                String extension = file.getName(); // unclean way of getting file extension
                int idx = extension.lastIndexOf(".");
                extension = idx > 0 ? extension.substring(idx+1) : "";
                
                exportFilter = ExportFilterManager.InstantiateExportFilterByExtension(structure.getClass(), extension);
                if(exportFilter != null) {
                    // configure export filter
                    ExportFilterParameters params = exportFilter.GetParameters(structure);
                    if(params != null) {
                        ExportFilterStage stage = new ExportFilterStage(exportFilter,params,this);
                        if(!stage.ShowAndWait())
                            return;
                    }
                    
                    exportFilter.DoExport(structure, file.getAbsolutePath(), params);
                    
                } else {
                    structure.WriteToFile(file.getAbsolutePath());
                }
                    
            }
            
        } catch(Exception ex) {
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait(ex);
        }        
    }

    public void menuFileOpenActivated() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All (*.*)", "*.*"),
            new FileChooser.ExtensionFilter("Graph Markup Language (*.graphml)", "*.graphml")
        );
        
        // add export-filters to list of extensions
        for(String format : ImportFilterManager.getImportFilterClasses()){
            ImportFilterDescription descr = ImportFilterManager.getImportFilterDescription(format);
            ExtensionFilter filter = new FileChooser.ExtensionFilter(descr.name() + " (*." + descr.fileextension() + ")", "*." + descr.fileextension());
            fileChooser.getExtensionFilters().add(filter);
        }
            
        fileChooser.setInitialFileName("*.*");
        fileChooser.setTitle("Open File");
        List<File> list = fileChooser.showOpenMultipleDialog(stage);
        if (list != null)
            for (File file : list)
                doOpenFile(file);
    }
    
    public void doOpenFile(File file)
    {
        try {
            String extension = ""; // unclean way of getting file extension
            int idx = file.getName().lastIndexOf(".");
            extension = idx > 0 ? file.getName().substring(idx+1) : "";

            ImportFilter importFilter = ImportFilterManager.InstantiateImportFilterByExtension(extension);            
            this.setStatus("Loading File " + file.getAbsolutePath() + "...");
            Structure structure = null;
            
            if(importFilter != null)
            {
                ImportFilterParameters params = importFilter.GetParameters();
                if(params != null)
                {
                    ImportFilterStage stage = new ImportFilterStage(importFilter, params, this);
                    if(!stage.ShowAndWait())
                    {
                        this.setStatus("");
                        return;
                    }
                }
                structure = importFilter.Import(file.getAbsolutePath(), params);
            }
            else
                structure = Structure.LoadFromFile(file.getAbsolutePath());
            
            if(structure != null)
                addTab("", structure);
        } catch(Exception ex) {
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait(ex);
        }
        this.setStatus("");
    }







    public void setStatus(String status) {
        statusBarMessage.setText(status);
    }

    public void addTab(String text, Structure structure)
    {
        Tab t = new Tab(text);
        StructurePane structurePane = new StructurePane(structure);
        t.setContent(structurePane);

        tabPane.getTabs().add(t);
        tabPane.getSelectionModel().select(t);
        structurePane.draw();
        structurePane.setOnSelectionChanged(e -> updateSelection(structurePane));


        /*
        JGraphPane jgraphpane = new JGraphPane();
        t.setContent(jgraphpane);
        tabPane.getTabs().add(t);
        tabPane.getSelectionModel().select(t);
        
        //screen.getWidth(); 
        //screen.getHeight(); 
        double pixelPerInch=96d;//java.awt.Toolkit.getDefaultToolkit().getScreenResolution(); 
        double pixelPerCM = pixelPerInch / 2.54d;

        Set<Vertex> V = (Set<Vertex>)structure.getVertices();
        for(Vertex v : V)
        {
            Vector<Double> coords = v.Coordinates;
            jgraphpane.putVertex( v, coords.get(0)*pixelPerCM, coords.get(1)*pixelPerCM);
        }
        Set<Edge> E = (Set<Edge>)structure.getEdges();
        for(Edge e : E)
        {
            jgraphpane.addEdge(e.source, e.target);
        }
        */
    }

    protected void updateStructures() {
        menuFileNew.getItems().clear();
        for(String str : StructureManager.getStructureClasses())
        {
            MenuItem item = new MenuItem(str);
            item.setOnAction(e -> menuFileNewActivated(str));
            menuFileNew.getItems().add(item);
        }
    }

    protected void updateGenerators() {
        menuFileGenerators.getItems().clear();
        for(String str : GeneratorManager.getGeneratorClasses())
        {
            MenuItem item = new MenuItem(str);
            item.setOnAction(e -> menuFileGeneratorActivated(str));
            menuFileGenerators.getItems().add(item);
        }
    }    
    
    protected void updateAlgorithms() {
        menuAlgo.getItems().clear();
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        if(tab == null)
            return;
        if(!(tab.getContent() instanceof StructurePane))
            return;
        StructurePane structurePane = (StructurePane)tab.getContent();
        Structure structure = structurePane.structure;
        
        for(String str : AlgorithmManager.getAlgorithms(structure.getClass()))
        {
            MenuItem item = new MenuItem(str);
            item.setOnAction(e -> menuAlgorithmActivated(str));
            menuAlgo.getItems().add(item);
        }
    }

    protected void updateSelection() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        StructurePane structurePane = null;
        
        if(tab != null)
            if(tab.getContent() instanceof StructurePane)
                structurePane = (StructurePane)tab.getContent();
        
        updateSelection(structurePane);
    }
    
    protected void updateSelection(StructurePane sender) {
        try {
            Set<Object> selection = null;
            if(sender != null)
            {
                selection = sender.Selection;
                sender.RequestRedraw();
            }
            objectInspector.SetObjects(selection);
        } catch(Exception ex) {
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait(ex);            
        }
    }
        
    
    
    
    
    
    
    public void menuFileGeneratorActivated(String str) {
        try {
            
            // prepare
            
            Generator gen  = GeneratorManager.InstantiateGenerator(str);
            GeneratorParameters params = gen.GetParameters();
            if(params != null)
            {
                GeneratorStage genstage = new GeneratorStage(gen, params, this);
                if(!genstage.ShowAndWait())
                    return;
            }

            // run
            
            Structure genResult = gen.Generate(params);
            if(genResult == null)
                return;
            this.addTab("", genResult);
            
        } catch(Exception ex) {
            this.setStatus("");
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait(ex);
        }
    }    
    
    public void menuAlgorithmActivated(String str) {
        try {

            // Prepare
            
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            StructurePane structurePane = (StructurePane)tab.getContent();
            Structure structure = structurePane.structure;
            Algorithm algo  = AlgorithmManager.InstantiateAlgorithm(structure.getClass(), str);
            
            AlgorithmParameters params = algo.GetParameters(structure);
            if(params != null)
            {
                AlgorithmStage algostage = new AlgorithmStage(algo, structure, params, this);
                if(!algostage.ShowAndWait())
                    return;
            }
            
            // Run
            this.setStatus("Running Algorithm \"" + str + "\"...");
            Object algoResult = algo.DoRun(structure, params);
            this.setStatus("");
            
            // Show Result
            if(algoResult == null)
                return;

            if(algoResult instanceof Structure)
            {
                this.addTab("", (Structure)algoResult);
            }
            else if(algoResult instanceof Set)
            {
                boolean isSelection = true;
                for(Object o : (Set)algoResult)
                    if( !(o instanceof Vertex)
                    &&  !(o instanceof Edge)
                    &&  !(o instanceof EdgeIntermediatePoint))
                        isSelection = false;
                if(isSelection)
                {
                    structurePane.Selection = (Set)algoResult;
                    structurePane.RequestRedraw();
                }
            }
            else if(algoResult instanceof String)
            {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Algorithm Result");
                alert.setHeaderText(null);
                alert.setContentText((String)algoResult);
                alert.showAndWait();
            }
        } catch(InvocationTargetException ex) {
            this.setStatus("");
            ExceptionBox exbox = new ExceptionBox();
            exbox.showAndWait((Exception)ex.getCause());
        } catch(Exception ex) {
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
            PluginManager.Initialize();
            launch(args);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }    
    
    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(root, 800, 600);
        this.stage = primaryStage;
        primaryStage.setTitle("GrALoG FX");
        primaryStage.setScene(scene);
        primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN, e -> windowShown());
        primaryStage.show();
    }

    
    public void windowShown() {
        
        // Load Config
        NodeList children = null;
        try {
            File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            File configFile = new File(jarFile.getParentFile().getAbsolutePath() + File.separator + "config.xml");
        
            if(configFile.exists())
            {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(new FileInputStream(configFile));
                doc.getDocumentElement().normalize();
                Element root = doc.getDocumentElement();
                children = root.getChildNodes();
            }
        } catch(Exception ex) { }

        // Load Plugins from Config
        if(children != null)
            for(int i = 0; i < children.getLength(); ++i) {
                Node childNode = children.item(i);
                if (childNode.getNodeType() != childNode.ELEMENT_NODE)
                    continue;
                Element child = (Element)childNode;
                if(!child.getTagName().equals("plugin"))
                    continue;

                doLoadPlugin(child.getAttribute("location"));
            }
        
        // Load Plugins from Parameters
        Application.Parameters params = this.getParameters();
        for(String s : params.getUnnamed())
            if(s.endsWith(".jar"))
                doLoadPlugin(s);

        // Open Files from Config
        if(children != null)
            for(int i = 0; i < children.getLength(); ++i) {
                Node childNode = children.item(i);
                if (childNode.getNodeType() != childNode.ELEMENT_NODE)
                    continue;
                Element child = (Element)childNode;
                if(!child.getTagName().equals("file"))
                    continue;

                doOpenFile(new File(child.getAttribute("location")));
            }

        // Open Files from Parameters
        for(String s : params.getUnnamed())
            if(!s.endsWith(".jar"))
                doOpenFile(new File(s));
    }
    
}
