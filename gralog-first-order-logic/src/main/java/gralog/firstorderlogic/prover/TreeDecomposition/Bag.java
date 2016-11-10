/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.firstorderlogic.prover.TreeDecomposition;

import gralog.structure.Vertex;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

/**
 *
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
