/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.finitegame.structure;

import gralog.plugins.XmlName;
import gralog.preferences.Configuration;
import gralog.structure.*;

@XmlName(name = "finitegamemove")
public class FiniteGameMove extends Edge {

    public FiniteGameMove() {

    }

    public FiniteGameMove(Configuration config) {
        super(config);
    }

}
