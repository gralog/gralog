/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.parser;

/**
 * Accepts integers, possibly restricted to a given range.
 */
public class IntSyntaxChecker extends SyntaxChecker {

    int lowerBound, upperBound;

    public IntSyntaxChecker() {
        this.lowerBound = Integer.MIN_VALUE;
        this.upperBound = Integer.MAX_VALUE;
    }

    public IntSyntaxChecker(int lowerBound, int upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public Result check(String input) {
        Result r = new Result();
        r.syntaxCorrect = false;
        try {
            int i = Integer.parseInt(input);
            if (i < lowerBound) {
                r.hint = "Please enter an integer larger or equal to " + lowerBound;
                return r;
            }
            if (i > upperBound) {
                r.hint = "Please enter an integer smaller or equal to " + upperBound;
                return r;
            }
        } catch (NumberFormatException e) {
            r.hint = "Please enter an integer";
            return r;
        }
        r.hint = "";
        r.syntaxCorrect = true;
        return r;
    }
}
