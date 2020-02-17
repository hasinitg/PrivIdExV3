package examples.generators.ECDL;

import circuit.eval.CircuitEvaluator;
import circuit.structure.CircuitGenerator;
import circuit.structure.Wire;
import examples.gadgets.ECDL.AffinePoint;
import examples.gadgets.ECDL.Constants;
import examples.gadgets.ECDL.ECDLBase;
import examples.gadgets.ECDL.EncodeOneGadget;

public class EncodeOneCircuitGenerator extends CircuitGenerator implements ECDLBase {

    private Wire[] secretBits;

    private Wire baseX;
    private Wire baseY;

    private Wire publicKeyX;
    private Wire publicKeyY;

    private EncodeOneGadget encodeOneGadget;

    public EncodeOneCircuitGenerator(String circuitName){
        super(circuitName);

    }
    @Override
    protected void buildCircuit() {
        secretBits = createProverWitnessWireArray(Constants.SECRET_BITWIDTH, "scalar");

        baseX = createConstantWire(Constants.BASE_X, "X coordinate of the base point");
        baseY = createConstantWire(computeYCoordinate(Constants.BASE_X), "Y coordinate of the base point");

        publicKeyX = createConstantWire(Constants.PUBLIC_KEY_X, "X coordinate of the public key point");
        publicKeyY = createConstantWire(computeYCoordinate(Constants.PUBLIC_KEY_X),
                "Y coordinate of the public key point");

        encodeOneGadget = new EncodeOneGadget(secretBits,new AffinePoint(baseX, baseY),
                new AffinePoint(publicKeyX, publicKeyY), Constants.DESC_ENCODE_ONE);
        makeOutputArray(encodeOneGadget.getOutputWires(), "Fresh encoding of one");

    }

    @Override
    public void generateSampleInput(CircuitEvaluator evaluator) {
        for(int i=0; i<Constants.SECRET_BITWIDTH; i++){
            evaluator.setWireValue(secretBits[i], Constants.K.testBit(i)?1:0);
        }

    }

    public static void main(String[] args) {
        EncodeOneCircuitGenerator circuitGenerator = new EncodeOneCircuitGenerator(Constants.DESC_ENCODE_ONE);
        circuitGenerator.generateCircuit();
        circuitGenerator.evalCircuit();
        circuitGenerator.prepFiles();
        circuitGenerator.runLibsnark();
    }
}
