/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.npcompleteness.propositionallogic.parser;

import gralog.parser.SyntaxChecker;

/**
 *
 */
public class PropositionalLogicSyntaxChecker extends SyntaxChecker {

    @Override
    public SyntaxChecker.Result check(String formula) {
        return checkWith(formula, PropositionalLogicParser::parseString);
    }

    public static String explanation() {
        return "Conjunction (and): P ∧ Q, P * Q\n"
            + "Disjunction (or): P ∨ Q, P + Q\n"
            + "Negation: ¬P, -P, ~P";
    }
}
