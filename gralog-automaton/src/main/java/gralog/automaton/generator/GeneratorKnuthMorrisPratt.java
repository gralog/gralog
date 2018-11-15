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
import java.util.HashMap;

/**
 *
 */
@GeneratorDescription(
    name = "Knuth Morris Pratt Algorithm",
    text = "Directly constructs a minimal, deterministic automaton that accepts inputs which contain a given word",
    url = "https://en.wikipedia.org/wiki/Knuth%E2%80%93Morris%E2%80%93Pratt_algorithm"
)
public class GeneratorKnuthMorrisPratt extends Generator {

    @Override
    public AlgorithmParameters getParameters() {
        return new KnuthMorrisPrattParameters();
    }

    @Override
    public Structure generate(AlgorithmParameters p) throws Exception {
        KnuthMorrisPrattParameters kmpp = (KnuthMorrisPrattParameters) (p);
        String str = kmpp.word;
        String alphabet = kmpp.alphabet;

        Automaton result = new Automaton();
        int n = str.length();
        HashMap<Integer, HashMap<Character, Integer>> delta = new HashMap<>();

        ArrayList<Boolean> current = new ArrayList<>();
        ArrayList<Boolean> next = new ArrayList<>();
        ArrayList<State> states = new ArrayList<>();
        for (int i = 0; i < n + 1; i++) {
            State s = result.addVertex();
            s.setCoordinates(i * 3d, 1d);
            s.startState = (i == 0);
            s.finalState = (i == n);
            s.label = str.substring(0, i);
            states.add(s);

            current.add(false);
            next.add(false);
            delta.put(i, new HashMap<>());
        }
        current.set(0, true);
        next.set(0, true);

        // create transition table
        for (int i = 0; i < str.length(); i++) {
            // make transitions for state i (depending on which states the NFA would
            // be in after reading w := str[0]..str[i-1]
            for (int j = 0; j < current.size(); j++) {
                if (current.get(j)) {
                    // current[j] == true  iff  the NFA can be in j after reading w
                    delta.get(i).put(str.charAt(j), j + 1);
                    if (str.charAt(j) == str.charAt(i)) {
                        next.set(j + 1, true);
                    }
                }
            }

            current = next;
            next = new ArrayList<>();
            for (int f = 0; f < n + 1; f++) {
                next.add(false);
            }
            next.set(0, true);
        }

        // loop transitions for final state
        for (int a = 0; a < alphabet.length(); a++) {
            int l = str.length();
            delta.get(l).put(alphabet.charAt(a), l);
        }

        // construct automaton
        for (int i = 0; i < str.length() + 1; i++) {
            State statei = states.get(i);
            for (int a = 0; a < alphabet.length(); a++) {
                int j;
                if (delta.get(i).containsKey(alphabet.charAt(a))) {
                    j = delta.get(i).get(alphabet.charAt(a));
                } else {
                    j = 0; // no transition for a => back to start
                }

                State statej = states.get(j);
                Transition trans = result.createEdge(statei, statej);
                trans.symbol = "" + alphabet.charAt(a);
                result.addEdge(trans);
            }
        }

        return result;
    }
}
