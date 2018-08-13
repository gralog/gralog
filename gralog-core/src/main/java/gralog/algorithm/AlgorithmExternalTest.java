/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.algorithm;

import gralog.exportfilter.*;

/**
 *
 */
@AlgorithmDescription(
    name = "External Algorithm Test",
    text = "Test-Class for running external algorithms",
    url = ""
)
public class AlgorithmExternalTest extends AlgorithmExternal {

    public AlgorithmExternalTest() {
    	// System.out.println("external");
        super(new TrivialGraphFormatExport(), false, "gralogexternaltest");
    }

}
