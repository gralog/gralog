/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.treedecomposition;

import gralog.structure.Vertex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
public class Bag {

    public Set<Vertex> nodes = new HashSet<>();
    public List<Bag> childBags = new ArrayList<>();

    @Override
    public String toString() {
        return "{"
                + nodes.stream().map((v) -> v.label).collect(Collectors.joining(","))
                + "}";
    }
}
