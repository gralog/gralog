/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.automaton.regularexpression.parser;

import gralog.parser.SyntaxChecker;

/**
 *
 * @author christoph
 */
public class RegularExpressionSyntaxChecker extends SyntaxChecker {

    @Override
    public SyntaxChecker.Result check(String formula) {
        return checkWith(formula, RegularExpressionParser::parseString);
    }

    public static String explanation() {
        return "Simple strings: a, b, ab\n"
            + "Alternation: a | b, a + b\n"
            + "Kleene star (zero or more matches): a*, (ab)*";
    }
}
