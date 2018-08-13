/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.computationtreelogic.formula;

import gralog.modallogic.*;
import java.util.HashSet;

/**
 * Base class for all formulas.
 *
 *
 */
public abstract class ComputationTreeLogicFormula {

    public ComputationTreeLogicFormula() {
    }

    public abstract HashSet<World> interpretation(KripkeStructure structure);
}
