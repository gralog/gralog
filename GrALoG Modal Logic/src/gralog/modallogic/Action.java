/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.modallogic;

import gralog.plugins.XmlName;
import gralog.structure.Edge;
import gralog.structure.Vertex;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author viktor
 */
@XmlName(name="action")
public class Action extends Edge {
    
    public String Name = null;
    
    @Override
    public Element ToXml(Document doc, HashMap<Vertex,String> ids) throws Exception {
        Element enode = super.ToXml(doc, ids);
        if(Name != null)
            enode.setAttribute("name", ""+Name);
        return enode;
    }
    
    @Override    
    public void FromXml(Element enode, HashMap<String,Vertex> ids) throws Exception {
        super.FromXml(enode, ids);
        Name = enode.getAttribute("name");
    }
    
}
