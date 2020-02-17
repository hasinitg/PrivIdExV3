package examples.gadgets.ECDL;

import circuit.operations.Gadget;
import circuit.structure.Wire;

//Gadget for computing fresh encoding of one, using the Elliptic Curve Elgamal in the exponent encoding scheme used in
// PrivIdEx. E.g. Encode(1) = (kB, kP+1B), where k is a secret, B is base point and P is public key.
public class EncodeOneGadget extends Gadget implements ECDLBase {

    //inputs
    private AffinePoint basePoint;
    private AffinePoint publicKeyPoint;
    private Wire[] secretBits;

    //sub circuits
    private MultScalarTwoPointsGadget multScalarTwoPoints;
    private AddTwoPointsGadget addTwoPoints;

    //output
    private Encoding encodingOfOne;

    public EncodeOneGadget(Wire[] secretBits, AffinePoint basePoint, AffinePoint publicKeyPoint, String desc){
        super(desc);
        this.basePoint = basePoint;
        this.publicKeyPoint = publicKeyPoint;
        this.secretBits = secretBits;

        buildCircuit();
    }

    protected void buildCircuit(){
        multScalarTwoPoints = new MultScalarTwoPointsGadget(secretBits, basePoint, publicKeyPoint,
                true, Constants.DESC_SCALR_MULT_TWO_POINTS_OVER_EC);

        Encoding encodingOfZero = new Encoding(multScalarTwoPoints.getResultPoint1(),
                multScalarTwoPoints.getResultPoint2());

        addTwoPoints = new AddTwoPointsGadget(encodingOfZero.getEncodingPart2(), basePoint,
                Constants.DESC_ADD_TWO_POINTS_OVER_EC);

        encodingOfOne = new Encoding(encodingOfZero.getEncodingPart1(), addTwoPoints.getResultPoint());
    }

    @Override
    public Wire[] getOutputWires() {
        return new Wire[]{encodingOfOne.getEncodingPart1().getX(), encodingOfOne.getEncodingPart1().getY(),
                encodingOfOne.getEncodingPart2().getX(), encodingOfOne.getEncodingPart2().getY()};
    }

    public Encoding getEncodingOfOne() {
        return encodingOfOne;
    }
}
