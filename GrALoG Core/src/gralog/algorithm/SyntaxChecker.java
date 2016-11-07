/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.algorithm;

/**
 * A syntax checker for strings.
 */
abstract public class SyntaxChecker {
    public static class Result {
        public String hint;
        public boolean syntaxCorrect;
    };

    abstract public Result check(String input);
}
