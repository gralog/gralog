/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.automaton;

import gralog.plugins.XmlName;
import gralog.preferences.Configuration;
import gralog.structure.Edge;
import gralog.structure.Vertex;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashMap;

/**
 *
 */
@XmlName(name = "transition")
public class Transition extends Edge {

    public String symbol = "";

    public Transition() {

    }

    public Transition(Configuration config) {
        super(config);
    }
    @Override
    public Element toXml(Document doc, HashMap<Vertex, String> ids) throws Exception {
        Element enode = super.toXml(doc, ids);
        enode.setAttribute("symbol", symbol);
        return enode;
    }

    @Override
    public void fromXml(Element enode, HashMap<String, Vertex> ids) throws Exception {
        super.fromXml(enode, ids);
        symbol = enode.getAttribute("symbol");
    }
}
