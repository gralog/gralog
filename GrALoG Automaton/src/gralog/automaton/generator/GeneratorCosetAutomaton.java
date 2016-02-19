/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton.generator;

import java.util.Vector;

import gralog.automaton.*;
import gralog.generator.Generator;
import gralog.generator.GeneratorDescription;
import gralog.generator.GeneratorParameters;
import gralog.generator.StringGeneratorParameter;
import gralog.structure.Structure;

@GeneratorDescription(
  name="Coset Automaton",
  text="Constructs an Automaton that accepts b-ary representations of numbers divisible by n",
  url="http://math.stackexchange.com/questions/140283/why-does-this-fsm-accept-binary-numbers-divisible-by-three"
)
public class GeneratorCosetAutomaton extends Generator {
    
    @Override
    public GeneratorParameters GetParameters() {
        return new CosetAutomatonParameters();
    }
    
    @Override
    public Structure Generate(GeneratorParameters p) throws Exception {
        CosetAutomatonParameters cap = (CosetAutomatonParameters)(p);

        int Base = cap.Base;
        int n = cap.Number;
        String Alphabet = cap.Alphabet;
        double diameter = 10d;
        
        Automaton result = new Automaton();
        Vector<State> states = new Vector<State>();
        
        for(int i = 0; i < n; i++)
        {
            State state = result.CreateVertex();
            state.Coordinates.add(((Math.cos(2d*Math.PI*i/n + Math.PI/2d)*diameter)+diameter)/2d);
            state.Coordinates.add(((Math.sin(2d*Math.PI*i/n + Math.PI/2d)*diameter)+diameter)/2d);
            state.Label = "" + i;
            
            states.add(state);
            result.AddVertex(state);
        }

        for(int i = 0; i < n; i++)
        {
            State statei = states.elementAt(i);
            for(int s = 0; s < Base; s++)
            {
                int j = (i * Base + s) % n;                     // Here's the magic
                State statej = states.elementAt(j);
                
                Transition transition = result.CreateEdge(statei, statej);
                transition.Symbol = "" + Alphabet.charAt(s);
                
                result.AddEdge(transition);
            }
        }
        
        State q0 = states.elementAt(0);
        q0.StartState = true;
        q0.FinalState = true;
        
        return result;
    }
    
}