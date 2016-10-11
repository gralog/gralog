package gralog.gralogfx.views;

//import java.util.Collection;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.beans.value.*;

import gralog.treedecomposition.*;
import gralog.gralogfx.StructurePane;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;

/**
 * @author viktor
 */

@ViewDescription(forClass=TreeDecomposition.class)
public class TreeDecompositionView extends GridPaneView
{
    
    protected void FillTreeView(TreeItem node, Bag bag)
    {
        node.setValue(bag);
        for(Bag b : bag.ChildBags)
        {
            TreeItem child = new TreeItem("bag");
            node.getChildren().add(child);
            FillTreeView(child, b);
        }
        node.setExpanded(true);
    }
    
    @Override
    public void Update()
    {
        this.getChildren().clear();
        
        if(displayObject != null)
        {
            TreeDecomposition treedecomp = (TreeDecomposition)displayObject;
            
            TreeItem root = new TreeItem("root");
            FillTreeView(root, treedecomp.rootBag);
            TreeView treeView = new TreeView(root);
            
            treeView.getSelectionModel().selectedItemProperty().addListener(
                    (ObservableValue observable, Object oldValue, Object newValue) -> {
                TreeItem selectedItem = (TreeItem) newValue;
                Bag bag = (Bag)selectedItem.getValue();
                structurePane.ClearSelection();
                structurePane.SelectAll(bag.Nodes);
            });

            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100);
            this.getColumnConstraints().add(columnConstraints);
    
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100);
            this.getRowConstraints().add(rowConstraints);
    
            this.add(treeView,0,0);
        }
    }
}
