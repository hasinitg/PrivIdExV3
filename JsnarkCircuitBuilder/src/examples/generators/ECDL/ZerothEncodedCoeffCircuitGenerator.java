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
    private EncodingBigInt existingEncZeroCoeff;

    //input wires to the inside gadgets
    /****secret witness*****/
    Wire[] commitmentInput;
    Wire[] hashIDAssetInput;
    Wire[] randKeyFreshEncZeroInput;
    /****public inputs********/
    private Encoding existingEncodedZerothCoefficient;
    /******constant inputs******/
    private AffinePoint basePoint;
    private AffinePoint publicKeyPoint;

    //intermediate gadgets
    private ZerothEncodedCoefficientGadget zerothEncodedCoefficientGadget;
    private SHA256Gadget sha256Gadget;

    public ZerothEncodedCoeffCircuitGenerator(BigInteger hashOfIDAsset, EncodingBigInt encOfZerothCoeff,
                                              BigInteger randForFreshEncZero, BigInteger commitmentSecret, String circuitName) {

        super(circuitName);
        this.hashOfIDAsset = hashOfIDAsset;
        this.existingEncZeroCoeff = encOfZerothCoeff;
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
        AffinePoint part1 = new AffinePoint(encPart1_X, encPart1_Y);
        Wire encPart2_X = createInputWire("X coordinate of part 2 of existing encoding of zeroth coefficient");
        Wire encPart2_Y = createInputWire("Y coordinate of part 2 of existing encoding of zeroth coefficient");
        AffinePoint part2 = new AffinePoint(encPart2_X, encPart2_Y);

        existingEncodedZerothCoefficient = new Encoding(part1, part2);

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

    }

    @Override
    public void generateSampleInput(CircuitEvaluator evaluator) {
        //String hashAsStringInHex = hashOfIDAsset.toString(16);
        //System.out.println(hashAsStringInHex.length());

        //String secretAsStringInHex = secretForCommitment.toString(16);

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
                existingEncZeroCoeff.getPart1().getX());
        evaluator.setWireValue(existingEncodedZerothCoefficient.getEncodingPart1().getY(),
                existingEncZeroCoeff.getPart1().getY());
        evaluator.setWireValue(existingEncodedZerothCoefficient.getEncodingPart2().getX(),
                existingEncZeroCoeff.getPart2().getX());
        evaluator.setWireValue(existingEncodedZerothCoefficient.getEncodingPart2().getY(),
                existingEncZeroCoeff.getPart2().getY());

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
            BigInteger hashInBigInt = Constants.K;
            BigInteger randKeyForFreshEncodingOfZero = Constants.K;

            //generate random big int for x coordinate of the curve and compute corresponding y coordinate when supplying
            //existing encoded coefficients as inputs
            BigInteger encPart1X = Constants.BASE_X;
            BigInteger encPart1Y = new ECDLBase(){}.computeYCoordinate(encPart1X);
            AffinePointBigInt encPart1 = new AffinePointBigInt(encPart1X, encPart1Y);

            BigInteger encPart2X = Constants.PUBLIC_KEY_X;
            BigInteger endPart2Y = new ECDLBase(){}.computeYCoordinate(encPart2X);
            AffinePointBigInt encPart2 = new AffinePointBigInt(encPart2X, endPart2Y);

            EncodingBigInt existingEncZerothCoeff = new EncodingBigInt(encPart1, encPart2);

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
