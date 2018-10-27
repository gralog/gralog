/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.importfilter;

/**
 *
 */
@ImportFilterDescription(
        name = "Space Separated Values",
        text = "",
        url = "https://reference.wolfram.com/language/ref/format/TSV.html",
        fileExtension = "ssv")
public class SpaceSeparatedValuesImport extends CommaSeparatedValuesImport {

    public SpaceSeparatedValuesImport() {
        super();
        this.cellSeparator = " ";
    }
}
