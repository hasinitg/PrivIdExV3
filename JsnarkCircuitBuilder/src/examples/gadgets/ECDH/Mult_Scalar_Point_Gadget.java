package examples.gadgets.ECDH;

import circuit.operations.Gadget;
import circuit.structure.Wire;

//Basic building block of multiplying a point on EC by a scalar
public class Mult_Scalar_Point_Gadget extends Gadget implements ECDHBase {
    @Override
    public Wire[] getOutputWires() {
        return new Wire[0];
    }



}
