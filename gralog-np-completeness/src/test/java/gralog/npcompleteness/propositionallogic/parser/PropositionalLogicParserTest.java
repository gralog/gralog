/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.npcompleteness.propositionallogic.parser;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 */
public class PropositionalLogicParserTest {
    private void parseAndCompare(String toParse) throws Exception {
        parseAndCompare(toParse, toParse);
    }

    private void parseAndCompare(String toParse, String result) throws Exception {
        assertEquals(result, PropositionalLogicParser.parseString(toParse).toString());
    }

    /**
     * Test that parsing a formula and printing it produces the same (or
     * equivalent) result.
     */
    @Test
    public void testParse() throws Exception {
        parseAndCompare("P");
        parseAndCompare("¬P");
        parseAndCompare("P ∧ Q");
        parseAndCompare("P ∨ Q");
        parseAndCompare("P ∧ Q ∨ R");
        parseAndCompare("P ∨ Q ∧ R");
        parseAndCompare("P ∧ (Q ∨ R)");
        parseAndCompare("(P ∨ Q) ∧ R");
        parseAndCompare("¬P ∧ Q ∧ R");
        parseAndCompare("¬P ∧ (Q ∧ R)");
        parseAndCompare("((¬P) ∧ Q) ∧ R", "¬P ∧ Q ∧ R");
        parseAndCompare("¬P ∨ Q ∨ R");
        parseAndCompare("¬P ∨ (Q ∨ R)");
        parseAndCompare("((¬P) ∨ Q) ∨ R", "¬P ∨ Q ∨ R");
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

        // ASCII notation
        parseAndCompare("-P", "¬P");
        parseAndCompare("~P", "¬P");
        parseAndCompare("P + Q", "P ∨ Q");
        parseAndCompare("P * Q", "P ∧ Q");
    }
}
