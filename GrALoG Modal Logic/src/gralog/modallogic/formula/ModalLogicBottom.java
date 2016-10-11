package gralog.modallogic.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import java.util.HashSet;

public class ModalLogicBottom extends ModalLogicFormula {

    public ModalLogicBottom() {
    }

    @Override
    public HashSet<World> interpretation(KripkeStructure structure) {
        return new HashSet<>();
    }
}
