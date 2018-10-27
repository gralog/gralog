/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.modallogic;

import gralog.preferences.Configuration;
import gralog.structure.*;
import gralog.plugins.*;

/**
 *
 */
@StructureDescription(
    name = "Kripke Structure",
    text = "",
    url = "https://en.wikipedia.org/wiki/Kripke_structure_(model_checking)")
@XmlName(name = "kripkestructure")
public class KripkeStructure extends Structure<World, Action> {

    @Override
    public World createVertex() {
        return new World();
    }

    @Override
    public World createVertex(Configuration config) {
        return new World(config);
    }

    @Override
    public Action createEdge(Configuration config) {
        return new Action(config);
    }

}
