/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.firstorderlogic.view;

import gralog.algorithm.ParseError;
import gralog.firstorderlogic.logic.firstorder.parser.FirstOrderParser;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 *
 */
public class FirstOrderSyntaxCheck {

    public static void check(TextField field, Text hint) {
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
}
