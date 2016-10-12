/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.generator;

import gralog.structure.*;

/**
 *
 */
public abstract class Generator {

    // null means it has no parameters
    public GeneratorParameters getParameters() {
        return null;
    }

    public abstract Structure generate(GeneratorParameters p) throws Exception;

    public GeneratorDescription getDescription() throws Exception {
        if (!this.getClass().isAnnotationPresent(GeneratorDescription.class))
            throw new Exception("class " + this.getClass().getName() + " has no @GeneratorDescription Annotation");
        return this.getClass().getAnnotation(GeneratorDescription.class);
    }
}
