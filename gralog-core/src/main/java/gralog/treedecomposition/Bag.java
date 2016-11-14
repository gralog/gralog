/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.treedecomposition;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import gralog.structure.*;
import java.util.List;

/**
 *
 */
public class Bag {

    public Set<Vertex> nodes = new HashSet<>();
    public List<Bag> childBags = new ArrayList<>();

    @Override
    public String toString() {
        String result = "{";
        String glue = "";

        for (Vertex v : nodes) {
            result += glue + v.label;
            glue = ",";
        }

        return result + "}";
    }

}
