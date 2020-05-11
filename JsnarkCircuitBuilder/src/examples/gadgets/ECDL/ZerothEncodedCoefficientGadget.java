package examples.gadgets.ECDL;

import circuit.operations.Gadget;
import circuit.structure.Wire;

/**
 * Gadget for computing the zeroth encoded coefficient of the updated polynomial, when registering a new identity asset.
 * In other words, this is the gadget to be used in the NP statement: NS4.1.
 * Given the existing zeroth encoded coefficient En(P_{0}), the cryptographic hash of the identity asset 'a' and a
 * secret 'k', compute: En(P'_{0}) = -a.En(P_{0}) + En(0), where En(0) = (k.B + k.P), where B is the base point and
 * P is the public key point. Note that a fresh encoding of zero is added to randomize the output.
 */
public class ZerothEncodedCoefficientGadget extends Gadget implements ECDLBase {

    //inputs
    Encoding zerothCoefficient;
    AffinePoint basePoint;
    AffinePoint publicKeyPoint;
    Wire[] key;

    Wire[] hashIDAsset;

    //intermediate gadgets
    MultScalarTwoPointsGadget multZerothCoeffByHash;
    MultScalarTwoPointsGadget createFreshEncodingOfZero;
    AddTwoEncodingsGadget outputRandomizedResult;

    //output
    Encoding updatedZerothCoefficient;

    public ZerothEncodedCoefficientGadget(Encoding existingZerothCoeff, AffinePoint basePoint,
                                          AffinePoint publicKeyPoint, Wire[] hashOfIDAsset, Wire[] randomKey,
                                          String desc){
        super(desc);
        this.zerothCoefficient = existingZerothCoeff;
        this.basePoint = basePoint;
        this.publicKeyPoint = publicKeyPoint;
        this.key = randomKey;
        this.hashIDAsset = hashOfIDAsset;

        buildCircuit();
    }

    protected void buildCircuit(){
        multZerothCoeffByHash = new MultScalarTwoPointsGadget(hashIDAsset, zerothCoefficient.getEncodingPart1(),
                zerothCoefficient.getEncodingPart2(), true, Constants.DESC_SCALR_MULT_TWO_POINTS_OVER_EC);
        //intermediate output
//        Encoding intermediateEnc = new Encoding(multZerothCoeffByHash.getResultPoint1(),
//                multZerothCoeffByHash.getResultPoint2());

        Encoding negatedIntermediateEnc = new Encoding(negateAffinePoint(multZerothCoeffByHash.getResultPoint1()),
                negateAffinePoint(multZerothCoeffByHash.getResultPoint2()));

        createFreshEncodingOfZero = new MultScalarTwoPointsGadget(key, basePoint, publicKeyPoint, true,
                Constants.DESC_SCALR_MULT_TWO_POINTS_OVER_EC);

        outputRandomizedResult = new AddTwoEncodingsGadget(negatedIntermediateEnc,
                createFreshEncodingOfZero.getResultingEncoding(), Constants.DESC_ADD_TWO_ENC);
        updatedZerothCoefficient = outputRandomizedResult.getEncodingResult();

    }

    @Override
    public Wire[] getOutputWires() {
        return new Wire[]{updatedZerothCoefficient.getEncodingPart1().getX(),
                updatedZerothCoefficient.getEncodingPart1().getY(), updatedZerothCoefficient.getEncodingPart2().getX(),
        updatedZerothCoefficient.getEncodingPart2().getY()};
    }

    public Encoding getUpdatedZerothCoefficient() {
        return updatedZerothCoefficient;
    }
}
