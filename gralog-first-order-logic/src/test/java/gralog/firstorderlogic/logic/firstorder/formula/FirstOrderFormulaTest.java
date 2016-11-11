/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.firstorderlogic.logic.firstorder.formula;

import gralog.algorithm.ParseError;
import gralog.firstorderlogic.logic.firstorder.parser.FirstOrderParser;
import gralog.structure.Vertex;
import java.util.ArrayList;
import java.util.TreeMap;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class FirstOrderFormulaTest {

    public FirstOrderFormulaTest() {
        ArrayList<String> params = new ArrayList<>();
        params.add("x");
        params.add("y");
        relation = new FirstOrderRelation("E", params);
    }

    private Vertex createVertex(String label) {
        Vertex v = new Vertex();
        v.label = label;
        return v;
    }

    /**
     * Test the pretty printing of variable assignments.
     */
    @Test
    public void testVariableAssignmentToString() {
        TreeMap<String, Vertex> assignment = new TreeMap<>();
        assertEquals("{ }", FirstOrderFormula.variableAssignmentToString(assignment));
        assignment.put("x", createVertex("0"));
        assertEquals("{ x↦0 }", FirstOrderFormula.variableAssignmentToString(assignment));
        assignment.put("y", createVertex("1"));
        assertEquals("{ x↦0, y↦1 }", FirstOrderFormula.variableAssignmentToString(assignment));
        assignment.put("z", createVertex("2"));
        assertEquals("{ x↦0, y↦1, z↦2 }", FirstOrderFormula.variableAssignmentToString(assignment));
    }

    /**
     * Test the pretty printing of formulas.
     */
    @Test
    public void testToStringSimple() {
        assertEquals("E(x,y)", relation.toString());
        assertEquals("E(x,y) ∧ E(x,y)", (new FirstOrderAnd(relation, relation)).toString());
        assertEquals("E(x,y) ∨ E(x,y)", (new FirstOrderOr(relation, relation)).toString());
        assertEquals("¬E(x,y)", (new FirstOrderNot(relation)).toString());
    }

    /**
     * Test the pretty printing of nested formulas.
     */
    @Test
    public void testToStringNested() {
        assertEquals("E(x,y) ∧ E(x,y) ∧ E(x,y)", (new FirstOrderAnd(new FirstOrderAnd(relation, relation), relation)).toString());
        assertEquals("E(x,y) ∧ (E(x,y) ∧ E(x,y))", (new FirstOrderAnd(relation, new FirstOrderAnd(relation, relation))).toString());
        assertEquals("E(x,y) ∨ E(x,y) ∨ E(x,y)", (new FirstOrderOr(new FirstOrderOr(relation, relation), relation)).toString());
        assertEquals("E(x,y) ∨ (E(x,y) ∨ E(x,y))", (new FirstOrderOr(relation, new FirstOrderOr(relation, relation))).toString());
        assertEquals("E(x,y) ∨ E(x,y) ∧ E(x,y)", (new FirstOrderOr(relation, new FirstOrderAnd(relation, relation))).toString());
        assertEquals("(E(x,y) ∨ E(x,y)) ∧ E(x,y)", (new FirstOrderAnd(new FirstOrderOr(relation, relation), relation)).toString());
        assertEquals("E(x,y) ∧ E(x,y) ∨ E(x,y)", (new FirstOrderOr(new FirstOrderAnd(relation, relation), relation)).toString());
        assertEquals("E(x,y) ∧ (E(x,y) ∨ E(x,y))", (new FirstOrderAnd(relation, new FirstOrderOr(relation, relation))).toString());
        assertEquals("E(x,y) ∧ (E(x,y) ∨ E(x,y))", (new FirstOrderAnd(relation, new FirstOrderOr(relation, relation))).toString());
    }

    /**
     * Test the pretty printing of quantifiers.
     */
    @Test
    public void testToStringQuantifiers() {
        assertEquals("∀x. ∃y. E(x,y)", (new FirstOrderForall("x", new FirstOrderExists("y", relation))).toString());
        assertEquals("∀x. ∃y. ¬E(x,y)", (new FirstOrderForall("x", new FirstOrderExists("y", new FirstOrderNot(relation)))).toString());
        assertEquals("∀x. ¬∃y. E(x,y)", (new FirstOrderForall("x", new FirstOrderNot(new FirstOrderExists("y", relation)))).toString());
        assertEquals("¬∀x. ∃y. E(x,y)", (new FirstOrderNot(new FirstOrderForall("x", new FirstOrderExists("y", relation)))).toString());
        assertEquals("∀x. E(x,y) ∧ E(x,y)", (new FirstOrderForall("x", new FirstOrderAnd(relation, relation))).toString());
        assertEquals("∀x. E(x,y) ∨ E(x,y)", (new FirstOrderForall("x", new FirstOrderOr(relation, relation))).toString());
        assertEquals("(∀x. E(x,y)) ∨ E(x,y)", (new FirstOrderOr(new FirstOrderForall("x", relation), relation)).toString());
        assertEquals("(∀x. E(x,y)) ∧ E(x,y)", (new FirstOrderAnd(new FirstOrderForall("x", relation), relation)).toString());
        assertEquals("E(x,y) ∨ ∀x. E(x,y)", (new FirstOrderOr(relation, new FirstOrderForall("x", relation))).toString());
        assertEquals("E(x,y) ∧ ∀x. E(x,y)", (new FirstOrderAnd(relation, new FirstOrderForall("x", relation))).toString());
    }

    private void parseAndCompare(String toParse) throws Exception {
        parseAndCompare(toParse, toParse);
    }

    private void parseAndCompare(String toParse, String result) throws Exception {
        assertEquals(result, FirstOrderParser.parseString(toParse).toString());
    }

    /**
     * Test that parsing a formula and printing it produces the same (or
     * equivalent) result.
     */
    @Test
    public void testParse() throws Exception {
        parseAndCompare("¬E(x,y)");
        // Check that ∧ and ∨ are left-associative.
        parseAndCompare("E(x,y) ∧ E(y,x) ∧ E(z,z)");
        parseAndCompare("E(x,y) ∨ E(y,x) ∨ E(z,z)");
        parseAndCompare("(E(x,y) ∧ E(y,x)) ∧ E(z,z)", "E(x,y) ∧ E(y,x) ∧ E(z,z)");
        parseAndCompare("(E(x,y) ∨ E(y,x)) ∨ E(z,z)", "E(x,y) ∨ E(y,x) ∨ E(z,z)");
        // Check overriding the left-associativity with parentheses.
        parseAndCompare("E(x,y) ∧ (E(y,x) ∧ E(z,z))");
        parseAndCompare("E(x,y) ∨ (E(y,x) ∨ E(z,z))");

        // Check quantifiers.
        parseAndCompare("∀x. ∃y. E(x,y)");
        parseAndCompare("E(x,y) ∧ ∃y. E(y,y)");
        parseAndCompare("E(x,y) ∨ ∃y. E(y,y)");
        parseAndCompare("E(x,y) ∨ (∀x. ∃y. E(y,y) ∧ E(z,z)) ∨ E(x,x)");
        parseAndCompare("E(x,y) ∧ E(x,y) ∨ ∃y. E(y,y)");
        parseAndCompare("E(x,y) ∨ E(x,y) ∧ ∃y. E(y,y)");
        parseAndCompare("∀x.∃y.E(x,y)", "∀x. ∃y. E(x,y)");
        // Check that the dot after the quantifiers is optional.
        parseAndCompare("∀x∃y E(x,y)", "∀x. ∃y. E(x,y)");
        // Check that quantifiers go as far as possible.
        parseAndCompare("∀x. E(x,y) ∧ E(x,y)");
        parseAndCompare("∀x. E(x,y) ∨ E(x,y)");
        parseAndCompare("∀x. (E(x,x) ∧ E(y,y))", "∀x. E(x,x) ∧ E(y,y)");
        parseAndCompare("(∀x. E(x,x)) ∧ E(y,y)", "(∀x. E(x,x)) ∧ E(y,y)");
        parseAndCompare("∀x. ¬E(x,y) ∨ E(x,y)");
        parseAndCompare("(∀x. ¬E(x,y)) ∨ E(x,y)");
        parseAndCompare("∀x. ¬(E(x,y) ∨ E(x,y))");
        parseAndCompare("∀x. (¬E(x,y) ∨ E(x,y))", "∀x. ¬E(x,y) ∨ E(x,y)");

        // Check negations with quantifiers.
        parseAndCompare("¬∀x¬∃y¬E(x,x) ∧ E(y,y)", "¬∀x. ¬∃y. ¬E(x,x) ∧ E(y,y)");
        parseAndCompare("¬∀x¬∃y¬E(x,x) ∧ E(y,y)", "¬∀x. ¬∃y. ¬E(x,x) ∧ E(y,y)");

        // Test excessive parentheses.
        parseAndCompare("((∀x. ((E(x,y)))))", "∀x. E(x,y)");
    }

    /**
     * Test that parsing LaTeX and ASCII notation works as expected.
     */
    @Test
    public void testParseAlternateNotation() throws Exception {
        // LaTeX notation
        parseAndCompare("\\neg E(x,y)", "¬E(x,y)");
        parseAndCompare("E(x,x) \\vee E(y,y)", "E(x,x) ∨ E(y,y)");
        parseAndCompare("E(x,x) \\wedge E(y,y)", "E(x,x) ∧ E(y,y)");
        parseAndCompare("\\forall x. E(x,y)", "∀x. E(x,y)");
        parseAndCompare("\\exists x. E(x,y)", "∃x. E(x,y)");

        // ASCII notation
        parseAndCompare("-E(x,y)", "¬E(x,y)");
        parseAndCompare("~E(x,y)", "¬E(x,y)");
        parseAndCompare("E(x,x) + E(y,y)", "E(x,x) ∨ E(y,y)");
        parseAndCompare("E(x,x) * E(y,y)", "E(x,x) ∧ E(y,y)");
        parseAndCompare("!x. E(x,y)", "∀x. E(x,y)");
        parseAndCompare("?x. E(x,y)", "∃x. E(x,y)");
    }

    private void parseAndFail(String toParse) throws Exception {
        try {
            FirstOrderParser.parseString(toParse);
        }
        catch(ParseError e) {
            return; // Everything is fine, this is what we expect.
        }
        throw new Exception("Parsing should have failed on: " + toParse);
    }

    /**
     * Test a few strings that should fail to parse.
     */
    @Test
    public void testParseFails() throws Exception {
        parseAndFail("E(x,y) ∧∧ E(x,y)");
        parseAndFail("E(x,y) ∨∨ E(x,y)");
        parseAndFail("E(x,y) !∧ E(x,y)");
        parseAndFail("∧ E(x,y)");
        parseAndFail("∨ E(x,y)");
        parseAndFail("∀ E(x,y)");
        parseAndFail("∀∀x. E(x,y)");
        parseAndFail("(E(x,y)");
        parseAndFail("E(x,y))");
    }

    private final FirstOrderFormula relation;
}
