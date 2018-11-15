/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.modalmucalculus.structure;

import gralog.finitegame.structure.*;
import gralog.preferences.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 */
public class ParityGamePosition extends FiniteGamePosition {

    public Integer priority = Integer.MAX_VALUE;

    public ParityGamePosition() {
        super();
    }

    public ParityGamePosition(Configuration config) {
        super(config);
    }

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
