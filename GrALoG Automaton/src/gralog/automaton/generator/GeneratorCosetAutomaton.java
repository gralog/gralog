/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton.generator;

import java.util.ArrayList;

import gralog.automaton.*;
import gralog.generator.Generator;
import gralog.generator.GeneratorDescription;
import gralog.generator.GeneratorParameters;
import gralog.rendering.Vector2D;
import gralog.structure.Structure;

@GeneratorDescription(
        name = "Coset Automaton",
        text = "Constructs an Automaton that accepts b-ary representations of numbers divisible by n",
        url = "http://math.stackexchange.com/questions/140283/why-does-this-fsm-accept-binary-numbers-divisible-by-three"
)
public class GeneratorCosetAutomaton extends Generator {

    @Override
    public GeneratorParameters getParameters() {
        return new CosetAutomatonParameters();
    }

    @Override
    public Structure generate(GeneratorParameters p) throws Exception {
        CosetAutomatonParameters cap = (CosetAutomatonParameters) (p);

        int Base = cap.base;
        int n = cap.number;
        String Alphabet = cap.alphabet;
        double diameter = 10d;

        Automaton result = new Automaton();
        ArrayList<State> states = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            State state = result.createVertex();
            state.coordinates = new Vector2D(
                    ((Math.cos(2d * Math.PI * i / n + Math.PI / 2d) * diameter) + diameter) / 2d,
                    ((Math.sin(2d * Math.PI * i / n + Math.PI / 2d) * diameter) + diameter) / 2d
            );
            state.label = "" + i;

            states.add(state);
            result.addVertex(state);
        }

        for (int i = 0; i < n; i++) {
            State statei = states.get(i);
            for (int s = 0; s < Base; s++) {
                int j = (i * Base + s) % n;                     // Here's the magic
                State statej = states.get(j);

                Transition transition = result.createEdge(statei, statej);
                transition.Symbol = "" + Alphabet.charAt(s);

                result.addEdge(transition);
            }
        }

        State q0 = states.get(0);
        q0.startState = true;
        q0.finalState = true;

        return result;
    }
}
