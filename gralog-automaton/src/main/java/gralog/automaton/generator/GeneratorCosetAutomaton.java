/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.automaton.generator;

import gralog.algorithm.AlgorithmParameters;
import gralog.automaton.Automaton;
import gralog.automaton.State;
import gralog.automaton.Transition;
import gralog.generator.Generator;
import gralog.generator.GeneratorDescription;
import gralog.structure.Structure;

import java.util.ArrayList;

/**
 *
 */

@GeneratorDescription(
    name = "Coset Automaton",
    text = "Constructs an Automaton that accepts b-ary representations of numbers divisible by n",
    url = "http://math.stackexchange.com/questions/140283/why-does-this-fsm-accept-binary-numbers-divisible-by-three"
)
public class GeneratorCosetAutomaton extends Generator {

    @Override
    public AlgorithmParameters getParameters() {
        return new CosetAutomatonParameters();
    }

    @Override
    public Structure generate(AlgorithmParameters p) throws Exception {
        CosetAutomatonParameters cap = (CosetAutomatonParameters) (p);

        int base = cap.base;
        int n = cap.number;
        String alphabet = cap.alphabet;
        double diameter = 10d;

        Automaton result = new Automaton();
        ArrayList<State> states = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            State state = result.addVertex();
            state.setCoordinates(
                ((Math.cos(2d * Math.PI * i / n + Math.PI / 2d) * diameter) + diameter) / 2d,
                ((Math.sin(2d * Math.PI * i / n + Math.PI / 2d) * diameter) + diameter) / 2d
            );
            state.label = "" + i;

            states.add(state);

        }

        for (int i = 0; i < n; i++) {
            State statei = states.get(i);
            for (int s = 0; s < base; s++) {
                int j = (i * base + s) % n;                     // Here's the magic
                State statej = states.get(j);

                Transition transition = result.createEdge(statei, statej);
                transition.symbol = "" + alphabet.charAt(s);

                result.addEdge(transition);
            }
        }

        State q0 = states.get(0);
        q0.startState = true;
        q0.finalState = true;

        return result;
    }
}
