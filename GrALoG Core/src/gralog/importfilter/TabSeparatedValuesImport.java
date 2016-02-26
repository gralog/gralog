/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.importfilter;

import gralog.structure.*;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.InputStream;
import java.util.StringTokenizer;


/**
 *
 * @author viktor
 */
@ImportFilterDescription(
  name="Tab Separated Values",
  text="",
  url="https://reference.wolfram.com/language/ref/format/TSV.html",
  fileextension="tsv"
)
public class TabSeparatedValuesImport extends CommaSeparatedValuesImport {

    public TabSeparatedValuesImport()
    {
        super();
        this.CellSeparator = "\t";
    }
    
}
