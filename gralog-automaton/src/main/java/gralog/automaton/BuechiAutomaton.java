/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.automaton;

import gralog.plugins.XmlName;
import gralog.preferences.Configuration;
import gralog.structure.Structure;
import gralog.structure.StructureDescription;

/**
 *
 */
@StructureDescription(
    name = "Buechi Automaton",
    text = "",
    url = "https://en.wikipedia.org/wiki/B%C3%BCchi_automaton")
@XmlName(name = "buechiautomaton")
public class BuechiAutomaton extends Structure<State, Transition> {

    @Override
    public State createVertex() {
        return new State();
    }

    @Override
    public State createVertex(Configuration config) {
        return new State(config);
    }

    @Override
    public Transition createEdge(Configuration config) {
        return new Transition(config);
    }
}
