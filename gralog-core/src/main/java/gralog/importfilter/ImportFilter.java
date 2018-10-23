/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.importfilter;

import gralog.structure.Structure;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 *
 */
public abstract class ImportFilter {

    // null means it has no parameters
    public ImportFilterParameters getParameters() {
        return null;
    }

    public Structure importGraph(String fileName, ImportFilterParameters params) throws Exception {
        try (FileInputStream stream = new FileInputStream(fileName)) {
            return importGraph(stream, params);
        }
    }

    public Structure importGraph(InputStream stream, ImportFilterParameters params) throws Exception {
        throw new Exception("class " + this.getClass().getName() + " has no Stream-based importGraph Method");
    }

    public ImportFilterDescription getDescription() throws Exception {
        if (!this.getClass().isAnnotationPresent(ImportFilterDescription.class))
            throw new Exception("class " + this.getClass().getName() + " has no @ImportFilterDescription Annotation");
        return this.getClass().getAnnotation(ImportFilterDescription.class);
    }
}
