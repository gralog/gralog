/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
