/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.automaton.generator;

import gralog.algorithm.AlgorithmParameters;
import gralog.algorithm.StringAlgorithmParameter;
import gralog.generator.*;
import gralog.automaton.*;
import gralog.automaton.regularexpression.*;
import gralog.automaton.regularexpression.parser.*;
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
        return new StringAlgorithmParameter("Regular expression", "");
    }

    @Override
    public Structure generate(AlgorithmParameters p) throws Exception {
        StringAlgorithmParameter sp = (StringAlgorithmParameter) (p);

        RegularExpressionParser parser = new RegularExpressionParser();
        RegularExpression regexp = parser.parseString(sp.parameter);
        Automaton result = regexp.thompsonConstruction();

        return result;
    }
}
