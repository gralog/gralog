
package gralog.computationtreelogic.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.Action;
import gralog.modallogic.World;
import gralog.structure.*;
import java.util.HashSet;



public class ComputationTreeLogicExistsGlobally extends ComputationTreeLogicFormula
{
    ComputationTreeLogicFormula subformula;

    public ComputationTreeLogicExistsGlobally(ComputationTreeLogicFormula subformula)
    {
        this.subformula = subformula;
    }

    @Override
    public HashSet<World> Interpretation(KripkeStructure structure)
    {
        HashSet<World> result = subformula.Interpretation(structure);
        HashSet<World> lastRemoved = new HashSet<>();
        HashSet<World> nextRemoved = new HashSet<>();
                
        // lastRemoved = V \ subformula result
        for(Vertex v : structure.getVertices())
            if(!result.contains((World)v))
                lastRemoved.add((World)v);

        // iteratively remove worlds from result, who have no more successors inside <result>
        
        HashSet<World> RemovalCandidates = new HashSet<>();
        while(!lastRemoved.isEmpty())
        {
            
            // examine predecessors of worlds that have been removed in the last iteration
            
            for(World l : lastRemoved)
                for(Edge e : l.getConnectedEdges())
                    if(e.getTarget() == l)
                        RemovalCandidates.add((World)e.getSource());

            
            // if the lost successor of r was the last sucessor inside <result>, then also remove r
            for(World r : RemovalCandidates)
            {
                boolean rMustBeRemoved = true;
                for(Edge e : r.getConnectedEdges())
                    if(e.getSource() == r)
                        if(result.contains((World)e.getTarget())) // no, r has another successor inside <result>
                        {
                            rMustBeRemoved = false;
                            break; 
                        }
                if(rMustBeRemoved)
                    nextRemoved.add(r);
            }

            result.removeAll(nextRemoved);
            
            RemovalCandidates.clear();
            HashSet<World> temp = lastRemoved;
            lastRemoved = nextRemoved;
            nextRemoved = temp;
            nextRemoved.clear();
        }
        
        return result;
    }
}

