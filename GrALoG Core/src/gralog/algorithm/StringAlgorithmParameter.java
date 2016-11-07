/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.algorithm;

/**
 *
 */
public class StringAlgorithmParameter extends AlgorithmParameters {

    public String parameter = "";
    private SyntaxChecker syntaxChecker = null;
    private String label;

    public StringAlgorithmParameter(String label, String initialValue) {
        this.label = label;
        this.parameter = initialValue;
    }

    public StringAlgorithmParameter(String label, String initialValue,
            SyntaxChecker syntaxChecker) {
        this.label = label;
        this.parameter = initialValue;
        this.syntaxChecker = syntaxChecker;
    }

    /**
     * Returns the result of syntax checking the parameter. Never returns null,
     * even if no syntax checker was given.
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
}
