/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.modalmucalculus.formula;

import gralog.structure.*;
import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import gralog.modalmucalculus.structure.ParityGame;
import gralog.modalmucalculus.structure.ParityGamePosition;
import gralog.rendering.Vector2D;
import java.util.HashMap;
import java.util.Map;

public class ModalMuCalculusNot extends ModalMuCalculusFormula {

    ModalMuCalculusFormula formula;

    public ModalMuCalculusNot(ModalMuCalculusFormula formula) {
        this.formula = formula;
    }

    @Override
    protected ModalMuCalculusFormula negationNormalForm(boolean negated) {
        return formula.negationNormalForm(!negated);
    }

    @Override
    protected ModalMuCalculusFormula negateVariable(String variable) {
        return new ModalMuCalculusNot(formula.negateVariable(variable));
    }

    @Override
    public double formulaWidth() {
        return formula.formulaWidth();
    }

    @Override
    public double formulaDepth() {
        return formula.formulaDepth();
    }

    @Override
    public void createParityGamePositions(double scale, Vector2D pos,
        Vector2D size, KripkeStructure s, ParityGame p, int nextPriority,
        Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index) throws Exception {
        // Parity game can only be constructed from NNF formulas.
        if (!(formula instanceof ModalMuCalculusProposition))
            throw new Exception("Formula is not in Negation Normal Form");
        ModalMuCalculusProposition prop = (ModalMuCalculusProposition) formula;

        for (Vertex v : s.getVertices()) {
            ParityGamePosition node = p.createVertex();
            node.setCoordinates(pos.plus(new Vector2D(
                scale * size.getX() * v.getCoordinates().getX(),
                scale * size.getY() * v.getCoordinates().getY())));
            node.label = prop.proposition;
            node.player1Position = ((World) v).satisfiesProposition(prop.proposition);
            p.addVertex(node);

            if (!index.containsKey((World) v))
                index.put((World) v, new HashMap<>());
            index.get((World) v).put(this, node);
        }
        //formula.CreateParityGamePositions(x, y, w, h, s, p, id);
    }

    @Override
    public void createParityGameTransitions(KripkeStructure s, ParityGame p,
        Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index,
        Map<String, ModalMuCalculusFormula> variableDefinitionPoints) throws Exception {
        if (!(formula instanceof ModalMuCalculusProposition))
            throw new Exception("Formula is not in Negation Normal Form");

        ModalMuCalculusProposition prop = (ModalMuCalculusProposition) formula;

        if (variableDefinitionPoints.containsKey(prop.proposition))
            throw new Exception("Formula contains bound variable \"" + prop.proposition + "\" negatively");
    }

    @Override
    public String toString(FormulaPosition pos, FormulaEndPosition endPos) {
        return "¬" + formula.toString(FormulaPosition.BoxDiamondNot, endPos);
    }
}
