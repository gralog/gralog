/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.importfilter;

/**
 *
 * @author viktor
 */
@ImportFilterDescription(
        name = "Space Separated Values",
        text = "",
        url = "https://reference.wolfram.com/language/ref/format/TSV.html",
        fileExtension = "ssv"
)
public class SpaceSeparatedValuesImport extends CommaSeparatedValuesImport {

    public SpaceSeparatedValuesImport() {
        super();
        this.cellSeparator = " ";
    }
}
