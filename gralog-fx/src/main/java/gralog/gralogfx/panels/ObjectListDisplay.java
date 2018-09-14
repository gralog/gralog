package gralog.gralogfx.panels;

import gralog.dialog.GralogList;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

import java.util.ArrayList;

public class ObjectListDisplay extends AnchorPane
{
    public ObservableList<GralogList> list = FXCollections.observableList(new ArrayList<>());

    public ObjectListDisplay(){

        var table = new TableView<GralogList>();


        var listID = new TableColumn<GralogList, String>("#");
        var listName = new TableColumn<GralogList, String>("Name");
        var objectIDs = new TableColumn<GralogList, String>("Objects");

        TableColumn actionCol = new TableColumn("");
        // taken from StackOverflow https://stackoverflow.com/a/29490190
        Callback<TableColumn<GralogList, String>, TableCell<GralogList, String>> cellFactory
                = //
                new Callback<>()
                {

                    @Override
                    public TableCell<GralogList, String> call(final TableColumn<GralogList, String> param)
                    {
                        final TableCell<GralogList, String> cell = new TableCell<>()
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
                                        list.remove(getIndex());
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

        listID.setCellValueFactory(
                p -> new ReadOnlyObjectWrapper(table.getItems().indexOf(p.getValue()))
        );
        listID.setSortable(false);
        listName.setCellValueFactory(cellData -> cellData.getValue().name);
        objectIDs.setCellValueFactory(cellData -> cellData.getValue().stringData);

        listID.setPrefWidth(20);
        DoubleBinding usedWidth = listName.widthProperty().add(20);
        objectIDs.prefWidthProperty().bind(table.widthProperty().subtract(usedWidth).subtract(55));
        actionCol.prefWidthProperty().bind(table.widthProperty().multiply(0).add(35));
        table.setItems(list);
        table.getColumns().addAll(listID, listName, objectIDs, actionCol);

        getChildren().addAll(table);

        AnchorPane.setTopAnchor(table, 0d);
        AnchorPane.setBottomAnchor(table, 0d);
        AnchorPane.setLeftAnchor(table, 0d);
        AnchorPane.setRightAnchor(table, 0d);

        //list.add(new GralogList<String>("list1"));
    }
    public String getUniqueDefaultName(){
        outer : for(int i = 0; true; i++){
            for(GralogList l : list){
                if(l.name.getValue().equals("List (" + i + ")")){
                    continue outer;
                }
            }
            return "List (" + i + ")";
        }

    }

}
