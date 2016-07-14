
package gralog.algorithm;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

import gralog.structure.*;
import gralog.progresshandler.*;

/**
 *
 * @author viktor
 */


@AlgorithmDescription(
  name="Strongly Connected Components",
  text="Finds the strongly connected components of a (mixed) graph",
  url="https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm"
)
public class StronglyConnectedComponents extends Algorithm
{
    public static void TarjanStrongComponents(Structure s, HashMap<Vertex, Integer> ComponentOfVertex,
                                ArrayList<ArrayList<Vertex> > VerticesInComponent)
    {
        int numScc = 0;
        int index = 0;
        Stack<Vertex> tarStack = new Stack<>();
        HashSet<Vertex> onStack = new HashSet<>();

        HashMap<Vertex, Integer> dfs = new HashMap<>();
        HashMap<Vertex, Integer> lowlink = new HashMap<>();
        HashMap<Vertex, Vertex> parent = new HashMap<>();

        HashMap<Vertex, ArrayList<Vertex>> children = new HashMap<>(); // children in dfs tree
        HashMap<Vertex, Integer> childIterationPos = new HashMap<>(); // children-iteration position

        Set<Vertex> V = s.getVertices();
        for (Vertex v : V)
        {
            if (dfs.containsKey(v)) // already processed
                continue;

            dfs.put(v, index);
            lowlink.put(v, index);
            index++;

            ArrayList<Vertex> vChildren = new ArrayList<>();
            for(Edge e : v.getConnectedEdges())
                if(e.getSource() == v)
                    vChildren.add(e.getTarget());
                else if(!e.isDirected)
                    vChildren.add(e.getSource());
            children.put(v, vChildren);
            childIterationPos.put(v, 0); // iteration starts at index 0

            tarStack.push(v);
            parent.put(v, null);
            onStack.add(v);
            Vertex current = v;

            while(current != null)
            {
                Integer currentChildIdx = childIterationPos.get(current);
                if(currentChildIdx < children.get(current).size()) // <current> has more children
                {
                    Vertex child = children.get(current).get(currentChildIdx);
                    childIterationPos.put(current, currentChildIdx+1);
                    if(!dfs.containsKey(child)) // child wasn't processed yet
                    {
                        parent.put(child, current);

                        ArrayList<Vertex> grandChildren = new ArrayList<>();
                        for(Edge e : child.getConnectedEdges())
                            if(e.getSource() == child)
                                grandChildren.add(e.getTarget());
                            else if(!e.isDirected)
                                grandChildren.add(e.getSource());
                        children.put(child, grandChildren);

                        childIterationPos.put(child, 0); // iteration (on child's children) starts at index 0
                        dfs.put(child, index);
                        lowlink.put(child, index);
                        index++;
                        tarStack.push(child);
                        onStack.add(child);
                        current = child; // recursion
                    }
                    else if(onStack.contains(child))
                    {
                        lowlink.put(current, Math.min(lowlink.get(current), dfs.get(child)));
                    }
                }
                else // collect current scc and go up one recursive call
                {
                    if(lowlink.get(current)    .equals(    dfs.get(current)  ))
                    {
                        Vertex top = null;
                        ArrayList<Vertex> scc = new ArrayList<>();
                        while(top != current)
                        {
                            top = tarStack.pop();
                            onStack.remove(top);
                            scc.add(top);
                            ComponentOfVertex.put(top, numScc);
                        }
                        VerticesInComponent.add(scc);
                        numScc++;
                    }

                    Vertex newLast = parent.get(current); //Go up one recursive call
                    if(newLast != null)
                        lowlink.put(newLast, Math.min(lowlink.get(newLast), lowlink.get(current)));
                    current = newLast;
                }
            } // while(last != null)
        } // for (Vertex i : V)
    }

    public Object Run(Structure s, AlgorithmParameters ap, Set<Object> selection, ProgressHandler onprogress) throws Exception
    {
        HashMap<Vertex, Integer> ComponentOfVertex = new HashMap<>();
        ArrayList<ArrayList<Vertex> > VerticesInComponent = new ArrayList<>();
        
        TarjanStrongComponents(s, ComponentOfVertex, VerticesInComponent);
        HashSet<Vertex> result = new HashSet<>();
        for(Vertex v : VerticesInComponent.get(0))
            result.add(v);
        
        return result;
    }
    
}
