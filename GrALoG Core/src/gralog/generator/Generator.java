/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.generator;

import gralog.structure.*;

/**
 *
 * @author viktor
 */
public abstract class Generator {
    
    // null means it has no parameters
    public GeneratorParameters GetParameters() {
        return null;
    }
    
    public abstract Structure Generate(GeneratorParameters p) throws Exception;
    
}
