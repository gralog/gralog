/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.modallogic.formula;

import gralog.modallogic.Action;
import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class ModalLogicFormulaTest {

    private static KripkeStructure structure;
    private static World a, b, c, d;

    // d(P,R) ->  a(P)  ->  b(P)
    //             |__> c(Q)
    @BeforeClass
    public static void setUpClass() {
        structure = new KripkeStructure();

        a = addVertex("P","a");
        b = addVertex("P","b");
        c = addVertex("Q", "c");
        d = addVertex("P,R", "d");


        structure.addEdge(a, b);
        structure.addEdge(a, c);
        structure.addEdge(d, a);
    }

    private static World addVertex(String propositions, String label) {
        World v = structure.addVertex();
        v.propositions = propositions;
        v.setLabel(label);
        return v;
    }

    @Test
    public void testTop() {
        ModalLogicFormula formula = new ModalLogicTop();
        HashSet<World> expResult = new HashSet<>(Arrays.asList(a, b, c, d));
        assertEquals(expResult, formula.interpretation(structure));
    }

    @Test
    public void testBottom() {
        ModalLogicFormula formula = new ModalLogicBottom();
        HashSet<World> expResult = new HashSet<>();
        assertEquals(expResult, formula.interpretation(structure));
    }

    @Test
    public void testProposition() {
        ModalLogicFormula formula = new ModalLogicProposition("P");
        HashSet<World> expResult = new HashSet<>(Arrays.asList(a, b, d));
        assertEquals(expResult, formula.interpretation(structure));
    }

    @Test
    public void testNot() {
        ModalLogicFormula formula = new ModalLogicNot(new ModalLogicProposition("P"));
        HashSet<World> expResult = new HashSet<>(Arrays.asList(c));
        assertEquals(expResult, formula.interpretation(structure));
    }

    @Test
    public void testOr() {
        ModalLogicFormula formula = new ModalLogicOr(
            new ModalLogicProposition("P"), new ModalLogicProposition("R"));
        HashSet<World> expResult = new HashSet<>(Arrays.asList(a, b, d));
        assertEquals(expResult, formula.interpretation(structure));
    }

    @Test
    public void testAnd() {
        ModalLogicFormula formula = new ModalLogicAnd(
            new ModalLogicProposition("P"), new ModalLogicProposition("R"));
        HashSet<World> expResult = new HashSet<>(Arrays.asList(d));
        assertEquals(expResult, formula.interpretation(structure));
    }

    @Test
    public void testDiamond() {
        ModalLogicFormula formula = new ModalLogicDiamond(new ModalLogicProposition("P"));
        // All nodes where P is true on at least one successor.
        HashSet<World> expResult = new HashSet<>(Arrays.asList(a, d));
        HashSet<Integer> expResultId = new HashSet<>();
        for (var w : expResult)
            expResultId.add(w.id);
        HashSet<Integer> resultId = new HashSet<>();
        for (var w : formula.interpretation(structure))
            resultId.add(w.id);
        assertEquals(expResultId, resultId);
    }

    @Test
    public void testBox() {
        ModalLogicFormula formula = new ModalLogicBox(new ModalLogicProposition("P"));
        // All nodes where P is true on all successors.
        HashSet<World> expResult = new HashSet<>(Arrays.asList(b, c, d));
        assertEquals(expResult, formula.interpretation(structure));
    }
}
