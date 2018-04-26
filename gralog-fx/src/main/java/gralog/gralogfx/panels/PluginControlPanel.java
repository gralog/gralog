package gralog.gralogfx.panels;

import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class PluginControlPanel extends AnchorPane {

    public PluginControlPanel(Runnable onPlay, Runnable onPause, Runnable onStep){
        HBox hbox = new HBox();
        hbox.prefWidthProperty().bind(this.widthProperty());
        prefWidthProperty().bind(this.widthProperty());

        Button b0 = createButton("\u25B6");
        b0.setOnMouseClicked(event -> onPlay.run());

        Button b1 = createButton(">>");
        b1.setOnMouseClicked(event -> onStep.run());

        Button b2 = createButton("||");
        b2.setOnMouseClicked(event -> onPause.run());

        Button b3 = createButton("∫");
        hbox.getChildren().addAll(b0, b1, b2, b3);

        AnchorPane.setLeftAnchor(hbox, 4.0);
        AnchorPane.setRightAnchor(hbox, 4.0);
        AnchorPane.setBottomAnchor(hbox, 4.0);
        AnchorPane.setTopAnchor(hbox, 2.0);

        this.getChildren().add(hbox);
        setMinHeight(200);
    }

    private Button createButton(String label){
        Button b = new Button(label);
        b.setPrefWidth(200);
        return b;
    }
}
