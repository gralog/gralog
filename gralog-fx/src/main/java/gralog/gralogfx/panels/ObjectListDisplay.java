package gralog.gralogfx.panels;

import gralog.dialog.GralogList;
import gralog.structure.Edge;
import gralog.structure.Vertex;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.image.Image;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

import java.util.ArrayList;

public class ObjectListDisplay extends AnchorPane
{
    public static ObservableList<GralogList<Vertex>> vertexList = FXCollections.observableList(new ArrayList<>());
    public static ObservableList<GralogList<Edge>> edgeList = FXCollections.observableList(new ArrayList<>());

    public ObjectListDisplay(){

        var table = new TableView<GralogList<Vertex>>();


        var listID = new TableColumn<GralogList<Vertex>, String>("#");
        var listName = new TableColumn<GralogList<Vertex>, String>("Name");
        var objectIDs = new TableColumn<GralogList<Vertex>, String>("Value");

        TableColumn actionCol = new TableColumn("");
        // taken from StackOverflow https://stackoverflow.com/a/29490190
        Callback<TableColumn<GralogList, String>, TableCell<GralogList, String>> cellFactory
                = //
                new Callback<>()
                {

                    @Override
                    public TableCell<GralogList, String> call(final TableColumn<GralogList, String> param)
                    {
                        return new TableCell<>()
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
                                    btn.setOnAction(event -> vertexList.remove(getIndex()));
                                    setGraphic(btn);
                                    setText(null);
                                }

                            }
                        };
                    }
                };

        actionCol.setCellFactory(cellFactory);

        listID.setCellValueFactory(
                p -> new ReadOnlyObjectWrapper(table.getItems().indexOf(p.getValue()))
        );

        listID.setSortable(false);
        objectIDs.setSortable(false);
        actionCol.setSortable(false);

        listName.setCellValueFactory(cellData -> cellData.getValue().name);
        objectIDs.setCellValueFactory(cellData -> cellData.getValue().stringData);

        listID.setPrefWidth(25);
        DoubleBinding usedWidth = listName.widthProperty().add(20);
        objectIDs.prefWidthProperty().bind(table.widthProperty().subtract(usedWidth).subtract(60));
        actionCol.prefWidthProperty().bind(table.widthProperty().multiply(0).add(35));
        table.setItems(vertexList);
        table.getColumns().addAll(listID, listName, objectIDs, actionCol);

        getChildren().addAll(table);

        AnchorPane.setTopAnchor(table, 0d);
        AnchorPane.setBottomAnchor(table, 0d);
        AnchorPane.setLeftAnchor(table, 0d);
        AnchorPane.setRightAnchor(table, 0d);

        //vertexList.add(new GralogList<String>("list1"));
    }
    public String getUniqueDefaultName(){
        outer : for(int i = 0; true; i++){
            for(GralogList l : vertexList){
                if(l.name.getValue().equals("list" + i)){
                    continue outer;
                }
            }
            return "list" + i;
        }

    }

}
