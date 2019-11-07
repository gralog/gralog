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

public class CycleParameters extends StringAlgorithmParametersList {
    public CycleParameters(List<String> initialValues) {
        super(initialValues);

        this.labels = Arrays.asList("cycleVertexNumber", "directed");

        IntSyntaxChecker isc = new IntSyntaxChecker(1,Integer.MAX_VALUE);
        List<SyntaxChecker> syntaxCheckers = Arrays.asList(isc,null);
        this.syntaxCheckers = syntaxCheckers;


        List<String> explanations = Arrays.asList("", "");
        this.explanations = explanations;


    }
}
