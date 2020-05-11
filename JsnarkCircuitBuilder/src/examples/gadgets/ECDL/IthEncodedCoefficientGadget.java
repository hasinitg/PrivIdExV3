package examples.gadgets.ECDL;

import circuit.operations.Gadget;
import circuit.structure.Wire;

/**
 * Gadget for computing the ith encoded coefficient of the updated polynomial, when registering a new identity asset.
 * In other words, this is the gadget to be used in the NP statement: NS4.3.
 * Given the existing zeroth encoded coefficients : En(P_{i}), En(P_{i-1}),
 * the cryptographic hash of the identity asset 'a' and a secret 'k',
 * compute: En(P'_{i}) = -a.En(P_{i}) + En(0), where En(0) = (k.B + k.P), where B is the base point and
 * P is the public key point. Note that a fresh encoding of zero is added to randomize the output.
 */
public class IthEncodedCoefficientGadget extends Gadget implements ECDLBase {

    //inputs
    Encoding ithCoefficient;
    Encoding iMinusOnethCoefficiant;

    AffinePoint basePoint;
    AffinePoint publicKeyPoint;
    Wire[] key;

    Wire[] hashIDAsset;

    //intermediate gadgets
    MultScalarTwoPointsGadget multIthCoeffByHash;
    MultScalarTwoPointsGadget createFreshEncodingOfZero;
    AddTwoEncodingsGadget addMultIthAndIMinusOneCoeffs;
    AddTwoEncodingsGadget randomizationGadget;

    //output
    Encoding updatedIthCoefficient;

    public IthEncodedCoefficientGadget(Encoding existingIthCoeff, Encoding existingIMinusOneCoeff,
                                       AffinePoint basePoint, AffinePoint publicKeyPoint, Wire[] hashIDAsset,
                                       Wire[] randomKey, String desc){
        super(desc);

        this.ithCoefficient = existingIthCoeff;
        this.iMinusOnethCoefficiant = existingIMinusOneCoeff;

        this.basePoint = basePoint;
        this.publicKeyPoint = publicKeyPoint;
        this.key = randomKey;

        this.hashIDAsset = hashIDAsset;
        buildCircuit();
    }

    protected void buildCircuit(){
        //multiply ith encoded coefficient of the existing polynomial by hash of the id asset
        multIthCoeffByHash = new MultScalarTwoPointsGadget(hashIDAsset, ithCoefficient.getEncodingPart1(),
                ithCoefficient.getEncodingPart2(), true, Constants.DESC_ADD_TWO_POINTS_OVER_EC);
        //negate the result
        Encoding negatedIntermediateEnc = new Encoding(negateAffinePoint(multIthCoeffByHash.getResultPoint1()),
                negateAffinePoint(multIthCoeffByHash.getResultPoint2()));

        //add the result to (i-1)th encoded coefficient of the existing polynomial
        addMultIthAndIMinusOneCoeffs = new AddTwoEncodingsGadget(negatedIntermediateEnc, iMinusOnethCoefficiant,
                Constants.DESC_ADD_TWO_POINTS_OVER_EC);

        createFreshEncodingOfZero = new MultScalarTwoPointsGadget(key, basePoint, publicKeyPoint, true,
                Constants.DESC_ADD_TWO_POINTS_OVER_EC);

        //add a fresh encoding of zero to randomize the result
        randomizationGadget = new AddTwoEncodingsGadget(addMultIthAndIMinusOneCoeffs.getEncodingResult(),
                createFreshEncodingOfZero.getResultingEncoding(), Constants.DESC_ADD_TWO_POINTS_OVER_EC);

        updatedIthCoefficient = randomizationGadget.getEncodingResult();

    }

    @Override
    public Wire[] getOutputWires() {
        return new Wire[]{updatedIthCoefficient.getEncodingPart1().getX(),
                updatedIthCoefficient.getEncodingPart1().getY(), updatedIthCoefficient.getEncodingPart2().getX(),
                updatedIthCoefficient.getEncodingPart2().getY()};
    }

    public Encoding getUpdatedIthCoefficient() {
        return updatedIthCoefficient;
    }
}
