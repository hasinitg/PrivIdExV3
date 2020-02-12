package examples.gadgets.ECDH;

import circuit.structure.CircuitGenerator;
import circuit.structure.Wire;
import examples.gadgets.diffieHellmanKeyExchange.ECDHKeyExchangeGadget;
import examples.gadgets.math.FieldDivisionGadget;

import java.math.BigInteger;

public interface ECDHBase {
    public final static int SECRET_BITWIDTH = 253;
    public final static BigInteger COEFF_A = new BigInteger("126932");

    public final static BigInteger CURVE_ORDER = new BigInteger(
            "21888242871839275222246405745257275088597270486034011716802747351550446453784");

    // As in curve25519, CURVE_ORDER = SUBGROUP_ORDER * 2^3
    public final static BigInteger SUBGROUP_ORDER = new BigInteger(
            "2736030358979909402780800718157159386074658810754251464600343418943805806723");

    default void checkSecretBits(Wire[] secretBits, CircuitGenerator generator) {
        /**
         * The secret key bits must be of length SECRET_BITWIDTH and are
         * expected to follow a little endian order. The most significant bit
         * should be 1, and the three least significant bits should be zero.
         */
        if (secretBits.length != SECRET_BITWIDTH) {
            throw new IllegalArgumentException();
        }
        generator.addZeroAssertion(secretBits[0],
                "Asserting secret bit conditions");
        generator.addZeroAssertion(secretBits[1],
                "Asserting secret bit conditions");
        generator.addZeroAssertion(secretBits[2],
                "Asserting secret bit conditions");
        generator.addOneAssertion(secretBits[SECRET_BITWIDTH - 1],
                "Asserting secret bit conditions");

        for (int i = 3; i < SECRET_BITWIDTH - 1; i++) {
            // verifying all other bit wires are binary (as this is typically a
            // secret
            // witness by the prover)
            generator.addBinaryAssertion(secretBits[i]);
        }
    }

    default AffinePoint[] preprocess(AffinePoint p, Wire[] secretBits) {
        AffinePoint[] precomputedTable = new AffinePoint[secretBits.length];
        precomputedTable[0] = p;
        for (int j = 1; j < secretBits.length; j += 1) {
            precomputedTable[j] = doubleAffinePoint(precomputedTable[j - 1]);
        }
        return precomputedTable;
    }

    default AffinePoint doubleAffinePoint(AffinePoint p) {
        Wire x_2 = p.getX().mul(p.getX());
        Wire l1 = new FieldDivisionGadget(x_2.mul(3)
                .add(p.getX().mul(COEFF_A).mul(2)).add(1), p.getY().mul(2))
                .getOutputWires()[0];
        Wire l2 = l1.mul(l1);
        Wire newX = l2.sub(COEFF_A).sub(p.getX()).sub(p.getX());
        Wire newY = p.getX().mul(3).add(COEFF_A).sub(l2).mul(l1).sub(p.getY());
        return new AffinePoint(newX, newY);
    }

    /**
     * Performs scalar multiplication (secretBits must comply with the
     * conditions above)
     */
    default AffinePoint mul(Wire[] secretBits,
                            AffinePoint[] precomputedTable) {

        AffinePoint result = new AffinePoint(
                precomputedTable[secretBits.length - 1]);
        for (int j = secretBits.length - 2; j >= 0; j--) {
            AffinePoint tmp = addAffinePoints(result, precomputedTable[j]);
            Wire isOne = secretBits[j];
            result.setX(result.getX().add(isOne.mul(tmp.getX().sub(result.getX()))));
            result.setY(result.getY().add(isOne.mul(tmp.getY().sub(result.getY()))));
        }
        return result;
    }

    default AffinePoint addAffinePoints(AffinePoint p1, AffinePoint p2) {
        Wire diffY = p1.getY().sub(p2.getY());
        Wire diffX = p1.getX().sub(p2.getX());
        Wire q = new FieldDivisionGadget(diffY, diffX).getOutputWires()[0];
        Wire q2 = q.mul(q);
        Wire q3 = q2.mul(q);
        Wire newX = q2.sub(COEFF_A).sub(p1.getX()).sub(p2.getX());
        Wire newY = p1.getX().mul(2).add(p2.getX()).add(COEFF_A).mul(q).sub(q3).sub(p1.getY());
        return new AffinePoint(newX, newY);
    }

}
