package gralog.gralogfx.windows;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Spawns a stage that contains all Gralog-relevant preferences
 *
 * Loads and stores all preferences (editor config, structure default vars, etc)
 * back in the user configuration file from gralog.preferences.
 * 
 */
public class PreferenceWindow extends Stage {

    private static final double WINDOW_WIDTH = 700;
    private static final double WINDOW_HEIGHT = 400;


    public PreferenceWindow() {
        Parent root = new Pane();
        Node generalPage = new Pane();
        Node structurePage = new Pane();

        try{
            URL fxmlURLMain = getClass().getClassLoader().getResource("preference_window.fxml");
            URL fxmlURLGeneral = getClass().getClassLoader().getResource("preference_general.fxml");
            URL fxmlURLStructure = getClass().getClassLoader().getResource("preference_structure.fxml");

            if(fxmlURLMain == null){
                System.err.println("The preference-fxml URL is null. Does the file exist?");
            }else if(fxmlURLGeneral ==  null){
                System.err.println("The general page pref fxml URL is null. Does the file exist?");
            }else if(fxmlURLStructure == null){
                System.err.println("The structure page pref fxml URL is null. Does the file exist?");
            }else{
                root = FXMLLoader.load(fxmlURLMain);
                generalPage = FXMLLoader.load(fxmlURLGeneral);
                structurePage = FXMLLoader.load(fxmlURLStructure);
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        Scene s = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        setupToggleGroups(s, generalPage, structurePage);

        setTitle("Preferences");
        setWidth(WINDOW_WIDTH);
        setHeight(WINDOW_HEIGHT);
        setScene(s);

        show();
        centerOnScreen();
    }

    /**
     * Loads all values from the configuration file into the
     * value fields of the given node.
     *
     * Example: TextField with ID k=StructurePane-hasGrid. If the
     * preference file has a key k then the value from k gets loaded
     * into the TextField.
     */
    void loadGeneralPage(Node generalPage){
        generalPage.lookup("");
    }

    /**
     * Sets up the toggle buttons of the preference window to be
     * combined into a ToggleGroup
     */
    void setupToggleGroups(Scene mainScene, Node generalPage, Node structurePage){

        ToggleGroup tgroup = new ToggleGroup();

        ToggleButton structureButton = (ToggleButton) mainScene.lookup("#structureButton");
        ToggleButton generalButton = (ToggleButton) mainScene.lookup("#generalButton");
        tgroup.getToggles().addAll(structureButton, generalButton);

        Pane container = (Pane) mainScene.lookup("#container");

        tgroup.selectedToggleProperty().addListener((e, oldVal, newVal) -> {
            if(newVal == null){
                oldVal.setSelected(true);
            }else{
                container.getChildren().clear();
                if(newVal == generalButton){
                    container.getChildren().add(generalPage);
                }else if(newVal == structureButton){
                    container.getChildren().addAll(structurePage);
                }
            }
        });

        container.getChildren().add(generalPage); // default
    }

}
