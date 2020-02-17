package examples.gadgets.ECDL;

import circuit.operations.Gadget;
import circuit.structure.Wire;

//Basic building block for multiplying a point on EC by a scalar.
//We do not validate points on EC given as inputs because in all our circuits, points on EC given as inputs are public,
//and therefore, they can be validated outside the SNARK circuit.
public class MultScalarPointGadget extends Gadget implements ECDLBase {
    //inputs
    private AffinePoint pointToBeMultiplied;
    private Wire[] secretBits;

    //intermediate
    private AffinePoint[] pointTable;

    //outputs
    private AffinePoint outPoint;

    //since this gadget maybe used as part of another gadget, the parent gadget should indicate whether to check secret
    //bits or not, in order to avoid duplicate checks
    public MultScalarPointGadget(AffinePoint pointOnEC, Wire[] secretBits, boolean checkSecretBits, String desc){
        super(desc);
        this.pointToBeMultiplied = pointOnEC;
        this.secretBits = secretBits;
        if(checkSecretBits){
            checkSecretBits(secretBits, generator);
        }
        buildCircuit();
    }

    protected void buildCircuit(){
        pointTable = preprocess(pointToBeMultiplied, secretBits);
        outPoint = mul(secretBits, pointTable);
    }

    @Override
    public Wire[] getOutputWires() {
        return new Wire[]{outPoint.getX(), outPoint.getY()};
    }

    public AffinePoint getOutPoint() {
        return outPoint;
    }

}
