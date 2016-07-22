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
import java.util.Set;

/**
 *
 * @author viktor
 */
@GeneratorDescription(
  name="SAT to Independent Set Instance",
  text="Constructs an Independent Set Instance from a SAT Formula",
  url="https://en.wikipedia.org/wiki/Independent_set_(graph_theory)"
)
public class SatToIndependentSet extends Generator {
    
    
    @Override
    public GeneratorParameters GetParameters()
    {
        return new StringGeneratorParameter("(a \\vee b \\vee c) \\wedge (\\neg a \\vee \\neg b \\vee c) \\wedge (a \\vee \\neg b \\vee \\neg c)");
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

            result.AddEdge(result.CreateEdge(pos, neg)); // connect them
            
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
            int j = 0;
            Set<Vertex> vertsOfClause = new HashSet<>();
            for(PropositionalLogicFormula literal : literals)
            {
                Vertex clauseVert = result.CreateVertex();
                clauseVert.Coordinates.add(6d*i  + 2*Math.cos( j * Math.PI / (literals.size()-1) ));
                clauseVert.Coordinates.add(3d - Math.sin( j * Math.PI / (literals.size()-1) ));
                clauseVert.Label = literal.toString();
                result.AddVertex(clauseVert);
                
                // connect vert to all verts in the same clause
                for(Vertex w : vertsOfClause)
                    result.AddEdge(result.CreateEdge(clauseVert, w));
                vertsOfClause.add(clauseVert);
                j++;
                
                // positive literal of clause connected to negative literal in variables
                // because connection means you cannot choose both together,
                // i.e. you cannot choose x=true and say that a clause with ¬x
                // was satisfied by that literal
                if(literal instanceof PropositionalLogicVariable)
                {
                    PropositionalLogicVariable v = (PropositionalLogicVariable)literal;
                    result.AddEdge(result.CreateEdge(clauseVert, NegNode.get(v.variable)));
                }
                else if(literal instanceof PropositionalLogicNot
                     && ((PropositionalLogicNot)literal).subformula instanceof PropositionalLogicVariable)
                {
                    PropositionalLogicNot plnot = (PropositionalLogicNot)literal;
                    PropositionalLogicVariable v = (PropositionalLogicVariable)plnot.subformula;
                    result.AddEdge(result.CreateEdge(clauseVert, PosNode.get(v.variable)));
                }
                else
                    throw new Exception("Formula is not in Conjunctive Normal Form");
            }
            
            i++;
        }
        
        return result;
    }
    
}
