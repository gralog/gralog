
package gralog.computationtreelogic.formula;

public class ComputationTreeLogicAlwaysFinally extends ComputationTreeLogicForwarder
{
    // AF phi = not EG not phi
    
    public ComputationTreeLogicAlwaysFinally(ComputationTreeLogicFormula subformula)
    {
        super(new ComputationTreeLogicNot(
                  new ComputationTreeLogicExistsGlobally(
                      new ComputationTreeLogicNot(subformula))));
    }
}

