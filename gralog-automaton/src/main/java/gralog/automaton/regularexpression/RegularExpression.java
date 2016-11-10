/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.automaton.regularexpression;

import gralog.automaton.*;

/**
 *
 */
abstract public class RegularExpression {

    abstract public Automaton thompsonConstruction(double scale);

    public Automaton thompsonConstruction() {
        return thompsonConstruction(2.0d);
    }
}