/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.modalmucalculus.parser;

import gralog.parser.SyntaxChecker;
import gralog.modallogic.parser.ModalLogicSyntaxChecker;

/**
 * A syntax checker for modal mu calculus formulas.
 */
public class ModalMuCalculusSyntaxChecker extends SyntaxChecker {

    @Override
    public SyntaxChecker.Result check(String formula) {
        return checkWith(formula, ModalMuCalculusParser::parseString);
    }

    public static String explanation() {
        return ModalLogicSyntaxChecker.explanation() + "\n"
            + "mu: μX. P, mu X. P\n"
            + "nu: νX. P, nu X. P";
    }
}
