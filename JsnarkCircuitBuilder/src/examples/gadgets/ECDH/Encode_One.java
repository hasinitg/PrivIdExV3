package examples.gadgets.ECDH;

import circuit.operations.Gadget;
import circuit.structure.Wire;

//Gadget for computing fresh encoding of one, using the Elliptic Curve Elgamal in the exponent encoding scheme used in
// PrivIdEx. E.g. Encode(1) = (kB, kP+1B), where k is a secret, B is base point and P is public key.
public class Encode_One extends Gadget implements ECDLBase {

    private AffinePoint basePoint;
    private AffinePoint publicKeyPoint;

    public Encode_One(Wire[] secretBits, Wire basePointX, Wire basePointY, Wire publicKeyPointX, Wire publicKeyPointY){

    }

    @Override
    public Wire[] getOutputWires() {
        return new Wire[0];
    }
}
