/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.algorithm;

import gralog.structure.*;

/**
 *
 * @author viktor
 */
public abstract class Algorithm {
    
    //public abstract Object Run(Structure structure, AlgorithmParameters params);
    
    // null means it has no parameters
    public AlgorithmParameters GetParameters(Structure structure) {
        return null;
    }
    
}
