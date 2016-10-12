/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.computationtreelogic.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import java.util.HashSet;

public class ComputationTreeLogicBottom extends ComputationTreeLogicFormula {

    public ComputationTreeLogicBottom() {
    }

    @Override
    public HashSet<World> interpretation(KripkeStructure structure) {
        return new HashSet<>();
    }

}
