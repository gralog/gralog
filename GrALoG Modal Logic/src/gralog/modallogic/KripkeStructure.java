/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.modallogic;

import gralog.structure.*;
import gralog.plugins.*;

/**
 *
 * @author viktor
 */
@StructureDescription(
    name="Kripke Structure",
    text="",
    url="https://en.wikipedia.org/wiki/Kripke_structure_(model_checking)"
)
@XmlName(name="kripkestructure")
public class KripkeStructure extends Structure<World, Action> {

    @Override
    public World CreateVertex() {
        return new World();
    }
    
    @Override
    public Action CreateEdge() {
        return new Action();
    }
    
}
