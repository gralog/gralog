/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.modallogic;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author christoph
 */
public class WorldTest {
    /**
     * Test of SatisfiesProposition method, of class World.
     */
    @Test
    public void testSatisfiesProposition() {
        World v = new World();
        v.Propositions = "A,B,C";
        assertEquals(true, v.satisfiesProposition("A"));
        assertEquals(true, v.satisfiesProposition("B"));
        assertEquals(true, v.satisfiesProposition("C"));
        assertEquals(false, v.satisfiesProposition("X"));
        assertEquals(false, v.satisfiesProposition("A,B"));
        assertEquals(false, v.satisfiesProposition(""));
    }
}
