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
 * @author viktor
 */
@XmlName(name="transition")
public class Transition extends Edge {
    
    public String Symbol = "";
    
    @Override
    public Element ToXml(Document doc, HashMap<Vertex,String> ids) throws Exception {
        Element enode = super.ToXml(doc, ids);
        enode.setAttribute("symbol", Symbol);
        return enode;
    }
    
    @Override    
    public void FromXml(Element enode, HashMap<String,Vertex> ids) {
        super.FromXml(enode, ids);
        Symbol = enode.getAttribute("symbol");
    }    
}
