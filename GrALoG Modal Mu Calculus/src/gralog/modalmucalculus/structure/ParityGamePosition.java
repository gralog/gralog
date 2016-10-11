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

    public Integer priority = Integer.MAX_VALUE;

    @Override
    public Element toXml(Document doc, String id) throws Exception {
        Element vnode = super.toXml(doc, id);
        vnode.setAttribute("priority", priority.toString());
        return vnode;
    }

    @Override
    public String fromXml(Element vnode) {
        String id = super.fromXml(vnode);
        if (vnode.hasAttribute("priority"))
            priority = Integer.parseInt(vnode.getAttribute("priority"));
        return id;
    }
}
