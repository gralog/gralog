package gralog.gralogfx.panels;

import gralog.dialog.*;
import gralog.gralogfx.StructurePane;
import gralog.gralogfx.Tabs;
import gralog.gralogfx.dialogfx.Dialogfx;
import gralog.structure.Edge;
import gralog.structure.Highlights;
import gralog.structure.Structure;
import gralog.structure.Vertex;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.*;
import java.util.function.Consumer;

import static gralog.dialog.DialogAction.NONE;
import static gralog.dialog.DialogState.*;

public class Console2 extends VBox implements GralogWindow {

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";


    private TextField input;
    private TextArea output;

    private Tabs tabs;
    private Dialog dialog;
    private Dialogfx dialogfx;
    private DialogParser parser;




    private LinkedList<String> history = new LinkedList<>();
    private int historyPointer = -1;

    private final Set<Consumer<String>> subscribers = new HashSet<>();

    public Console2(Tabs tabs) {

        this.tabs = tabs;

        input = new TextField();
        input.setMaxHeight(20);
        input.setMinHeight(20);
        input.prefWidthProperty().bind(this.widthProperty());
        input.setFont(Font.font("Monospaced", FontWeight.NORMAL, 11));

        dialogfx = new Dialogfx();
        dialog = new Dialog();
        parser = new DialogParser();


        input.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            String inputText = input.getText();
            if(e.getCode() == KeyCode.ENTER) {
                output.appendText("./> " + inputText + "\n");

                if(!inputText.isEmpty()) {
                    history.add(inputText);
                    onEnter(inputText, tabs.getCurrentStructurePane());
                }
                input.clear();
            }
        });

        output = new TextArea();
        output.setWrapText(true);
        output.setFont(Font.font("Monospaced", FontWeight.NORMAL, 11));
        output.setEditable(false);
        output.setFocusTraversable(false);
        output.prefHeightProperty().bind(this.heightProperty().subtract(20));
        output.prefWidthProperty().bind(this.widthProperty());

        getChildren().addAll(output, input);
        input.setStyle("-fx-background-color: #123455;");
    }

    /**
     * Executes Consumer after console input has been submitted
     */
    public void registerMethod(Consumer<String> c) {
        subscribers.add(c);
    }

    public void onEnter(String text, StructurePane currentPane) {
        for(Consumer<String> consumer : subscribers) {
            consumer.accept(text);
        }

        parser.parse(text);
        ActionType type = parser.getType();
        ArrayList<String> parameters = parser.getParameters();

        // the input was "filter [all] selected"
        // if only vertices or only edges are selected, guess this, don't let the user write it
        // if nothing, abort
        // if both, ask what to select
        if (parser.getDialogState() == FILTER_SELECTED) {
            boolean existsSelectedVertex = false;
            for (Object v : currentPane.getHighlights().getSelection())
                if (v instanceof Vertex) {
                    existsSelectedVertex = true;
                    break;
                }
            boolean existsSelectedEdge = false;
            for (Object v : currentPane.getHighlights().getSelection())
                if (v instanceof Edge) {
                    existsSelectedEdge = true;
                    break;
                }
            if (existsSelectedVertex & ! existsSelectedEdge) {
                parser.setErrorMsg("");
                parser.setDialogState(FILTER_WHAT);
                parser.addParameter("VERTICES");
                if (text.indexOf('d') == text.length() - 1) { // the input was only "filter [all] selected"
                    output("Specify conditions, start with \"where\".\n");
                    return;
                }
                String remainingText = text.substring(text.indexOf('d')+1); // more text was entered
                parser.parse(remainingText);
            }
            if (existsSelectedEdge & ! existsSelectedVertex) {
                parser.setErrorMsg("");
                parser.setDialogState(FILTER_WHAT);
                parser.addParameter("EDGES");
                if (text.indexOf('d') == text.length() - 1) {
                    output("Specify conditions, start with \"where\".\n");
                    return;
                }
                String remainingText = text.substring(text.indexOf('d')+1); // more text was entered
                parser.parse(remainingText);
            }
            if (!existsSelectedEdge & !existsSelectedVertex) {
                parser.setErrorMsg("Nothing is selected! Aborting.\n");
                parser.setDialogState(DONE);
                parser.setDialogAction(NONE);
            }
        }

        output(parser.getErrorMsg());
        parser.setErrorMsg("");

        if (parser.getDialogState() == DONE) {
            switch (parser.getDialogAction()) {
                case SELECT_ALL:                 dialogfx.selectAll(currentPane);
                                                break;
                case SELECT_ALL_VERTICES:       dialogfx.selectAllVertices(currentPane);
                                                break;
                case SELECT_ALL_EDGES:          dialogfx.selectAllEdges(currentPane);
                                                break;
                case DESELECT_ALL:               dialogfx.deselectAll(currentPane);
                                                break;
                case DESELECT_ALL_VERTICES:     dialogfx.deselectAllVertices(currentPane);
                                                break;
                case DESELECT_ALL_EDGES:        dialogfx.deselectAllEdges(currentPane);
                                                break;
                case FILTER:
                                                dialog.filter(parser.getParameters(),
                                                                currentPane.getStructure(),
                                                                currentPane.getHighlights());
                                                output(dialog.getErrorMsg());
                                                parameters.clear();
                                                break;
                case NONE:                      return;
            }
        }
    }

    public void output(String text) {

        output.appendText(text);

    }

    public void clear() {
        if(input != null) {
            input.clear();
            historyPointer = -1;
        }
    }

    

    @Override
    public void notifyStructureChange(Structure structure) { }

    @Override
    public void notifyHighlightChange(Highlights highlights) { }
}
