package gralog.gralogfx.views;

import java.util.Collection;
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
            StructurePane structurePane = this.structurePane;
            
            treeView.getSelectionModel().selectedItemProperty().addListener( new ChangeListener() {

                   @Override
                   public void changed(ObservableValue observable, Object oldValue, Object newValue)
                   {
                       TreeItem selectedItem = (TreeItem) newValue;
                       Bag bag = (Bag)selectedItem.getValue();
                       structurePane.ClearSelection();
                       structurePane.SelectAll(bag.Nodes);
                   }

                 });

            
            this.add(treeView,1,1);
        }
    }
}
