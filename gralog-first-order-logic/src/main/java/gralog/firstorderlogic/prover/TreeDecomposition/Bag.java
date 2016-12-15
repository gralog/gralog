/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.firstorderlogic.prover.TreeDecomposition;

import gralog.firstorderlogic.formula.FirstOrderFormula;
import gralog.structure.Vertex;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 */
public class Bag {

    public String caption = "";
    public Map<String, Vertex> assignment = new HashMap<>();
    public Boolean eval = false;
    public Set<Vertex> nodes = new HashSet<>();
    public List<Bag> childBags = new ArrayList<>();

    @Override
    public String toString() {
        return FirstOrderFormula.variableAssignmentToString(assignment) + " " + caption;
    }

    /**
     * @param v A vertex
     * @return A string consisting of the variable names that map to this
     * vertex, separated by commas.
     */
    public String getVertexAssignment(Vertex v) {
        List<String> variableNames = new ArrayList<>();
        for (Entry<String, Vertex> entry : assignment.entrySet()) {
            if (entry.getValue() == v)
                variableNames.add(entry.getKey());
        }
        return String.join(", ", variableNames);
    }
}
