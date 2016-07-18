/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.npcompleteness.generator;

import gralog.structure.*;
import gralog.generator.*;
import gralog.npcompleteness.propositionallogic.parser.*;
import gralog.npcompleteness.propositionallogic.formula.*;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

/**
 *
 * @author viktor
 */
@GeneratorDescription(
  name="SAT to Dominating Set Instance",
  text="Constructs a Dominating Set Instance from a SAT Formula",
  url=""
)
public class SatToDominatingSet extends Generator {

        
    @Override
    public GeneratorParameters GetParameters() {
        return new StringGeneratorParameter("(a \\vee b \\vee c) \\wedge (\\neg a \\vee \\neg b \\vee c)");
    }
    
    
    protected void GenerateClauseNodes(PropositionalLogicFormula cnf)
    {
        
    }
    
    protected void GenerateVarNodes(PropositionalLogicFormula cnf)
    {
        
    }
    
    
    @Override
    public Structure Generate(GeneratorParameters p) throws Exception
    {
        StringGeneratorParameter sp = (StringGeneratorParameter)(p);
        
        PropositionalLogicParser parser = new PropositionalLogicParser();
        PropositionalLogicFormula phi = parser.parseString(sp.parameter);
        PropositionalLogicFormula cnf = phi;//.ConjunctiveNormalForm();
        
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

            Vertex dummy1 = result.CreateVertex(); // 2 dummies
            dummy1.Coordinates.add(6d*i);
            dummy1.Coordinates.add(12d);
            dummy1.Label = var + "'";
            result.AddVertex(dummy1);

            Vertex dummy2 = result.CreateVertex();
            dummy2.Coordinates.add(6d*i + 2);
            dummy2.Coordinates.add(12d);
            dummy2.Label = var + "''";
            result.AddVertex(dummy2);

            result.AddEdge(result.CreateEdge(pos, neg)); // connections
            result.AddEdge(result.CreateEdge(pos, dummy1));
            result.AddEdge(result.CreateEdge(pos, dummy2));
            result.AddEdge(result.CreateEdge(neg, dummy1));
            result.AddEdge(result.CreateEdge(neg, dummy2));
            
            i++;
        }
        

        // create nodes for clauses
        Set<PropositionalLogicFormula> clauses = new HashSet<>();
        cnf.GetClauses(clauses);
        Set<PropositionalLogicFormula> literals = new HashSet<>();

        i = 0;
        for(PropositionalLogicFormula clause : clauses)
        {
            Vertex clauseVert = result.CreateVertex();
            clauseVert.Coordinates.add(6d*i);
            clauseVert.Coordinates.add(1d);
            clauseVert.Label = clause.toString();
            result.AddVertex(clauseVert);

            // connect clause-vertices to the nodes of their member-literals
            literals.clear();
            clause.GetLiterals(literals);
            for(PropositionalLogicFormula literal : literals)
            {
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
