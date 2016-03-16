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
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

import gralog.structure.*;
import gralog.rendering.*;
import gralog.progresshandler.*;
import gralog.exportfilter.ExportFilter;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author viktor
 */
public abstract class AlgorithmExternal extends Algorithm {
    
    // set these explicitly in the constructor of your derived classes!
    // e.g. the command should not be set via an AlgorithmParameter
    // the class must know these - it is not the users job to know them!
    protected List<String> Command = new LinkedList<String>();
    ExportFilter exportFilter = null;
    boolean PassStructureViaFile = false;
    
    
    protected AlgorithmExternal(ExportFilter exportFilter, boolean PassStructureViaFile, String... Command)
    {
        for(String s : Command)
            this.Command.add(s);
        this.exportFilter = exportFilter;
        this.PassStructureViaFile = PassStructureViaFile;
    }
    
    
    
    public Object Run(Structure structure, AlgorithmParameters params, ProgressHandler onprogress) throws Exception
    {
        List<String> EffectiveCommand = null;
        if(PassStructureViaFile)
        {
            File temp = File.createTempFile("GrALoG", "." + exportFilter.getDescription().fileextension()); 
            exportFilter.DoExport(structure, new OutputStreamWriter(new FileOutputStream(temp)), null);
            EffectiveCommand = new LinkedList<String>();
            for(String s : Command)
                EffectiveCommand.add(s.replaceAll("%u", "\"" + temp.getAbsolutePath() + "\""));
        }
        else
        {
            EffectiveCommand = Command;
        }
        
        ProcessBuilder pb = new ProcessBuilder(EffectiveCommand);
        Process proc = pb.start();
        Scanner in = new Scanner(proc.getInputStream());
        OutputStream outstream = proc.getOutputStream();
        PrintWriter out = new PrintWriter(outstream);
        
        HashMap<String, Vertex> VertexIndex = new HashMap<String, Vertex>();
        HashMap<String, Edge> EdgeIndex = new HashMap<String, Edge>();
        
        if(!PassStructureViaFile)
        {
            exportFilter.DoExport(structure, new OutputStreamWriter(outstream), null);
            outstream.flush();
            outstream.close();
        }
        
        
        boolean quitted = false;
        while(in.hasNextLine() && !quitted)
        {
            String line = in.nextLine();
            String[] parts = line.split(" ");
            
            switch(parts[0])
            {
                case "CREATEVERTEX":
                {
                    if(parts.length < 2)
                        throw new Exception("External Algorithm tries to create vertex, but supplied no vertex-handle");
                    if(VertexIndex.containsKey(parts[1]))
                        throw new Exception("External Algorithm tries to create vertex, but handle \"" + parts[1] + "\" already exists");
                    Vertex v = structure.CreateVertex();
                    v.Coordinates.add(0d);
                    v.Coordinates.add(0d);
                    VertexIndex.put(parts[1], v);
                    structure.AddVertex(v);
                    break;
                }

                case "DELETEVERTEX":
                {
                    if(parts.length < 2)
                        throw new Exception("External Algorithm tries to delete vertex, but supplied no vertex-handle");
                    if(!VertexIndex.containsKey(parts[1]))
                        throw new Exception("External Algorithm tries to delete nonexistent vertex \"" + parts[1] + "\"");
                    Vertex v = VertexIndex.get(parts[1]);
                    structure.RemoveVertex(v);
                    VertexIndex.remove(parts[1]);
                    break;
                }

                case "SETVERTEXLABEL":
                {
                    if(parts.length < 2)
                        throw new Exception("External Algorithm tries to set vertex label, but supplied no vertex-handle");
                    if(!VertexIndex.containsKey(parts[1]))
                        throw new Exception("External Algorithm tries to set label to nonexistent vertex \"" + parts[1] + "\"");

                    String label = "";
                    if(parts.length > 2)
                        label = parts[2];

                    Vertex v = VertexIndex.get(parts[1]);
                    v.Label = label;
                    break;
                }

                case "SETVERTEXCOLOR":
                {
                    if(parts.length < 2)
                        throw new Exception("External Algorithm tries to colorize vertex, but supplied no vertex-handle");
                    if(!VertexIndex.containsKey(parts[1]))
                        throw new Exception("External Algorithm tries to colorize nonexistent vertex \"" + parts[1] + "\"");
                    if(parts.length < 3)
                        throw new Exception("External Algorithm tries to colorize vertex, but supplied no color");

                    Vertex v = VertexIndex.get(parts[1]);
                    GralogColor color = GralogColor.parseColor(parts[2]);
                    v.FillColor = color;
                    break;
                }

                case "MOVEVERTEX":
                {
                    if(parts.length < 2)
                        throw new Exception("External Algorithm tries to move vertex, but supplied no vertex-handle");
                    if(!VertexIndex.containsKey(parts[1]))
                        throw new Exception("External Algorithm tries to move nonexistent vertex \"" + parts[1] + "\"");
                    if(parts.length < 4)
                        throw new Exception("External Algorithm tries to move vertex, but did not supply 2 coordinates");

                    Vertex v = VertexIndex.get(parts[1]);
                    Double x = Double.parseDouble(parts[2]);
                    Double y = Double.parseDouble(parts[3]);

                    v.Coordinates.clear();
                    v.Coordinates.add(x);
                    v.Coordinates.add(y);
                    break;
                }

                
                
                case "CREATEEDGE":
                {
                    if(parts.length < 4)
                        throw new Exception("External Algorithm tries to create edge, but did not supply 1 edge-handle and 2 vertex-handles");
                    if(EdgeIndex.containsKey(parts[1]))
                        throw new Exception("External Algorithm tries to create edge, but edge-handle \"" + parts[1] + "\" already exists");
                    if(!VertexIndex.containsKey(parts[2]))
                        throw new Exception("External Algorithm tries to create edge for nonexistent vertex \"" + parts[2] + "\"");
                    if(!VertexIndex.containsKey(parts[3]))
                        throw new Exception("External Algorithm tries to create edge for nonexistent vertex \"" + parts[3] + "\"");
                    Vertex v1 = VertexIndex.get(parts[2]);
                    Vertex v2 = VertexIndex.get(parts[3]);
                    Edge e = structure.CreateEdge(v1, v2);
                    EdgeIndex.put(parts[1], e);
                    structure.AddEdge(e);
                    break;
                }

                case "DELETEEDGE":
                {
                    if(parts.length < 2)
                        throw new Exception("External Algorithm tries to delete edge, but supplied no edge-handle");
                    if(!EdgeIndex.containsKey(parts[1]))
                        throw new Exception("External Algorithm tries to delete nonexistent edge \"" + parts[1] + "\"");
                    Edge e = EdgeIndex.get(parts[1]);
                    structure.RemoveEdge(e);
                    EdgeIndex.remove(parts[1]);
                    break;
                }

                case "SETEDGECOLOR":
                {
                    if(parts.length < 2)
                        throw new Exception("External Algorithm tries to colorize edge, but supplied no edge-handle");
                    if(!EdgeIndex.containsKey(parts[1]))
                        throw new Exception("External Algorithm tries to colorize nonexistent edge \"" + parts[1] + "\"");
                    if(parts.length < 3)
                        throw new Exception("External Algorithm tries to colorize edge, but supplied no color");

                    Edge e = EdgeIndex.get(parts[1]);
                    GralogColor color = GralogColor.parseColor(parts[2]);
                    e.Color = color;
                    break;
                }

                case "SETEDGELABEL":
                {
                    if(parts.length < 2)
                        throw new Exception("External Algorithm tries to set edge label, but supplied no edge-handle");
                    if(!EdgeIndex.containsKey(parts[1]))
                        throw new Exception("External Algorithm tries to set label to nonexistent edge \"" + parts[1] + "\"");

                    String label = "";
                    if(parts.length > 2)
                        label = parts[2];

                    Edge e = EdgeIndex.get(parts[1]);
                    e.Label = label;
                    break;
                }

                case "QUIT":
                    quitted = true;
                    break;
            }

            if(onprogress != null)
                onprogress.OnProgress(structure);
            
        } // while(in.hasNextLine())
        
        if(!proc.waitFor(5000, TimeUnit.MILLISECONDS))
        {
            proc.destroyForcibly();
            throw new Exception("External Algorithm did not terminate within 5 seconds after QUIT message");
        }
        
        return null;
    }
}
