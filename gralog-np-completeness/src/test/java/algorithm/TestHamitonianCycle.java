package algorithm;

import gralog.algorithm.CycleParameters;
import gralog.generator.Cycle;
import gralog.structure.Structure;
import gralog.structure.UndirectedGraph;
import org.junit.Test;

import static gralog.npcompleteness.algorithm.HamiltonianCycle.findHamiltonianCycle;
import static org.junit.Assert.*;
import gralog.npcompleteness.algorithm.HamiltonianCycle.*;


import java.util.Arrays;

public class TestHamitonianCycle {
    Structure c4;

    public TestHamitonianCycle(){
        c4 = (new Cycle()).generate(
                new CycleParameters(Arrays.asList("20","undirected")));
    }

    @Test
    public void testHamiltonian(){

        assertEquals(findHamiltonianCycle((UndirectedGraph) c4), c4.getEdges());
    }
}
