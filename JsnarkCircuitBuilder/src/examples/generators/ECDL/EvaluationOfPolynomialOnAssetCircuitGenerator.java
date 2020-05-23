package examples.generators.ECDL;

import circuit.eval.CircuitEvaluator;
import circuit.structure.CircuitGenerator;
import circuit.structure.Wire;
import examples.gadgets.ECDL.*;
import examples.gadgets.hash.SHA256Gadget;
import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class EvaluationOfPolynomialOnAssetCircuitGenerator extends CircuitGenerator implements ECDLBase {

    //inputs to the circuit
    private BigInteger hashOfIDAsset;
    private BigInteger commitmentSecret;
    private BigInteger randKey;
    private int coeffOfRegisteringAsset;

    private List<EncodingBigInt> encodingBigInts;

    //input wires to inside gadgets
    /***secret witness*****/
    private Wire[] commitmentInput;
    private Wire[] keyForFreshEncZero;
    private List<Wire[]> powersOfIDHash;

    /***public inputs****/
    private List<Encoding> existingEncodings;

    /***constants inputs***/
    private AffinePoint basePoint;
    private AffinePoint publicKeyPoint;

    //intermediate gadgets
    private EvaluationOfPolynomialOnAssetGadget evaluationOfPolynomialOnAssetGadget;
    private SHA256Gadget sha256Gadget;

    //local variables
    int numberOfPowersOfIDHash;
    int numberOfExistingEncodings;

    public EvaluationOfPolynomialOnAssetCircuitGenerator(List<EncodingBigInt> encodings, int assetCoeff,
                                                         BigInteger hashOfIDAsset, BigInteger randKey,
                                                         BigInteger commitmentSecret, String desc) {
        super(desc);
        this.hashOfIDAsset = hashOfIDAsset;
        this.commitmentSecret = commitmentSecret;
        this.randKey = randKey;
        this.encodingBigInts = encodings;
        coeffOfRegisteringAsset = assetCoeff;

    }

    @Override
    protected void buildCircuit() {
        //create input wires and create gadgets using them. Set values for constant wires
        commitmentInput = createProverWitnessWireArray(512);
        sha256Gadget = new SHA256Gadget(commitmentInput, 1, 64, false,
                false, Constants.DESC_COMMITMENT_SHA_256);
        Wire[] commitment = sha256Gadget.getOutputWires();
        makeOutputArray(commitment, Constants.DESC_COMMITMENT_SHA_256);

        numberOfPowersOfIDHash = coeffOfRegisteringAsset - 1;
        powersOfIDHash = new ArrayList<>(numberOfPowersOfIDHash);
        for (int i = 0; i < numberOfPowersOfIDHash; i++) {
            powersOfIDHash.add(createProverWitnessWireArray(Constants.SECRET_BITWIDTH));
        }
        keyForFreshEncZero = createProverWitnessWireArray(Constants.SECRET_BITWIDTH);

        //create list of existing encodings
        numberOfExistingEncodings = coeffOfRegisteringAsset;
        existingEncodings = new ArrayList<>(numberOfExistingEncodings);
        for (int i = 0; i < numberOfExistingEncodings; i++) {
            Wire encPart1_X = createInputWire("X coordinate of part 1 of " + i + "th encoded coefficient");
            Wire encPart1_Y = createInputWire("Y coordinate of part 1 of " + i + "th encoded coefficient");

            Wire encPart2_X = createInputWire("X coordinate of part 2 of " + i + "th encoded coefficient");
            Wire encPart2_Y = createInputWire("Y coordinate of part 2 of " + i + "th encoded coefficient");

            Encoding existingEncodedCoefficient = new Encoding(new AffinePoint(encPart1_X, encPart1_Y),
                    new AffinePoint(encPart2_X, encPart2_Y));
            existingEncodings.add(existingEncodedCoefficient);
        }


        //TODO: abstract out following methods to a super class which extends circuit generator**************
        Wire baseX = createConstantWire(Constants.BASE_X, "X coordinate of the base point");
        Wire baseY = createConstantWire(computeYCoordinate(Constants.BASE_X), "Y coordinate of the base point");
        basePoint = new AffinePoint(baseX, baseY);

        Wire publicKeyX = createConstantWire(Constants.PUBLIC_KEY_X, "X coordinate of the public key point");
        Wire publicKeyY = createConstantWire(computeYCoordinate(Constants.PUBLIC_KEY_X),
                "Y coordinate of the public key point");
        publicKeyPoint = new AffinePoint(publicKeyX, publicKeyY);


        evaluationOfPolynomialOnAssetGadget = new EvaluationOfPolynomialOnAssetGadget(existingEncodings,
                coeffOfRegisteringAsset, powersOfIDHash, basePoint, publicKeyPoint, keyForFreshEncZero,
                Constants.DESC_EVALUATE_ON_POLYNOMIAL_ON_ASSET);
        makeOutputArray(evaluationOfPolynomialOnAssetGadget.getOutputWires());
    }

    @Override
    public void generateSampleInput(CircuitEvaluator evaluator) {
        //populate the wire inputs for powers of ID asset hash from highest to the lowest
        int index = 0;
        for (int i = numberOfPowersOfIDHash; i > 1; i--) {
            BigInteger powerOfIDAsset = hashOfIDAsset.pow(i);
            //due to the policy of underlying jsnark framework, the secret should have first 3 bits set to zero
            //and last bit 1
            evaluator.setWireValue(powersOfIDHash.get(index)[0],0);
            evaluator.setWireValue(powersOfIDHash.get(index)[1],0);
            evaluator.setWireValue(powersOfIDHash.get(index)[2],0);
            for (int j = 3; j < Constants.SECRET_BITWIDTH-1; j++) {

                evaluator.setWireValue(powersOfIDHash.get(index)[j], powerOfIDAsset.testBit(j) ? 1 : 0);
            }
            evaluator.setWireValue(powersOfIDHash.get(index)[Constants.SECRET_BITWIDTH-1],1);
            index++;
        }
        for (int j = 0; j < Constants.SECRET_BITWIDTH; j++) {
            evaluator.setWireValue(powersOfIDHash.get(index)[j], hashOfIDAsset.testBit(j) ? 1 : 0);
        }

        //set values for user input wires
        for (int i = 0; i < Constants.COMMITMENT_SECRET_LENGTH; i++) {
            evaluator.setWireValue(commitmentInput[i], commitmentSecret.testBit(i) ? 1 : 0);
        }
        for (int i = 0; i < Constants.COMMITMENT_SECRET_LENGTH; i++) {
            evaluator.setWireValue(commitmentInput[Constants.COMMITMENT_SECRET_LENGTH + i],
                    hashOfIDAsset.testBit(i) ? 1 : 0);
        }
        for (int i = 0; i < Constants.SECRET_BITWIDTH; i++) {
            evaluator.setWireValue(keyForFreshEncZero[i], randKey.testBit(i) ? 1 : 0);
        }
        //populate the encodings list form highest to the lowest
        for (int i = 0; i < numberOfExistingEncodings; i++) {

            evaluator.setWireValue(existingEncodings.get(i).getEncodingPart1().getX(),
                    encodingBigInts.get(i).getPart1().getX());
            evaluator.setWireValue(existingEncodings.get(i).getEncodingPart1().getY(),
                    encodingBigInts.get(i).getPart1().getY());

            evaluator.setWireValue(existingEncodings.get(i).getEncodingPart2().getX(),
                    encodingBigInts.get(i).getPart2().getX());
            evaluator.setWireValue(existingEncodings.get(i).getEncodingPart2().getY(),
                    encodingBigInts.get(i).getPart2().getY());
        }

    }

    public static void main(String[] args) throws IOException {

        SecureRandom secureRandom = new SecureRandom();
        BigInteger secret = new BigInteger(Constants.COMMITMENT_SECRET_LENGTH, secureRandom);

        BigInteger hashInBigInt = Constants.a;
        BigInteger randKeyForFreshEncodingOfZero = Constants.K;
        List<EncodingBigInt> encodingBigInts = null;
        //define array of different values for registering asset's coefficient (2, 4, 8, 16, 32)
        //for each such value, read the encodings from the file, and run the circuit
        int[] registeringIDAssetCoefficients = {2, 4, 8, 16, 32};
        int registeringIDAssetCoefficient = 0;
        ECDLBase ecdlBase = new ECDLBase(){};
        for (int i = 0; i < registeringIDAssetCoefficients.length; i++) {
            registeringIDAssetCoefficient = registeringIDAssetCoefficients[i];
            int numberOfEncodingsRequired = registeringIDAssetCoefficient;
            //list for storing encodings
            encodingBigInts = new ArrayList<>(numberOfEncodingsRequired);
            //read X coordinates of ECPoints from file, compute Y coordinates, create encodings and populate the list
            File inputFile = new File("Encodings" + "_" + numberOfEncodingsRequired);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
            for(int j=0; j<numberOfEncodingsRequired; j++){
                //read x coordinate for the first point in the encoding
                String bigIntForX = bufferedReader.readLine();
                BigInteger xCoordinate = new BigInteger(bigIntForX);
                BigInteger yCoordinate = ecdlBase.computeYCoordinate(xCoordinate);

                AffinePointBigInt part1 = new AffinePointBigInt(xCoordinate, yCoordinate);

                //read x coordinate for the second point in the encoding
                bigIntForX = bufferedReader.readLine();
                xCoordinate = new BigInteger(bigIntForX);
                yCoordinate = ecdlBase.computeYCoordinate(xCoordinate);

                AffinePointBigInt part2 = new AffinePointBigInt(xCoordinate, yCoordinate);
                EncodingBigInt encodingBigInt = new EncodingBigInt(part1, part2);
                encodingBigInts.add(encodingBigInt);
            }
            EvaluationOfPolynomialOnAssetCircuitGenerator circuitGenerator =
                    new EvaluationOfPolynomialOnAssetCircuitGenerator(encodingBigInts, registeringIDAssetCoefficient,
                            hashInBigInt, randKeyForFreshEncodingOfZero, secret,
                            Constants.DESC_EVALUATE_ON_POLYNOMIAL_ON_ASSET);
            circuitGenerator.generateCircuit();
            circuitGenerator.evalCircuit();
            circuitGenerator.prepFiles();
            System.out.println("############### Results for : registering id asset coefficient = " +
                    registeringIDAssetCoefficient + "#####################################");
            circuitGenerator.runLibsnark();
        }
    }
}
