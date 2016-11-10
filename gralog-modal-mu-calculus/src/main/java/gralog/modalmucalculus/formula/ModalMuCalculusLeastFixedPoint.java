/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.modalmucalculus.formula;

import gralog.structure.*;
import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import gralog.modalmucalculus.structure.ParityGame;
import gralog.modalmucalculus.structure.ParityGamePosition;
import gralog.rendering.Vector2D;
import java.util.HashMap;
import java.util.Map;

public class ModalMuCalculusLeastFixedPoint extends ModalMuCalculusFormula {

    ModalMuCalculusFormula formula;
    String variable;

    public ModalMuCalculusLeastFixedPoint(String variable,
            ModalMuCalculusFormula formula) {
        this.variable = variable;
        this.formula = formula;
    }

    @Override
    protected ModalMuCalculusFormula negationNormalForm(boolean negated) {
        if (negated) {
            ModalMuCalculusFormula temp = this.negateVariable(variable);
            return new ModalMuCalculusGreatestFixedPoint(variable, temp.negationNormalForm(negated));
        }
        else
            return new ModalMuCalculusLeastFixedPoint(variable, formula.negationNormalForm(negated));
    }

    @Override
    protected ModalMuCalculusFormula negateVariable(String variable) {
        if (variable.equals(this.variable))
            return this; // don't negate in subformula, because inside the variable refers to a different one

        return new ModalMuCalculusLeastFixedPoint(this.variable, formula.negateVariable(variable));
    }

    @Override
    public double formulaWidth() {
        return formula.formulaWidth();
    }

    @Override
    public double formulaDepth() {
        return formula.formulaDepth() + 1;
    }

    @Override
    public void createParityGamePositions(double scale, double x, double y,
            double w, double h, KripkeStructure s, ParityGame p,
            int NextPriority,
            Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index) throws Exception {
        int MyPriority = NextPriority + 1 - NextPriority % 2;
        formula.createParityGamePositions(scale, x, y + scale, w, h, s, p, MyPriority, index);

        for (Vertex v : s.getVertices()) {
            ParityGamePosition node = p.createVertex();
            //node.Coordinates.add(scale * w * v.Coordinates.get(0) + x);
            node.coordinates = new Vector2D(
                    index.get((World) v).get(formula).coordinates.getX(),
                    scale * h * v.coordinates.getY() + y
            );
            node.label = "μ" + variable;
            node.priority = MyPriority;
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
        for (Vertex v : s.getVertices()) {
            ParityGamePosition pv = index.get((World) v).get(this);
            ParityGamePosition ps = index.get((World) v).get(formula);
            p.addEdge(p.createEdge(pv, ps));
        }

        ModalMuCalculusFormula olddef = variableDefinitionPoints.get(variable);
        variableDefinitionPoints.put(variable, this);

        formula.createParityGameTransitions(s, p, index, variableDefinitionPoints);

        variableDefinitionPoints.remove(variable);
        if (olddef != null)
            variableDefinitionPoints.put(variable, olddef);
    }

    @Override
    public String toString(FormulaPosition pos, FormulaEndPosition endPos) {
        String result = "μ" + variable + ". "
                        + formula.toString(FormulaPosition.Fixpoint, FormulaEndPosition.AT_END);
        if (endPos == FormulaEndPosition.MIDDLE)
            return "(" + result + ")";
        return result;
    }
}
