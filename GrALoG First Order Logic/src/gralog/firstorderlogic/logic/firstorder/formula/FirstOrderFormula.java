/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.logic.firstorder.formula;

import gralog.structure.*;
import java.util.HashMap;


/**
 *
 * @author Hv
 */
abstract public class FirstOrderFormula {
    
    abstract public boolean Evaluate(Structure s, HashMap<String, Vertex> varassign) throws Exception;
    
}
