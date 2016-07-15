
package gralog.computationtreelogic.formula;

public class ComputationTreeLogicAlwaysUntil extends ComputationTreeLogicForwarder
{
    
    // A phi U psi = (not E((not psi) U (not phi and not psi))) AND (not EG not psi)
    // there is no path
    //                                  that stops having phis
    //                without starting to have psis
    
    //                                                          AND  psi is actually reached
    //                                                               (not an infinite path of phis)
    
    public ComputationTreeLogicAlwaysUntil(ComputationTreeLogicFormula before, ComputationTreeLogicFormula after)
    {
        super(
            new ComputationTreeLogicAnd(
                new ComputationTreeLogicNot(
                    new ComputationTreeLogicExistsUntil(
                        new ComputationTreeLogicNot(after)
                    ,
                        new ComputationTreeLogicNot(
                            new ComputationTreeLogicOr(before,after)
                        )
                    )
                )
            ,
                new ComputationTreeLogicNot(
                    new ComputationTreeLogicExistsGlobally(
                        new ComputationTreeLogicNot(after)
                    )
                )
            )
        );
    }
}

