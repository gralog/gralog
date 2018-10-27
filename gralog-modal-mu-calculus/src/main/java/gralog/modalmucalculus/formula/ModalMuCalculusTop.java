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

public class ModalMuCalculusTop extends ModalMuCalculusFormula {

    public ModalMuCalculusTop() {
    }

    @Override
    protected ModalMuCalculusFormula negationNormalForm(boolean negated) {
        if (negated)
            return new ModalMuCalculusBottom();
        else
            return new ModalMuCalculusTop();
    }

    @Override
    protected ModalMuCalculusFormula negateVariable(String variable) {
        return new ModalMuCalculusTop();
    }

    @Override
    public void createParityGamePositions(double scale, Vector2D pos,
        Vector2D size, KripkeStructure s, ParityGame p, int nextPriority,
        Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index) {
        for (Vertex v : s.getVertices()) {
            ParityGamePosition node = p.createVertex();
            node.coordinates = pos.plus(new Vector2D(
                scale * size.getX() * v.coordinates.getX(),
                scale * size.getY() * v.coordinates.getY()));
            node.label = "⊤";
            node.player1Position = false; // verifier wins
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

    }

    @Override
    public String toString(FormulaPosition pos, FormulaEndPosition endPos) {
        return "⊤";
    }
}
