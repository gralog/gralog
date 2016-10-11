/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.algorithm;

import java.io.PrintWriter;
import java.util.Scanner;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;

import gralog.structure.*;
import gralog.rendering.*;
import gralog.progresshandler.*;
import gralog.exportfilter.ExportFilter;
import java.util.Arrays;
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

    public Object run(Structure structure, AlgorithmParameters params,
            Set<Object> selection, ProgressHandler onProgress) throws Exception {
        List<String> EffectiveCommand = null;
        if (passStructureViaFile) {
            File temp = File.createTempFile("GrALoG", "." + exportFilter.getDescription().fileExtension());
            exportFilter.exportGraph(structure, new OutputStreamWriter(new FileOutputStream(temp)), null);
            EffectiveCommand = new LinkedList<>();
            for (String s : command)
                EffectiveCommand.add(s.replaceAll("%u", "\"" + temp.getAbsolutePath() + "\""));
        }
        else {
            EffectiveCommand = command;
        }

        ProcessBuilder pb = new ProcessBuilder(EffectiveCommand);
        Process proc = pb.start();
        Scanner in = new Scanner(proc.getInputStream());

        Map<String, Vertex> vertexIndex = exportFilter.getVertexNames(structure, null);
        Map<String, Edge> edgeIndex = exportFilter.getEdgeNames(structure, null);

        if (!passStructureViaFile) {
            try (OutputStreamWriter out = new OutputStreamWriter(proc.getOutputStream())) {
                exportFilter.exportGraph(structure, out, null);
            }
        }

        boolean quit = false;
        while (in.hasNextLine() && !quit) {
            String line = in.nextLine();
            String[] parts = line.split(" ");

            switch (parts[0]) {
                case "CREATEVERTEX": {
                    if (parts.length < 2)
                        throw new Exception("External Algorithm tries to create vertex, but supplied no vertex-handle");
                    if (vertexIndex.containsKey(parts[1]))
                        throw new Exception("External Algorithm tries to create vertex, but handle \"" + parts[1] + "\" already exists");
                    Vertex v = structure.createVertex();
                    v.coordinates = new Vector2D(0d, 0d);
                    vertexIndex.put(parts[1], v);
                    structure.addVertex(v);
                    break;
                }

                case "DELETEVERTEX": {
                    if (parts.length < 2)
                        throw new Exception("External Algorithm tries to delete vertex, but supplied no vertex-handle");
                    if (!vertexIndex.containsKey(parts[1]))
                        throw new Exception("External Algorithm tries to delete nonexistent vertex \"" + parts[1] + "\"");
                    Vertex v = vertexIndex.get(parts[1]);
                    structure.removeVertex(v);
                    vertexIndex.remove(parts[1]);
                    break;
                }

                case "SETVERTEXLABEL": {
                    if (parts.length < 2)
                        throw new Exception("External Algorithm tries to set vertex label, but supplied no vertex-handle");
                    if (!vertexIndex.containsKey(parts[1]))
                        throw new Exception("External Algorithm tries to set label to nonexistent vertex \"" + parts[1] + "\"");

                    String label = "";
                    if (parts.length > 2)
                        label = parts[2];

                    Vertex v = vertexIndex.get(parts[1]);
                    v.label = label;
                    break;
                }

                case "SETVERTEXCOLOR": {
                    if (parts.length < 2)
                        throw new Exception("External Algorithm tries to colorize vertex, but supplied no vertex-handle");
                    if (!vertexIndex.containsKey(parts[1]))
                        throw new Exception("External Algorithm tries to colorize nonexistent vertex \"" + parts[1] + "\"");
                    if (parts.length < 3)
                        throw new Exception("External Algorithm tries to colorize vertex, but supplied no color");

                    Vertex v = vertexIndex.get(parts[1]);
                    GralogColor color = GralogColor.parseColor(parts[2]);
                    v.fillColor = color;
                    break;
                }

                case "MOVEVERTEX": {
                    if (parts.length < 2)
                        throw new Exception("External Algorithm tries to move vertex, but supplied no vertex-handle");
                    if (!vertexIndex.containsKey(parts[1]))
                        throw new Exception("External Algorithm tries to move nonexistent vertex \"" + parts[1] + "\"");
                    if (parts.length < 4)
                        throw new Exception("External Algorithm tries to move vertex, but did not supply 2 coordinates");

                    Vertex v = vertexIndex.get(parts[1]);
                    Double x = Double.parseDouble(parts[2]);
                    Double y = Double.parseDouble(parts[3]);

                    v.coordinates = new Vector2D(x, y);
                    break;
                }

                case "CREATEEDGE": {
                    if (parts.length < 4)
                        throw new Exception("External Algorithm tries to create edge, but did not supply 1 edge-handle and 2 vertex-handles");
                    if (edgeIndex.containsKey(parts[1]))
                        throw new Exception("External Algorithm tries to create edge, but edge-handle \"" + parts[1] + "\" already exists");
                    if (!vertexIndex.containsKey(parts[2]))
                        throw new Exception("External Algorithm tries to create edge for nonexistent vertex \"" + parts[2] + "\"");
                    if (!vertexIndex.containsKey(parts[3]))
                        throw new Exception("External Algorithm tries to create edge for nonexistent vertex \"" + parts[3] + "\"");
                    Vertex v1 = vertexIndex.get(parts[2]);
                    Vertex v2 = vertexIndex.get(parts[3]);
                    Edge e = structure.createEdge(v1, v2);
                    edgeIndex.put(parts[1], e);
                    structure.addEdge(e);
                    break;
                }

                case "DELETEEDGE": {
                    if (parts.length < 2)
                        throw new Exception("External Algorithm tries to delete edge, but supplied no edge-handle");
                    if (!edgeIndex.containsKey(parts[1]))
                        throw new Exception("External Algorithm tries to delete nonexistent edge \"" + parts[1] + "\"");
                    Edge e = edgeIndex.get(parts[1]);
                    structure.removeEdge(e);
                    edgeIndex.remove(parts[1]);
                    break;
                }

                case "SETEDGECOLOR": {
                    if (parts.length < 2)
                        throw new Exception("External Algorithm tries to colorize edge, but supplied no edge-handle");
                    if (!edgeIndex.containsKey(parts[1]))
                        throw new Exception("External Algorithm tries to colorize nonexistent edge \"" + parts[1] + "\"");
                    if (parts.length < 3)
                        throw new Exception("External Algorithm tries to colorize edge, but supplied no color");

                    Edge e = edgeIndex.get(parts[1]);
                    GralogColor color = GralogColor.parseColor(parts[2]);
                    e.color = color;
                    break;
                }

                case "SETEDGELABEL": {
                    if (parts.length < 2)
                        throw new Exception("External Algorithm tries to set edge label, but supplied no edge-handle");
                    if (!edgeIndex.containsKey(parts[1]))
                        throw new Exception("External Algorithm tries to set label to nonexistent edge \"" + parts[1] + "\"");

                    String label = "";
                    if (parts.length > 2)
                        label = parts[2];

                    Edge e = edgeIndex.get(parts[1]);
                    e.label = label;
                    break;
                }

                case "QUIT":
                    quit = true;
                    break;
            }

            if (onProgress != null)
                onProgress.onProgress(structure);

        } // while(in.hasNextLine())

        if (!proc.waitFor(5000, TimeUnit.MILLISECONDS)) {
            proc.destroyForcibly();
            throw new Exception("External Algorithm did not terminate within 5 seconds after QUIT message");
        }

        return null;
    }
}
