package examples.gadgets.ECDL;

import circuit.operations.Gadget;
import circuit.structure.Wire;

//Gadget for updating the zeroth encoded coefficient of the existing polynomial, when registering a new identity asset.
//In other words, this is the gadget to be used in the NP statement: NS4.2.
//Given the existing zeroth encoded coefficient En(P_{0}), cryptographic hash of the identity asset 'a' and a secret 'k',
//compute: En(P'_{0}) = -a.En(P_{0}) + En(0), where En(0) = (k.B + k.P), where B is the base point and P is the public key point.
//Note that a fresh encoding of zero is added to randomize the output.
public class UpdateZerothEncodedCoefficientGadget extends Gadget implements ECDLBase {

    //inputs
    Encoding zerothCoefficient;
    AffinePoint basePoint;
    AffinePoint publicKeyPoint;
    Wire[] hashIDAsset;
    Wire[] key;

    //intermediate gadgets
    MultScalarTwoPointsGadget multZerothEncodingByHash;
    MultScalarTwoPointsGadget createFreshEncodingOfZero;
    AddTwoEncodingsGadget outputRandomizedResult;

    //output
    Encoding updatedZerothCoefficient;

    public UpdateZerothEncodedCoefficientGadget(Encoding existingZerothCoeff, AffinePoint basePoint,
                                                AffinePoint publicKeyPoint, Wire[] hashOfIDAsset, Wire[] randomKey,
                                                String desc){
        super(desc);
        this.zerothCoefficient = existingZerothCoeff;
        this. basePoint = basePoint;
        this.publicKeyPoint = publicKeyPoint;
        this.hashIDAsset = hashOfIDAsset;
        this.key = randomKey;

        buildCircuit();
    }

    protected void buildCircuit(){
        multZerothEncodingByHash = new MultScalarTwoPointsGadget(hashIDAsset, zerothCoefficient.getEncodingPart1(),
                zerothCoefficient.getEncodingPart2(), true, Constants.DESC_SCALR_MULT_TWO_POINTS_OVER_EC);
        //intermediate output
        Encoding updatedZerothCoeff = new Encoding(multZerothEncodingByHash.getResultPoint1(),
                multZerothEncodingByHash.getResultPoint2());

        createFreshEncodingOfZero = new MultScalarTwoPointsGadget(key, basePoint, publicKeyPoint, true,
                Constants.DESC_SCALR_MULT_TWO_POINTS_OVER_EC);
        //intermediate output
        Encoding freshEncZero = new Encoding(createFreshEncodingOfZero.getResultPoint1(),
                createFreshEncodingOfZero.getResultPoint2());

        outputRandomizedResult = new AddTwoEncodingsGadget(updatedZerothCoeff, freshEncZero, Constants.DESC_ADD_TWO_ENC);
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
