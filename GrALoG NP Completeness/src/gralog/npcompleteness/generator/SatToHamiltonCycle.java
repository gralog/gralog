/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.npcompleteness.generator;

import gralog.algorithm.AlgorithmParameters;
import gralog.algorithm.StringAlgorithmParameter;
import gralog.generator.*;
import gralog.npcompleteness.propositionallogic.formula.PropositionalLogicFormula;
import gralog.npcompleteness.propositionallogic.formula.PropositionalLogicNot;
import gralog.npcompleteness.propositionallogic.formula.PropositionalLogicVariable;
import gralog.npcompleteness.propositionallogic.parser.PropositionalLogicParser;
import gralog.preferences.Preferences;
import gralog.rendering.Vector2D;
import gralog.structure.Structure;
import gralog.structure.DirectedGraph;
import gralog.structure.Vertex;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@GeneratorDescription(
        name = "SAT to Hamiltonian Cycle",
        text = "Constructs a Hamiltonian-Cycle Instance from a SAT Formula",
        url = "https://en.wikipedia.org/wiki/Hamiltonian_path_problem"
)
public class SatToHamiltonCycle extends Generator {

    @Override
    public AlgorithmParameters getParameters() {
        return new StringAlgorithmParameter(
                "A propositional formula",
                Preferences.getString(getClass(), "formula", "(a ∨ b ∨ c) ∧ (¬a ∨ ¬b ∨ c) ∧ (a ∨ ¬b ∨ ¬c)"),
                new PropositionalLogicSyntaxChecker(),
                PropositionalLogicSyntaxChecker.explanation());
    }

    @Override
    public Structure generate(AlgorithmParameters p) throws Exception {
        StringAlgorithmParameter sp = (StringAlgorithmParameter) (p);
        Preferences.setString(getClass(), "formula", sp.parameter);

        PropositionalLogicParser parser = new PropositionalLogicParser();
        PropositionalLogicFormula phi = parser.parseString(sp.parameter);
        PropositionalLogicFormula cnf = phi;
        if (!phi.hasConjunctiveNormalForm())
            cnf = phi.conjunctiveNormalForm();

        DirectedGraph result = new DirectedGraph();

        Set<String> vars = new HashSet<>();
        cnf.getVariables(vars);
        Set<PropositionalLogicFormula> clauses = new HashSet<>();
        cnf.getClauses(clauses);

        // create nodes for clauses
        HashMap<PropositionalLogicFormula, Vertex> ClauseNodes = new HashMap<>();
        int clausej = 0;
        for (PropositionalLogicFormula clause : clauses) {
            Vertex cnode = result.createVertex();
            cnode.coordinates = new Vector2D(5d + 5d * clausej, -2d);
            cnode.label = clause.toString();
            result.addVertex(cnode);
            ClauseNodes.put(clause, cnode);
            clausej++;
        }

        Vertex start = result.createVertex(); // start node
        start.coordinates = new Vector2D(3d + (5 * clauses.size()) / 2, 1d);
        start.label = "start";
        result.addVertex(start);
        Set<Vertex> lastRow = new HashSet<>();
        lastRow.add(start);

        // create rows for the variables
        int i = 0;
        for (String var : vars) {
            Vertex pos = result.createVertex(); // node for positive assignment to var
            pos.coordinates = new Vector2D(1d, 5 * i + 3d);
            pos.label = var;
            result.addVertex(pos);

            Vertex last = pos;
            int j = -1;
            for (PropositionalLogicFormula clause : clauses) {
                j++;
                // test if clause contains the variable
                HashSet<String> clauseVars = new HashSet<>();
                clause.getVariables(clauseVars);
                boolean clauseContainsVar = false;
                for (String clauseVar : clauseVars)
                    if (clauseVar.equals(var))
                        clauseContainsVar = true;
                if (!clauseContainsVar)
                    continue;

                // create 2 nodes for occurence of var in clause
                Vertex a = result.createVertex();
                a.coordinates = new Vector2D(4d + 5 * j, 5 * i + 3d);
                result.addEdge(result.createEdge(last, a));
                result.addEdge(result.createEdge(a, last));
                result.addVertex(a);

                Vertex b = result.createVertex();
                b.coordinates = new Vector2D(4d + 5 * j + 2, 5 * i + 3d);
                result.addEdge(result.createEdge(b, a));
                result.addEdge(result.createEdge(a, b));
                result.addVertex(b);

                // connect them to the node for the clause (edges may go in
                // both directions, if clause contains x and !x
                Vertex clauseNode = ClauseNodes.get(clause);

                Set<PropositionalLogicFormula> literals = new HashSet<>();
                clause.getLiterals(literals);
                for (PropositionalLogicFormula literal : literals) {
                    if (literal instanceof PropositionalLogicVariable) {
                        PropositionalLogicVariable v = (PropositionalLogicVariable) literal;
                        if (var.equals(v.variable)) {
                            result.addEdge(result.createEdge(a, clauseNode));
                            result.addEdge(result.createEdge(clauseNode, b));
                        }
                    }
                    else if (literal instanceof PropositionalLogicNot
                             && ((PropositionalLogicNot) literal).subformula instanceof PropositionalLogicVariable) {
                        PropositionalLogicNot plnot = (PropositionalLogicNot) literal;
                        PropositionalLogicVariable v = (PropositionalLogicVariable) plnot.subformula;
                        if (var.equals(v.variable)) {
                            result.addEdge(result.createEdge(b, clauseNode));
                            result.addEdge(result.createEdge(clauseNode, a));
                        }
                    }
                    else
                        throw new Exception("Formula is not in Conjunctive Normal Form");
                }

                last = b;
            }

            Vertex neg = result.createVertex(); // node for negative assignment to var
            neg.coordinates = new Vector2D(4d + 5 * clauses.size(), 5 * i + 3d);
            neg.label = "¬" + var;
            result.addVertex(neg);

            result.addEdge(result.createEdge(last, neg));
            result.addEdge(result.createEdge(neg, last));

            // connect to start and end of last row
            for (Vertex l : lastRow) {
                result.addEdge(result.createEdge(l, neg));
                result.addEdge(result.createEdge(l, pos));
            }
            lastRow.clear();
            lastRow.add(pos);
            lastRow.add(neg);

            i++;
        }

        Vertex end = result.createVertex(); // the end-node
        end.coordinates = new Vector2D(3d + (5 * clauses.size()) / 2, 5d * vars.size());
        end.label = "end";
        result.addVertex(end);
        for (Vertex l : lastRow) {
            result.addEdge(result.createEdge(l, end));
        }

        return result;
    }
}
