/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.automaton.regularexpression.parser;

import gralog.algorithm.ParseError;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 */
public class RegularExpressionParserTest {

    private void parseAndCompare(String toParse) throws Exception {
        parseAndCompare(toParse, toParse);
    }

    private void parseAndCompare(String toParse, String result) throws Exception {
        assertEquals(result, RegularExpressionParser.parseString(toParse).toString());
    }

    /**
     * Test that parsing a formula and printing it produces the same result.
     */
    @Test
    public void testParse() throws Exception {
        parseAndCompare("a");
        parseAndCompare("a*");
        parseAndCompare("a | b");

        parseAndCompare("abc");
        parseAndCompare("abc*");
        parseAndCompare("ab | cd | ef");
        parseAndCompare("ab + cd + ef", "ab | cd | ef");
    }

    /**
     * Test that pretty printing omits unnecessary parentheses.
     */
    @Test
    public void testPrettyPrinting() throws Exception {
        parseAndCompare("(ab)(c*)", "abc*");
        parseAndCompare("((ab) | (cd)) | (ef)", "ab | cd | ef");
    }

    private void parseAndFail(String toParse) throws Exception {
        try {
            RegularExpressionParser.parseString(toParse);
        } catch (ParseError e) {
            return; // Everything is fine, this is what we expect.
        }
        throw new Exception("Parsing should have failed on: " + toParse);
    }

    /**
     * Test a few strings that should fail to parse.
     */
    @Test
    public void testParseFails() throws Exception {
        parseAndFail("");
        parseAndFail("*");
        parseAndFail("|");
        parseAndFail("a |");
        parseAndFail("| b");
        parseAndFail("a**");
    }
}
