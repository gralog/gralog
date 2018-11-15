/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.exportfilter;

import gralog.structure.Edge;
import gralog.structure.Structure;
import gralog.structure.Vertex;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 */
public abstract class ExportFilter {

    //public abstract void Export(Structure structure, OutputStreamWriter stream, ExportParameters params);
    // null means it has no parameters
    public ExportFilterParameters getParameters(Structure structure) {
        return null;
    }

    public void exportGraph(Structure structure, String fileName,
                            ExportFilterParameters params) throws Exception {
        FileOutputStream stream = new FileOutputStream(fileName);
        try (OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8")) {
            exportGraph(structure, writer, params);
        }
    }

    public void exportGraph(Structure structure, OutputStreamWriter stream,
                            ExportFilterParameters params) throws Exception {
        Method[] methods = this.getClass().getMethods();
        for (Method method : methods) {
            if (!method.getName().equals("export"))
                continue;
            Class[] paramTypes = method.getParameterTypes();
            if (paramTypes.length != 3)
                continue;

            method.invoke(this, new Object[] {structure, stream, params});
            break;
        }
    }

    public Map<String, Vertex> getVertexNames(Structure structure,
                                              ExportFilterParameters params) throws Exception {
        throw new Exception("class " + this.getClass().getName() + " has no method \"getVertexNames\"");
    }

    public Map<String, Edge> getEdgeNames(Structure structure,
                                          ExportFilterParameters params) throws Exception {
        throw new Exception("class " + this.getClass().getName() + " has no method \"getEdgeNames\"");
    }

    public ExportFilterDescription getDescription() throws Exception {
        if (!this.getClass().isAnnotationPresent(ExportFilterDescription.class))
            throw new Exception("class " + this.getClass().getName() + " has no @ExportFilterDescription Annotation");
        return this.getClass().getAnnotation(ExportFilterDescription.class);
    }
}
