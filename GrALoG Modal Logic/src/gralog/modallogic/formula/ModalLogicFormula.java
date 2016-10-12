/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.modallogic.formula;

import gralog.modallogic.*;
import java.util.HashSet;

/**
 * Base class for all formulas.
 *
 *
 */
public abstract class ModalLogicFormula {

    public ModalLogicFormula() {
    }

    abstract public HashSet<World> interpretation(KripkeStructure structure);
}
