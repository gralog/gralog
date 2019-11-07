/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.modalmucalculus.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import gralog.modalmucalculus.structure.ParityGame;
import gralog.modalmucalculus.structure.ParityGamePosition;
import gralog.rendering.Vector2D;
import gralog.structure.*;
import java.util.HashMap;
import java.util.Map;

public class ModalMuCalculusProposition extends ModalMuCalculusFormula {

    String proposition;

    public ModalMuCalculusProposition(String proposition) {
        this.proposition = proposition;
    }

    @Override
    protected ModalMuCalculusFormula negationNormalForm(boolean negated) {
        if (negated)
            return new ModalMuCalculusNot(new ModalMuCalculusProposition(proposition));
        else
            return new ModalMuCalculusProposition(proposition);
    }

    @Override
    protected ModalMuCalculusFormula negateVariable(String variable) {
        if (variable.equals(proposition))
            return new ModalMuCalculusNot(new ModalMuCalculusProposition(proposition));
        else
            return new ModalMuCalculusProposition(proposition);
    }

    @Override
    public void createParityGamePositions(double scale, Vector2D pos,
        Vector2D size, KripkeStructure s, ParityGame p, int nextPriority,
        Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index) {
        for (Vertex v : s.getVertices()) {
            ParityGamePosition node = p.createVertex();
            node.setCoordinates(pos.plus(new Vector2D(
                scale * size.getX() * v.getCoordinates().getX(),
                scale * size.getY() * v.getCoordinates().getY())));
            node.label = proposition;
            node.player1Position = !((World) v).satisfiesProposition(proposition);
            p.addVertex(node);

            if (!index.containsKey((World) v))
                index.put((World) v, new HashMap<>());
            index.get((World) v).put(this, node);
        }
    }

    @Override
    public void createParityGameTransitions(KripkeStructure s, ParityGame p,
        Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index,
        Map<String, ModalMuCalculusFormula> variableDefinitionPoints) {
        if (!variableDefinitionPoints.containsKey(proposition))
            return; // simple proposition

        for (Vertex v : s.getVertices()) {
            ParityGamePosition pv = index.get((World) v).get(this);
            ParityGamePosition pdef = index.get((World) v).get(variableDefinitionPoints.get(proposition));
            p.addEdge(p.createEdge(pv, pdef));
        }
    }

    @Override
    public String toString(FormulaPosition pos, FormulaEndPosition endPos) {
        return proposition;
    }
}
