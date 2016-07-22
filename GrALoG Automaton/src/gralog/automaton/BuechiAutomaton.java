package gralog.automaton;

import gralog.plugins.XmlName;
import gralog.structure.Structure;
import gralog.structure.StructureDescription;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author viktor
 */
@StructureDescription(
    name="Büchi Automaton",
    text="",
    url="https://en.wikipedia.org/wiki/B%C3%BCchi_automaton"
)
@XmlName(name="buechiautomaton")
public class BuechiAutomaton extends Structure<State, Transition>
{
    
    @Override
    public State CreateVertex() {
        return new State();
    }
    
    @Override
    public Transition CreateEdge() {
        return new Transition();
    }
    
}
