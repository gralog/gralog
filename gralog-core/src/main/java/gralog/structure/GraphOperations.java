/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.structure;

import java.util.ArrayList;
import java.util.HashSet;

public final class GraphOperations {

    private GraphOperations() { }

    public static <T> void unionLists(ArrayList<T> source1, ArrayList<T> source2, ArrayList<T> target) {
        target.clear();
        target.addAll(source1);
        target.addAll(source2);
    }

    // note: source1 and source2 are guaranteed to have every element only once
    public static <T> void intersectionLists(ArrayList<T> source1, ArrayList<T> source2, ArrayList<T> target) {
        target.clear();
        HashSet tmp = new HashSet<>(source1);
        for (T x : source2)
            if (tmp.contains(x))
                target.add(x);
    }

    public static <T> void symmetricDifferenceLists(ArrayList<T> source1, ArrayList<T> source2, ArrayList<T> target) {
        target.clear();
        HashSet tmp1 = new HashSet<>(source1);
        HashSet tmp2 = new HashSet<>(source2);
        for (T x : source1)
            if (!tmp2.contains(x))
                target.add(x);
        for (T x : source2)
            if (!tmp1.contains(x))
                target.add(x);
    }

    public static <T> void differenceLists(ArrayList<T> source1, ArrayList<T> source2, ArrayList<T> target) {
        target.clear();
        HashSet tmp = new HashSet<>(source2);
        for (T x : source1)
            if (!tmp.contains(x))
                target.add(x);
    }
}
