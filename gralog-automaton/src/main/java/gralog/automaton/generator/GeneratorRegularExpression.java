/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.automaton.generator;

import gralog.algorithm.AlgorithmParameters;
import gralog.algorithm.StringAlgorithmParameter;
import gralog.automaton.Automaton;
import gralog.automaton.regularexpression.RegularExpression;
import gralog.automaton.regularexpression.parser.RegularExpressionParser;
import gralog.automaton.regularexpression.parser.RegularExpressionSyntaxChecker;
import gralog.generator.Generator;
import gralog.generator.GeneratorDescription;
import gralog.preferences.Preferences;
import gralog.structure.Structure;

/**
 *
 */
@GeneratorDescription(
    name = "Thompson's Construction",
    text = "",
    url = "https://en.wikipedia.org/wiki/Thompson%27s_construction"
)
public class GeneratorRegularExpression extends Generator {

    @Override
    public AlgorithmParameters getParameters() {
        return new StringAlgorithmParameter(
            "Regular expression",
            Preferences.getString(this.getClass(), "regex", "a*b"),
            new RegularExpressionSyntaxChecker(),
            RegularExpressionSyntaxChecker.explanation());
    }

    @Override
    public Structure generate(AlgorithmParameters p) throws Exception {
        StringAlgorithmParameter sp = (StringAlgorithmParameter) (p);
        Preferences.setString(this.getClass(), "regex", sp.parameter);

        RegularExpression regexp = RegularExpressionParser.parseString(sp.parameter);
        Automaton result = regexp.thompsonConstruction();

        return result;
    }
}
