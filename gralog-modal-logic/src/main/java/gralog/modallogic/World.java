/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.modallogic;

import gralog.plugins.XmlName;
import gralog.preferences.Configuration;
import gralog.structure.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 */
@XmlName(name = "world")
public class World extends Vertex {

    public String propositions = "";


    public World() {
        super();
    }

    public World(Configuration config) {
        super(config);
    }

    @Override
    public Element toXml(Document doc, String id) throws Exception {
        Element vnode = super.toXml(doc, id);
        vnode.setAttribute("propositions", propositions);
        return vnode;
    }

    @Override
    public String fromXml(Element vnode) {
        String id = super.fromXml(vnode);
        if (vnode.hasAttribute("propositions"))
            propositions = vnode.getAttribute("propositions");
        return id;
    }

    public boolean satisfiesProposition(String proposition) {
        proposition = proposition.trim();
        String[] props = propositions.split(",");
        for (String prop : props)
            if (proposition.equalsIgnoreCase(prop.trim()))
                return true;
        return false;
    }
}
