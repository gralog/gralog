/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.modallogic;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class WorldTest {

    /**
     * Test of SatisfiesProposition method, of class World.
     */
    @Test
    public void testSatisfiesProposition() {
        World v = new World();
        v.propositions = "A,B,C";
        assertEquals(true, v.satisfiesProposition("A"));
        assertEquals(true, v.satisfiesProposition("B"));
        assertEquals(true, v.satisfiesProposition("C"));
        assertEquals(false, v.satisfiesProposition("X"));
        assertEquals(false, v.satisfiesProposition("A,B"));
        assertEquals(false, v.satisfiesProposition(""));
    }
}
