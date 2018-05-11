package gralog.gralogfx.panels;

import gralog.structure.Highlights;
import gralog.structure.Structure;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.dockfx.DockNode;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Consumer;


public class Console extends VBox implements GralogWindow{

    private TextField input;
    private TextArea output;

    private LinkedList<String> history = new LinkedList<>();
    private int historyPointer = -1;

    private final Set<Consumer<String>> subscribers = new HashSet<>();

    public Console(){
        input = new TextField();
        input.setMaxHeight(20);
        input.setMinHeight(20);
        input.prefWidthProperty().bind(this.widthProperty());
        input.setFont(Font.font("Monospaced", FontWeight.NORMAL, 11));

        input.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            String inputText = input.getText();
            if(e.getCode() == KeyCode.ENTER){
                output.appendText("./> " + inputText + "\n");

                if(!inputText.isEmpty()){
                    history.add(inputText);
                    submit(inputText);
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
    public void submit(String text){
        for(Consumer<String> consumer : subscribers){
            consumer.accept(text);
        }
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
