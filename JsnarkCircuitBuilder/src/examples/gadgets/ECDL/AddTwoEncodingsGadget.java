package examples.gadgets.ECDL;

import circuit.operations.Gadget;
import circuit.structure.Wire;

//Gadget for adding two encodings: given enc1 = x1, x2 and enc2 = y1, y2, result = x1+y1, x2+y2
public class AddTwoEncodingsGadget extends Gadget implements ECDLBase {
    //inputs
    private Encoding encoding1;
    private Encoding encoding2;

    //sub gadget
    private AddTwoPointsGadget addTwoPointsGadget1;
    private AddTwoPointsGadget addTwoPointsGadget2;

    //output
    private Encoding encodingResult;

    //Wire representation is avoided for inputs because there are eight wires for the total input of the gadget.
    public AddTwoEncodingsGadget(Encoding enc1, Encoding enc2, String desc){
        super(desc);
        this.encoding1 = enc1;
        this.encoding2 = enc2;
        buildCircuit();
    }

    public void buildCircuit(){
        addTwoPointsGadget1 = new AddTwoPointsGadget(encoding1.getEncodingPart1(), encoding2.getEncodingPart1(),
                Constants.DESC_ADD_TWO_POINTS_OVER_EC);
        addTwoPointsGadget2 = new AddTwoPointsGadget(encoding1.getEncodingPart2(), encoding2.getEncodingPart2(),
                Constants.DESC_ADD_TWO_POINTS_OVER_EC);
        encodingResult = new Encoding(addTwoPointsGadget1.getResultPoint(), addTwoPointsGadget2.getResultPoint());

    }

    @Override
    public Wire[] getOutputWires() {
        return new Wire[]{encodingResult.getEncodingPart1().getX(), encodingResult.getEncodingPart1().getX(),
        encodingResult.getEncodingPart2().getX(), encodingResult.getEncodingPart2().getY()};
    }

    public Encoding getEncodingResult() {
        return encodingResult;
    }
}
