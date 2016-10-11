/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.modalmucalculus.structure;

import gralog.plugins.XmlName;
import gralog.structure.*;
import gralog.finitegame.structure.*;

@StructureDescription(
        name = "Parity Game",
        text = "Parity Game Arena",
        url = "https://en.wikipedia.org/wiki/Parity_game"
)
@XmlName(name = "paritygame")
public class ParityGame extends Structure<ParityGamePosition, FiniteGameMove> {

    @Override
    public ParityGamePosition createVertex() {
        return new ParityGamePosition();
    }

    @Override
    public FiniteGameMove createEdge() {
        return new FiniteGameMove();
    }
}
