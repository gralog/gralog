package gralog.gralogfx.panels;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class PluginControlPanel extends HBox {

    public PluginControlPanel(Runnable onPlay, Runnable onPause, Runnable onStep){
        prefWidthProperty().bind(this.widthProperty());

        Button b0 = createButton("\u25B6");
        b0.setOnMouseClicked(event -> onPlay.run());

        Button b1 = createButton(">>");
        b1.setOnMouseClicked(event -> onStep.run());

        Button b2 = createButton("||");
        b2.setOnMouseClicked(event -> onPause.run());

        Button b3 = createButton("");
        getChildren().addAll(b0, b1, b2, b3);

        setMinHeight(200);
    }

    private Button createButton(String label){
        Button b = new Button(label);
        b.setPrefWidth(200);
        return b;
    }
}
