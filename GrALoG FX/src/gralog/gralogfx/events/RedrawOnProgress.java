/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.gralogfx.events;

import gralog.structure.Structure;
import gralog.gralogfx.StructurePane;
import gralog.progresshandler.*;

/**
 *
 * @author viktor
 */
public class RedrawOnProgress extends ProgressHandler {
    
    StructurePane pane;
    
    public RedrawOnProgress(StructurePane pane) {
        this.pane = pane;
    }
    
    @Override
    public void OnProgress(Structure s) throws Exception {
        this.pane.RequestRedraw();
    }
    
}
