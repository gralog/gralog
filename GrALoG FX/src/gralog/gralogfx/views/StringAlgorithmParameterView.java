/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.gralogfx.views;

import gralog.algorithm.StringAlgorithmParameter;
import gralog.algorithm.SyntaxChecker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * Asks the user for string with live syntax checking.
 */
@ViewDescription(forClass = StringAlgorithmParameter.class)
public class StringAlgorithmParameterView extends GridPaneView {

    @Override
    public void update() {
        this.getChildren().clear();
        if (displayObject == null)
            return;

        StringAlgorithmParameter param = (StringAlgorithmParameter) displayObject;
        TextField valueField = new TextField(param.parameter);
        valueField.setPrefWidth(1000);

        Text hint = new Text();

        syntaxCheck(valueField, hint);

        valueField.textProperty().addListener(e -> {
            param.parameter = valueField.getText();
            syntaxCheck(valueField, hint);
        });
        add(new Label(param.getLabel() + ": "), 0, 0);
        add(valueField, 1, 0);
        add(hint, 0, 1, 2, 1);
    }

    private void syntaxCheck(TextField valueField, Text hint) {
        StringAlgorithmParameter param = (StringAlgorithmParameter) displayObject;
        SyntaxChecker.Result syntax = param.syntaxCheck();
        if (syntax.syntaxCorrect)
            valueField.setStyle("-fx-text-inner-color: black;");
        else
            valueField.setStyle("-fx-text-inner-color: red;");
        hint.setText(syntax.hint);
    }
}
