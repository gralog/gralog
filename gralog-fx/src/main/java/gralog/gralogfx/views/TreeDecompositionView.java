/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx.views;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.beans.value.*;

import gralog.treedecomposition.*;
import java.util.function.Consumer;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;

/**
 */
@ViewDescription(forClass = TreeDecomposition.class)
public class TreeDecompositionView extends GridPaneView<TreeDecomposition> {

    protected void fillTreeView(TreeItem node, Bag bag) {
        node.setValue(bag);
        for (Bag b : bag.childBags) {
            TreeItem child = new TreeItem("bag");
            node.getChildren().add(child);
            fillTreeView(child, b);
        }
        node.setExpanded(true);
    }

    @Override
    public void setObject(TreeDecomposition treedecomp, Consumer<Boolean> submitPossible) {
        this.getChildren().clear();
        if (treedecomp == null)
            return;

        TreeItem root = new TreeItem("root");
        fillTreeView(root, treedecomp.rootBag);
        TreeView treeView = new TreeView(root);

        treeView.getSelectionModel().selectedItemProperty().addListener(
            (ObservableValue observable, Object oldValue, Object newValue) -> {
                TreeItem selectedItem = (TreeItem) newValue;
                Bag bag = (Bag) selectedItem.getValue();
                structurePane.clearSelection();
                structurePane.selectAll(bag.nodes);
            });

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(100);
        this.getColumnConstraints().add(columnConstraints);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setPercentHeight(100);
        this.getRowConstraints().add(rowConstraints);

        this.add(treeView, 0, 0);
    }
}
