package gralog.gralogfx.panels;

import gralog.dialog.GralogList;
import gralog.structure.Vertex;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class ObjectListDisplay extends VBox
{
    public ObservableList<GralogList> list = FXCollections.observableList(new ArrayList<>());

    public ObjectListDisplay(){

        var table = new TableView<GralogList>();
        var listID = new TableColumn<GralogList, String>("Nr.");
        var objectIDs = new TableColumn<GralogList, String>("Objects");

        listID.setCellValueFactory(cellData -> cellData.getValue().name);
        objectIDs.setCellValueFactory(cellData -> cellData.getValue().stringData);

        DoubleBinding usedWidth = listID.widthProperty().add(0);
        objectIDs.prefWidthProperty().bind(table.widthProperty().subtract(usedWidth).subtract(3));

        table.setItems(list);
        table.getColumns().addAll(listID, objectIDs);

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
