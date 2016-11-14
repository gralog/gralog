/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.modallogic;

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
    public Action createEdge() {
        return new Action();
    }
}
