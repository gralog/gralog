/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.modalmucalculus.structure;

import gralog.finitegame.structure.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author viktor
 */
public class ParityGamePosition extends FiniteGamePosition {
    
    public Integer Priority = 0;
    
    @Override
    public Element ToXml(Document doc, String id) throws Exception
    {
        Element vnode = super.ToXml(doc, id);
        vnode.setAttribute("priority", Priority.toString());
        return vnode;
    }
    
    @Override
    public String FromXml(Element vnode)
    {
        String id = super.FromXml(vnode);
        if(vnode.hasAttribute("priority"))
            Priority = Integer.parseInt(vnode.getAttribute("priority"));
        return id;
    }
    
}
