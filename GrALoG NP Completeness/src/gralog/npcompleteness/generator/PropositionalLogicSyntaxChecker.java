/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.npcompleteness.generator;

import gralog.algorithm.ParseError;
import gralog.algorithm.SyntaxChecker;
import gralog.npcompleteness.propositionallogic.parser.PropositionalLogicParser;

/**
 *
 */
public class PropositionalLogicSyntaxChecker extends SyntaxChecker {
    @Override
    public SyntaxChecker.Result check(String formula) {
        SyntaxChecker.Result result = new SyntaxChecker.Result();
        result.hint = "";
        result.syntaxCorrect = false;
        if (formula.isEmpty())
            result.syntaxCorrect = true;
        else {
            try {
                PropositionalLogicParser parser = new PropositionalLogicParser();
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
               + "Negation: ¬P, -P, ~P";
    }
}
