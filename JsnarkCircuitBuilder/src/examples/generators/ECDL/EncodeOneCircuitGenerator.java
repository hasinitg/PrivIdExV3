package examples.generators.ECDL;

import circuit.eval.CircuitEvaluator;
import circuit.structure.CircuitGenerator;
import circuit.structure.Wire;
import examples.gadgets.ECDL.AffinePoint;
import examples.gadgets.ECDL.Constants;
import examples.gadgets.ECDL.ECDLBase;
import examples.gadgets.ECDL.EncodeOneGadget;

import java.math.BigInteger;

public class EncodeOneCircuitGenerator extends CircuitGenerator implements ECDLBase {

    //input to the circuit
    private BigInteger secretK;

    //wire inputs to the gadgets
    private Wire[] secretBits;

    private AffinePoint basePoint;
    private AffinePoint publicKeyPoint;

    //gadget
    private EncodeOneGadget encodeOneGadget;

    public EncodeOneCircuitGenerator(BigInteger secretK, String circuitName){
        super(circuitName);
        this.secretK = secretK;

    }
    @Override
    protected void buildCircuit() {
        secretBits = createProverWitnessWireArray(Constants.SECRET_BITWIDTH, "scalar");

        //TODO: abstract out following methods to a super class which extends circuit generator**************
        Wire baseX = createConstantWire(Constants.BASE_X, "X coordinate of the base point");
        Wire baseY = createConstantWire(computeYCoordinate(Constants.BASE_X), "Y coordinate of the base point");
        basePoint = new AffinePoint(baseX, baseY);

        Wire publicKeyX = createConstantWire(Constants.PUBLIC_KEY_X, "X coordinate of the public key point");
        Wire publicKeyY = createConstantWire(computeYCoordinate(Constants.PUBLIC_KEY_X),
                "Y coordinate of the public key point");
        publicKeyPoint = new AffinePoint(publicKeyX, publicKeyY);
        ///***************************************************************************************************

        encodeOneGadget = new EncodeOneGadget(secretBits, basePoint, publicKeyPoint, Constants.DESC_ENCODE_ONE);
        makeOutputArray(encodeOneGadget.getOutputWires(), Constants.DESC_OP_FRESH_ENC_ONE);

    }

    @Override
    public void generateSampleInput(CircuitEvaluator evaluator) {
        for(int i=0; i<Constants.SECRET_BITWIDTH; i++){
            evaluator.setWireValue(secretBits[i], secretK.testBit(i)?1:0);
        }

    }

    public static void main(String[] args) {
        EncodeOneCircuitGenerator circuitGenerator = new EncodeOneCircuitGenerator(Constants.K, Constants.DESC_ENCODE_ONE);
        circuitGenerator.generateCircuit();
        circuitGenerator.evalCircuit();
        circuitGenerator.prepFiles();
        circuitGenerator.runLibsnark();
    }
}
