/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx.views;

import gralog.algorithm.StringAlgorithmParameter;
import gralog.parser.SyntaxChecker;
import java.util.function.Consumer;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * Asks the user for string with live syntax checking.
 */
@ViewDescription(forClass = StringAlgorithmParameter.class)
public class StringAlgorithmParameterView extends GridPaneView<StringAlgorithmParameter> {

    @Override
    public void setObject(StringAlgorithmParameter param,
        Consumer<Boolean> submitPossible) {
        this.getChildren().clear();
        if (param == null)
            return;

        TextField valueField = new TextField(param.parameter);
        valueField.setPrefWidth(100);
        valueField.setMaxWidth(1000);

        Text hint = new Text();

        syntaxCheck(param, valueField, hint, submitPossible);

        valueField.textProperty().addListener(e -> {
            param.parameter = valueField.getText();
            syntaxCheck(param, valueField, hint, submitPossible);
        });
        add(new Label(param.getLabel() + ": "), 0, 0);
        add(valueField, 1, 0);
        add(hint, 0, 1, 2, 1);

        String explanation = param.getExplanation();
        if (!explanation.isEmpty())
            add(new Text(explanation), 0, 2, 2, 1);
    }

    private void syntaxCheck(StringAlgorithmParameter param,
        TextField valueField, Text hint, Consumer<Boolean> submitPossible) {
        SyntaxChecker.Result syntax = param.syntaxCheck();
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
