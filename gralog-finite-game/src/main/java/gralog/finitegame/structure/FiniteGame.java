/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.finitegame.structure;

import gralog.plugins.XmlName;
import gralog.preferences.Configuration;
import gralog.structure.*;

@StructureDescription(
    name = "Finite Game",
    text = "A Finite, two-player game-graph",
    url = "http://mathworld.wolfram.com/FiniteGame.html")
@XmlName(name = "finitegame")
public class FiniteGame extends Structure<FiniteGamePosition, FiniteGameMove> {

    @Override
    public FiniteGamePosition createVertex() {
        return new FiniteGamePosition();
    }

    @Override
    public FiniteGamePosition createVertex(Configuration config) {
        return new FiniteGamePosition(config);
    }

    @Override
    public FiniteGameMove createEdge(Configuration config) {
        return new FiniteGameMove(config);
    }
}
