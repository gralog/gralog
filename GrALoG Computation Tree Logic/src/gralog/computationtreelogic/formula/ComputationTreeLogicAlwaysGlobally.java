package gralog.computationtreelogic.formula;

public class ComputationTreeLogicAlwaysGlobally extends ComputationTreeLogicForwarder {
    // AG phi = not EF not phi

    public ComputationTreeLogicAlwaysGlobally(
            ComputationTreeLogicFormula subformula) {
        super(new ComputationTreeLogicNot(
                new ComputationTreeLogicExistsFinally(
                        new ComputationTreeLogicNot(subformula)
                )));
    }
}
