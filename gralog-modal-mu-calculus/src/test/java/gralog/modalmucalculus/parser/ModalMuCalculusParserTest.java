/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.modalmucalculus.parser;

import gralog.parser.ParserTestHelper;
import org.junit.Test;

/**
 *
 */
public class ModalMuCalculusParserTest {

    private final ParserTestHelper p = new ParserTestHelper(ModalMuCalculusParser::parseString);

    /**
     * Test that parsing a formula and printing it produces the same (or
     * equivalent) result.
     */
    @Test
    public void testParse() throws Exception {
        p.parseAndCompare("abc");
        p.parseAndCompare("P ∧ Q");
        p.parseAndCompare("P ∨ Q");
        p.parseAndCompare("P ∧ Q ∨ R");
        p.parseAndCompare("(P ∧ Q) ∨ R", "P ∧ Q ∨ R");
        p.parseAndCompare("P ∧ (Q ∨ R)");

        // Negation
        p.parseAndCompare("¬P");
        p.parseAndCompare("¬P ∧ Q");
        p.parseAndCompare("(¬P) ∧ Q", "¬P ∧ Q");
        p.parseAndCompare("¬(P ∧ Q)");
        p.parseAndCompare("¬¬P ∨ Q");

        // Box and diamond
        p.parseAndCompare("□P");
        p.parseAndCompare("□P ∧ Q");
        p.parseAndCompare("(□P) ∧ Q", "□P ∧ Q");
        p.parseAndCompare("□(P ∧ Q)");
        p.parseAndCompare("□□P ∨ Q");
        p.parseAndCompare("[abc]P");

        p.parseAndCompare("◊P");
        p.parseAndCompare("◊P ∧ Q");
        p.parseAndCompare("(◊P) ∧ Q", "◊P ∧ Q");
        p.parseAndCompare("◊(P ∧ Q)");
        p.parseAndCompare("◊◊P ∨ Q");
        p.parseAndCompare("<abc>P");
    }

    /**
     * Test that parsing LaTeX and ASCII notation works as expected.
     */
    @Test
    public void testParseAlternateNotation() throws Exception {
        // LaTeX notation
        p.parseAndCompare("\\neg P", "¬P");
        p.parseAndCompare("P \\vee Q", "P ∨ Q");
        p.parseAndCompare("P \\wedge Q", "P ∧ Q");
        p.parseAndCompare("\\diamond P", "◊P");
        p.parseAndCompare("\\box P", "□P");
        p.parseAndCompare("\\mu X. P", "μX. P");
        p.parseAndCompare("\\nu X. P", "νX. P");

        // ASCII notation
        p.parseAndCompare("-P", "¬P");
        p.parseAndCompare("~P", "¬P");
        p.parseAndCompare("P + Q", "P ∨ Q");
        p.parseAndCompare("P * Q", "P ∧ Q");
        p.parseAndCompare("<>P", "◊P");
        p.parseAndCompare("[]P", "□P");
        p.parseAndCompare("mu X. P", "μX. P");
        p.parseAndCompare("nu X. P", "νX. P");
    }

    /**
     * Test parsing of fixpoint operators.
     */
    @Test
    public void testParseFixpoints() throws Exception {
        p.parseAndCompare("μX. P");
        p.parseAndCompare("νX. P");
        p.parseAndCompare("μX. P ∧ Q");
        p.parseAndCompare("μX. (P ∧ Q)", "μX. P ∧ Q");
        p.parseAndCompare("P ∧ μX. Q");
        p.parseAndCompare("P ∧ ¬μX. Q ∧ R");
        p.parseAndCompare("¬νX. P ∧ μY. Q ∨ R");
        p.parseAndCompare("¬(νX. (P ∧ (μY. (Q ∨ R))))", "¬νX. P ∧ μY. Q ∨ R");
    }
}
