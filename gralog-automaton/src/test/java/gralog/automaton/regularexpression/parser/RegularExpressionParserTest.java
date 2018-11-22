/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.automaton.regularexpression.parser;

import gralog.parser.ParserTestHelper;
import org.junit.Test;

/**
 *
 */
public class RegularExpressionParserTest {

    private final ParserTestHelper p = new ParserTestHelper(RegularExpressionParser::parseString);

    /**
     * Test that parsing a formula and printing it produces the same result.
     */
    @Test
    public void testParse() throws Exception {
        p.parseAndCompare("a");
        p.parseAndCompare("a*");
        p.parseAndCompare("a | b");

        p.parseAndCompare("abc");
        p.parseAndCompare("abc*");
        p.parseAndCompare("ab | cd | ef");
        p.parseAndCompare("ab + cd + ef", "ab | cd | ef");
    }

    /**
     * Test that pretty printing omits unnecessary parentheses.
     */
    @Test
    public void testPrettyPrinting() throws Exception {
        p.parseAndCompare("(ab)(c*)", "abc*");
        p.parseAndCompare("((ab) | (cd)) | (ef)", "ab | cd | ef");
    }

    /**
     * Test a few strings that should fail to parse.
     */
    @Test
    public void testParseFails() throws Exception {
        p.parseAndFail("");
        p.parseAndFail("*");
        p.parseAndFail("|");
        p.parseAndFail("a |");
        p.parseAndFail("| b");
        p.parseAndFail("a**");
    }
}
