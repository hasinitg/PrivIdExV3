package examples.gadgets.ECDH;

import circuit.operations.Gadget;
import circuit.structure.Wire;

//The gadget for multiplying two different points by the same scalar. It just combines two gadgets
// of the type Mult_Scalar_Point_Gadget and returns their outputs.
public class Mult_Scalar_TwoPoints_Gadget extends Gadget implements ECDLBase {
    //inputs
    private AffinePoint point1;
    private AffinePoint point2;
    private Wire[] secretBits;

    //outputs
    private AffinePoint resultPoint1;
    private AffinePoint resultPoint2;

    //sub gadgets
    private Mult_Scalar_Point_Gadget multPoint1;
    private Mult_Scalar_Point_Gadget multPoint2;

    public Mult_Scalar_TwoPoints_Gadget(Wire[] secretBits, Wire point1X, Wire point1Y, Wire point2X, Wire point2Y,
                                        String desc){
        super(desc);
        this.secretBits = secretBits;
        this.point1 = new AffinePoint(point1X, point1Y);
        this.point2 = new AffinePoint(point2X, point2Y);
        checkSecretBits(secretBits, generator);
        buildCircuit();
    }

    protected void buildCircuit(){
        multPoint1 = new Mult_Scalar_Point_Gadget(point1.getX(), point1.getY(), secretBits, false, description);
        multPoint2 = new Mult_Scalar_Point_Gadget(point2.getX(), point2.getY(), secretBits, false, description);
    }

    @Override
    public Wire[] getOutputWires() {
        return new Wire[]{multPoint1.getOutputWires()[0], multPoint1.getOutputWires()[1],
                multPoint2.getOutputWires()[0], multPoint2.getOutputWires()[1]};
    }
}
