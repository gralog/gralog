/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools |  Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.prover.TreeDecomposition;

import gralog.structure.Vertex;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author Hv
 */
public class Bag {
    public String caption;
    public Set<Vertex> Nodes = new HashSet<Vertex>();
    public Vector<Bag> ChildBags = new Vector<Bag>();
 
     @Override
    public String toString()
    {
        return caption;
    }
   
}
