/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx.events;

import gralog.structure.Structure;
import gralog.gralogfx.StructurePane;
import gralog.progresshandler.*;

/**
 *
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
