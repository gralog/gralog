/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx.views;

import gralog.firstorderlogic.formula.FirstOrderFormula;
import gralog.firstorderlogic.formula.Subformula;
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
@ViewDescription(forClass = Subformula.class)
public class SubformulaView extends GridPaneView<Subformula> {

    protected void fillTreeView(TreeItem node, Subformula subformula) {
        node.setValue(subformula);
        for (Subformula b : subformula.children) {
            TreeItem child = new TreeItem();
            node.getChildren().add(child);
            fillTreeView(child, b);
        }
        node.setExpanded(false);
    }

    private static class Cell extends TreeCell<Subformula> {

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
        protected void updateItem(Subformula item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                assignment.setText(
                    FirstOrderFormula.variableAssignmentToString(item.assignment));
                caption.setText(item.subformula);
                caption.setStyle(null);
                if (item.value) {
                    caption.setStyle("-fx-fill:green");
                } else {
                    caption.setStyle("-fx-fill:red");
                }
                setGraphic(hbox);
            }
        }
    }

    @Override
    public void setObject(Subformula treedecomp, Consumer<Boolean> submitPossible) {
        this.getChildren().clear();
        if (treedecomp == null)
            return;

        TreeItem root = new TreeItem("root");

        fillTreeView(root, treedecomp);
        root.setExpanded(true);
        TreeView treeView = new TreeView(root);
        treeView.setStyle("-fx-accent: #c7e1ff");

        treeView.setCellFactory(tv -> new Cell());

        treeView.getSelectionModel()
            .selectedItemProperty()
            .addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                TreeItem selectedItem = (TreeItem) newValue;
                Subformula subformula = (Subformula) selectedItem.getValue();

                structurePane.clearSelection();
                structurePane.selectAll(subformula.validVertices);

                structurePane.clearAnnotations();
                for (Vertex v : (Set<Vertex>) structurePane.getStructure().getVertices()) {
                    String assignment = subformula.getVertexAssignment(v);
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
        if(structurePane != null){
            structurePane.clearAnnotations();
            structurePane.clearSelection();
        }
    }
}
