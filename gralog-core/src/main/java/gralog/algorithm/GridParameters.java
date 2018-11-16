package gralog.algorithm;

import gralog.parser.IntSyntaxChecker;
import gralog.parser.SyntaxChecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
*
 */

public class GridParameters extends StringAlgorithmParametersList {
    public GridParameters(List<String> initialValues) {
        super(initialValues);

        this.labels = Arrays.asList("length", "width");

        IntSyntaxChecker sc = new IntSyntaxChecker(1,Integer.MAX_VALUE);
        this.syntaxCheckers = Arrays.asList(sc,sc);


        this.explanations = Arrays.asList("The length should be a positive integer.",
                "The width should be a positive integer.");



    }
}
