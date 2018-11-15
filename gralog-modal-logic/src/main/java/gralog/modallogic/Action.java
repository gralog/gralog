/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.modallogic;

import gralog.plugins.XmlName;
import gralog.preferences.Configuration;
import gralog.structure.Edge;
import gralog.structure.Vertex;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 */
@XmlName(name = "action")
public class Action extends Edge {

    public String name = null;

    public Action() {

    }

    public Action(Configuration config) {
        super(config);
    }

    @Override
    public Element toXml(Document doc, HashMap<Vertex, String> ids) throws Exception {
        Element enode = super.toXml(doc, ids);
        if (name != null)
            enode.setAttribute("name", "" + name);
        return enode;
    }

    @Override
    public void fromXml(Element enode, HashMap<String, Vertex> ids) throws Exception {
        super.fromXml(enode, ids);
        name = enode.getAttribute("name");
    }

}
