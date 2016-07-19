/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.npcompleteness.generator;

import gralog.generator.*;
import gralog.npcompleteness.propositionallogic.formula.PropositionalLogicFormula;
import gralog.npcompleteness.propositionallogic.formula.PropositionalLogicNot;
import gralog.npcompleteness.propositionallogic.formula.PropositionalLogicVariable;
import gralog.npcompleteness.propositionallogic.parser.PropositionalLogicParser;
import gralog.structure.Structure;
import gralog.structure.UndirectedGraph;
import gralog.structure.Vertex;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author viktor
 */
@GeneratorDescription(
  name="SAT to 3-Colorability Instance",
  text="Constructs a 3-Colorability Instance from a SAT Formula",
  url="https://en.wikipedia.org/wiki/Graph_coloring"
)
public class SatToColor3 extends Generator {
    
    
    @Override
    public GeneratorParameters GetParameters()
    {
        return new StringGeneratorParameter("(a \\vee b \\vee c) \\wedge (\\neg a \\vee \\neg b \\vee c) \\wedge (a \\vee \\neg b \\vee \\neg c)");
    }
    
    // if a literal-node gets the same color as the "true" node, it means
    // this literal becomes true
    // the center top node of a gadget is only legally colorable,
    // iff one of its literals has the "true"-color.
    // see https://www.cs.cmu.edu/~ckingsf/bioinfo-lectures/sat.pdf

    @Override
    public Structure Generate(GeneratorParameters p) throws Exception
    {
        StringGeneratorParameter sp = (StringGeneratorParameter)(p);
        
        PropositionalLogicParser parser = new PropositionalLogicParser();
        PropositionalLogicFormula phi = parser.parseString(sp.parameter);
        PropositionalLogicFormula cnf = phi.ConjunctiveNormalForm();
        
        UndirectedGraph result = new UndirectedGraph();
        
        Set<String> vars = new HashSet<>();
        HashMap<String, Vertex> PosNode = new HashMap();
        HashMap<String, Vertex> NegNode = new HashMap();
        cnf.GetVariables(vars);

        
        // create the bottom gadget (triangle true-false-dummy)
        Vertex trueVert = result.CreateVertex();
        trueVert.Coordinates.add(8d);
        trueVert.Coordinates.add(14d);
        trueVert.Label = "true";
        result.AddVertex(trueVert);

        Vertex falseVert = result.CreateVertex();
        falseVert.Coordinates.add(12d);
        falseVert.Coordinates.add(14d);
        falseVert.Label = "false";
        result.AddVertex(falseVert);
        result.AddEdge(result.CreateEdge(trueVert, falseVert));

        Vertex dummyVert = result.CreateVertex();
        dummyVert.Coordinates.add(10d);
        dummyVert.Coordinates.add(12d);
        result.AddVertex(dummyVert);
        result.AddEdge(result.CreateEdge(trueVert, dummyVert));
        result.AddEdge(result.CreateEdge(falseVert, dummyVert));

        
        // create gadgets for the literals
        int i = 0;
        for(String var : vars)
        {
            Vertex pos = result.CreateVertex(); // the positive literal
            pos.Coordinates.add(6d*i);
            pos.Coordinates.add(8d);
            pos.Label = var;
            result.AddVertex(pos);
            PosNode.put(var, pos);
            
            Vertex neg = result.CreateVertex(); // the negative literal
            neg.Coordinates.add(6d*i + 2);
            neg.Coordinates.add(8d);
            neg.Label = "¬"+var;
            result.AddVertex(neg);
            NegNode.put(var, neg);
            
            result.AddEdge(result.CreateEdge(pos, neg));
            result.AddEdge(result.CreateEdge(pos, dummyVert));
            result.AddEdge(result.CreateEdge(neg, dummyVert));
            
            i++;
        }
        

        // create nodes for clauses
        Set<PropositionalLogicFormula> clauses = new HashSet<>();
        cnf.GetClauses(clauses);
        Set<PropositionalLogicFormula> literals = new HashSet<>();

        i = 0;
        for(PropositionalLogicFormula clause : clauses)
        {
            literals.clear();
            clause.GetLiterals(literals);

            // create gadget for clause (6 nodes)

            Vertex leftBottomVert = result.CreateVertex();
            leftBottomVert.Coordinates.add(8d*i);
            leftBottomVert.Coordinates.add(4d);
            result.AddVertex(leftBottomVert);

            Vertex leftTopVert = result.CreateVertex();
            leftTopVert.Coordinates.add(8d*i);
            leftTopVert.Coordinates.add(2d);
            result.AddVertex(leftTopVert);

            Vertex centerBottomVert = result.CreateVertex();
            centerBottomVert.Coordinates.add(8d*i+2);
            centerBottomVert.Coordinates.add(3d);
            result.AddVertex(centerBottomVert);

            Vertex centerTopVert = result.CreateVertex();
            centerTopVert.Coordinates.add(8d*i+2);
            centerTopVert.Coordinates.add(1d);
            centerTopVert.Label = clause.toString();
            result.AddVertex(centerTopVert);

            Vertex rightBottomVert = result.CreateVertex();
            rightBottomVert.Coordinates.add(8d*i+4);
            rightBottomVert.Coordinates.add(4d);
            result.AddVertex(rightBottomVert);

            Vertex rightTopVert = result.CreateVertex();
            rightTopVert.Coordinates.add(8d*i+4);
            rightTopVert.Coordinates.add(2d);
            result.AddVertex(rightTopVert);
            
            // create edges in gadget
            result.AddEdge(result.CreateEdge(centerTopVert, leftTopVert));
            result.AddEdge(result.CreateEdge(centerTopVert, centerBottomVert));
            result.AddEdge(result.CreateEdge(centerTopVert, rightTopVert));

            result.AddEdge(result.CreateEdge(rightTopVert, rightBottomVert));
            result.AddEdge(result.CreateEdge(leftTopVert, leftBottomVert));

            result.AddEdge(result.CreateEdge(leftBottomVert, trueVert));
            result.AddEdge(result.CreateEdge(centerBottomVert, trueVert));
            result.AddEdge(result.CreateEdge(rightBottomVert, trueVert));
            
            result.AddEdge(result.CreateEdge(leftTopVert, trueVert));
            result.AddEdge(result.CreateEdge(rightTopVert, falseVert));

            
            ArrayList<Vertex> connectors = new ArrayList<>();
            connectors.add(leftBottomVert);
            connectors.add(centerBottomVert);
            connectors.add(rightBottomVert);
            
            // connect gadget to corresponding nodes for the literals
            int literalIter = 0;
            for(PropositionalLogicFormula literal : literals)
            {
                Vertex connector = connectors.get(literalIter);
                connector.Label = literal.toString();
                literalIter++;
                
                if(literal instanceof PropositionalLogicVariable)
                {
                    PropositionalLogicVariable v = (PropositionalLogicVariable)literal;
                    result.AddEdge(result.CreateEdge(connector, PosNode.get(v.variable)));
                }
                else if(literal instanceof PropositionalLogicNot
                     && ((PropositionalLogicNot)literal).subformula instanceof PropositionalLogicVariable)
                {
                    PropositionalLogicNot plnot = (PropositionalLogicNot)literal;
                    PropositionalLogicVariable v = (PropositionalLogicVariable)plnot.subformula;
                    result.AddEdge(result.CreateEdge(connector, NegNode.get(v.variable)));
                }
                else
                    throw new Exception("Formula is not in Conjunctive Normal Form");
            }
            
            i++;
        }
        
        return result;
    }
    
}
