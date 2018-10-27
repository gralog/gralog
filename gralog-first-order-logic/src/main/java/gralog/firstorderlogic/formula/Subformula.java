/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.firstorderlogic.formula;

import gralog.structure.Vertex;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a subformula with a concrete variable assignment, that is, a part
 * of some larger formula. A subformula has an assignment for each bound
 * variable. It also knows if it evaluates to true or false under this
 * assignment.
 *
 * Additionally, if the subformula starts with a quantifier binding a variable
 * X, for example "∀X.s", then it knows the set of vertices where s evaluates to
 * true.
 */
public class Subformula {

    public String subformula = "";
    public Map<String, Vertex> assignment = new HashMap<>();
    public Boolean value = false;
    public Set<Vertex> validVertices = new HashSet<>();
    public List<Subformula> children = new ArrayList<>();

    @Override
    public String toString() {
        return FirstOrderFormula.variableAssignmentToString(assignment) + " " + subformula;
    }

    /**
     * @param v A vertex
     * @return A string consisting of the variable names that map to this
     * vertex, separated by commas.
     */
    public String getVertexAssignment(Vertex v) {
        List<String> variableNames = new ArrayList<>();
        for (Map.Entry<String, Vertex> entry : assignment.entrySet()) {
            if (entry.getValue() == v)
                variableNames.add(entry.getKey());
        }
        return String.join(", ", variableNames);
    }
}
