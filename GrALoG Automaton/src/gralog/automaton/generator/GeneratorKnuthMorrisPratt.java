/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton.generator;

import java.util.ArrayList;
import java.util.HashMap;

import gralog.automaton.*;
import gralog.generator.Generator;
import gralog.generator.GeneratorDescription;
import gralog.generator.GeneratorParameters;
import gralog.structure.Structure;

/**
 *
 * @author viktor
 */

@GeneratorDescription(
  name="Knuth Morris Pratt Algorithm",
  text="Directly constructs a minimal, deterministic automaton that accepts inputs which contain a given word",
  url="https://en.wikipedia.org/wiki/Knuth%E2%80%93Morris%E2%80%93Pratt_algorithm"
)
public class GeneratorKnuthMorrisPratt extends Generator {
    
    @Override
    public GeneratorParameters GetParameters() {
        return new KnuthMorrisPrattParameters();
    }

    @Override
    public Structure Generate(GeneratorParameters p) throws Exception {
        KnuthMorrisPrattParameters kmpp = (KnuthMorrisPrattParameters)(p);
        String str = kmpp.Word;
        String Alphabet = kmpp.Alphabet;
        
        
        Automaton result = new Automaton();
        int n = str.length();
        HashMap<Integer, HashMap<Character, Integer>> delta = new HashMap<>();

        
        ArrayList<Boolean> current = new ArrayList<>();
        ArrayList<Boolean> next = new ArrayList<>();
        ArrayList<State> states = new ArrayList<>();
        for(int i = 0; i < n+1; i++) {
            State s = result.CreateVertex();
            s.Coordinates.add(i*3d);
            s.Coordinates.add(1d);
            s.StartState = (i == 0);
            s.FinalState = (i == n);
            s.Label = str.substring(0, i);
            result.AddVertex(s);
            states.add(s);
            
            current.add(false);
            next.add(false);
            delta.put(i, new HashMap<>());
        }
        current.set(0, true);
        next.set(0, true);
        

        // create transition table
	for(int i = 0; i < str.length(); i++)
        {
            // make transitions for state i (depending on which states the NFA would
            // be in after reading w := str[0]..str[i-1]
            for(int j = 0; j < current.size(); j++)
                if(current.get(j))
                {
                    // current[j] == true  iff  the NFA can be in j after reading w
                    delta.get(i).put(str.charAt(j), j+1);
                    if(str.charAt(j) == str.charAt(i))
                        next.set(j+1, true);
                }
            
            current = next;
            next = new ArrayList<>();
            for(int f = 0; f < n+1; f++)
                next.add(false);
            next.set(0, true);
        }
        
        
        // loop transitions for final state
        for(int a = 0; a < Alphabet.length(); a++)
        {
            int l = str.length();
            delta.get(l).put(Alphabet.charAt(a),l);
        }
        
        
        // construct automaton
	for(int i = 0; i < str.length()+1; i++)
        {
            State statei = states.get(i);
            for(int a = 0; a < Alphabet.length(); a++)
            {
                int j;
                if(delta.get(i).containsKey(Alphabet.charAt(a)))
                    j = delta.get(i).get(Alphabet.charAt(a));
                else
                    j = 0; // no transition for a => back to start
                
                State statej = states.get(j);
                Transition trans = result.CreateEdge(statei, statej);
                trans.Symbol = "" + Alphabet.charAt(a);
                result.AddEdge(trans);
            }
	}
            
        
        return result;
    }

}
