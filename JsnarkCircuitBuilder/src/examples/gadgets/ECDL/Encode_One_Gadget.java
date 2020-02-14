package examples.gadgets.ECDL;

import circuit.operations.Gadget;
import circuit.structure.Wire;

//Gadget for computing fresh encoding of one, using the Elliptic Curve Elgamal in the exponent encoding scheme used in
// PrivIdEx. E.g. Encode(1) = (kB, kP+1B), where k is a secret, B is base point and P is public key.
public class Encode_One_Gadget extends Gadget implements ECDLBase {

    //inputs
    private AffinePoint basePoint;
    private AffinePoint publicKeyPoint;
    private Wire[] secretBits;

    //sub circuits
    private Mult_Scalar_TwoPoints_Gadget multScalarTwoPoints;
    private Add_TwoPoints_Gadget addTwoPoints;

    //output
    Encoding encodingOfOne;

    public Encode_One_Gadget(Wire[] secretBits, Wire basePointX, Wire basePointY, Wire publicKeyPointX, Wire publicKeyPointY,
                             String desc){
        super(desc);
        this.basePoint = new AffinePoint(basePointX, basePointY);
        this.publicKeyPoint = new AffinePoint(publicKeyPointX, publicKeyPointY);
        this.secretBits = secretBits;

        buildCircuit();
    }

    protected void buildCircuit(){
        multScalarTwoPoints = new Mult_Scalar_TwoPoints_Gadget(secretBits, basePoint.getX(), basePoint.getY(),
                publicKeyPoint.getX(), publicKeyPoint.getY(), true, Constants.DESC_SCALR_MULT_TWO_POINTS_OVER_EC);
        Wire[] encodingZeroOutWires = multScalarTwoPoints.getOutputWires();
        Encoding encodingOfZero = new Encoding(new AffinePoint(encodingZeroOutWires[0], encodingZeroOutWires[1]),
                new AffinePoint(encodingZeroOutWires[2], encodingZeroOutWires[3]));

        addTwoPoints = new Add_TwoPoints_Gadget(encodingOfZero.getEncodingPart2().getX(), encodingOfZero.getEncodingPart2().getY(),
                basePoint.getX(), basePoint.getY(), Constants.DESC_ADD_TWO_POINTS_OVER_EC);

        Wire[] addTwoPointsOut = addTwoPoints.getOutputWires();

        encodingOfOne = new Encoding(encodingOfZero.getEncodingPart1(), new AffinePoint(addTwoPointsOut[0], addTwoPointsOut[1]));
    }

    @Override
    public Wire[] getOutputWires() {
        return new Wire[]{encodingOfOne.getEncodingPart1().getX(), encodingOfOne.getEncodingPart1().getY(),
                encodingOfOne.getEncodingPart2().getX(), encodingOfOne.getEncodingPart2().getY()};
    }
}
