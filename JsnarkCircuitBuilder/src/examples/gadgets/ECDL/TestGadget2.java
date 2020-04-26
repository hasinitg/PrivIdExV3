package examples.gadgets.ECDL;

import circuit.operations.Gadget;
import circuit.structure.Wire;

/**
 * This is for the second step of trouble shooting the issue of computing the negation of a scalar multiplication
 * of an encoding and then using it in remaining computations such as addition with another encoding.
 */
public class TestGadget2 extends Gadget implements ECDLBase {
    private Encoding toBeMult;
    private Encoding toBeAdded;
    private Wire[] scalar;

    private Encoding resultingEncoding;

    private MultScalarTwoPointsGadget multScalarEncodingGadget;
    private AddTwoEncodingsGadget addTwoEncodingsGadget;

    public TestGadget2(Encoding forMult, Encoding forAdd, Wire[] secret, String desc){
        super(desc);
        this.toBeMult = forMult;
        this.toBeAdded = forAdd;
        this.scalar = secret;

        buildCircuit();

    }

    protected void buildCircuit(){
        multScalarEncodingGadget = new MultScalarTwoPointsGadget(scalar, toBeMult.getEncodingPart1(),
                toBeMult.getEncodingPart2(), true, Constants.DESC_SCALAR_MULT_POINT_OVER_EC);

        AffinePoint negatedIntermediateResult1 = negateAffinePoint(multScalarEncodingGadget.getResultPoint1());
        AffinePoint negatedIntermediateResult2 = negateAffinePoint(multScalarEncodingGadget.getResultPoint2());

        addTwoEncodingsGadget = new AddTwoEncodingsGadget(new Encoding(negatedIntermediateResult1,
                negatedIntermediateResult2), toBeAdded, Constants.DESC_ADD_TWO_POINTS_OVER_EC);

        resultingEncoding = addTwoEncodingsGadget.getEncodingResult();
    }
    @Override
    public Wire[] getOutputWires() {
        return new Wire[]{resultingEncoding.getEncodingPart1().getX(), resultingEncoding.getEncodingPart1().getY(),
        resultingEncoding.getEncodingPart2().getX(), resultingEncoding.getEncodingPart2().getY()};
    }

    public Encoding getResultPoint() {
        return resultingEncoding;
    }
}
