package gralog.gralogfx.panels;

import gralog.dialog.*;
import gralog.gralogfx.StructurePane;
import gralog.gralogfx.Tabs;
import gralog.gralogfx.dialogfx.Dialogfx;
import gralog.structure.Highlights;
import gralog.structure.Structure;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.*;
import java.util.function.Consumer;

import static gralog.dialog.DialogState.*;

public class Console extends VBox implements GralogWindow{

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";


    private TextField input;
    private TextArea output;

    private Tabs tabs;
    private Dialog dialog;
    private Dialogfx dialogfx;
    private DialogParser parser;
    private DialogState dialogState;



    private LinkedList<String> history = new LinkedList<>();
    private int historyPointer = -1;

    private final Set<Consumer<String>> subscribers = new HashSet<>();

    public Console(Tabs tabs){

        this.tabs = tabs;

        input = new TextField();
        input.setMaxHeight(20);
        input.setMinHeight(20);
        input.prefWidthProperty().bind(this.widthProperty());
        input.setFont(Font.font("Monospaced", FontWeight.NORMAL, 11));

        dialogfx = new Dialogfx();
        dialog = new Dialog();
        dialogState = DONE;
        parser = new DialogParser();


        input.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            String inputText = input.getText();
            if(e.getCode() == KeyCode.ENTER){
                output.appendText("./> " + inputText + "\n");

                if(!inputText.isEmpty()){
                    history.add(inputText);
                    onEnter(inputText, tabs.getCurrentStructurePane());
                }
                input.clear();
            }else if(e.getCode() == KeyCode.BACK_SPACE){
                input.deletePreviousChar();
            }
            if(e.getCode() == KeyCode.UP){
                historyPointer = Math.min(history.size() - 1, historyPointer + 1);
                input.setText(history.get(history.size() - 1 - historyPointer));
                input.positionCaret(inputText.length());
            }
            else if(e.getCode() == KeyCode.DOWN){
                historyPointer = Math.max(0, historyPointer - 1);
                input.setText(history.get(history.size() - 1 - historyPointer));
                input.positionCaret(inputText.length());
            }
            else{
                historyPointer = -1;
            }
            e.consume();
        });

        output = new TextArea();
        output.setWrapText(true);
        output.setFont(Font.font("Monospaced", FontWeight.NORMAL, 11));
        output.setEditable(false);
        output.setFocusTraversable(false);
        output.prefHeightProperty().bind(this.heightProperty().subtract(20));
        output.prefWidthProperty().bind(this.widthProperty());

        getChildren().addAll(output, input);
    }

    /**
     * Executes Consumer after console input has been submitted
     */
    public void registerMethod(Consumer<String> c){
        subscribers.add(c);
    }

    public void onEnter(String text, StructurePane currentPane){
        for(Consumer<String> consumer : subscribers){
            consumer.accept(text);
        }

        parser.parse(dialogState,text);
        System.out.println(ANSI_RED + "console: parsed\n" + ANSI_RESET);
        ActionType type = parser.getType(); // draw smth: FX, change graph: CORE
        DialogAction dialogAction = parser.getDialogAction();
        ArrayList<String> parameters = parser.getParameters();

        dialogState = parser.getDialogState();
        System.out.println(ANSI_RED + "console: dialogState=" + dialogState + ANSI_RESET);

        if (dialogState == SELECT){
            System.out.println(ANSI_RED+parser.getErrorMsg()+ANSI_RESET);
            output(parser.getErrorMsg());
        }

        if (dialogState == DONE){
            switch (dialogAction) {
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
            }
        }
    }

    public void output(String text){

        output.appendText(text);

    }

    public void clear(){
        if(input != null){
            input.clear();
            historyPointer = -1;
        }
    }

    

    @Override
    public void notifyStructureChange(Structure structure) { }

    @Override
    public void notifyHighlightChange(Highlights highlights) { }
}
