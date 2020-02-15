package examples.gadgets.ECDL;

import circuit.operations.Gadget;
import circuit.structure.Wire;

//Gadget for adding two encodings: given enc1 = x1, x2 and enc2 = y1, y2, result = x1+y1, x2+y2
public class AddTwoEncodingsGadget extends Gadget implements ECDLBase {
    //inputs
    Encoding encoding1;
    Encoding encoding2;

    //sub gadget
    AddTwoPointsGadget addTwoPointsGadget1;
    AddTwoPointsGadget addTwoPointsGadget2;

    //output
    Encoding encodingResult;

    //Wire representation is avoided for inputs because there are eight wires for the total input of the gadget.
    public AddTwoEncodingsGadget(Encoding enc1, Encoding enc2, String desc){
        super(desc);
        this.encoding1 = enc1;
        this.encoding2 = enc2;
        buildCircuit();
    }

    public void buildCircuit(){
        //addTwoPointsGadget1 = new AddTwoPointsGadget(encoding1.getEncodingPart1().getX(), )
    }

    @Override
    public Wire[] getOutputWires() {
        return new Wire[0];
    }
}
