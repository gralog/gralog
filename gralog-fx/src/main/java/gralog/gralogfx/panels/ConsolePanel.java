package gralog.gralogfx.panels;

import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class ConsolePanel extends AnchorPane {

    public ConsolePanel(Runnable onExecute) {
        HBox hbox = new HBox();
        hbox.prefWidthProperty().bind(this.widthProperty());
        prefWidthProperty().bind(this.widthProperty());

        Button executeButton = createButton("exec");
        executeButton.setOnMouseClicked(event -> onExecute.run());



        hbox.getChildren().addAll(executeButton);

        AnchorPane.setLeftAnchor(hbox, 0.0);
        AnchorPane.setRightAnchor(hbox, 0.0);
        AnchorPane.setBottomAnchor(hbox, 0.0);
        AnchorPane.setTopAnchor(hbox, 0.0);
        HBox.setHgrow(executeButton, Priority.ALWAYS);

        this.getChildren().add(hbox);
        setMinHeight(200);
        setMinWidth(400);
        hbox.setStyle("-fx-background-color: white;");
    }

    private Button createButton(String label) {
        Button b = new Button(label);
        b.setPrefWidth(200);
        return b;
    }
}
