/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx.views;

import gralog.algorithm.StringAlgorithmParameter;
import gralog.algorithm.StringAlgorithmParametersList;
import gralog.parser.SyntaxChecker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Asks the user for string with live syntax checking.
 */
@ViewDescription(forClass = StringAlgorithmParametersList.class)
public class StringAlgorithmParametersListView extends GridPaneView<StringAlgorithmParametersList> {

    @Override
    public void setObject(StringAlgorithmParametersList param,
        Consumer<Boolean> submitPossible) {
        this.getChildren().clear();
        if (param == null)
            return;

        for (int i = 0; i < param.parameters.size(); i++) {
            TextField valueField = new TextField(param.parameters.get(i));
            valueField.setPrefWidth(1000);

            Text hint = new Text();

            syntaxCheck(param, valueField, hint, submitPossible);

            // THIS SHOULD WORK!!!

//            AtomicReference<String> parameter = new AtomicReference<>(param.parameters.get(i));
//            valueField.textProperty().addListener(e -> {
//                parameter = valueField.getText();
//                syntaxCheck(param, valueField, hint, submitPossible);
//            });
//            add(new Label(param.getLabel() + ": "), 0, 0);
//            add(valueField, 1, 0);
//            add(hint, 0, 1, 2, 1);
//
//            String explanation = param.getExplanation();
//            if (!explanation.isEmpty())
//                add(new Text(explanation), 0, 2, 2, 1);
        }
    }

    private void syntaxCheck(StringAlgorithmParametersList param,
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
