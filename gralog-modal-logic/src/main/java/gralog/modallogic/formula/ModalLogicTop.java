/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.modallogic.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import gralog.structure.Vertex;

import java.util.HashSet;

public class ModalLogicTop extends ModalLogicFormula {

    public ModalLogicTop() {
    }

    @Override
    public HashSet<World> interpretation(KripkeStructure structure) {
        HashSet<World> result = new HashSet<>();
        for (Vertex v : structure.getVertices())
            if (v instanceof World)
                result.add((World) v);
        return result;
    }

    @Override
    public String toString(FormulaPosition pos) {
        return "⊤";
    }
}
