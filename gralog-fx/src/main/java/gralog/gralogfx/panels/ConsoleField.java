package gralog.gralogfx.panels;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;


public class ConsoleField extends TextArea {
    boolean changed = false;
    public ConsoleField(String text) {
        super(text);
        this.setWrapText(true);
        
        
        super.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!ConsoleField.this.changed) {
                    ConsoleField.this.changed = true;
                    setText(oldValue);
                    
                }else {
                    changed = false;
                }
            }
        });
    }
    public ConsoleField() {
        super();
        super.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                
                if (!ConsoleField.this.changed) {
                    ConsoleField.this.changed = true;
                    setText(oldValue);
                    
                }else {
                    ConsoleField.this.changed = false;
                }
            }
        });
    }
    
}
