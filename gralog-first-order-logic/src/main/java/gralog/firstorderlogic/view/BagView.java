/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.firstorderlogic.view;

import gralog.firstorderlogic.logic.firstorder.formula.FirstOrderFormula;
import gralog.firstorderlogic.prover.TreeDecomposition.Bag;
import gralog.gralogfx.views.GridPaneView;
import gralog.gralogfx.views.ViewDescription;
import gralog.structure.Vertex;
import java.util.Set;
import java.util.function.Consumer;

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
public class BagView extends GridPaneView<Bag> {

    protected void fillTreeView(TreeItem node, Bag bag) {
        node.setValue(bag);
        for (Bag b : bag.childBags) {
            TreeItem child = new TreeItem("bag");
            node.getChildren().add(child);
            fillTreeView(child, b);
        }
        node.setExpanded(false);
    }

    private static class Cell extends TreeCell<Bag> {

        private final Text assignment;
        private final Text caption;
        HBox hbox;

        Cell() {
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
            } else {
                assignment.setText(
                    FirstOrderFormula.variableAssignmentToString(item.assignment));
                caption.setText(item.caption);
                caption.setStyle(null);
                if (item.eval) {
                    caption.setStyle("-fx-fill:green");
                }
                setGraphic(hbox);
            }
        }
    }

    @Override
    public void setObject(Bag treedecomp, Consumer<Boolean> submitPossible) {
        this.getChildren().clear();
        if (treedecomp == null)
            return;

        TreeItem root = new TreeItem("root");

        fillTreeView(root, treedecomp);
        root.setExpanded(true);
        TreeView treeView = new TreeView(root);

        treeView.setCellFactory(tv -> new Cell());

        treeView.getSelectionModel()
            .selectedItemProperty()
            .addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                TreeItem selectedItem = (TreeItem) newValue;
                Bag bag = (Bag) selectedItem.getValue();

                structurePane.clearSelection();
                structurePane.selectAll(bag.nodes);

                structurePane.clearAnnotations();
                for (Vertex v : (Set<Vertex>) structurePane.getStructure().getVertices()) {
                    String assignment = bag.getVertexAssignment(v);
                    if (!assignment.equals(""))
                        structurePane.annotate(v, assignment);
                }
            });

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(100);
        this.getColumnConstraints().add(columnConstraints);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setPercentHeight(100);
        this.getRowConstraints().add(rowConstraints);

        this.add(treeView, 0, 0);
    }

    @Override
    public void onClose() {
        structurePane.clearAnnotations();
        structurePane.clearSelection();
    }
}
