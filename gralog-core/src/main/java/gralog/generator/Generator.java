/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.generator;

import gralog.algorithm.AlgorithmParameters;
import gralog.structure.Structure;

/**
 *
 */


// todo: refactor Generator! Should be: Structure path20 = new Path(20);
public abstract class Generator {

    // null means it has no parameters
    public AlgorithmParameters getParameters() {
        return null;
    }

    public abstract Structure generate(AlgorithmParameters p) throws Exception;

    public GeneratorDescription getDescription() throws Exception {
        if (!this.getClass().isAnnotationPresent(GeneratorDescription.class))
            throw new Exception("class " + this.getClass().getName() + " has no @GeneratorDescription Annotation");
        return this.getClass().getAnnotation(GeneratorDescription.class);
    }
}
