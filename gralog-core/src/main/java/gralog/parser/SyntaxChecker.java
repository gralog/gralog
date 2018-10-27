/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.parser;

import gralog.algorithm.ParseError;

/**
 * A syntax checker for strings.
 */
public abstract class SyntaxChecker {

    /**
     * Performs a syntax check on the given string.
     *
     * @param toCheck The string to check.
     * @return The result of syntax checking the given string.
     */
    public abstract SyntaxChecker.Result check(String toCheck);


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
            } catch (ParseError e) {
                result.hint = e.getMessage();
            } catch (Exception e) {
                result.hint = "Parse error";
            }
        }
        return result;
    }

    /**
     * This function should parse the given formula and in case of an error it
     * should throw an exception or return null. Returning a non-null value is
     * interpreted as success.
     *
     * @param <T> The type produced by the parser.
     */
    @FunctionalInterface
    public interface Parser<T> {

        T test(String formula) throws Exception;
    }

    public static class Result {

        public String hint;
        public boolean syntaxCorrect;
    }
}
