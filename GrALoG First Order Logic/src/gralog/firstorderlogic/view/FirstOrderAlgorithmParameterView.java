/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.firstorderlogic.view;

import gralog.firstorderlogic.algorithm.FirstOrderAlgorithmParameter;
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
        FirstOrderSyntaxCheck.check(valueField, hint);
        valueField.textProperty().addListener(e -> {
            FirstOrderSyntaxCheck.check(valueField, hint);
            param.parameter = valueField.getText();
        });
        add(new Label("Formula: "), 0, 0);
        add(valueField, 1, 0);
        add(hint, 0, 1, 2, 1);
    }
}
