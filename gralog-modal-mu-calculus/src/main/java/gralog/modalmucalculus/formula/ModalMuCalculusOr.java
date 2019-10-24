/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.modalmucalculus.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import gralog.modalmucalculus.structure.ParityGame;
import gralog.modalmucalculus.structure.ParityGamePosition;
import gralog.rendering.Vector2D;
import gralog.structure.Vertex;
import java.util.HashMap;
import java.util.Map;

public class ModalMuCalculusOr extends ModalMuCalculusFormula {

    ModalMuCalculusFormula left;
    ModalMuCalculusFormula right;

    public ModalMuCalculusOr(ModalMuCalculusFormula left,
        ModalMuCalculusFormula right) {
        this.left = left;
        this.right = right;
    }

    @Override
    protected ModalMuCalculusFormula negationNormalForm(boolean negated) {
        if (negated)
            return new ModalMuCalculusAnd(left.negationNormalForm(negated), right.negationNormalForm(negated));
        else
            return new ModalMuCalculusOr(left.negationNormalForm(negated), right.negationNormalForm(negated));
    }

    @Override
    protected ModalMuCalculusFormula negateVariable(String variable) {
        return new ModalMuCalculusOr(left.negateVariable(variable),
            right.negateVariable(variable));
    }

    @Override
    public double formulaWidth() {
        return left.formulaWidth() + right.formulaWidth() + 1;
    }

    @Override
    public double formulaDepth() {
        return Math.max(left.formulaDepth(), right.formulaDepth()) + 1;
    }

    @Override
    public void createParityGamePositions(double scale, Vector2D pos,
        Vector2D size, KripkeStructure s, ParityGame p, int nextPriority,
        Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index) throws Exception {
        Double lw = left.formulaWidth();
        left.createParityGamePositions(scale,
            pos.plus(new Vector2D(0, scale)), size, s, p, nextPriority, index);
        right.createParityGamePositions(scale,
            pos.plus(new Vector2D(scale * lw + scale, scale)), size, s, p, nextPriority, index);

        for (Vertex v : s.getVertices()) {
            ParityGamePosition node = p.createVertex();
            //node.Coordinates.add(scale * w * v.Coordinates.get(0) + x + scale*(lw + 0.5d));
            node.setCoordinates(
                (index.get((World) v).get(left).getCoordinates().getX()
                + index.get((World) v).get(right).getCoordinates().getX()) / 2d,
                scale * size.getY() * v.getCoordinates().getY() + pos.getY()
            );
            node.label = "∨";
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
        left.createParityGameTransitions(s, p, index, variableDefinitionPoints);
        right.createParityGameTransitions(s, p, index, variableDefinitionPoints);

        for (Vertex v : s.getVertices()) {
            p.addEdge(p.createEdge(index.get((World) v).get(this), index.get((World) v).get(left)));
            p.addEdge(p.createEdge(index.get((World) v).get(this), index.get((World) v).get(right)));
        }
    }

    @Override
    public String toString(FormulaPosition pos, FormulaEndPosition endPos) {
        switch (pos) {
            case BoxDiamondNot:
            case AndLeft:
            case AndRight:
                return "(" + left.toString(FormulaPosition.OrLeft, FormulaEndPosition.MIDDLE) + " ∨ "
                    + right.toString(FormulaPosition.OrRight, FormulaEndPosition.AT_END) + ")";
            default:
                return left.toString(FormulaPosition.OrLeft, FormulaEndPosition.AT_END) + " ∨ "
                    + right.toString(FormulaPosition.OrRight, endPos);
        }
    }
}
