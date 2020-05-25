package examples.generators.ECDL;

import circuit.eval.CircuitEvaluator;
import circuit.structure.CircuitGenerator;
import circuit.structure.Wire;
import examples.gadgets.ECDL.*;
import examples.gadgets.hash.SHA256Gadget;

import java.math.BigInteger;
import java.security.SecureRandom;

public class IthEncodedCircuitGenerator extends CircuitGenerator implements ECDLBase {

    //inputs to the circuit
    private BigInteger hashOfIDAssetBigInt;
    private BigInteger randKeyForFreshEncZeroBigInt;
    private BigInteger secretForCommitmentBigInt;
    private EncodingBigInt existingEncIthCoeffBigInt;
    private EncodingBigInt existingEncIMinusOneCoeffBigInt;

    //input wires to the inside gadgets
    /***********secret inputs*************/
    Wire[] commitmentInput;
    Wire[] hashIDAssetInput;
    Wire[] randKeyForFreshEncZero;

    //public inputs
    private Encoding existingEncodedIthCoefficient;
    private Encoding existingEncodedIMinusOnethCoefficient;
    private AffinePoint basePoint;
    private AffinePoint publicKeyPoint;

    //intermediate gadgets
    private IthEncodedCoefficientGadget ithEncodedCoefficientGadget;
    private SHA256Gadget sha256Gadget;

    public IthEncodedCircuitGenerator(BigInteger hashOfIDAsset, EncodingBigInt encIthCoeff,
                                      EncodingBigInt encIMinusOneCoeff, BigInteger randForFreshEncZero,
                                      BigInteger commitmentSecret, String circName){
        super(circName);
        this.hashOfIDAssetBigInt = hashOfIDAsset;
        this.existingEncIthCoeffBigInt = encIthCoeff;
        this.existingEncIMinusOneCoeffBigInt = encIMinusOneCoeff;
        this.randKeyForFreshEncZeroBigInt = randForFreshEncZero;
        this.secretForCommitmentBigInt = commitmentSecret;
    }

    @Override
    protected void buildCircuit() {
        //SHA256 gadget
        commitmentInput = createProverWitnessWireArray(Constants.COMMITMENT_SECRET_LENGTH*2);
        sha256Gadget = new SHA256Gadget(commitmentInput, 1, 64,
                false, false, Constants.DESC_COMMITMENT_SHA_256);

        Wire[] commitment = sha256Gadget.getOutputWires();
        makeOutputArray(commitment, Constants.DESC_COMMITMENT_SHA_256);

        hashIDAssetInput = createProverWitnessWireArray(Constants.SECRET_BITWIDTH);
        randKeyForFreshEncZero = createProverWitnessWireArray(Constants.SECRET_BITWIDTH);

        //ith encoded coefficient in the updated polynomial
        Wire encPart1_X = createInputWire("X coordinate of part 1 of existing encoding of ith coefficient");
        Wire encPart1_Y = createInputWire("Y coordinate of part 1 of existing encoding of ith coefficient");

        Wire encPart2_X = createInputWire("X coordinate of part 2 of existing encoding of ith coefficient");
        Wire encPart2_Y = createInputWire("Y coordinate of part 2 of existing encoding of ith coefficient");

        existingEncodedIthCoefficient = new Encoding(new AffinePoint(encPart1_X, encPart1_Y),
                new AffinePoint(encPart2_X, encPart2_Y));

        //ith encoded coefficient in the updated polynomial
        Wire enc2Part1_X = createInputWire("X coordinate of part 1 of existing encoding of ith coefficient");
        Wire enc2Part1_Y = createInputWire("Y coordinate of part 1 of existing encoding of ith coefficient");

        Wire enc2Part2_X = createInputWire("X coordinate of part 2 of existing encoding of ith coefficient");
        Wire enc2Part2_Y = createInputWire("Y coordinate of part 2 of existing encoding of ith coefficient");

        existingEncodedIMinusOnethCoefficient = new Encoding(new AffinePoint(enc2Part1_X, enc2Part1_Y),
                new AffinePoint(enc2Part2_X, enc2Part2_Y));

        //TODO: abstract out following methods to a super class which extends circuit generator**************
        Wire baseX = createConstantWire(Constants.BASE_X, "X coordinate of the base point");
        Wire baseY = createConstantWire(computeYCoordinate(Constants.BASE_X), "Y coordinate of the base point");
        basePoint = new AffinePoint(baseX, baseY);

        Wire publicKeyX = createConstantWire(Constants.PUBLIC_KEY_X, "X coordinate of the public key point");
        Wire publicKeyY = createConstantWire(computeYCoordinate(Constants.PUBLIC_KEY_X),
                "Y coordinate of the public key point");
        publicKeyPoint = new AffinePoint(publicKeyX, publicKeyY);

        ithEncodedCoefficientGadget = new IthEncodedCoefficientGadget(existingEncodedIthCoefficient,
                existingEncodedIMinusOnethCoefficient, basePoint, publicKeyPoint, hashIDAssetInput,
                randKeyForFreshEncZero, Constants.DESC_UPDATE_ITH_ENCODED_COEFFICIENT);
        makeOutputArray(ithEncodedCoefficientGadget.getOutputWires());

    }

    @Override
    public void generateSampleInput(CircuitEvaluator evaluator) {
        //set values for user input wires
        for (int i = 0; i < Constants.COMMITMENT_SECRET_LENGTH; i++) {
            evaluator.setWireValue(commitmentInput[i], secretForCommitmentBigInt.testBit(i) ? 1 : 0);
        }
        for (int i = 0; i < Constants.COMMITMENT_SECRET_LENGTH; i++) {
            evaluator.setWireValue(commitmentInput[Constants.COMMITMENT_SECRET_LENGTH + i],
                    hashOfIDAssetBigInt.testBit(i) ? 1 : 0);
        }

        for (int i = 0; i < Constants.SECRET_BITWIDTH; i++) {
            evaluator.setWireValue(hashIDAssetInput[i], hashOfIDAssetBigInt.testBit(i) ? 1 : 0);
        }
        for (int i = 0; i < Constants.SECRET_BITWIDTH; i++) {
            evaluator.setWireValue(randKeyForFreshEncZero[i], randKeyForFreshEncZeroBigInt.testBit(i) ? 1 : 0);
        }
        evaluator.setWireValue(existingEncodedIthCoefficient.getEncodingPart1().getX(),
                existingEncIthCoeffBigInt.getPart1().getX());
        evaluator.setWireValue(existingEncodedIthCoefficient.getEncodingPart1().getY(),
                existingEncIthCoeffBigInt.getPart1().getY());
        evaluator.setWireValue(existingEncodedIthCoefficient.getEncodingPart2().getX(),
                existingEncIthCoeffBigInt.getPart2().getX());
        evaluator.setWireValue(existingEncodedIthCoefficient.getEncodingPart2().getY(),
                existingEncIthCoeffBigInt.getPart2().getY());

        evaluator.setWireValue(existingEncodedIMinusOnethCoefficient.getEncodingPart1().getX(),
                existingEncIMinusOneCoeffBigInt.getPart1().getX());
        evaluator.setWireValue(existingEncodedIMinusOnethCoefficient.getEncodingPart1().getY(),
                existingEncIMinusOneCoeffBigInt.getPart1().getY());
        evaluator.setWireValue(existingEncodedIMinusOnethCoefficient.getEncodingPart2().getX(),
                existingEncIMinusOneCoeffBigInt.getPart2().getX());
        evaluator.setWireValue(existingEncodedIMinusOnethCoefficient.getEncodingPart2().getY(),
                existingEncIMinusOneCoeffBigInt.getPart2().getY());

    }

    public static void main(String[] args) {
        SecureRandom secureRandom = new SecureRandom();

        BigInteger secret = new BigInteger(Constants.COMMITMENT_SECRET_LENGTH, secureRandom);

        BigInteger hashInBigInt = Constants.a;
        BigInteger randKeyForFreshEncodingOfZero = Constants.K;

        //generate random big int for x coordinate of the curve and compute corresponding y coordinate when supplying
        //existing encoded coefficients as inputs
        BigInteger encPart1X = Constants.RANDOM_POINT1_X;
        BigInteger encPart1Y = new ECDLBase(){}.computeYCoordinate(encPart1X);

        BigInteger encPart2X = Constants.RANDOM_POINT2_X;
        BigInteger encPart2Y = new ECDLBase(){}.computeYCoordinate(encPart2X);

        EncodingBigInt existingEncIthCoeff = new EncodingBigInt(new AffinePointBigInt(encPart1X, encPart1Y),
                new AffinePointBigInt(encPart2X, encPart2Y));

        BigInteger enc2Part1X = Constants.RANDOM_POINT3_X;
        BigInteger enc2Part1Y = new ECDLBase(){}.computeYCoordinate(enc2Part1X);

        BigInteger enc2Part2X = Constants.RANDOM_POINT4_X;
        BigInteger enc2Part2Y = new ECDLBase(){}.computeYCoordinate(enc2Part2X);

        EncodingBigInt existingEncIMinusOneCoeff = new EncodingBigInt(new AffinePointBigInt(enc2Part1X, enc2Part1Y),
                new AffinePointBigInt(enc2Part2X, enc2Part2Y));

        IthEncodedCircuitGenerator IEC = new IthEncodedCircuitGenerator(hashInBigInt,
                existingEncIthCoeff, existingEncIMinusOneCoeff,randKeyForFreshEncodingOfZero, secret,
                Constants.DESC_UPDATE_ITH_ENCODED_COEFFICIENT);
        IEC.generateCircuit();
        IEC.evalCircuit();
        IEC.prepFiles();
        IEC.runLibsnark();
    }
}
