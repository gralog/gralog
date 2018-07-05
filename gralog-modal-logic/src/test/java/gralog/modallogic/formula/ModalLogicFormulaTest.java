/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
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

    @BeforeClass
    public static void setUpClass() {
        structure = new KripkeStructure();

        a = addVertex("P");
        b = addVertex("P");
        c = addVertex("Q");
        d = addVertex("P,R");

        addEdge(a, b);
        addEdge(a, c);
        addEdge(d, a);
    }

    private static World addVertex(String propositions) {
        World v = structure.createVertex();
        v.propositions = propositions;
        structure.addVertex(v);
        return v;
    }

    private static Action addEdge(World source, World target) {
        Action e = structure.createEdge(null);
        e.setSource(source);
        e.setTarget(target);
        structure.addEdge(e);
        return e;
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
        assertEquals(expResult, formula.interpretation(structure));
    }

    @Test
    public void testBox() {
        ModalLogicFormula formula = new ModalLogicBox(new ModalLogicProposition("P"));
        // All nodes where P is true on all successors.
        HashSet<World> expResult = new HashSet<>(Arrays.asList(b, c, d));
        assertEquals(expResult, formula.interpretation(structure));
    }
}
