package examples.gadgets.ECDL;

import circuit.operations.Gadget;
import circuit.structure.Wire;

//Basic building block for multiplying a point on EC by a scalar.
//We do not validate points on EC given as inputs because in all our circuits, points on EC given as inputs are public,
//and therefore, they can be validated outside the SNARK circuit.
public class Mult_Scalar_Point_Gadget extends Gadget implements ECDLBase {
    //inputs
    private AffinePoint pointToBeMultiplied;
    private Wire[] secretBits;

    //intermediate
    private AffinePoint[] pointTable;

    //outputs
    private Wire outPointX;
    private Wire outPointY;

    //since this gadget maybe used as part of another gadget, the parent gadget should indicate whether to check secret
    //bits or not, in order to avoid duplicate checks
    public Mult_Scalar_Point_Gadget(Wire pointX, Wire pointY, Wire[] secretBits, boolean checkSecretBits, String desc){
        super(desc);
        this.pointToBeMultiplied = new AffinePoint(pointX, pointY);
        this.secretBits = secretBits;
        if(checkSecretBits){
            checkSecretBits(secretBits, generator);
        }
        buildCircuit();
    }

    protected void buildCircuit(){
        pointTable = preprocess(pointToBeMultiplied, secretBits);
        AffinePoint outPoint = mul(secretBits, pointTable);
        outPointX = outPoint.getX();
        outPointY = outPoint.getY();
    }

    @Override
    public Wire[] getOutputWires() {
        return new Wire[]{outPointX, outPointY};
    }

}
