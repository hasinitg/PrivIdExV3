package examples.gadgets.ECDH;

import circuit.operations.Gadget;
import circuit.structure.Wire;

//Basic building block for adding two points on EC.
public class Add_TwoPoints_Gadget extends Gadget implements ECDLBase {

    private AffinePoint point1;
    private AffinePoint point2;
    private AffinePoint result;

    public Add_TwoPoints_Gadget(Wire point1X, Wire point1Y, Wire point2X, Wire point2Y){
        this.point1 = new AffinePoint(point1X, point1Y);
        this.point2 = new AffinePoint(point2X, point2Y);
        buildCircuit();
    }

    protected void buildCircuit(){
        result = addAffinePoints(point1, point2);
    }



    @Override
    public Wire[] getOutputWires() {
        return new Wire[]{result.getX(), result.getY()};
    }
}
