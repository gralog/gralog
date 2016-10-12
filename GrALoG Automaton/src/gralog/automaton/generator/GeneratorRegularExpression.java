/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.automaton.generator;

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
    public GeneratorParameters getParameters() {
        return new StringGeneratorParameter("");
    }

    @Override
    public Structure generate(GeneratorParameters p) throws Exception {
        StringGeneratorParameter sp = (StringGeneratorParameter) (p);

        RegularExpressionParser parser = new RegularExpressionParser();
        RegularExpression regexp = parser.parseString(sp.parameter);
        Automaton result = regexp.thompsonConstruction();

        return result;
    }
}
