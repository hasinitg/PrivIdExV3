package examples.generators.ECDL;

import circuit.eval.CircuitEvaluator;
import circuit.structure.CircuitGenerator;
import circuit.structure.Wire;
import examples.gadgets.ECDL.AffinePoint;
import examples.gadgets.ECDL.Constants;
import examples.gadgets.ECDL.ECDLBase;
import examples.gadgets.ECDL.TestGadget;

import java.math.BigInteger;

/**
 * This is to trouble shoot the issue of computing the negation of a scalar multiplication of a point and then
 * using it in remaining computations such as addition with another point.
 */
public class TestGenerator extends CircuitGenerator implements ECDLBase {
    //inputs to the circuit
    private BigInteger secret;

    //inputs into the gadget
    private Wire[] scalar;
    private AffinePoint pointToBeMultiplied;
    private AffinePoint pointToBeAdded;

    private TestGadget testGadget;

    public TestGenerator(BigInteger scalar, String circuitName){
        super(circuitName);
        this.secret = scalar;
    }

    @Override
    protected void buildCircuit() {
        scalar = createProverWitnessWireArray(Constants.SECRET_BITWIDTH);

        Wire point1X = createConstantWire(Constants.BASE_X, "baseX");
        Wire point1Y = createConstantWire(computeYCoordinate(Constants.BASE_X), "baseY");

        pointToBeMultiplied = new AffinePoint(point1X, point1Y);

        Wire point2X = createConstantWire(Constants.PUBLIC_KEY_X);
        Wire point2Y = createConstantWire(computeYCoordinate(Constants.PUBLIC_KEY_X));

        pointToBeAdded = new AffinePoint(point2X, point2Y);

        testGadget = new TestGadget(pointToBeMultiplied, pointToBeAdded, scalar, "Test");
    }

    @Override
    public void generateSampleInput(CircuitEvaluator evaluator) {
        //it is enough only to get the secret as user input, can make the two points constants for testing purpose
        for (int i = 0; i < Constants.COMMITMENT_SECRET_LENGTH; i++) {
            evaluator.setWireValue(scalar[i], secret.testBit(i) ? 1 : 0);
        }
    }

    public static void main(String[] args) {

        TestGenerator gen = new TestGenerator(Constants.K, "Test");
        gen.generateCircuit();
        gen.evalCircuit();
        gen.prepFiles();
        gen.runLibsnark();


    }
}