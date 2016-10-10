/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.treedecomposition;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import gralog.structure.*;
import java.util.List;

/**
 *
 * @author viktor
 */
public class Bag {
    
    public Set<Vertex> Nodes = new HashSet<>();
    
    public List<Bag> ChildBags = new ArrayList<>();
    
    @Override
    public String toString()
    {
        String result = "{";
        String glue = "";
        
        for(Vertex v : Nodes)
        {
            result += glue + v.Label;
            glue = ",";
        }
        
        return result + "}";
    }
    
}
