/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.firstorderlogic.view;

import gralog.firstorderlogic.algorithm.FirstOrderAlgorithmParameter;
import gralog.firstorderlogic.logic.firstorder.parser.FirstOrderParser;
import gralog.gralogfx.views.GridPaneView;
import gralog.gralogfx.views.ViewDescription;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 *
 */
@ViewDescription(forClass = FirstOrderAlgorithmParameter.class)
public class FirstOrderAlgorithmParameterView extends GridPaneView {

    private void syntaxCheck(TextField field) {
        String formula = field.getText();
        boolean success = false;
        try {
            FirstOrderParser parser = new FirstOrderParser();
            success = parser.parseString(formula) != null;
        }
        catch(Exception e) {}
        if(success)
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
        syntaxCheck(valueField);
        valueField.textProperty().addListener(e -> {
            try {
                syntaxCheck(valueField);
                param.parameter = valueField.getText();
                requestRedraw();
            }
            catch (Exception ex) {
            }
        });
        add(new Label("Formula: "), 0, 0);
        add(valueField, 1, 0);
    }
}
