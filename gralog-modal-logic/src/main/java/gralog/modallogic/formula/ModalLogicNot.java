/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.modallogic.formula;

import gralog.structure.*;
import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import java.util.HashSet;

public class ModalLogicNot extends ModalLogicFormula {

    ModalLogicFormula formula;

    public ModalLogicNot(ModalLogicFormula formula) {
        this.formula = formula;
    }

    @Override
    public HashSet<World> interpretation(KripkeStructure structure) {
        HashSet<World> result = new HashSet<>();
        HashSet<World> fresult = formula.interpretation(structure);

        // difference to set of all worlds
        for (Vertex v : structure.getVertices())
            if (v instanceof World)
                if (!fresult.contains((World) v))
                    result.add((World) v);

        return result;
    }

    @Override
    public String toString(FormulaPosition pos) {
        return "¬" + formula.toString(FormulaPosition.BoxDiamondNot);
    }
}
