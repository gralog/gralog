/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
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
import gralog.firstorderlogic.algorithm.FirstOrderProverParameters;

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

        Label hintLabel = new Label("Syntax:");
        hintLabel.setMinWidth(USE_PREF_SIZE);
        setConstraints(hintLabel, 0, 2);

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
        setConstraints(hints, 1, 2);

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
        RowConstraints hintRow = new RowConstraints();
        hintRow.setValignment(VPos.TOP);
        getRowConstraints().addAll(new RowConstraints(), textAreaRow, hintRow);

        this.getChildren().addAll(label, recentQueriesLabel, hintLabel,
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
