package gralog.computationtreelogic.formula;

public class ComputationTreeLogicAlwaysNext extends ComputationTreeLogicForwarder {
    // AX phi = not EX not phi

    public ComputationTreeLogicAlwaysNext(ComputationTreeLogicFormula subformula) {
        super(new ComputationTreeLogicNot(
                new ComputationTreeLogicExistsNext(
                        new ComputationTreeLogicNot(subformula))));
    }
}
