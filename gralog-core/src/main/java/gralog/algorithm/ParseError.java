/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.algorithm;

/**
 * A parse error.
 */
public class ParseError extends Exception {

    private final String inputString;
    private final int errorIndex;

    public ParseError(String message, String inputString, int errorIndex) {
        super("Parse error: " + message + " at: "
                + inputString.substring(0, errorIndex) + " <HERE> "
                + inputString.substring(errorIndex));
        this.inputString = inputString;
        this.errorIndex = errorIndex;
    }

    public String getGoodPrefix() {
        return inputString.substring(0, errorIndex);
    }

    public String getBadSuffix() {
        return inputString.substring(errorIndex);
    }

    public String getInputString() {
        return inputString;
    }

    public int getErrorIndex() {
        return errorIndex;
    }
}
