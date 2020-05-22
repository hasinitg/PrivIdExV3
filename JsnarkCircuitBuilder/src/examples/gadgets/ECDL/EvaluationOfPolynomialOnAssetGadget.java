package examples.gadgets.ECDL;

import circuit.operations.Gadget;
import circuit.structure.Wire;

import java.util.ArrayList;
import java.util.List;

public class EvaluationOfPolynomialOnAssetGadget extends Gadget implements ECDLBase {

    //inputs
    private int coefficientOfRegisteringAsset;
    //from highest coefficient to the zeroth coefficient
    private List<Encoding> existingEncodings;
    private List<Wire[]> powersOfIDAssetHash;

    private AffinePoint basePoint;
    private AffinePoint publicPoint;
    private Wire[] key;

    //output
    private Encoding evaluationOfPolynomial;

    public EvaluationOfPolynomialOnAssetGadget(List<Encoding> encodings, int assetCoeff, List<Wire[]> powersOfIDAsset,
                                               AffinePoint basePoint, AffinePoint publickeyPoint, Wire[] randKey,
                                               String desc){
        super(desc);
        this.existingEncodings = encodings;
        this.coefficientOfRegisteringAsset = assetCoeff;
        this.basePoint = basePoint;
        this.publicPoint = publickeyPoint;
        this.key = randKey;
        this.powersOfIDAssetHash = powersOfIDAsset;

        buildCircuit();

    }

    protected void buildCircuit(){
        List<Encoding> multResults = new ArrayList<>();
        int numberOfPowersOfIDHash = coefficientOfRegisteringAsset -1;
        int i;
        for(i=0; i<numberOfPowersOfIDHash; i++){
            Wire[] hashOfAssetRaisedToI = powersOfIDAssetHash.get(i);
            Encoding existingIthCoefficient = existingEncodings.get(i);
            MultScalarTwoPointsGadget multScalarTwoPointsGadget = new MultScalarTwoPointsGadget(hashOfAssetRaisedToI,
                    existingIthCoefficient.getEncodingPart1(), existingIthCoefficient.getEncodingPart2(),
                    true, Constants.DESC_SCALAR_MULT_POINT_OVER_EC);

            multResults.add(multScalarTwoPointsGadget.getResultingEncoding());

        }
        //add zeroth existing coefficient and fresh encoding of zero to multResults, because they needed to be added to
        //the final result in the next step below
        multResults.add(existingEncodings.get(i));
        MultScalarTwoPointsGadget createFreshEncodingOfZero = new MultScalarTwoPointsGadget(key, basePoint, publicPoint,
                true, Constants.DESC_SCALR_MULT_TWO_POINTS_OVER_EC);
        multResults.add(createFreshEncodingOfZero.getResultingEncoding());

        Encoding finalResult = multResults.get(0);
        for(int j=1; j<multResults.size(); j++){
            Encoding nextCandidateForAddition = multResults.get(j);
            AddTwoEncodingsGadget addTwoEncodingsGadget = new AddTwoEncodingsGadget(finalResult,
                    nextCandidateForAddition, Constants.DESC_ADD_TWO_ENC);
            finalResult = addTwoEncodingsGadget.getEncodingResult();
        }
        evaluationOfPolynomial = finalResult;
    }


    @Override
    public Wire[] getOutputWires() {
        return new Wire[]{evaluationOfPolynomial.getEncodingPart1().getX(),
                evaluationOfPolynomial.getEncodingPart1().getY(), evaluationOfPolynomial.getEncodingPart2().getX(),
                evaluationOfPolynomial.getEncodingPart2().getY()};
    }

    public Encoding getEvaluationOfPolynomial() {
        return evaluationOfPolynomial;
    }
}
