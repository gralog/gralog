/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools |  Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.prover.TreeDecomposition;

import gralog.structure.Vertex;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hv
 */
public class Bag {

    public String caption = "";
    public String assignment = "";
    public Boolean eval = false;
    public Set<Vertex> Nodes = new HashSet<>();
    public List<Bag> ChildBags = new ArrayList<>();

    @Override
    public String toString() {
        return assignment + " " + caption;
    }
}
