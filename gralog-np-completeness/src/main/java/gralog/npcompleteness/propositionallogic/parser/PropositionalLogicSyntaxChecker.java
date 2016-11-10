/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.npcompleteness.propositionallogic.parser;

import gralog.parser.SyntaxChecker;
import gralog.npcompleteness.propositionallogic.parser.PropositionalLogicParser;

/**
 *
 */
public class PropositionalLogicSyntaxChecker extends SyntaxChecker {

    @Override
    public SyntaxChecker.Result check(String formula) {
        return checkWith(formula, (new PropositionalLogicParser())::parseString);
    }

    public static String explanation() {
        return "Conjunction (and): P ∧ Q, P * Q\n"
               + "Disjunction (or): P ∨ Q, P + Q\n"
               + "Negation: ¬P, -P, ~P";
    }
}
