
package gralog.computationtreelogic.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import gralog.structure.*;
import java.util.HashSet;



public class ComputationTreeLogicExistsUntil extends ComputationTreeLogicFormula
{
    ComputationTreeLogicFormula before;
    ComputationTreeLogicFormula after;

    public ComputationTreeLogicExistsUntil(ComputationTreeLogicFormula before, ComputationTreeLogicFormula after)
    {
        this.before = before;
        this.after = after;
    }

    @Override
    public HashSet<World> Interpretation(KripkeStructure structure)
    {
        HashSet<World> beforeresult = before.Interpretation(structure);
        HashSet<World> result = after.Interpretation(structure);
        
        HashSet<World> lastIteration = new HashSet<>();
        lastIteration.addAll(result);
        HashSet<World> nextIteration = new HashSet<>();

        // iteratively add those worlds to the result, that
        // a) satisfy <before> AND
        // b) have sucessors inside the current <result> set
        
        while(!lastIteration.isEmpty())
        {
            for(World l : lastIteration)
                for(Edge e : l.getConnectedEdges())
                    if(e.getTarget() == l)
                        if(beforeresult.contains((World)e.getSource())
                        && !result.contains((World)e.getSource()))
                            nextIteration.add((World)e.getSource());
                            
            result.addAll(nextIteration);
            HashSet<World> temp = lastIteration;
            lastIteration = nextIteration;
            nextIteration = temp;
            nextIteration.clear();
        }
        
        return result;
    }

}

