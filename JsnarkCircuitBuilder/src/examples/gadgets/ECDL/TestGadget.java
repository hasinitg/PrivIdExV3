package examples.gadgets.ECDL;

import circuit.operations.Gadget;
import circuit.structure.Wire;

/**
 * This is to trouble shoot the issue of computing the negation of a scalar multiplication of a point and then
 * using it in remaining computations such as addition with another point.
 */
public class TestGadget extends Gadget implements ECDLBase
{
    private AffinePoint toBeMult;
    private AffinePoint toBeAdded;
    private Wire[] scalar;

    private AffinePoint resultPoint;

    private MultScalarPointGadget multScalarPointGadget;
    private AddTwoPointsGadget addTwoPointsGadget;

    public TestGadget(AffinePoint ap1, AffinePoint ap2, Wire[] secret, String desc){
        super(desc);
        this.toBeMult = ap1;
        this.toBeAdded = ap2;
        this.scalar = secret;

        buildCircuit();

    }

    protected void buildCircuit(){
        multScalarPointGadget = new MultScalarPointGadget(toBeMult, scalar, true,
                Constants.DESC_SCALAR_MULT_POINT_OVER_EC);

        AffinePoint negatedIntermediateResult = negateAffinePoint(multScalarPointGadget.getOutPoint());

        addTwoPointsGadget = new AddTwoPointsGadget(negatedIntermediateResult, toBeAdded,
                Constants.DESC_ADD_TWO_POINTS_OVER_EC);

        resultPoint = addTwoPointsGadget.getResultPoint();
    }
    @Override
    public Wire[] getOutputWires() {
        return new Wire[]{resultPoint.getX(), resultPoint.getY()};
    }

    public AffinePoint getResultPoint() {
        return resultPoint;
    }

}
