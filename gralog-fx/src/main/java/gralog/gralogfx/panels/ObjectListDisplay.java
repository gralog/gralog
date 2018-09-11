package gralog.gralogfx.panels;

import gralog.dialog.GralogList;
import javafx.scene.image.Image;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

import java.util.ArrayList;

public class    ObjectListDisplay extends VBox
{
    public ObservableList<GralogList> list = FXCollections.observableList(new ArrayList<>());

    public ObjectListDisplay(){

        var table = new TableView<GralogList>();


        var listID = new TableColumn<GralogList, String>("Nr.");
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

        listID.setCellValueFactory(cellData -> cellData.getValue().name);
        objectIDs.setCellValueFactory(cellData -> cellData.getValue().stringData);

        DoubleBinding usedWidth = listID.widthProperty().add(0);
        objectIDs.prefWidthProperty().bind(table.widthProperty().subtract(usedWidth).subtract(45));
        actionCol.prefWidthProperty().bind(table.widthProperty().multiply(0).add(40));
        table.setItems(list);
        table.getColumns().addAll(listID, objectIDs, actionCol);

        getChildren().addAll(table);

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
