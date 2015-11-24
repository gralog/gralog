/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton;

import gralog.plugins.XmlName;
import gralog.structure.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author viktor
 */
@XmlName(name="state")
public class State extends Vertex {
    
    public boolean StartState;
    public boolean FinalState;

    @Override
    public Element ToXml(Document doc, String id) throws Exception {
        Element vnode = super.ToXml(doc, id);
        vnode.setAttribute("initial", StartState ? "true" : "false");
        vnode.setAttribute("final", FinalState ? "true" : "false");
        return vnode;
    }
    
    @Override
    public String FromXml(Element vnode) {
        String id = super.FromXml(vnode);
        StartState = (vnode.getAttribute("initial").equals("true"));
        FinalState = (vnode.getAttribute("final").equals("true"));
        return id;
    }
    
}
