package gralog.gralogfx.panels;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class ObjectListDisplay extends VBox
{

    public ObjectListDisplay(){

        TableView table = new TableView();

        TableColumn listID = new TableColumn("Nr.");
        TableColumn objectIDs = new TableColumn("Objects");

        table.getColumns().addAll(listID, objectIDs);

        getChildren().addAll(table);
    }


}
