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
    long sleepTime = 0;

    public RedrawOnProgress(StructurePane pane, double sleepTimeSEC) {
        this.pane = pane;
        this.sleepTime = (long) (sleepTimeSEC * 1000);
    }

    @Override
    public void onProgress(Structure s) throws Exception {
        this.pane.requestRedraw();
        if (sleepTime > 0)
            Thread.sleep(sleepTime);
    }
}
