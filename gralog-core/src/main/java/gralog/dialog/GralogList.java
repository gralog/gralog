/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.dialog;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

public class GralogList<T> extends ArrayList<T> {
    //default toString methods to choose from

    public SimpleStringProperty name;
    public SimpleStringProperty stringData;
    private Function<T, String> toString;


    public GralogList(String name) {
        this.name = new SimpleStringProperty(name);
        this.stringData = new SimpleStringProperty("");
    }

    public GralogList(String name, Function<T, String> toString) {
        this(name);
        overrideToString(toString);
    }

    public void overrideToString(Function<T, String> toString) {
        this.toString = toString;
    }

    public void updateStringData() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size(); i++) {
            T elem = get(i);
            if (toString == null) {
                sb.append(elem.toString());
            } else {
                sb.append(toString.apply(elem));
            }

            if (i < size() - 1) {
                sb.append(", ");
            }
        }
        stringData.set(sb.toString());
    }

    @Override
    public String toString() {
        updateStringData();
        return stringData.getValue();
    }

    @Override
    public boolean add(T element) {
        var ret = super.add(element);
        updateStringData();
        return ret;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        var ret = super.addAll(c);
        updateStringData();
        return ret;
    }

    @Override
    public boolean remove(Object element) {
        var ret = super.remove(element);
        updateStringData();
        return ret;
    }

}
