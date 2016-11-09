/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
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
import java.util.Set;

/**
 *
 */
@GeneratorDescription(
        name = "SAT to Independent Set Instance",
        text = "Constructs an Independent Set Instance from a SAT Formula",
        url = "https://en.wikipedia.org/wiki/Independent_set_(graph_theory)"
)
public class SatToIndependentSet extends Generator {

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

        UndirectedGraph result = new UndirectedGraph();

        Set<String> vars = new HashSet<>();
        HashMap<String, Vertex> PosNode = new HashMap();
        HashMap<String, Vertex> NegNode = new HashMap();
        cnf.getVariables(vars);

        // create gadgets for the literals
        int i = 0;
        for (String var : vars) {
            Vertex pos = result.createVertex(); // the positive literal
            pos.coordinates = new Vector2D(6d * i, 10d);
            pos.label = var;
            result.addVertex(pos);
            PosNode.put(var, pos);

            Vertex neg = result.createVertex(); // the negative literal
            neg.coordinates = new Vector2D(6d * i + 2, 10d);
            neg.label = "¬" + var;
            result.addVertex(neg);
            NegNode.put(var, neg);

            result.addEdge(result.createEdge(pos, neg)); // connect them

            i++;
        }

        // create nodes for clauses
        Set<PropositionalLogicFormula> clauses = new HashSet<>();
        cnf.getClauses(clauses);
        Set<PropositionalLogicFormula> literals = new HashSet<>();

        i = 0;
        for (PropositionalLogicFormula clause : clauses) {
            literals.clear();
            clause.getLiterals(literals);
            int j = 0;
            Set<Vertex> vertsOfClause = new HashSet<>();
            for (PropositionalLogicFormula literal : literals) {
                Vertex clauseVert = result.createVertex();
                clauseVert.coordinates = new Vector2D(
                        6d * i + 2 * Math.cos(j * Math.PI / (literals.size() - 1)),
                        3d - Math.sin(j * Math.PI / (literals.size() - 1))
                );
                clauseVert.label = literal.toString();
                result.addVertex(clauseVert);

                // connect vert to all verts in the same clause
                for (Vertex w : vertsOfClause)
                    result.addEdge(result.createEdge(clauseVert, w));
                vertsOfClause.add(clauseVert);
                j++;

                // positive literal of clause connected to negative literal in variables
                // because connection means you cannot choose both together,
                // i.e. you cannot choose x=true and say that a clause with ¬x
                // was satisfied by that literal
                if (literal instanceof PropositionalLogicVariable) {
                    PropositionalLogicVariable v = (PropositionalLogicVariable) literal;
                    result.addEdge(result.createEdge(clauseVert, NegNode.get(v.variable)));
                }
                else if (literal instanceof PropositionalLogicNot
                         && ((PropositionalLogicNot) literal).subformula instanceof PropositionalLogicVariable) {
                    PropositionalLogicNot plnot = (PropositionalLogicNot) literal;
                    PropositionalLogicVariable v = (PropositionalLogicVariable) plnot.subformula;
                    result.addEdge(result.createEdge(clauseVert, PosNode.get(v.variable)));
                }
                else
                    throw new Exception("Formula is not in Conjunctive Normal Form");
            }

            ++i;
        }

        return result;
    }
}
