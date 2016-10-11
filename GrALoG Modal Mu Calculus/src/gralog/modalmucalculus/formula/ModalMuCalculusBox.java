package gralog.modalmucalculus.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.Action;
import gralog.modallogic.World;
import gralog.modalmucalculus.structure.ParityGame;
import gralog.modalmucalculus.structure.ParityGamePosition;
import gralog.rendering.Vector2D;
import gralog.structure.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ModalMuCalculusBox extends ModalMuCalculusFormula {

    String transitiontype;
    ModalMuCalculusFormula subformula;

    public ModalMuCalculusBox(ModalMuCalculusFormula subformula) {
        this(null, subformula);
    }

    public ModalMuCalculusBox(String transitiontype,
            ModalMuCalculusFormula subformula) {
        this.transitiontype = transitiontype;
        this.subformula = subformula;
    }

    @Override
    protected ModalMuCalculusFormula negationNormalForm(boolean negated) {
        if (negated)
            return new ModalMuCalculusDiamond(transitiontype, subformula.negationNormalForm(negated));
        else
            return new ModalMuCalculusBox(transitiontype, subformula.negationNormalForm(negated));
    }

    @Override
    protected ModalMuCalculusFormula negateVariable(String variable) {
        return new ModalMuCalculusBox(transitiontype, subformula.negateVariable(variable));
    }

    @Override
    public double formulaWidth() {
        return subformula.formulaWidth();
    }

    @Override
    public double formulaDepth() {
        return subformula.formulaDepth() + 1;
    }

    @Override
    public void createParityGamePositions(double scale, double x, double y,
            double w, double h, KripkeStructure s, ParityGame p,
            int NextPriority,
            Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index) throws Exception {
        subformula.createParityGamePositions(scale, x, y + scale, w, h, s, p, NextPriority, index);

        for (Vertex v : s.getVertices()) {
            ParityGamePosition node = p.createVertex();
            //node.Coordinates.add(scale * w * v.Coordinates.get(0) + x);
            node.coordinates = new Vector2D(
                    index.get((World) v).get(subformula).coordinates.getX(),
                    scale * h * v.coordinates.getY() + y
            );
            node.label = transitiontype == null ? "☐" : ("[" + transitiontype + "]");
            node.player1Position = false;
            p.addVertex(node);

            if (!index.containsKey((World) v))
                index.put((World) v, new HashMap<>());
            index.get((World) v).put(this, node);
        }
    }

    @Override
    public void createParityGameTransitions(KripkeStructure s, ParityGame p,
            Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index,
            Map<String, ModalMuCalculusFormula> variableDefinitionPoints) throws Exception {
        subformula.createParityGameTransitions(s, p, index, variableDefinitionPoints);

        for (Vertex v : s.getVertices()) {
            ParityGamePosition vp = index.get((World) v).get(this);

            for (Edge e : v.getConnectedEdges()) {
                Action a = (Action) e;
                if (e.getSource() != v)
                    continue;
                if (this.transitiontype != null && !a.Name.equals(transitiontype))
                    continue;

                ParityGamePosition tp = index.get((World) e.getTarget()).get(subformula);
                p.addEdge(p.createEdge(vp, tp));
            }
        }
    }
}
