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
  name="SAT to Vertex Cover Instance",
  text="Constructs a Vertex-Cover Instance from a SAT Formula",
  url=""
)
public class SatToVertexCover extends Generator {
    
    
    @Override
    public GeneratorParameters GetParameters()
    {
        return new StringGeneratorParameter("(a \\vee b \\vee c) \\wedge (\\neg a \\vee \\neg b \\vee c) \\wedge (a \\vee \\neg b \\vee \\neg c)");
    }
    
    
    // notice that the size of a min vertex cover becomes |vars| + 2*|clauses|
    // we select the literal-node corresponding to an assignment and the 2
    // remaining nodes in the clause-gadgets
    

    @Override
    public Structure Generate(GeneratorParameters p) throws Exception
    {
        StringGeneratorParameter sp = (StringGeneratorParameter)(p);
        
        PropositionalLogicParser parser = new PropositionalLogicParser();
        PropositionalLogicFormula phi = parser.parseString(sp.parameter);
        PropositionalLogicFormula cnf = phi.ConjunctiveNormalForm3(); // need 3-SAT
        
        
        UndirectedGraph result = new UndirectedGraph();
        
        Set<String> vars = new HashSet<>();
        HashMap<String, Vertex> PosNode = new HashMap();
        HashMap<String, Vertex> NegNode = new HashMap();
        cnf.GetVariables(vars);
        
        
        // create gadgets for the literals
        int i = 0;
        for(String var : vars)
        {
            Vertex pos = result.CreateVertex(); // the positive literal
            pos.Coordinates.add(6d*i);
            pos.Coordinates.add(10d);
            pos.Label = var;
            result.AddVertex(pos);
            PosNode.put(var, pos);
            
            Vertex neg = result.CreateVertex(); // the negative literal
            neg.Coordinates.add(6d*i + 2);
            neg.Coordinates.add(10d);
            neg.Label = "¬"+var;
            result.AddVertex(neg);
            NegNode.put(var, neg);
            
            result.AddEdge(result.CreateEdge(pos, neg));
            
            i++;
        }
        

        // create gadgets for clauses
        Set<PropositionalLogicFormula> clauses = new HashSet<>();
        cnf.GetClauses(clauses);
        Set<PropositionalLogicFormula> literals = new HashSet<>();

        i = 0;
        for(PropositionalLogicFormula clause : clauses)
        {
            literals.clear();
            clause.GetLiterals(literals);
            
            Vertex clauseVert1 = result.CreateVertex();
            clauseVert1.Coordinates.add(5d*i);
            clauseVert1.Coordinates.add(3d);
            result.AddVertex(clauseVert1);

            Vertex clauseVert2 = result.CreateVertex();
            clauseVert2.Coordinates.add(5d*i+2);
            clauseVert2.Coordinates.add(3d);
            result.AddVertex(clauseVert2);

            Vertex clauseVert3 = result.CreateVertex();
            clauseVert3.Coordinates.add(5d*i+1);
            clauseVert3.Coordinates.add(2d);
            result.AddVertex(clauseVert3);

            result.AddEdge(result.CreateEdge(clauseVert1, clauseVert2));
            result.AddEdge(result.CreateEdge(clauseVert2, clauseVert3));
            result.AddEdge(result.CreateEdge(clauseVert3, clauseVert1));
            
            ArrayList<Vertex> gadget = new ArrayList<>();
            gadget.add(clauseVert1);
            gadget.add(clauseVert2);
            gadget.add(clauseVert3);

            int j = 0;
            for(PropositionalLogicFormula literal : literals)
            {
                Vertex clauseVert = gadget.get(j);
                clauseVert.Label = literal.toString();
                j++;
                
                if(literal instanceof PropositionalLogicVariable)
                {
                    PropositionalLogicVariable v = (PropositionalLogicVariable)literal;
                    result.AddEdge(result.CreateEdge(clauseVert, PosNode.get(v.variable)));
                }
                else if(literal instanceof PropositionalLogicNot
                     && ((PropositionalLogicNot)literal).subformula instanceof PropositionalLogicVariable)
                {
                    PropositionalLogicNot plnot = (PropositionalLogicNot)literal;
                    PropositionalLogicVariable v = (PropositionalLogicVariable)plnot.subformula;
                    result.AddEdge(result.CreateEdge(clauseVert, NegNode.get(v.variable)));
                }
                else
                    throw new Exception("Formula is not in Conjunctive Normal Form");
            }
            
            i++;
        }
        
        return result;
    }
    
}
