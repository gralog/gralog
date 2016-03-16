/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.algorithm;

import gralog.structure.*;
import gralog.exportfilter.*;
import gralog.progresshandler.ProgressHandler;

/**
 *
 * @author viktor
 */
@AlgorithmDescription(
  name="External Algorithm Test",
  text="Test-Class for running external algorithms",
  url=""
)
public class AlgorithmExternalTest extends AlgorithmExternal {
    
    public AlgorithmExternalTest() {
        super(new TrivialGraphFormatExport(), false, "");
    }
    

    @Override
    public AlgorithmParameters GetParameters(Structure structure) {
        return new StringAlgorithmParameter("");
    }    
    
    @Override
    public Object Run(Structure structure, AlgorithmParameters params, ProgressHandler onprogress) throws Exception
    {
        StringAlgorithmParameter p = (StringAlgorithmParameter)params;
        Command.clear();
        Command.add(p.parameter);
        return super.Run(structure, params, onprogress);
    }
    
}
