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
import gralog.rendering.Vector2D;
import gralog.structure.Structure;
import gralog.structure.DirectedGraph;
import gralog.structure.Vertex;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author viktor
 */
@GeneratorDescription(
  name="SAT to Hamiltonian Cycle",
  text="Constructs a Hamiltonian-Cycle Instance from a SAT Formula",
  url="https://en.wikipedia.org/wiki/Hamiltonian_path_problem"
)
public class SatToHamiltonCycle extends Generator {
    
    
    @Override
    public GeneratorParameters GetParameters()
    {
        return new StringGeneratorParameter("(a \\vee b \\vee c) \\wedge (\\neg a \\vee c) \\wedge (a \\vee \\neg c)");
    }
    
    

    @Override
    public Structure Generate(GeneratorParameters p) throws Exception
    {
        StringGeneratorParameter sp = (StringGeneratorParameter)(p);
        
        PropositionalLogicParser parser = new PropositionalLogicParser();
        PropositionalLogicFormula phi = parser.parseString(sp.parameter);
        PropositionalLogicFormula cnf = phi;
        if(!phi.hasConjunctiveNormalForm())
            cnf = phi.ConjunctiveNormalForm();
        
        
        DirectedGraph result = new DirectedGraph();
        
        Set<String> vars = new HashSet<>();
        cnf.GetVariables(vars);
        Set<PropositionalLogicFormula> clauses = new HashSet<>();
        cnf.GetClauses(clauses);

        
        // create nodes for clauses
        HashMap<PropositionalLogicFormula, Vertex> ClauseNodes = new HashMap<>();
        int clausej = 0;
        for(PropositionalLogicFormula clause : clauses)
        {
            Vertex cnode = result.CreateVertex();
            cnode.Coordinates = new Vector2D(5d + 5d*clausej, -2d);
            cnode.Label = clause.toString();
            result.AddVertex(cnode);
            ClauseNodes.put(clause, cnode);
            clausej++;
        }
        
        
        Vertex start = result.CreateVertex(); // start node
        start.Coordinates = new Vector2D(3d + (5*clauses.size())/2, 1d);
        start.Label = "start";
        result.AddVertex(start);
        Set<Vertex> lastRow = new HashSet<>();
        lastRow.add(start);
        
        
        // create rows for the variables
        int i = 0;
        for(String var : vars)
        {
            Vertex pos = result.CreateVertex(); // node for positive assignment to var
            pos.Coordinates = new Vector2D(1d, 5*i+3d);
            pos.Label = var;
            result.AddVertex(pos);
            
            
            Vertex last = pos;
            int j = -1;
            for(PropositionalLogicFormula clause : clauses)
            {
                j++;
                // test if clause contains the variable
                HashSet<String> clauseVars = new HashSet<>();
                clause.GetVariables(clauseVars);
                boolean clauseContainsVar = false;
                for(String clauseVar : clauseVars)
                    if(clauseVar.equals(var))
                        clauseContainsVar = true;
                if(!clauseContainsVar)
                    continue;
                
                
                // create 2 nodes for occurence of var in clause
                Vertex a = result.CreateVertex();
                a.Coordinates = new Vector2D(4d + 5*j, 5*i+3d);
                result.AddEdge(result.CreateEdge(last, a));
                result.AddEdge(result.CreateEdge(a, last));
                result.AddVertex(a);

                Vertex b = result.CreateVertex();
                b.Coordinates = new Vector2D(4d + 5*j + 2, 5*i+3d);
                result.AddEdge(result.CreateEdge(b, a));
                result.AddEdge(result.CreateEdge(a, b));
                result.AddVertex(b);

                // connect them to the node for the clause (edges may go in
                // both directions, if clause contains x and !x
                Vertex clauseNode = ClauseNodes.get(clause);

                Set<PropositionalLogicFormula> literals = new HashSet<>();
                clause.GetLiterals(literals);
                for(PropositionalLogicFormula literal : literals)
                {
                    if(literal instanceof PropositionalLogicVariable)
                    {
                        PropositionalLogicVariable v = (PropositionalLogicVariable)literal;
                        if(var.equals(v.variable))
                        {
                            result.AddEdge(result.CreateEdge(a, clauseNode));
                            result.AddEdge(result.CreateEdge(clauseNode, b));
                        }
                    }
                    else if(literal instanceof PropositionalLogicNot
                         && ((PropositionalLogicNot)literal).subformula instanceof PropositionalLogicVariable)
                    {
                        PropositionalLogicNot plnot = (PropositionalLogicNot)literal;
                        PropositionalLogicVariable v = (PropositionalLogicVariable)plnot.subformula;
                        if(var.equals(v.variable))
                        {
                            result.AddEdge(result.CreateEdge(b, clauseNode));
                            result.AddEdge(result.CreateEdge(clauseNode, a));
                        }
                    }
                    else
                        throw new Exception("Formula is not in Conjunctive Normal Form");
                }

                
                last = b;
            }
            
            Vertex neg = result.CreateVertex(); // node for negative assignment to var
            neg.Coordinates = new Vector2D(4d + 5*clauses.size(), 5*i+3d);
            neg.Label = "¬"+var;
            result.AddVertex(neg);
            
            result.AddEdge(result.CreateEdge(last, neg));
            result.AddEdge(result.CreateEdge(neg, last));
            
            // connect to start and end of last row
            for(Vertex l : lastRow)
            {
                result.AddEdge(result.CreateEdge(l, neg));
                result.AddEdge(result.CreateEdge(l, pos));
            }
            lastRow.clear();
            lastRow.add(pos);
            lastRow.add(neg);
            
            i++;
        }
        

        Vertex end = result.CreateVertex(); // the end-node
        end.Coordinates = new Vector2D(3d + (5*clauses.size())/2, 5d*vars.size());
        end.Label = "end";
        result.AddVertex(end);
        for(Vertex l : lastRow)
        {
            result.AddEdge(result.CreateEdge(l, end));
        }

        return result;
    }
    
}
