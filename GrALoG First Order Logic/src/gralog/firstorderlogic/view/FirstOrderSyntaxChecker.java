/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.firstorderlogic.view;

import gralog.algorithm.ParseError;
import gralog.algorithm.SyntaxChecker;
import gralog.firstorderlogic.logic.firstorder.parser.FirstOrderParser;

/**
 * A syntax checker for first-order formulas.
 */
public class FirstOrderSyntaxChecker extends SyntaxChecker {

    @Override
    public Result check(String formula) {
        Result result = new Result();
        result.hint = "";
        result.syntaxCorrect = false;
        if (formula.isEmpty())
            result.syntaxCorrect = true;
        else {
            try {
                FirstOrderParser parser = new FirstOrderParser();
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
        return "Edge from x to y: E(x,y)\n"
               + "x has label P: P(x)\n"
               + "Conjunction (and): E(x,y) ∧ E(y,z), E(x,y) * E(y,z)\n"
               + "Disjunction (or): E(x,y) ∨ E(y,z), E(x,y) + E(y,z)\n"
               + "Negation: ¬E(x,y), -E(x,y), ~E(x,y)\n"
               + "For all: ∀x. E(x,x), !x. E(x,x)\n"
               + "Exists: ∃x. E(x,x), ?x. E(x,x)";
    }
}
