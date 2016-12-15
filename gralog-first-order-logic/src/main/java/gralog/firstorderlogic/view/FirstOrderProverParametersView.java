/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.firstorderlogic.view;

import gralog.firstorderlogic.algorithm.FirstOrderProver;
import gralog.gralogfx.RecentQueries;
import gralog.firstorderlogic.parser.FirstOrderSyntaxChecker;
import gralog.parser.SyntaxChecker;
import gralog.firstorderlogic.algorithm.FirstOrderProverParameters;
import gralog.gralogfx.UIConstants;
import gralog.gralogfx.views.*;
import java.util.function.Consumer;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;

/**
 *
 */
@ViewDescription(forClass = FirstOrderProverParameters.class)
public class FirstOrderProverParametersView
    extends GridPaneView<FirstOrderProverParameters> {

    @Override
    public void setObject(FirstOrderProverParameters params,
        Consumer<Boolean> submitPossible) {
        this.getChildren().clear();
        if (params == null)
            return;
        this.setVgap(8);
        this.setHgap(UIConstants.HBOX_SPACING);

        Label label = new Label("Formula:");
        label.setMinWidth(USE_PREF_SIZE);
        setConstraints(label, 0, 0);

        Label recentQueriesLabel = new Label("Recent queries:");
        recentQueriesLabel.setMinWidth(USE_PREF_SIZE);
        setConstraints(recentQueriesLabel, 0, 1);

        TextField formulaField = new TextField(params.parameter);
        setConstraints(formulaField, 1, 0);
        formulaField.promptTextProperty().set("Please enter a first-order formula");
        formulaField.setPrefWidth(1000);

        ListView<String> recentQueriesList = new ListView<>(
            FXCollections.observableList(RecentQueries.get(FirstOrderProver.class)));
        setConstraints(recentQueriesList, 1, 1);

        // Update the formula when the user selects one of the recent queries.
        recentQueriesList.getSelectionModel()
            .selectedItemProperty()
            .addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)
                -> formulaField.setText(newValue)
            );

        Text hint = new Text();
        syntaxCheck(formulaField, hint, submitPossible);

        HBox hints = new HBox(new Text(FirstOrderSyntaxChecker.explanation()), hint);
        hints.setSpacing(UIConstants.HBOX_SPACING);
        setConstraints(hints, 1, 3);

        params.parameter = formulaField.getText();
        formulaField.textProperty().addListener(e -> {
            syntaxCheck(formulaField, hint, submitPossible);
            params.parameter = formulaField.getText();
        });

        ColumnConstraints textAreaColumn = new ColumnConstraints();
        textAreaColumn.setHgrow(Priority.ALWAYS);
        getColumnConstraints().addAll(new ColumnConstraints(), textAreaColumn);

        RowConstraints textAreaRow = new RowConstraints();
        textAreaRow.setVgrow(Priority.ALWAYS);
        textAreaRow.setValignment(VPos.TOP);
        getRowConstraints().addAll(new RowConstraints(), textAreaRow, new RowConstraints());

        this.getChildren().addAll(label, recentQueriesLabel,
            formulaField, recentQueriesList, hints);
    }

    private void syntaxCheck(TextField valueField, Text hint,
        Consumer<Boolean> submitPossible) {
        FirstOrderSyntaxChecker check = new FirstOrderSyntaxChecker();
        SyntaxChecker.Result syntax = check.check(valueField.getText());
        if (syntax.syntaxCorrect) {
            valueField.setStyle("-fx-text-inner-color: black;");
            submitPossible.accept(true);
        } else {
            valueField.setStyle("-fx-text-inner-color: red;");
            submitPossible.accept(false);
        }
        hint.setText(syntax.hint);
    }
}
