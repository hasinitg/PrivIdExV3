package examples.gadgets.ECDL;

import circuit.operations.Gadget;
import circuit.structure.Wire;

public class UpdateZerothCoefficientGadget extends Gadget implements ECDLBase {



    @Override
    public Wire[] getOutputWires() {
        return new Wire[0];
    }
}
