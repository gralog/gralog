/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.firstorderlogic.view;

import gralog.algorithm.ParseError;
import gralog.firstorderlogic.algorithm.FirstOrderAlgorithmParameter;
import gralog.firstorderlogic.logic.firstorder.parser.FirstOrderParser;
import gralog.gralogfx.views.GridPaneView;
import gralog.gralogfx.views.ViewDescription;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * Asks the user for a first-order formula with live syntax checking.
 */
@ViewDescription(forClass = FirstOrderAlgorithmParameter.class)
public class FirstOrderAlgorithmParameterView extends GridPaneView {

    private void syntaxCheck(TextField field, Text hint) {
        String formula = field.getText();
        hint.setText("");
        boolean success = false;
        if (formula.isEmpty())
            success = true;
        else {
            try {
                FirstOrderParser parser = new FirstOrderParser();
                success = parser.parseString(formula) != null;
            }
            catch (ParseError e) {
                hint.setText(e.getMessage());
            }
            catch (Exception e) {
                hint.setText("Parse error");
            }
        }
        if (success)
            field.setStyle("-fx-text-inner-color: black;");
        else
            field.setStyle("-fx-text-inner-color: red;");
    }

    @Override
    public void update() {
        this.getChildren().clear();
        if (displayObject == null)
            return;

        FirstOrderAlgorithmParameter param = (FirstOrderAlgorithmParameter) displayObject;
        TextField valueField = new TextField(param.parameter);
        valueField.promptTextProperty().set("Please enter a first-order formula");
        valueField.setPrefWidth(1000);
        Text hint = new Text();
        syntaxCheck(valueField, hint);
        valueField.textProperty().addListener(e -> {
            try {
                syntaxCheck(valueField, hint);
                param.parameter = valueField.getText();
                requestRedraw();
            }
            catch (Exception ex) {
            }
        });
        add(new Label("Formula: "), 0, 0);
        add(valueField, 1, 0);
        add(hint, 0, 1, 2, 1);
    }
}
