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
        assertEquals(true, v.SatisfiesProposition("A"));
        assertEquals(true, v.SatisfiesProposition("B"));
        assertEquals(true, v.SatisfiesProposition("C"));
        assertEquals(false, v.SatisfiesProposition("X"));
        assertEquals(false, v.SatisfiesProposition("A,B"));
        assertEquals(false, v.SatisfiesProposition(""));
    }
}
