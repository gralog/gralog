package gralog.computationtreelogic.formula;

public class ComputationTreeLogicExistsFinally extends ComputationTreeLogicForwarder {
    // EF phi = E true U phi

    public ComputationTreeLogicExistsFinally(
            ComputationTreeLogicFormula subformula) {
        super(new ComputationTreeLogicExistsUntil(new ComputationTreeLogicTop(), subformula));
    }
}
