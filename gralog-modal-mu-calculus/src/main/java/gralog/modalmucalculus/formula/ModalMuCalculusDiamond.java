/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.modalmucalculus.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.Action;
import gralog.modallogic.World;
import gralog.modalmucalculus.structure.ParityGame;
import gralog.modalmucalculus.structure.ParityGamePosition;
import gralog.rendering.Vector2D;
import gralog.structure.*;
import java.util.HashMap;
import java.util.Map;

public class ModalMuCalculusDiamond extends ModalMuCalculusFormula {

    String transitiontype;
    ModalMuCalculusFormula subformula;

    public ModalMuCalculusDiamond(ModalMuCalculusFormula subformula) {
        this(null, subformula);
    }

    public ModalMuCalculusDiamond(String transitiontype,
        ModalMuCalculusFormula subformula) {
        this.transitiontype = transitiontype;
        this.subformula = subformula;
    }

    @Override
    protected ModalMuCalculusFormula negationNormalForm(boolean negated) {
        if (negated)
            return new ModalMuCalculusBox(transitiontype, subformula.negationNormalForm(negated));
        else
            return new ModalMuCalculusDiamond(transitiontype, subformula.negationNormalForm(negated));
    }

    @Override
    protected ModalMuCalculusFormula negateVariable(String variable) {
        return new ModalMuCalculusDiamond(transitiontype, subformula.negateVariable(variable));
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
    public void createParityGamePositions(double scale, Vector2D pos, Vector2D size, KripkeStructure s, ParityGame p, int nextPriority, Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index) throws Exception {
        subformula.createParityGamePositions(scale,
            pos.plus(new Vector2D(0, scale)), size, s, p, nextPriority, index);

        for (Vertex v : s.getVertices()) {
            ParityGamePosition node = p.createVertex();
            //node.Coordinates.add(scale * w * v.Coordinates.get(0) + x);
            node.setCoordinates(
                index.get((World) v).get(subformula).getCoordinates().getX(),
                scale * size.getY() * v.getCoordinates().getY() + pos.getY()
            );
            node.label = transitiontype == null ? "◇" : ("<" + transitiontype + ">");
            node.player1Position = true;
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

            for (Edge e : v.getIncidentEdges()) {
                Action a = (Action) e;
                if (e.getSource() != v)
                    continue;
                if (this.transitiontype != null && !a.name.equals(transitiontype))
                    continue;

                ParityGamePosition tp = index.get((World) e.getTarget()).get(subformula);
                p.addEdge(p.createEdge(vp, tp));
            }
        }
    }

    @Override
    public String toString(FormulaPosition pos, FormulaEndPosition endPos) {
        if (transitiontype != null)
            return "<" + transitiontype + ">" + subformula.toString(FormulaPosition.BoxDiamondNot, endPos);
        return "◊" + subformula.toString(FormulaPosition.BoxDiamondNot, endPos);
    }
}
