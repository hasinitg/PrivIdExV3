package examples.gadgets.ECDL;

import circuit.operations.Gadget;
import circuit.structure.Wire;

//The gadget for multiplying two different points by the same scalar. It just combines two gadgets
// of the type Mult_Scalar_Point_Gadget and returns their outputs.
//This gadget can be used to compute a  fresh encoding of zero, i.e. En(0) = (k.B, k.P + 0.B) = (k.B, k.P).
//or to multiply an existing encoding z =(z1, z2) by a scalar En(z) = (k.z1, k.z2)
public class MultScalarTwoPointsGadget extends Gadget implements ECDLBase {
    //inputs
    private AffinePoint point1;
    private AffinePoint point2;
    private Wire[] secretBits;

    //outputs
    private AffinePoint resultPoint1;
    private AffinePoint resultPoint2;

    //sub gadgets
    private MultScalarPointGadget multPoint1;
    private MultScalarPointGadget multPoint2;

    public MultScalarTwoPointsGadget(Wire[] secretBits,
                                     AffinePoint point1, AffinePoint point2, boolean checkSecretBits, String desc){
        super(desc);
        this.secretBits = secretBits;
        this.point1 = point1;
        this.point2 = point2;
        if(checkSecretBits) {
            checkSecretBits(secretBits, generator);
        }
        buildCircuit();
    }

    protected void buildCircuit(){
        multPoint1 = new MultScalarPointGadget(point1, secretBits, false,
                Constants.DESC_SCALAR_MULT_POINT_OVER_EC);
        multPoint2 = new MultScalarPointGadget(point2, secretBits, false,
                Constants.DESC_SCALAR_MULT_POINT_OVER_EC);
        resultPoint1 = multPoint1.getOutPoint();
        resultPoint2 =multPoint2.getOutPoint();
    }

    @Override
    public Wire[] getOutputWires() {
        return new Wire[]{resultPoint1.getX(), resultPoint1.getY(), resultPoint2.getX(), resultPoint2.getY()};
    }

    public AffinePoint getResultPoint1() {
        return resultPoint1;
    }

    public AffinePoint getResultPoint2() {
        return resultPoint2;
    }
}
