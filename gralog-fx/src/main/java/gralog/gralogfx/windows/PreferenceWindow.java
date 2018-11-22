package gralog.gralogfx.windows;

import gralog.preferences.Preferences;
import gralog.rendering.GralogColor;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.File;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

/**
 * Spawns a stage that contains all Gralog-relevant preferences
 *
 * Loads and stores all preferences (editor config, structure default vars, etc)
 * back in the user configuration file from gralog.preferences.
 *
 * All changes are being stored offline in the persistent configuration, NOT in
 * a runtime object.
 *
 */
public class PreferenceWindow extends Stage {

    private static final double WINDOW_WIDTH = 700;
    private static final double WINDOW_HEIGHT = 420;
    FileChooser pipingSourceFileChooser;
    File pipingFile = null;
    File newPipingFile = null;


    public PreferenceWindow() {
        final Parent generalPageCopy;
        final Parent structurePageCopy;

        this.pipingSourceFileChooser = new FileChooser();

        Parent root = new Pane();
        Pane generalPage = new Pane();
        Pane structurePage = new Pane();

        try {
            URL fxmlURLMain = getClass().getClassLoader().getResource("fxml/preference_window.fxml");
            URL fxmlURLGeneral = getClass().getClassLoader().getResource("fxml/preference_general.fxml");
            URL fxmlURLStructure = getClass().getClassLoader().getResource("fxml/preference_structure.fxml");

            if(fxmlURLMain == null) {
                System.err.println("The preference-fxml URL is null. Does the file exist?");
            }else if(fxmlURLGeneral ==  null) {
                System.err.println("The general page pref fxml URL is null. Does the file exist?");
            }else if(fxmlURLStructure == null) {
                System.err.println("The structure page pref fxml URL is null. Does the file exist?");
            }else {
                root = FXMLLoader.load(fxmlURLMain);
                generalPage = FXMLLoader.load(fxmlURLGeneral);
                structurePage = FXMLLoader.load(fxmlURLStructure);
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
        generalPageCopy = generalPage;
        structurePageCopy = structurePage;
        
        Scene s = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        Button okButton = findButton(root, "ok");
        okButton.setOnAction(e -> savePreferences(generalPageCopy, structurePageCopy));

        Button cancelButton = findButton(root, "cancel");
        cancelButton.setOnAction(e -> hide());

        loadPreferences(generalPage);
        loadPreferences(structurePage);

        setupToggleGroups(s, generalPage, structurePage);

        setTitle("Preferences");
        setWidth(WINDOW_WIDTH);
        setHeight(WINDOW_HEIGHT);
        setScene(s);

        show();
        centerOnScreen();
    }

    private Button findButton(Parent root, String name) {
        for(Parent p : getChildrenRecursively(root)) {
            if(p instanceof Button && p.getId().equals(name)) {
                return (Button)p;
            }
        }
        return null;
    }

    /**
     * Loads all values from the configuration file into the
     * value fields of the given node.
     *
     * Example: TextField with ID k=StructurePane-hasGrid. If the
     * preference file has a key k then the value from k gets loaded
     * into the TextField.
     */
    private void loadPreferences(Parent root) {
        for(Node node : getChildrenRecursively(root)) {
            String id = node.getId();
            if(id != null && !id.isEmpty()) {
                if(node instanceof CheckBox) {
                    Boolean b = Preferences.getBoolean(node.getId(), false);
                    ((CheckBox)node).setSelected(b);
                }
                if(node instanceof TextField) {
                    Double d = Preferences.getDouble(node.getId(), 0);
                    ((TextField)node).setText(d.toString());
                }
                if(node instanceof ColorPicker) {
                    GralogColor c = Preferences.getColor(node.getId(), GralogColor.BLACK);
                    ((ColorPicker)node).setValue(Color.rgb(c.r, c.g, c.b));
                }
                if(node instanceof Button) {
                    Button button = (Button)node;
                    this.pipingFile = Preferences.getFile(node.getId(),null);
                    if (pipingFile != null) {
                        String simpleName = this.pipingFile.getName();
                        button.setText(simpleName);
                        button.setOnAction(e -> {
                            this.newPipingFile = this.pipingSourceFileChooser.showOpenDialog(this);
                            button.setText(newPipingFile.getName());
                        });
                    }
                }

            }
        }
    }

    /**
     * Saves all preferences corresponding to the IDs to the children
     * of the given root nodes
     * @param root
     */
    private void savePreferences(Parent... root) {
        for(int i = 0; i < root.length; i++) {
            for(Node node : getChildrenRecursively(root[i])) {
                String id = node.getId();
                if(id != null && !id.isEmpty()) {
                    if(node instanceof CheckBox) {
                        Boolean b = ((CheckBox)node).isSelected();
                        Preferences.setBoolean(node.getId(), b);
                    }
                    if(node instanceof TextField) {
                        Double d = Double.parseDouble(((TextField)node).getText());
                        Preferences.setDouble(node.getId(),  d);
                    }
                    if(node instanceof ColorPicker) {
                        GralogColor c = GralogColor.parseColorAlpha(((ColorPicker)node).getValue().toString());
                        Preferences.setColor(node.getId(), c);
                    }
                    if(node instanceof Button) {
                        if (this.newPipingFile != null) {
                            this.pipingFile = this.newPipingFile;
                        }
                        Preferences.setFile(node.getId(),this.pipingFile);
                        
                    }
                }
            }
        }

        hide();
    }

    /**
     * Gets all nodes below the given one
     */
    private LinkedList<Parent> getChildrenRecursively(Parent root) {
        LinkedList<Parent> result = new LinkedList<>();
        for(Node x : root.getChildrenUnmodifiable()) {
            if(x instanceof Parent) {
                result.add((Parent)x);
                result.addAll(getChildrenRecursively((Parent)x));
            }
        }
        return result;
    }
    /**
     * Sets up the toggle buttons of the preference window to be
     * combined into a ToggleGroup
     */
    private void setupToggleGroups(Scene mainScene, Node generalPage, Node structurePage) {

        ToggleGroup tgroup = new ToggleGroup();

        ToggleButton structureButton = (ToggleButton) mainScene.lookup("#structureButton");
        ToggleButton generalButton = (ToggleButton) mainScene.lookup("#generalButton");

        tgroup.getToggles().addAll(structureButton, generalButton);

        Pane container = (Pane) mainScene.lookup("#container");

        tgroup.selectedToggleProperty().addListener((e, oldVal, newVal) -> {
            if(newVal == null) {
                oldVal.setSelected(true);
            }else {
                container.getChildren().clear();
                if(newVal == generalButton) {
                    container.getChildren().add(generalPage);
                }else if(newVal == structureButton) {
                    container.getChildren().addAll(structurePage);
                }
            }
        });

        container.getChildren().add(structurePage); // default
        structureButton.setSelected(true);
    }

}
