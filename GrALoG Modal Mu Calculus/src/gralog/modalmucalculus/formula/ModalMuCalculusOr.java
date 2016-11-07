/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
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
    public void createParityGamePositions(double scale, double x, double y,
            double w, double h, KripkeStructure s, ParityGame p,
            int NextPriority,
            Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index) throws Exception {
        Double lw = left.formulaWidth();
        left.createParityGamePositions(scale, x, y + scale, w, h, s, p, NextPriority, index);
        right.createParityGamePositions(scale, x + scale * lw + scale, y + scale, w, h, s, p, NextPriority, index);

        for (Vertex v : s.getVertices()) {
            ParityGamePosition node = p.createVertex();
            //node.Coordinates.add(scale * w * v.Coordinates.get(0) + x + scale*(lw + 0.5d));
            node.coordinates = new Vector2D(
                    (index.get((World) v).get(left).coordinates.getX()
                     + index.get((World) v).get(right).coordinates.getX()) / 2d,
                    scale * h * v.coordinates.getY() + y
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
