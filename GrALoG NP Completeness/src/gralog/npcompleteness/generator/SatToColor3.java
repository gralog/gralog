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
import java.util.ArrayList;
import java.util.Set;

/**
 *
 */
@GeneratorDescription(
        name = "SAT to 3-Colorability Instance",
        text = "Constructs a 3-Colorability Instance from a SAT Formula",
        url = "https://en.wikipedia.org/wiki/Graph_coloring"
)
public class SatToColor3 extends Generator {

    @Override
    public AlgorithmParameters getParameters() {
        return new StringAlgorithmParameter(
                "A propositional formula",
                Preferences.getString(getClass(), "formula", "(a ∨ b ∨ c) ∧ (¬a ∨ ¬b ∨ c) ∧ (a ∨ ¬b ∨ ¬c)"),
                new PropositionalLogicSyntaxChecker(),
                PropositionalLogicSyntaxChecker.explanation());
    }

    // if a literal-node gets the same color as the "true" node, it means
    // this literal becomes true
    // the center top node of a gadget is only legally colorable,
    // iff one of its literals has the "true"-color.
    // see https://www.cs.cmu.edu/~ckingsf/bioinfo-lectures/sat.pdf
    @Override
    public Structure generate(AlgorithmParameters p) throws Exception {
        StringAlgorithmParameter sp = (StringAlgorithmParameter) (p);
        Preferences.setString(getClass(), "formula", sp.parameter);

        PropositionalLogicParser parser = new PropositionalLogicParser();
        PropositionalLogicFormula phi = parser.parseString(sp.parameter);
        PropositionalLogicFormula cnf = phi.conjunctiveNormalForm3(); // need 3-SAT

        UndirectedGraph result = new UndirectedGraph();

        Set<String> vars = new HashSet<>();
        HashMap<String, Vertex> PosNode = new HashMap();
        HashMap<String, Vertex> NegNode = new HashMap();
        cnf.getVariables(vars);

        // create the bottom gadget (triangle true-false-dummy)
        Vertex trueVert = result.createVertex();
        trueVert.coordinates = new Vector2D(8d, 14d);
        trueVert.label = "true";
        result.addVertex(trueVert);

        Vertex falseVert = result.createVertex();
        falseVert.coordinates = new Vector2D(12d, 14d);
        falseVert.label = "false";
        result.addVertex(falseVert);
        result.addEdge(result.createEdge(trueVert, falseVert));

        Vertex dummyVert = result.createVertex();
        dummyVert.coordinates = new Vector2D(10d, 12d);
        result.addVertex(dummyVert);
        result.addEdge(result.createEdge(trueVert, dummyVert));
        result.addEdge(result.createEdge(falseVert, dummyVert));

        // create gadgets for the literals
        int i = 0;
        for (String var : vars) {
            Vertex pos = result.createVertex(); // the positive literal
            pos.coordinates = new Vector2D(6d * i, 8d);
            pos.label = var;
            result.addVertex(pos);
            PosNode.put(var, pos);

            Vertex neg = result.createVertex(); // the negative literal
            neg.coordinates = new Vector2D(6d * i + 2, 8d);
            neg.label = "¬" + var;
            result.addVertex(neg);
            NegNode.put(var, neg);

            result.addEdge(result.createEdge(pos, neg));
            result.addEdge(result.createEdge(pos, dummyVert));
            result.addEdge(result.createEdge(neg, dummyVert));

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

            // create gadget for clause (6 nodes)
            Vertex leftBottomVert = result.createVertex();
            leftBottomVert.coordinates = new Vector2D(8d * i, 4d);
            result.addVertex(leftBottomVert);

            Vertex leftTopVert = result.createVertex();
            leftTopVert.coordinates = new Vector2D(8d * i, 2d);
            result.addVertex(leftTopVert);

            Vertex centerBottomVert = result.createVertex();
            centerBottomVert.coordinates = new Vector2D(8d * i + 2, 3d);
            result.addVertex(centerBottomVert);

            Vertex centerTopVert = result.createVertex();
            centerTopVert.coordinates = new Vector2D(8d * i + 2, 1d);
            centerTopVert.label = clause.toString();
            result.addVertex(centerTopVert);

            Vertex rightBottomVert = result.createVertex();
            rightBottomVert.coordinates = new Vector2D(8d * i + 4, 4d);
            result.addVertex(rightBottomVert);

            Vertex rightTopVert = result.createVertex();
            rightTopVert.coordinates = new Vector2D(8d * i + 4, 2d);
            result.addVertex(rightTopVert);

            // create edges in gadget
            result.addEdge(result.createEdge(centerTopVert, leftTopVert));
            result.addEdge(result.createEdge(centerTopVert, centerBottomVert));
            result.addEdge(result.createEdge(centerTopVert, rightTopVert));

            result.addEdge(result.createEdge(rightTopVert, rightBottomVert));
            result.addEdge(result.createEdge(leftTopVert, leftBottomVert));

            result.addEdge(result.createEdge(leftBottomVert, trueVert));
            result.addEdge(result.createEdge(centerBottomVert, trueVert));
            result.addEdge(result.createEdge(rightBottomVert, trueVert));

            result.addEdge(result.createEdge(leftTopVert, trueVert));
            result.addEdge(result.createEdge(rightTopVert, falseVert));

            ArrayList<Vertex> connectors = new ArrayList<>();
            connectors.add(leftBottomVert);
            connectors.add(centerBottomVert);
            connectors.add(rightBottomVert);

            // connect gadget to corresponding nodes for the literals
            int literalIter = 0;
            for (PropositionalLogicFormula literal : literals) {
                Vertex connector = connectors.get(literalIter);
                connector.label = literal.toString();
                literalIter++;

                if (literal instanceof PropositionalLogicVariable) {
                    PropositionalLogicVariable v = (PropositionalLogicVariable) literal;
                    result.addEdge(result.createEdge(connector, PosNode.get(v.variable)));
                }
                else if (literal instanceof PropositionalLogicNot
                         && ((PropositionalLogicNot) literal).subformula instanceof PropositionalLogicVariable) {
                    PropositionalLogicNot plnot = (PropositionalLogicNot) literal;
                    PropositionalLogicVariable v = (PropositionalLogicVariable) plnot.subformula;
                    result.addEdge(result.createEdge(connector, NegNode.get(v.variable)));
                }
                else
                    throw new Exception("Formula is not in Conjunctive Normal Form");
            }

            i++;
        }

        return result;
    }
}
