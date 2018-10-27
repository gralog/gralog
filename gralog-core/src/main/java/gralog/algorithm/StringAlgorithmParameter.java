/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.algorithm;

import gralog.parser.SyntaxChecker;

/**
 *
 */
public class StringAlgorithmParameter extends AlgorithmParameters {

    public final String label, explanation;
    public String parameter = "";
    private SyntaxChecker syntaxChecker = null;

    public StringAlgorithmParameter(String label, String initialValue) {
        this.label = label;
        this.explanation = "";
        this.parameter = initialValue;
    }

    public StringAlgorithmParameter(String label, String initialValue, String explanation) {
        this.label = label;
        this.explanation = explanation;
        this.parameter = initialValue;
    }

    public StringAlgorithmParameter(String label, String initialValue,
                                    SyntaxChecker syntaxChecker, String explanation) {
        this.label = label;
        this.explanation = explanation;
        this.parameter = initialValue;
        this.syntaxChecker = syntaxChecker;
    }

    /**
     * Returns the result of syntax checking the parameter. Never returns null,
     * even if no syntax checker was given.
     *
     * @return The result of the syntax check.
     */
    public SyntaxChecker.Result syntaxCheck() {
        if (syntaxChecker == null) {
            SyntaxChecker.Result result = new SyntaxChecker.Result();
            result.hint = "";
            result.syntaxCorrect = true;
            return result;
        }
        return syntaxChecker.check(parameter);
    }

    public String getLabel() {
        return label;
    }

    public String getExplanation() {
        return explanation;
    }
}
