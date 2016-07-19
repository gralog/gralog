
package gralog.modalmucalculus.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.Action;
import gralog.modallogic.World;
import gralog.modalmucalculus.structure.ParityGame;
import gralog.modalmucalculus.structure.ParityGamePosition;
import gralog.structure.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;



public class ModalMuCalculusBox extends ModalMuCalculusFormula
{
    String transitiontype;
    ModalMuCalculusFormula subformula;

    public ModalMuCalculusBox(ModalMuCalculusFormula subformula)
    {
        this(null, subformula);
    }

    public ModalMuCalculusBox(String transitiontype, ModalMuCalculusFormula subformula)
    {
        this.transitiontype = transitiontype;
        this.subformula = subformula;
    }
    
    @Override
    protected ModalMuCalculusFormula NegationNormalForm(boolean negated)
    {
        if(negated)
            return new ModalMuCalculusDiamond(transitiontype, subformula.NegationNormalForm(negated));
        else
            return new ModalMuCalculusBox(transitiontype, subformula.NegationNormalForm(negated));
    }
    
    @Override
    public Double FormulaWidth()
    {
        return subformula.FormulaWidth();
    }

    @Override
    public Double FormulaDepth()
    {
        return subformula.FormulaDepth() + 1;
    }
    
    
    @Override
    public void CreateParityGamePositions(Double x, Double y, Double w, Double h,
            KripkeStructure s, ParityGame p,
            Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index) throws Exception
    {
        subformula.CreateParityGamePositions(x, y+1, w, h, s, p, index);
        
        for(Vertex v : s.getVertices())
        {
            ParityGamePosition node = p.CreateVertex();
            node.Coordinates.add(w * v.Coordinates.get(0) + x);
            node.Coordinates.add(h * v.Coordinates.get(1) + y);
            node.Label = transitiontype == null ? "☐" : ("["+transitiontype+"]");
            node.Player1Position = false;
            p.AddVertex(node);
            
            if(!index.containsKey((World)v))
                index.put((World)v, new HashMap<>());
            index.get((World)v).put(this, node);
        }
    }
    
    @Override
    public void CreateParityGameTransitions(KripkeStructure s, ParityGame p,
            Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index,
            Map<String, ModalMuCalculusFormula> variableDefinitionPoints) throws Exception
    {
        subformula.CreateParityGameTransitions(s, p, index, variableDefinitionPoints);
        
        for(Vertex v : s.getVertices())
        {
            ParityGamePosition vp = index.get((World)v).get(this);
            
            for(Edge e : v.getConnectedEdges())
            {
                Action a = (Action)e;
                if(e.getSource() != v)
                    continue;
                if(this.transitiontype != null && !a.Name.equals(transitiontype))
                    continue;
            
                ParityGamePosition tp = index.get((World)e.getTarget()).get(subformula);
                p.AddEdge(p.CreateEdge(vp, tp));
            }
        }
    }
}

