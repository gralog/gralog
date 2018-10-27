/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.modallogic.parser;

import gralog.parser.SyntaxChecker;

/**
 * A syntax checker for modal logic formulas.
 */
public class ModalLogicSyntaxChecker extends SyntaxChecker {

    @Override
    public SyntaxChecker.Result check(String formula) {
        return checkWith(formula, ModalLogicParser::parseString);
    }

    public static String explanation() {
        return "Conjunction (and): P ∧ Q, P * Q\n"
            + "Disjunction (or): P ∨ Q, P + Q\n"
            + "Negation: ¬P, -P, ~P\n"
            + "Box: □P, []P, [edgelabel]P\n"
            + "Diamond: ◊P, <>P, <edgelabel>P";
    }
}
