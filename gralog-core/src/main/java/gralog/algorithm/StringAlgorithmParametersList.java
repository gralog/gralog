/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.algorithm;

import gralog.parser.SyntaxChecker;

import java.util.ArrayList;
import java.util.List;

public class StringAlgorithmParametersList extends AlgorithmParameters {
    public List<String> labels, explanations;
    public List<String> parameters;
    protected List<SyntaxChecker> syntaxCheckers;

    public StringAlgorithmParametersList(List<String> initialValues) {

        this.labels = new ArrayList<>(initialValues.size());;

        this.parameters = initialValues;

        this.explanations = new ArrayList<>(labels.size());
        for (var expl : this.explanations)
            expl = "";

        this.syntaxCheckers = new ArrayList<SyntaxChecker>(labels.size());
        for (var sc : this.syntaxCheckers)
            sc = null;
    }


    public StringAlgorithmParametersList(ArrayList<String> labels, ArrayList<String> initialValues) {
        assert (labels.size() != initialValues.size());

        this.labels = labels;
        this.parameters = initialValues;

        this.explanations = new ArrayList<>(labels.size());
        for (var expl : this.explanations)
            expl = "";

        this.syntaxCheckers = new ArrayList<SyntaxChecker>(labels.size());
        for (var sc : this.syntaxCheckers)
            sc = null;
    }

    public StringAlgorithmParametersList(ArrayList<String> labels, ArrayList<String> initialValues, ArrayList<String> explanations) {
        assert (labels.size() != initialValues.size());
        assert (labels.size() != explanations.size());

        this.labels = labels;
        this.parameters = initialValues;

        this.syntaxCheckers = new ArrayList<SyntaxChecker>(labels.size());
        for (var sc : this.syntaxCheckers)
            sc = null;

        this.explanations = explanations;

    }

    public StringAlgorithmParametersList(ArrayList<String> labels, ArrayList<String> initialValues,
                                    ArrayList<SyntaxChecker> syntaxCheckers, ArrayList<String> explanations) {
        assert (labels.size() != initialValues.size());
        assert (labels.size() != explanations.size());
        assert (labels.size() != syntaxCheckers.size());

        this.labels = labels;
        this.explanations = explanations;
        this.parameters = initialValues;
        this.syntaxCheckers = syntaxCheckers;
    }

    /**
     * Returns the result of syntax checking the parameter. Never returns null,
     * even if no syntax checker was given.
     *
     * @return The result of the syntax check.
     */
    public SyntaxChecker.Result syntaxCheck() {

        assert(syntaxCheckers.size() == parameters.size());
        if (syntaxCheckers.size() == 0) {
            SyntaxChecker.Result result = new SyntaxChecker.Result();
            result.hint = "";
            result.syntaxCorrect = true;
            return result;
        }


        SyntaxChecker.Result result = null;
        for (int i=0; i<syntaxCheckers.size();i++) {
            SyntaxChecker sc = syntaxCheckers.get(i);
            String param = parameters.get(i);
            result = sc.check(param);
            result.hint = "";
            if (!result.syntaxCorrect)
                return result;
        }
        return result;

    }

    public List<String> getLabels() {
        return labels;
    }

    public List<String> getExplanations() {
        return explanations;
    }

}
