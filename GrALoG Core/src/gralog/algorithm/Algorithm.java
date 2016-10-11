/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.algorithm;

import gralog.structure.*;
import gralog.progresshandler.*;
import java.lang.reflect.Method;
import java.util.Set;

/**
 *
 */
public abstract class Algorithm {

    //public Object Run(Structure structure, AlgorithmParameters params, ProgressHandler onprogress);
    // null means it has no parameters
    public AlgorithmParameters getParameters(Structure structure) {
        return null;
    }

    public Object doRun(Structure structure, AlgorithmParameters params,
            Set<Object> selection, ProgressHandler onprogress) throws Exception {
        Object algoResult = null;
        Method[] methods = this.getClass().getMethods();
        for (Method method : methods) {
            if (!method.getName().equals("run"))
                continue;
            Class[] paramTypes = method.getParameterTypes();
            if (paramTypes.length != 4)
                continue;

            algoResult = method.invoke(this, new Object[]{structure, params, selection, onprogress});
            break;
        }

        return algoResult;
    }

    public AlgorithmDescription getDescription() throws Exception {
        if (!this.getClass().isAnnotationPresent(AlgorithmDescription.class))
            throw new Exception("class " + this.getClass().getName() + " has no @AlgorithmDescription Annotation");
        return this.getClass().getAnnotation(AlgorithmDescription.class);
    }

}
