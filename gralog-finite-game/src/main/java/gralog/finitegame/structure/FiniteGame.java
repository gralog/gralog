/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.finitegame.structure;

import gralog.plugins.XmlName;
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
    public FiniteGameMove createEdge() {
        return new FiniteGameMove();
    }
}
