package gralog.gralogfx.panels;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import org.dockfx.DockNode;

import java.util.LinkedList;
import java.util.List;


public class Console extends DockNode {

    private TextField textField;

    private List<Runnable> onSubmit = new LinkedList<>();

    public Console(Node contents){
        this(contents, "");
    }
    public Console(Node contents, String title){
        this(contents, title, null);
    }
    public Console(Node contents, String title, Node graphic){
        super(contents, title, graphic);
        init();
    }

    private void init(){
        //TODO: initialize text field
    }

    /**
     * Executes Runnable after console input has been submitted
     */
    public void registerMethod(){
        //TODO: switch from runnable to string parametrization
    }
    public void submit(){

    }

    public void clear(){

    }
}
