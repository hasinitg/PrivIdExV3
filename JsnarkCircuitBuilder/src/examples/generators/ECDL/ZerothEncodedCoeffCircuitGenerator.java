package examples.generators.ECDL;

import circuit.eval.CircuitEvaluator;
import circuit.structure.CircuitGenerator;
import examples.gadgets.ECDL.Constants;
import examples.gadgets.ECDL.ECDLBase;
import examples.gadgets.ECDL.Encoding;
import examples.gadgets.ECDL.ZerothEncodedCoefficientGadget;
import examples.gadgets.hash.SHA256Gadget;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

public class ZerothEncodedCoeffCircuitGenerator extends CircuitGenerator implements ECDLBase {

    //inputs to the circuit
    private BigInteger hashOfIDAsset;
    private List<BigInteger> existingEncodedZerothCoefficient;
    private BigInteger randomKeyForFreshEncZero;

    private BigInteger secretForCommitment;

    //input wires to the inside gadgets



    //intermediate gadgets
    private ZerothEncodedCoefficientGadget zerothEncodedCoefficientGadget;
    private SHA256Gadget sha256Gadget;

    public ZerothEncodedCoeffCircuitGenerator(BigInteger hashOfIDAsset, List<BigInteger> encOfZerothCoeff, BigInteger randKey,
                                              BigInteger commitmentSecret, String circuitName){

        super(circuitName);
        this.hashOfIDAsset = hashOfIDAsset;
        this.existingEncodedZerothCoefficient = encOfZerothCoeff;
        this.randomKeyForFreshEncZero = randKey;
        this.secretForCommitment = commitmentSecret;
        buildCircuit();

    }

    @Override
    protected void buildCircuit() {
        //create input wires and create gadgets using them. Set values for constant wires


    }

    @Override
    public void generateSampleInput(CircuitEvaluator evaluator) {
        String hashAsStringInHex = hashOfIDAsset.toString(16);
        //System.out.println(hashAsStringInHex.length());

        String secretAsStringInHex = secretForCommitment.toString(16);

        //set values for user input wires

    }

    public static void main(String[] args) {
        try {
            SecureRandom secureRandom = new SecureRandom();

            //sample identity asset as a string
            String identityAsset = "Name:Jane Cleark, SSN:5678432, Passport: 234657890, email:jane@gmail.com";
            //secret for the commitment
            //String secret = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijkl";

            BigInteger secret = new BigInteger(SECRET_BITWIDTH, secureRandom);


            //get hash of the identity asset
            MessageDigest MD = MessageDigest.getInstance("SHA-256");
            byte[] idAssetHash = MD.digest(identityAsset.getBytes(StandardCharsets.UTF_8));
            //this is to be given as input to UpdateZerothEncodedCoefficientGadget
            BigInteger hashInBigInt = new BigInteger(1, idAssetHash);

            //String hashAsStringInDecimal = hashInBigInt.toString();
            //System.out.println(hashAsStringInDecimal);
            //output of above sout:
            //5956018361484594241756432284109372535225036440460752841575723218785082028040

            //System.out.println(hashAsStringInDecimal.length());
            //length = 76

            //generate random big int for x coordinate of the curve and compute corresponding y coordinate when supplying
            //existing encoded coefficients as inputs

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}
