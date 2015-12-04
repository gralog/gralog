/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.algorithm;

import gralog.structure.*;
import java.lang.reflect.Method;

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
    
    public Object DoRun(Structure structure, AlgorithmParameters params) throws Exception
    {
        Object algoResult = null;
        Method[] methods = this.getClass().getMethods();
        for(Method method : methods)
        {
            if(!method.getName().equals("Run"))
                continue;
            Class[] paramTypes = method.getParameterTypes();
            if(paramTypes.length != 2)
                continue;
                
            algoResult = method.invoke(this, new Object[]{structure, params});
            break;
        }
        
        return algoResult;
    }
    
    public AlgorithmDescription getDescription() throws Exception {
        if(!this.getClass().isAnnotationPresent(AlgorithmDescription.class))
            throw new Exception("class " + this.getClass().getName() + " has no @AlgorithmDescription Annotation");
        return this.getClass().getAnnotation(AlgorithmDescription.class);
    }
    
}
