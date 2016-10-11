/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton;

import gralog.plugins.XmlName;
import gralog.structure.*;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 */
@XmlName(name = "transition")
public class Transition extends Edge {

    public String Symbol = "";

    @Override
    public Element toXml(Document doc, HashMap<Vertex, String> ids) throws Exception {
        Element enode = super.toXml(doc, ids);
        enode.setAttribute("symbol", Symbol);
        return enode;
    }

    @Override
    public void fromXml(Element enode, HashMap<String, Vertex> ids) throws Exception {
        super.fromXml(enode, ids);
        Symbol = enode.getAttribute("symbol");
    }
}
