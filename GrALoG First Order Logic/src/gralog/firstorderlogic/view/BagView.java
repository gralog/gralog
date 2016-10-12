/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.firstorderlogic.view;

import gralog.firstorderlogic.prover.TreeDecomposition.Bag;
import gralog.gralogfx.views.GridPaneView;
import gralog.gralogfx.views.ViewDescription;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;

import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;

/**
 *
 */
@ViewDescription(
        forClass = gralog.firstorderlogic.prover.TreeDecomposition.Bag.class)
public class BagView extends GridPaneView {

    protected void FillTreeView(TreeItem node, Bag bag) {
        node.setValue(bag);
        for (Bag b : bag.ChildBags) {
            TreeItem child = new TreeItem("bag");
            node.getChildren().add(child);
            FillTreeView(child, b);
        }
        node.setExpanded(false);
    }

    @Override
    public void update() {
        this.getChildren().clear();

        if (displayObject != null) {

            Bag treedecomp = (Bag) displayObject;

            TreeItem root = new TreeItem("root");

            FillTreeView(root, treedecomp);
            root.setExpanded(true);
            TreeView treeView = new TreeView(root);

            treeView.setCellFactory(tv -> {
                return new TreeCell<Bag>() {

                    private final Text assignment;
                    private final Text caption;
                    HBox hbox;

                    {
                        assignment = new Text();
                        caption = new Text();
                        assignment.setStyle("-fx-fill:blue");
                        hbox = new HBox(4, assignment, caption);
                    }

                    @Override
                    protected void updateItem(Bag item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        }
                        else {
                            assignment.setText(item.assignment);
                            caption.setText(item.caption);
                            caption.setStyle(null);
                            if (item.eval) {
                                caption.setStyle("-fx-fill:green");
                            }
                            setGraphic(hbox);
                        }
                    }

                };
            });

            treeView.getSelectionModel()
                    .selectedItemProperty()
                    .addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                        TreeItem selectedItem = (TreeItem) newValue;
                        Bag bag = (Bag) selectedItem.getValue();
                        structurePane.clearSelection();
                        structurePane.selectAll(bag.Nodes);
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
}
