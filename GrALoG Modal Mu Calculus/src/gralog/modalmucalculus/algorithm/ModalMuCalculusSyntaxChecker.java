/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */

package gralog.modalmucalculus.algorithm;

import gralog.algorithm.ParseError;
import gralog.algorithm.SyntaxChecker;
import gralog.modalmucalculus.parser.ModalMuCalculusParser;

/**
 * A syntax checker for modal mu calculusformulas.
 */
public class ModalMuCalculusSyntaxChecker extends SyntaxChecker {

    @Override
    public SyntaxChecker.Result check(String formula) {
        SyntaxChecker.Result result = new SyntaxChecker.Result();
        result.hint = "";
        result.syntaxCorrect = false;
        if (formula.isEmpty())
            result.syntaxCorrect = true;
        else {
            try {
                ModalMuCalculusParser parser = new ModalMuCalculusParser();
                result.syntaxCorrect = parser.parseString(formula) != null;
            }
            catch (ParseError e) {
                result.hint = e.getMessage();
            }
            catch (Exception e) {
                result.hint = "Parse error";
            }
        }
        return result;
    }
}
