/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.importfilter;

/**
 *
 */
@ImportFilterDescription(
        name = "Tab Separated Values",
        text = "",
        url = "https://reference.wolfram.com/language/ref/format/TSV.html",
        fileExtension = "tsv"
)
public class TabSeparatedValuesImport extends CommaSeparatedValuesImport {

    public TabSeparatedValuesImport() {
        super();
        this.cellSeparator = "\t";
    }
}
