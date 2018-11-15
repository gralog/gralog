/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.modallogic.parser;

import gralog.parser.ParserTestHelper;
import org.junit.Test;

/**
 *
 */
public class ModalLogicParserTest {

    private final ParserTestHelper p = new ParserTestHelper(ModalLogicParser::parseString);

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

        // ASCII notation
        p.parseAndCompare("-P", "¬P");
        p.parseAndCompare("~P", "¬P");
        p.parseAndCompare("P + Q", "P ∨ Q");
        p.parseAndCompare("P * Q", "P ∧ Q");
        p.parseAndCompare("<>P", "◊P");
        p.parseAndCompare("[]P", "□P");
    }
}
