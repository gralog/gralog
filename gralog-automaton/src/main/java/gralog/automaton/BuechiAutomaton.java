/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.automaton;

import gralog.plugins.XmlName;
import gralog.structure.Structure;
import gralog.structure.StructureDescription;

/**
 *
 */
@StructureDescription(
    name = "Büchi Automaton",
    text = "",
    url = "https://en.wikipedia.org/wiki/B%C3%BCchi_automaton")
@XmlName(name = "buechiautomaton")
public class BuechiAutomaton extends Structure<State, Transition> {

    @Override
    public State createVertex() {
        return new State();
    }

    @Override
    public Transition createEdge() {
        return new Transition();
    }
}
