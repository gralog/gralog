/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.computationtreelogic.parser;

import gralog.parser.SyntaxChecker;

/**
 *
 */
public class ComputationTreeLogicSyntaxChecker extends SyntaxChecker {

    @Override
    public SyntaxChecker.Result check(String formula) {
        return checkWith(formula, ComputationTreeLogicParser::parseString);
    }

    public static String explanation() {
        return "";
    }
}
