package gralog.algorithm;

import gralog.parser.DoubleSyntaxChecker;
import gralog.parser.IntSyntaxChecker;
import gralog.parser.SyntaxChecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 *
 */

public class RandomGraphParameters extends StringAlgorithmParametersList {
    public RandomGraphParameters(List<String> initialValues) {
        super(initialValues);

        this.labels = Arrays.asList("number of vertices", "probability of an edge", "directed");

        IntSyntaxChecker isc = new IntSyntaxChecker(1,Integer.MAX_VALUE);
        DoubleSyntaxChecker dsc = new DoubleSyntaxChecker(0.0, 1.0);
        List<SyntaxChecker> syntaxCheckers = Arrays.asList(isc,dsc,null);
        this.syntaxCheckers = syntaxCheckers;

        List<String> explanations = Arrays.asList("The number of vertices in a random graph should be at least 1.",
                "The probability of an edge is a real number between 0 and 1.", "");

        this.explanations = explanations;

    }
}

