package examples.generators.ECDL;

import circuit.eval.CircuitEvaluator;
import circuit.structure.CircuitGenerator;
import circuit.structure.Wire;
import examples.gadgets.ECDL.*;
import examples.gadgets.hash.SHA256Gadget;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class EvaluationOfPolynomialOnAssetCircuitGenerator extends CircuitGenerator implements ECDLBase {

    //inputs to the circuit
    private BigInteger hashofIDAsset;
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

    public EvaluationOfPolynomialOnAssetCircuitGenerator(List<EncodingBigInt> encodings, int assetCoeff,
                                                         BigInteger hashOfIDAsset, BigInteger randKey,
                                                         BigInteger commitmentSecret, String desc) {
        super(desc);
        this.hashofIDAsset = hashOfIDAsset;
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

        powersOfIDHash = new ArrayList<>(coeffOfRegisteringAsset - 1);
        for (int i = 0; i < coeffOfRegisteringAsset; i++) {
            powersOfIDHash.add(createProverWitnessWireArray(Constants.SECRET_BITWIDTH));
        }
        keyForFreshEncZero = createProverWitnessWireArray(Constants.SECRET_BITWIDTH);

        //create list of existing encodings

        //TODO: abstract out following methods to a super class which extends circuit generator**************
        Wire baseX = createConstantWire(Constants.BASE_X, "X coordinate of the base point");
        Wire baseY = createConstantWire(computeYCoordinate(Constants.BASE_X), "Y coordinate of the base point");
        basePoint = new AffinePoint(baseX, baseY);

        Wire publicKeyX = createConstantWire(Constants.PUBLIC_KEY_X, "X coordinate of the public key point");
        Wire publicKeyY = createConstantWire(computeYCoordinate(Constants.PUBLIC_KEY_X),
                "Y coordinate of the public key point");
        publicKeyPoint = new AffinePoint(publicKeyX, publicKeyY);






    }

    @Override
    public void generateSampleInput(CircuitEvaluator evaluator) {
        //set values for user input wires
        for (int i = 0; i < Constants.COMMITMENT_SECRET_LENGTH; i++) {
            evaluator.setWireValue(commitmentInput[i], commitmentSecret.testBit(i) ? 1 : 0);
        }
        for (int i = 0; i < Constants.COMMITMENT_SECRET_LENGTH; i++) {
            evaluator.setWireValue(commitmentInput[Constants.COMMITMENT_SECRET_LENGTH + i],
                    hashOfIDAsset.testBit(i) ? 1 : 0);
        }

        public static void main (String[]args){

        }
    }
