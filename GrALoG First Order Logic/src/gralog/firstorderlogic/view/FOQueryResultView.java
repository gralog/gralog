package gralog.firstorderlogic.view;

import gralog.firstorderlogic.prover.TreeDecomposition.Bag;
import gralog.firstorderlogic.prover.TreeDecomposition.FOQueryResult;
import gralog.gralogfx.views.GridPaneView;
import gralog.gralogfx.views.ViewDescription;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.beans.value.*;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Hv
 */
@ViewDescription(forClass=gralog.firstorderlogic.prover.TreeDecomposition.FOQueryResult.class)
public class FOQueryResultView extends GridPaneView{

   
    
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
            
            FOQueryResult treedecomp = (FOQueryResult)displayObject;
            
            
            //TextBox tb = new TextBox("hello world");
            
            TreeItem root = new TreeItem("root");
            FillTreeView(root, treedecomp.rootBag);
            TreeView treeView = new TreeView(root);
            
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

            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100);
            this.getColumnConstraints().add(columnConstraints);
    
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100);
            this.getRowConstraints().add(rowConstraints);
    
            this.add(treeView,0,0);
            //this.add(tb, 1, 0);
        }
    }

}
