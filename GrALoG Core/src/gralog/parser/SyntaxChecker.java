/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.parser;

import gralog.algorithm.ParseError;

/**
 * A syntax checker for strings.
 */
abstract public class SyntaxChecker {

    public static class Result {

        public String hint;
        public boolean syntaxCorrect;
    };

    /**
     * Performs a syntax check on the given string.
     *
     * @param toCheck The string to check.
     * @return The result of syntax checking the given string.
     */
    abstract public SyntaxChecker.Result check(String toCheck);

    /**
     * This function should parse the given formula and in case of an error it
     * should throw an exception or return null. Returning a non-null value is
     * interpreted as success.
     *
     * @param <T> The type produced by the parser.
     */
    @FunctionalInterface
    public static interface Parser<T> {

        public T test(String formula) throws Exception;
    }

    protected SyntaxChecker.Result checkWith(String toCheck,
            Parser parser) {
        SyntaxChecker.Result result = new SyntaxChecker.Result();
        result.hint = "";
        result.syntaxCorrect = false;
        if (toCheck.isEmpty())
            result.syntaxCorrect = true;
        else {
            try {
                result.syntaxCorrect = parser.test(toCheck) != null;
            }
            catch (ParseError e) {
                result.hint = e.getMessage();
            }
            catch (Exception e) {
                result.hint = "Parse error";
            }
        }
        return result;
    }
}
