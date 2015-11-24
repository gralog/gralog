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
    
    public HashSet<String> propositions = new HashSet<String>();

    @Override
    public Element ToXml(Document doc, String id) throws Exception {
        Element vnode = super.ToXml(doc, id);
        String props = "";
        for(String str : propositions)
            props = props + "," + str;
        if(!props.equals(""))
            vnode.setAttribute("propositions", props.substring(1));
        return vnode;
    }
    
    @Override
    public String FromXml(Element vnode) {
        String id = super.FromXml(vnode);
        String props = vnode.getAttribute("propositions");
        if(props != null)
            for(String prop : props.split(","))
                if(!prop.equals(""))
                    this.propositions.add(prop);
        return id;
    }
    
}
