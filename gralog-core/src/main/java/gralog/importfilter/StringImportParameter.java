/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.importfilter;

/**
 *
 */
public class StringImportParameter extends ImportFilterParameters {

    public String parameter;

    public StringImportParameter() {
        this.parameter = "";
    }

    public StringImportParameter(String parameter) {
        this.parameter = parameter;
    }
}
