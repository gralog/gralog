/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.algorithm;

import gralog.exportfilter.*;

/**
 *
 * @author viktor
 */
@AlgorithmDescription(
        name = "External Algorithm Test",
        text = "Test-Class for running external algorithms",
        url = ""
)
public class AlgorithmExternalTest extends AlgorithmExternal {

    public AlgorithmExternalTest() {
        super(new TrivialGraphFormatExport(), false, "gralogexternaltest");
    }

}
