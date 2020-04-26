package examples.generators.ECDL;

import circuit.eval.CircuitEvaluator;
import circuit.structure.CircuitGenerator;
import circuit.structure.Wire;
import examples.gadgets.ECDL.*;

import java.math.BigInteger;

public class Test2Generator extends CircuitGenerator implements ECDLBase {

    //inputs to the circuit
    private BigInteger secret;

    //inputs into the gadget
    private Wire[] scalar;
    private Encoding encodingToBeMultiplied;
    private Encoding encodingToBeAdded;

    private TestGadget2 testGadget2;

    public Test2Generator(BigInteger scalar, String circuitName){
        super(circuitName);
        this.secret = scalar;

    }
    @Override
    protected void buildCircuit() {
        scalar = createProverWitnessWireArray(Constants.SECRET_BITWIDTH);

        Wire point1X = createConstantWire(Constants.RANDOM_POINT1_X, "rand point 1_x");
        Wire point1Y = createConstantWire(computeYCoordinate(Constants.RANDOM_POINT1_X),"rand point 1_y");

        Wire point2X = createConstantWire(Constants.RANDOM_POINT2_X, "rand_point 2_x");
        Wire point2Y = createConstantWire(computeYCoordinate(Constants.RANDOM_POINT2_X),"rand point 2_y");

        encodingToBeMultiplied = new Encoding(new AffinePoint(point1X, point1Y), new AffinePoint(point2X, point2Y));

        Wire point3X = createConstantWire(Constants.RANDOM_POINT3_X, "rand point 3_x");
        Wire point3Y = createConstantWire(computeYCoordinate(Constants.RANDOM_POINT3_X), "rand_point 3_x");

        Wire point4X = createConstantWire(Constants.RANDOM_POINT4_X, "rand point 4_x");
        Wire point4Y = createConstantWire(computeYCoordinate(Constants.RANDOM_POINT4_X), "rand_point 4_x");

        encodingToBeAdded = new Encoding(new AffinePoint(point3X, point3Y), new AffinePoint(point4X, point4Y));

        testGadget2 = new TestGadget2(encodingToBeMultiplied, encodingToBeAdded, scalar, "Test2 gadget");

    }

    @Override
    public void generateSampleInput(CircuitEvaluator evaluator) {
        for (int i = 0; i < Constants.SECRET_BITWIDTH; i++) {
            evaluator.setWireValue(scalar[i], secret.testBit(i) ? 1 : 0);
        }

    }

    public static void main(String[] args) {
        Test2Generator gen = new Test2Generator(Constants.K, "Test2");
        gen.generateCircuit();
        gen.evalCircuit();
        gen.prepFiles();
        gen.runLibsnark();
    }
}
