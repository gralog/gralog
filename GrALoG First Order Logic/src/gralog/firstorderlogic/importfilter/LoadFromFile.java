/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.importfilter;

import gralog.structure.*;
import gralog.importfilter.*;
import java.io.*;
import java.util.Set;

@ImportFilterDescription(
    name="first order logic formulae",
    text="load formulae from a file",
    url="",
    fileextension="txt"
)
/**
 *
 * @author Hv
 */
public class LoadFromFile extends ImportFilter {
    
    static public int count=1;
    @Override
    public Structure Import(InputStream stream,ImportFilterParameters params) throws Exception{
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        PrintWriter out = new PrintWriter(new BufferedWriter(
                                                 new FileWriter("Formulae" + count + ".txt", true)));
            count++;
        while( (line=reader.readLine())!=null ){
            out.println(line);
          
        }
          out.close();
        return new DirectedGraph();
    }
    
}
