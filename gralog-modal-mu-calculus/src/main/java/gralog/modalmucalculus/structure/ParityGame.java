/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.modalmucalculus.structure;

import gralog.plugins.XmlName;
import gralog.preferences.Configuration;
import gralog.structure.*;
import gralog.finitegame.structure.*;

@StructureDescription(
    name = "Parity Game",
    text = "Parity Game Arena",
    url = "https://en.wikipedia.org/wiki/Parity_game")
@XmlName(name = "paritygame")
public class ParityGame extends Structure<ParityGamePosition, FiniteGameMove> {

    @Override
    public ParityGamePosition createVertex() {
        return new ParityGamePosition();
    }

    @Override
    public ParityGamePosition createVertex(Configuration config) {
        return new ParityGamePosition(config);
    }


    @Override
    public FiniteGameMove createEdge(Configuration config) {
        return new FiniteGameMove(config);
    }

}
