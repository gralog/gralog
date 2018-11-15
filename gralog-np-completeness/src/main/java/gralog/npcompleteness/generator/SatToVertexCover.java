/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.npcompleteness.generator;

import gralog.npcompleteness.propositionallogic.parser.PropositionalLogicSyntaxChecker;
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
import gralog.structure.UndirectedGraph;
import gralog.structure.Vertex;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;

/**
 *
 */
@GeneratorDescription(
    name = "SAT to Vertex Cover Instance",
    text = "Constructs a Vertex-Cover Instance from a SAT Formula",
    url = "")
public class SatToVertexCover extends Generator {

    @Override
    public AlgorithmParameters getParameters() {
        return new StringAlgorithmParameter(
            "A propositional formula",
            Preferences.getString(getClass(), "formula", "(a ∨ b ∨ c) ∧ (¬a ∨ ¬b ∨ c) ∧ (a ∨ ¬b ∨ ¬c)"),
            new PropositionalLogicSyntaxChecker(),
            PropositionalLogicSyntaxChecker.explanation());
    }

    // notice that the size of a min vertex cover becomes |vars| + 2*|clauses|
    // we select the literal-node corresponding to an assignment and the 2
    // remaining nodes in the clause-gadgets
    @Override
    public Structure generate(AlgorithmParameters p) throws Exception {
        StringAlgorithmParameter sp = (StringAlgorithmParameter) (p);
        Preferences.setString(getClass(), "formula", sp.parameter);

        PropositionalLogicFormula phi = PropositionalLogicParser.parseString(sp.parameter);
        PropositionalLogicFormula cnf = phi.conjunctiveNormalForm3(); // need 3-SAT

        UndirectedGraph result = new UndirectedGraph();

        Set<String> vars = new HashSet<>();
        HashMap<String, Vertex> posNode = new HashMap();
        HashMap<String, Vertex> negNode = new HashMap();
        cnf.getVariables(vars);

        // create gadgets for the literals
        int i = 0;
        for (String var : vars) {
            Vertex pos = result.addVertex(); // the positive literal
            pos.setCoordinates(6d * i, 10d);
            pos.label = var;

            posNode.put(var, pos);

            Vertex neg = result.addVertex(); // the negative literal
            neg.setCoordinates(6d * i + 2, 10d);
            neg.label = "¬" + var;

            negNode.put(var, neg);

            result.addEdge(result.createEdge(pos, neg));

            i++;
        }

        // create gadgets for clauses
        Set<PropositionalLogicFormula> clauses = new HashSet<>();
        cnf.getClauses(clauses);
        Set<PropositionalLogicFormula> literals = new HashSet<>();

        i = 0;
        for (PropositionalLogicFormula clause : clauses) {
            literals.clear();
            clause.getLiterals(literals);

            Vertex clauseVert1 = result.addVertex();
            clauseVert1.setCoordinates(5d * i, 3d);


            Vertex clauseVert2 = result.addVertex();
            clauseVert2.setCoordinates(5d * i + 2, 3d);


            Vertex clauseVert3 = result.addVertex();
            clauseVert3.setCoordinates(5d * i + 1, 2d);


            result.addEdge(result.createEdge(clauseVert1, clauseVert2));
            result.addEdge(result.createEdge(clauseVert2, clauseVert3));
            result.addEdge(result.createEdge(clauseVert3, clauseVert1));

            ArrayList<Vertex> gadget = new ArrayList<>();
            gadget.add(clauseVert1);
            gadget.add(clauseVert2);
            gadget.add(clauseVert3);

            int j = 0;
            for (PropositionalLogicFormula literal : literals) {
                Vertex clauseVert = gadget.get(j);
                clauseVert.label = literal.toString();
                j++;

                if (literal instanceof PropositionalLogicVariable) {
                    PropositionalLogicVariable v = (PropositionalLogicVariable) literal;
                    result.addEdge(result.createEdge(clauseVert, posNode.get(v.variable)));
                } else if (literal instanceof PropositionalLogicNot
                    && ((PropositionalLogicNot) literal).subformula instanceof PropositionalLogicVariable) {
                    PropositionalLogicNot plnot = (PropositionalLogicNot) literal;
                    PropositionalLogicVariable v = (PropositionalLogicVariable) plnot.subformula;
                    result.addEdge(result.createEdge(clauseVert, negNode.get(v.variable)));
                } else
                    throw new Exception("Formula is not in Conjunctive Normal Form");
            }
            ++i;
        }

        return result;
    }
}
