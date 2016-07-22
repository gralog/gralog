/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.modallogic;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import gralog.plugins.XmlName;
import gralog.structure.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.HashSet;

/**
 *
 * @author viktor
 */
@XmlName(name="world")
public class World extends Vertex {
    
    public String Propositions = "";

    @Override
    public Element ToXml(Document doc, String id) throws Exception
    {
        Element vnode = super.ToXml(doc, id);
        vnode.setAttribute("propositions", Propositions);
        return vnode;
    }
    
    @Override
    public String FromXml(Element vnode)
    {
        String id = super.FromXml(vnode);
        if(vnode.hasAttribute("propositions"))
            Propositions = vnode.getAttribute("propositions");
        return id;
    }
    
    public boolean SatisfiesProposition(String proposition)
    {
        proposition = proposition.trim();
        String[] props = Propositions.split(",");
        for(String prop : props)
            if(proposition.equalsIgnoreCase(prop.trim()))
                return true;
        return false;
    }
    
}
