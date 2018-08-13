package gralog.gralogfx.panels;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class ConsoleField extends TextField{
    boolean changed = false;
    public ConsoleField(String text){
        super(text);
        
        super.textProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("changed: " + changed);
                System.out.println("oldvalue: " + oldValue + " newValue " + newValue);
                if (!ConsoleField.this.changed){
                    ConsoleField.this.changed = true;
                    setText(oldValue);
                    
                }else{
                    changed = false;
                }
            }
        });
    }
    public ConsoleField(){
        super();
        super.textProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                
                if (!ConsoleField.this.changed){
                    ConsoleField.this.changed = true;
                    setText(oldValue);
                    
                }else{
                    ConsoleField.this.changed = false;
                }
            }
        });
    }
    
}