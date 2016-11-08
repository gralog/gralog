/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */

package gralog.modallogic;

import gralog.algorithm.ParseError;
import gralog.algorithm.SyntaxChecker;
import gralog.modallogic.parser.ModalLogicParser;

/**
 * A syntax checker for modal logic formulas.
 */
public class ModalLogicSyntaxChecker extends SyntaxChecker {

    @Override
    public SyntaxChecker.Result check(String formula) {
        SyntaxChecker.Result result = new SyntaxChecker.Result();
        result.hint = "";
        result.syntaxCorrect = false;
        if (formula.isEmpty())
            result.syntaxCorrect = true;
        else {
            try {
                ModalLogicParser parser = new ModalLogicParser();
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

    public static String explanation() {
        return "Conjunction (and): P ∧ Q, P * Q\n"
               + "Disjunction (or): P ∨ Q, P + Q\n"
               + "Negation: ¬P, -P, ~P\n"
               + "Box: □P, []P, [edgelabel]P\n"
               + "Diamond: ◊P, <>P, <edgelabel>P";
    }
}
