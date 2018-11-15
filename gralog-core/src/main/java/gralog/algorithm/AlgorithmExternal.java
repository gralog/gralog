/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.algorithm;

import gralog.exportfilter.ExportFilter;
import gralog.progresshandler.ProgressHandler;
import gralog.rendering.GralogColor;
import gralog.structure.Edge;
import gralog.structure.Structure;
import gralog.structure.Vertex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public abstract class AlgorithmExternal extends Algorithm {

    // set these explicitly in the constructor of your derived classes!
    // e.g. the command should not be set via an AlgorithmParameter
    // the class must know these - it is not the users job to know them!
    protected List<String> command;
    ExportFilter exportFilter;
    boolean passStructureViaFile;

    protected AlgorithmExternal(ExportFilter exportFilter,
                                boolean passStructureViaFile, String... command) {
        this.command = new LinkedList<>(Arrays.asList(command));
        this.exportFilter = exportFilter;
        this.passStructureViaFile = passStructureViaFile;
    }

    private List<String> getEffectiveCommand(Structure structure) throws Exception {
        if (!passStructureViaFile)
            return command;

        File temp = File.createTempFile("gralog", "." + exportFilter.getDescription().fileExtension());
        exportFilter.exportGraph(structure,
                new OutputStreamWriter(new FileOutputStream(temp), "UTF-8"), null);
        List<String> effectiveCommand = new LinkedList<>();
        for (String s : command)
            effectiveCommand.add(s.replaceAll("%u", "\"" + temp.getAbsolutePath() + "\""));
        return effectiveCommand;
    }

    public Object run(Structure structure, AlgorithmParameters params,
                      Set<Object> selection, ProgressHandler onProgress) throws Exception {
        List<String> effectiveCommand = getEffectiveCommand(structure);

        ProcessBuilder pb = new ProcessBuilder(effectiveCommand);
        Process proc = pb.start();
        Scanner in = new Scanner(proc.getInputStream(), "UTF-8");

        Map<String, Vertex> vertexIndex = exportFilter.getVertexNames(structure, null);
        Map<String, Edge> edgeIndex = exportFilter.getEdgeNames(structure, null);

        if (!passStructureViaFile) {
            try (OutputStreamWriter out = new OutputStreamWriter(proc.getOutputStream(), "UTF-8")) {
                exportFilter.exportGraph(structure, out, null);
            }
        }

        boolean quit = false;
        while (in.hasNextLine() && !quit) {
            String line = in.nextLine();
            String[] parts = line.split(" ");

            switch (parts[0]) {
                case "CREATEVERTEX":
                    handleCreateVertex(parts, vertexIndex, structure);
                    break;
                case "DELETEVERTEX":
                    handleDeleteVertex(parts, vertexIndex, structure);
                    break;
                case "SETVERTEXLABEL":
                    handleSetVertexLabel(parts, vertexIndex);
                    break;
                case "SETVERTEXCOLOR":
                    handleSetVertexColor(parts, vertexIndex);
                    break;
                case "MOVEVERTEX":
                    handleMoveVertex(parts, vertexIndex);
                    break;
                case "CREATEEDGE":
                    handleCreateEdge(parts, edgeIndex, vertexIndex, structure);
                    break;
                case "DELETEEDGE":
                    handleDeleteEdge(parts, edgeIndex, structure);
                    break;
                case "SETEDGECOLOR":
                    handleSetEdgeColor(parts, edgeIndex);
                    break;
                case "SETEDGELABEL":
                    handleSetEdgeLabel(parts, edgeIndex);
                    break;
                case "QUIT":
                    quit = true;
                    break;
                default:
                    throw new Exception("Unknown command: " + parts[0]);
            }
            if (onProgress != null)
                onProgress.onProgress(structure);
        }

        if (!proc.waitFor(5000, TimeUnit.MILLISECONDS)) {
            proc.destroyForcibly();
            throw new Exception("External Algorithm did not terminate within 5 seconds after QUIT message");
        }

        return null;
    }

    private void handleSetEdgeLabel(String[] parts, Map<String, Edge> edgeIndex) throws Exception {
        if (parts.length < 2)
            throw new Exception("External Algorithm tries to set edge label, but supplied no edge-handle");
        if (!edgeIndex.containsKey(parts[1]))
            throw new Exception("External Algorithm tries to set label to nonexistent edge \"" + parts[1] + "\"");

        String label = "";
        if (parts.length > 2)
            label = parts[2];

        Edge e = edgeIndex.get(parts[1]);
        e.label = label;
    }

    private void handleSetEdgeColor(String[] parts, Map<String, Edge> edgeIndex) throws Exception {
        if (parts.length < 2)
            throw new Exception("External Algorithm tries to colorize edge, but supplied no edge-handle");
        if (!edgeIndex.containsKey(parts[1]))
            throw new Exception("External Algorithm tries to colorize nonexistent edge \"" + parts[1] + "\"");
        if (parts.length < 3)
            throw new Exception("External Algorithm tries to colorize edge, but supplied no color");

        Edge e = edgeIndex.get(parts[1]);
        GralogColor color = GralogColor.parseColor(parts[2]);
        e.color = color;
    }

    private void handleDeleteEdge(String[] parts, Map<String, Edge> edgeIndex, Structure structure) throws Exception {
        if (parts.length < 2)
            throw new Exception("External Algorithm tries to delete edge, but supplied no edge-handle");
        if (!edgeIndex.containsKey(parts[1]))
            throw new Exception("External Algorithm tries to delete nonexistent edge \"" + parts[1] + "\"");
        Edge e = edgeIndex.get(parts[1]);
        structure.removeEdge(e);
        edgeIndex.remove(parts[1]);
    }

    private void handleCreateEdge(String[] parts, Map<String, Edge> edgeIndex,
                                  Map<String, Vertex> vertexIndex, Structure structure) throws Exception {
        if (parts.length < 4)
            throw new Exception("External Algorithm tries to create edge, "
                    + "but did not supply 1 edge-handle and 2 vertex-handles");
        if (edgeIndex.containsKey(parts[1]))
            throw new Exception("External Algorithm tries to create edge, but edge-handle \""
                    + parts[1] + "\" already exists");
        if (!vertexIndex.containsKey(parts[2]))
            throw new Exception("External Algorithm tries to create edge for nonexistent vertex \""
                    + parts[2] + "\"");
        if (!vertexIndex.containsKey(parts[3]))
            throw new Exception("External Algorithm tries to create edge for nonexistent vertex \""
                    + parts[3] + "\"");
        Vertex v1 = vertexIndex.get(parts[2]);
        Vertex v2 = vertexIndex.get(parts[3]);
        Edge e = structure.createEdge(v1, v2);
        edgeIndex.put(parts[1], e);
        structure.addEdge(e);
    }

    private void handleMoveVertex(String[] parts, Map<String, Vertex> vertexIndex) throws Exception {
        if (parts.length < 2)
            throw new Exception("External Algorithm tries to move vertex, but supplied no vertex-handle");
        if (!vertexIndex.containsKey(parts[1]))
            throw new Exception("External Algorithm tries to move nonexistent vertex \"" + parts[1] + "\"");
        if (parts.length < 4)
            throw new Exception("External Algorithm tries to move vertex, but did not supply 2 coordinates");

        Vertex v = vertexIndex.get(parts[1]);
        Double x = Double.parseDouble(parts[2]);
        Double y = Double.parseDouble(parts[3]);

        v.setCoordinates(x, y);
    }

    private void handleSetVertexColor(String[] parts, Map<String, Vertex> vertexIndex) throws Exception {
        if (parts.length < 2)
            throw new Exception("External Algorithm tries to colorize vertex, but supplied no vertex-handle");
        if (!vertexIndex.containsKey(parts[1]))
            throw new Exception("External Algorithm tries to colorize nonexistent vertex \"" + parts[1] + "\"");
        if (parts.length < 3)
            throw new Exception("External Algorithm tries to colorize vertex, but supplied no color");

        Vertex v = vertexIndex.get(parts[1]);
        GralogColor color = GralogColor.parseColor(parts[2]);
        v.fillColor = color;
    }

    private void handleSetVertexLabel(String[] parts, Map<String, Vertex> vertexIndex) throws Exception {
        if (parts.length < 2)
            throw new Exception("External Algorithm tries to set vertex label, but supplied no vertex-handle");
        if (!vertexIndex.containsKey(parts[1]))
            throw new Exception("External Algorithm tries to set label to nonexistent vertex \"" + parts[1] + "\"");

        String label = "";
        if (parts.length > 2)
            label = parts[2];

        Vertex v = vertexIndex.get(parts[1]);
        v.label = label;
    }

    private void handleDeleteVertex(String[] parts,
                                    Map<String, Vertex> vertexIndex, Structure structure) throws Exception {
        if (parts.length < 2)
            throw new Exception("External Algorithm tries to delete vertex, but supplied no vertex-handle");
        if (!vertexIndex.containsKey(parts[1]))
            throw new Exception("External Algorithm tries to delete nonexistent vertex \"" + parts[1] + "\"");
        Vertex v = vertexIndex.get(parts[1]);
        structure.removeVertex(v);
        vertexIndex.remove(parts[1]);
    }

    private void handleCreateVertex(String[] parts,
                                    Map<String, Vertex> vertexIndex, Structure structure) throws Exception {
        if (parts.length < 2)
            throw new Exception("External Algorithm tries to create vertex, but supplied no vertex-handle");
        if (vertexIndex.containsKey(parts[1]))
            throw new Exception("External Algorithm tries to create vertex, but handle \""
                    + parts[1] + "\" already exists");
        Vertex v = structure.createVertex();
        v.setCoordinates(0d, 0d);
        vertexIndex.put(parts[1], v);
        structure.addVertex(v);
    }
}
