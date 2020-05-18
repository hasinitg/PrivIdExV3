package examples.generators.ECDL;

import circuit.eval.CircuitEvaluator;
import circuit.structure.CircuitGenerator;
import circuit.structure.Wire;
import examples.gadgets.ECDL.*;
import examples.gadgets.hash.SHA256Gadget;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class ZerothEncodedCoeffCircuitGenerator extends CircuitGenerator implements ECDLBase {

    //inputs to the circuit
    private BigInteger hashOfIDAsset;
    private BigInteger randomKeyForFreshEncZero;
    private BigInteger secretForCommitment;
    private EncodingBigInt existingEncZeroCoeffBigInt;

    //input wires to the inside gadgets
    /****secret witness*****/
    private Wire[] commitmentInput;
    private Wire[] hashIDAssetInput;
    private Wire[] randKeyFreshEncZeroInput;

    /****public inputs********/
    private Encoding existingEncodedZerothCoefficient;
    /******constant inputs******/
    private AffinePoint basePoint;
    private AffinePoint publicKeyPoint;

    //intermediate gadgets
    private ZerothEncodedCoefficientGadget zerothEncodedCoefficientGadget;
    private SHA256Gadget sha256Gadget;

    public ZerothEncodedCoeffCircuitGenerator(BigInteger hashOfIDAsset, EncodingBigInt encOfZerothCoeff,
                                              BigInteger randForFreshEncZero, BigInteger commitmentSecret,
                                              String circuitName) {

        super(circuitName);
        this.hashOfIDAsset = hashOfIDAsset;
        this.existingEncZeroCoeffBigInt = encOfZerothCoeff;
        this.randomKeyForFreshEncZero = randForFreshEncZero;
        this.secretForCommitment = commitmentSecret;

    }

    @Override
    protected void buildCircuit() {
        //create input wires and create gadgets using them. Set values for constant wires
        commitmentInput = createProverWitnessWireArray(512);
        sha256Gadget = new SHA256Gadget(commitmentInput, 1, 64, false,
                false, Constants.DESC_COMMITMENT_SHA_256);
        Wire[] commitment = sha256Gadget.getOutputWires();
        makeOutputArray(commitment, Constants.DESC_COMMITMENT_SHA_256);

        hashIDAssetInput = createProverWitnessWireArray(Constants.SECRET_BITWIDTH);
        randKeyFreshEncZeroInput = createProverWitnessWireArray(Constants.SECRET_BITWIDTH);

        //create input wires for the existing encoding of zeroth coefficient
        Wire encPart1_X = createInputWire("X coordinate of part 1 of existing encoding of zeroth coefficient");
        Wire encPart1_Y = createInputWire("Y coordinate of part 1 of existing encoding of zeroth coefficient");

        Wire encPart2_X = createInputWire("X coordinate of part 2 of existing encoding of zeroth coefficient");
        Wire encPart2_Y = createInputWire("Y coordinate of part 2 of existing encoding of zeroth coefficient");

        existingEncodedZerothCoefficient = new Encoding(new AffinePoint(encPart1_X, encPart1_Y),
                new AffinePoint(encPart2_X, encPart2_Y));

        //TODO: abstract out following methods to a super class which extends circuit generator**************
        Wire baseX = createConstantWire(Constants.BASE_X, "X coordinate of the base point");
        Wire baseY = createConstantWire(computeYCoordinate(Constants.BASE_X), "Y coordinate of the base point");
        basePoint = new AffinePoint(baseX, baseY);

        Wire publicKeyX = createConstantWire(Constants.PUBLIC_KEY_X, "X coordinate of the public key point");
        Wire publicKeyY = createConstantWire(computeYCoordinate(Constants.PUBLIC_KEY_X),
                "Y coordinate of the public key point");
        publicKeyPoint = new AffinePoint(publicKeyX, publicKeyY);
        ///***************************************************************************************************

        zerothEncodedCoefficientGadget = new ZerothEncodedCoefficientGadget(existingEncodedZerothCoefficient,
                basePoint, publicKeyPoint, hashIDAssetInput,
                randKeyFreshEncZeroInput, Constants.DESC_UPDATE_ZEROTH_ENCODED_COEFFICIENT);
        makeOutputArray(zerothEncodedCoefficientGadget.getOutputWires());

    }

    @Override
    public void generateSampleInput(CircuitEvaluator evaluator) {

        //set values for user input wires
        for (int i = 0; i < Constants.COMMITMENT_SECRET_LENGTH; i++) {
            evaluator.setWireValue(commitmentInput[i], secretForCommitment.testBit(i) ? 1 : 0);
        }
        for (int i = 0; i < Constants.COMMITMENT_SECRET_LENGTH; i++) {
            evaluator.setWireValue(commitmentInput[Constants.COMMITMENT_SECRET_LENGTH + i],
                    hashOfIDAsset.testBit(i) ? 1 : 0);
        }
        for (int i = 0; i < Constants.SECRET_BITWIDTH; i++) {
            evaluator.setWireValue(hashIDAssetInput[i], hashOfIDAsset.testBit(i) ? 1 : 0);
        }
        for (int i = 0; i < Constants.SECRET_BITWIDTH; i++) {
            evaluator.setWireValue(randKeyFreshEncZeroInput[i], randomKeyForFreshEncZero.testBit(i) ? 1 : 0);
        }
        evaluator.setWireValue(existingEncodedZerothCoefficient.getEncodingPart1().getX(),
                existingEncZeroCoeffBigInt.getPart1().getX());
        evaluator.setWireValue(existingEncodedZerothCoefficient.getEncodingPart1().getY(),
                existingEncZeroCoeffBigInt.getPart1().getY());
        evaluator.setWireValue(existingEncodedZerothCoefficient.getEncodingPart2().getX(),
                existingEncZeroCoeffBigInt.getPart2().getX());
        evaluator.setWireValue(existingEncodedZerothCoefficient.getEncodingPart2().getY(),
                existingEncZeroCoeffBigInt.getPart2().getY());

    }

    public static void main(String[] args) {
        try {
            SecureRandom secureRandom = new SecureRandom();

            //sample identity asset as a string
            String identityAsset = "Name:Jane Cleark, SSN:5678432, Passport: 234657890, email:jane@gmail.com";
            //secret for the commitment
            //String secret = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijkl";

            BigInteger secret = new BigInteger(Constants.COMMITMENT_SECRET_LENGTH, secureRandom);

            //get hash of the identity asset
            MessageDigest MD = MessageDigest.getInstance("SHA-256");
            byte[] idAssetHash = MD.digest(identityAsset.getBytes(StandardCharsets.UTF_8));
            //this is to be given as input to UpdateZerothEncodedCoefficientGadget
            //BigInteger hashInBigInt = new BigInteger(1, idAssetHash);
            //BigInteger hashInBigInt = new BigInteger(Constants.COMMITMENT_SECRET_LENGTH, secureRandom);
            BigInteger hashInBigInt = Constants.a;
            BigInteger randKeyForFreshEncodingOfZero = Constants.K;

            //generate random big int for x coordinate of the curve and compute corresponding y coordinate when supplying
            //existing encoded coefficients as inputs
            BigInteger encPart1X = Constants.RANDOM_POINT1_X;
            BigInteger encPart1Y = new ECDLBase(){}.computeYCoordinate(encPart1X);

            BigInteger encPart2X = Constants.RANDOM_POINT2_X;
            BigInteger endPart2Y = new ECDLBase(){}.computeYCoordinate(encPart2X);

            EncodingBigInt existingEncZerothCoeff = new EncodingBigInt(new AffinePointBigInt(encPart1X, encPart1Y),
                    new AffinePointBigInt(encPart2X, endPart2Y));

            ZerothEncodedCoeffCircuitGenerator ZEC = new ZerothEncodedCoeffCircuitGenerator(hashInBigInt,
                    existingEncZerothCoeff, randKeyForFreshEncodingOfZero, secret, Constants.DESC_UPDATE_ZEROTH_ENCODED_COEFFICIENT);
            ZEC.generateCircuit();
            ZEC.evalCircuit();
            ZEC.prepFiles();
            ZEC.runLibsnark();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

}
