/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.algorithm;

import gralog.progresshandler.ProgressHandler;
import gralog.structure.Highlights;
import gralog.structure.Structure;
import gralog.structure.Vertex;

import java.lang.reflect.Method;
import java.util.Set;

/**
 *
 */
public abstract class Algorithm {

    //public Object Run(Structure structure, AlgorithmParameters params, ProgressHandler onprogress);
    // null means it has no parameters
    public AlgorithmParameters getParameters(Structure structure, Highlights highlights) {
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

            algoResult = method.invoke(this, new Object[] {structure, params, selection, onprogress});
            break;
        }
        return algoResult;
    }

    public AlgorithmDescription getDescription() throws Exception {
        if (!this.getClass().isAnnotationPresent(AlgorithmDescription.class))
            throw new Exception("class " + this.getClass().getName() + " has no @AlgorithmDescription Annotation");
        return this.getClass().getAnnotation(AlgorithmDescription.class);
    }

    /**
     * If there is a unique vertex among the selected objects, return it.
     * Otherwise return null.
     * @param selection
     * @return
     */
    protected static Vertex selectedUniqueVertex(Set<Object> selection){
        boolean foundSelectedVertices = false;
        Vertex v = null;
        for (Object o: selection)
            if (o instanceof Vertex) {
                if (foundSelectedVertices)
                    return null;
                else{
                    foundSelectedVertices = true;
                    v = (Vertex) o;
                }
            }
        return v;
    }


}
