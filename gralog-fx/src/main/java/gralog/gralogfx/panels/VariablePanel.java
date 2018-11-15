package gralog.gralogfx.panels;

import gralog.dialog.GralogList;
import gralog.structure.Structure;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

import java.util.ArrayList;

public class VariablePanel extends AnchorPane {

    private static class Variable {

        SimpleStringProperty name;
        DoubleProperty value;

        public Variable(String name, double value) {
            this.name.setValue(name);
            this.value.setValue(value);
        }
    }

    private ObservableList<Variable> variables;

    public VariablePanel() {
        variables = FXCollections.observableList(new ArrayList<>());

        var table = new TableView<Variable>();


        var listID = new TableColumn<Variable, String>("No.");
        var objectIDs = new TableColumn<Variable, Number>("Objects");
        TableColumn actionCol = new TableColumn("");
        // taken from StackOverflow https://stackoverflow.com/a/29490190
        Callback<TableColumn<Variable, String>, TableCell<Variable, String>> cellFactory
                = //
                new Callback<>()
                {

                    @Override
                    public TableCell<Variable, String> call(final TableColumn<Variable, String> param)
                    {
                        final TableCell<Variable, String> cell = new TableCell<>()
                        {

                            final Button btn = new Button();

                            @Override
                            public void updateItem(String item, boolean empty)
                            {
                                btn.setTextAlignment(TextAlignment.CENTER);
                                btn.setStyle("-fx-border-color: transparent;-fx-background-color: transparent;");
                                btn.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/Rubbish.png"))));
                                super.updateItem(item, empty);
                                if (empty)
                                {
                                    setGraphic(null);
                                    setText(null);
                                } else
                                {
                                    btn.setOnAction(event -> {

                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }

                            }
                        };
                        return cell;
                    }
                };

        actionCol.setCellFactory(cellFactory);

        listID.setCellValueFactory(cellData -> cellData.getValue().name);
        objectIDs.setCellValueFactory(cellData -> cellData.getValue().value);

        DoubleBinding usedWidth = listID.widthProperty().add(0);
        objectIDs.prefWidthProperty().bind(table.widthProperty().subtract(usedWidth).subtract(55));
        actionCol.prefWidthProperty().bind(table.widthProperty().multiply(0).add(35));
        table.setItems(variables);
        table.getColumns().addAll(listID, objectIDs, actionCol);

        getChildren().addAll(table);

        AnchorPane.setTopAnchor(table, 0d);
        AnchorPane.setBottomAnchor(table, 0d);
        AnchorPane.setLeftAnchor(table, 0d);
        AnchorPane.setRightAnchor(table, 0d);
    }
    public double getVariable(String name) {
        return 0;
    }

    /**
     * Either updates or creates a new variable.
     * @param name
     */
    public void setVariable(String name) {
        variables.add(new Variable(name, 2));
    }
}
