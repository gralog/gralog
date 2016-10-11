package gralog.modallogic.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import gralog.structure.*;
import java.util.HashSet;

public class ModalLogicTop extends ModalLogicFormula {

    public ModalLogicTop() {
    }

    @Override
    public HashSet<World> interpretation(KripkeStructure structure) {
        HashSet<World> result = new HashSet<>();
        for (Vertex v : structure.getVertices())
            if (v instanceof World)
                result.add((World) v);
        return result;
    }
}
