package examples.gadgets.ECDL;

import circuit.operations.Gadget;
import circuit.structure.Wire;

//Basic building block for adding two points on EC.
public class AddTwoPointsGadget extends Gadget implements ECDLBase {
    //inputs
    private AffinePoint point1;
    private AffinePoint point2;

    //outputs
    private AffinePoint resultPoint;

    public AddTwoPointsGadget(AffinePoint point1, AffinePoint point2, String desc){
        super(desc);
        this.point1 = point1;
        this.point2 = point2;
        buildCircuit();
    }

    protected void buildCircuit(){
        resultPoint = addAffinePoints(point1, point2);
    }

    @Override
    public Wire[] getOutputWires() {
        return new Wire[]{resultPoint.getX(), resultPoint.getY()};
    }

    public AffinePoint getResultPoint() {
        return resultPoint;
    }
}
