/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton.algorithm;

import gralog.algorithm.Algorithm;
import gralog.algorithm.AlgorithmParameters;
import gralog.algorithm.AlgorithmResult;
import gralog.plugins.Description;
import gralog.automaton.Automaton;

/**
 *
 * @author viktor
 */
@Description(
  name="Rabin-Scott Powerset Construction",
  text="",
  url="https://en.wikipedia.org/wiki/Powerset_construction"
)
public class PowersetConstruction extends Algorithm {
    
    public Object Run(Automaton s, AlgorithmParameters p) {
        if(p == null)
            throw new NullPointerException("p");
        return null;
    }    
    
}
