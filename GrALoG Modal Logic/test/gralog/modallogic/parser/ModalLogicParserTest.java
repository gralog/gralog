/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.modallogic.parser;

import gralog.modallogic.formula.ModalLogicFormula;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class ModalLogicParserTest {

    private void parseAndCompare(String toParse) throws Exception {
        parseAndCompare(toParse, toParse);
    }

    private void parseAndCompare(String toParse, String result) throws Exception {
        ModalLogicParser parser = new ModalLogicParser();
        assertEquals(result, parser.parseString(toParse).toString());
    }

    /**
     * Test that parsing a formula and printing it produces the same (or
     * equivalent) result.
     */
    @Test
    public void testParse() throws Exception {
        parseAndCompare("abc");
        parseAndCompare("P ∧ Q");
        parseAndCompare("P ∨ Q");
        parseAndCompare("P ∧ Q ∨ R");
        parseAndCompare("(P ∧ Q) ∨ R", "P ∧ Q ∨ R");
        parseAndCompare("P ∧ (Q ∨ R)");

        // Negation
        parseAndCompare("¬P");
        parseAndCompare("¬P ∧ Q");
        parseAndCompare("(¬P) ∧ Q", "¬P ∧ Q");
        parseAndCompare("¬(P ∧ Q)");
        parseAndCompare("¬¬P ∨ Q");

        // Box and diamond
        parseAndCompare("□P");
        parseAndCompare("□P ∧ Q");
        parseAndCompare("(□P) ∧ Q", "□P ∧ Q");
        parseAndCompare("□(P ∧ Q)");
        parseAndCompare("□□P ∨ Q");
        parseAndCompare("[abc]P");

        parseAndCompare("◊P");
        parseAndCompare("◊P ∧ Q");
        parseAndCompare("(◊P) ∧ Q", "◊P ∧ Q");
        parseAndCompare("◊(P ∧ Q)");
        parseAndCompare("◊◊P ∨ Q");
        parseAndCompare("<abc>P");
    }

    /**
     * Test that parsing LaTeX and ASCII notation works as expected.
     */
    @Test
    public void testParseAlternateNotation() throws Exception {
        // LaTeX notation
        parseAndCompare("\\neg P", "¬P");
        parseAndCompare("P \\vee Q", "P ∨ Q");
        parseAndCompare("P \\wedge Q", "P ∧ Q");
        parseAndCompare("\\diamond P", "◊P");
        parseAndCompare("\\box P", "□P");

        // ASCII notation
        parseAndCompare("-P", "¬P");
        parseAndCompare("~P", "¬P");
        parseAndCompare("P + Q", "P ∨ Q");
        parseAndCompare("P * Q", "P ∧ Q");
        parseAndCompare("<>P", "◊P");
        parseAndCompare("[]P", "□P");
    }
}
