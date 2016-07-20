/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.structure;

import gralog.structure.*;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Set;
import gralog.plugins.XmlName;
import gralog.structure.*;
/**
 *
 * @author Hv
 */
@StructureDescription(
  name="Game Graph",
  text="",
  url=""
)
@XmlName(name="GameGraph")
public class GameGraph extends Structure<GamePosition,GameMove>{
   
   

    @Override
    public GamePosition CreateVertex() {
        return new GamePosition();
    }

    @Override
    public GameMove CreateEdge() {
        return new GameMove();
    }
    
}
