/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.exportfilter;

import gralog.plugins.PluginManager;
import gralog.structure.DirectedGraph;
import gralog.structure.Structure;
import gralog.structure.Vertex;
import org.junit.After;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.isOneOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Test ExportFilterManager and a few export filters.
 */
public class ExportFilterManagerTest {

    private DirectedGraph getTestStructure() {
        DirectedGraph structure = new DirectedGraph();
        Vertex v = structure.addVertex("v");
        Vertex w = structure.addVertex("w");
        structure.addEdge(v, w);
        return structure;
    }

    @After
    public void clearStaticState() {
        // ExportFilterManager and PluginManager have static state, so we need
        // to call their clear() methods after each test.
        ExportFilterManager.clear();
        PluginManager.clear();
    }

    @Test
    public void testGetExportFilters() throws Exception {
        ArrayList<String> filters = new ArrayList<>();
        assertEquals(filters,
                ExportFilterManager.getExportFilters(Structure.class));

        ExportFilterManager.registerExportFilterClass(
                TrivialGraphFormatExport.class, "TrivialGraphFormatExport");
        filters.add("Trivial Graph Format");
        assertEquals(filters,
                ExportFilterManager.getExportFilters(Structure.class));
    }

    @Test
    public void testInstantiateExportFilterByExtension() throws Exception {
        PluginManager.registerClass(TrivialGraphFormatExport.class);

        assertThat(ExportFilterManager.instantiateExportFilterByExtension(Structure.class, "tgf"),
                instanceOf(TrivialGraphFormatExport.class));

        // Negative tests.  Unknown extensions should return null.
        assertEquals(null,
                ExportFilterManager.instantiateExportFilterByExtension(Structure.class, "xyz"));
        assertEquals(null,
                ExportFilterManager.instantiateExportFilterByExtension(Structure.class, ""));
    }

    @Test
    public void testTrivialGraphFormatExport() throws Exception {
        PluginManager.registerClass(TrivialGraphFormatExport.class);

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        OutputStreamWriter out = new OutputStreamWriter(result);

        ExportFilterManager.instantiateExportFilterByExtension(Structure.class, "tgf")
                .exportGraph((Structure) getTestStructure(), out, null);
        out.flush();

        // Because vertices in TGF have no labels, their order is undetermined.
        // So we accept both possibilities.
        assertThat(result.toString(),
                isOneOf("0\n1\n#\n0 1\n#\n",
                        "0\n1\n#\n1 0\n#\n"));
    }

    @Test
    public void testTikZExport() throws Exception {
        PluginManager.registerClass(TikZExport.class);

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        OutputStreamWriter out = new OutputStreamWriter(result);

        ExportFilterManager.instantiateExportFilterByExtension(DirectedGraph.class, "tikz")
                .exportGraph(getTestStructure(), out, null);
        out.flush();

        // A very simple check to see if we got something resembling a tikzpicture.
        assertThat(result.toString(), containsString("\\begin{tikzpicture}"));
    }

    @Test
    public void testLEDAExport() throws Exception {
        PluginManager.registerClass(LEDAExport.class);

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        OutputStreamWriter out = new OutputStreamWriter(result);

        ExportFilterManager.instantiateExportFilterByExtension(DirectedGraph.class, "lgr")
                .exportGraph(getTestStructure(), out, null);
        out.flush();

        // A very simple check to see if we got something resembling a LEDA file.
        assertThat(result.toString(), startsWith("LEDA.GRAPH\n"));
    }
}
