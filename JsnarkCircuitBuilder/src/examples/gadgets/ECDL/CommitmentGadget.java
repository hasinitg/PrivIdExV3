package examples.gadgets.ECDL;

import circuit.operations.Gadget;
import circuit.structure.Wire;
import examples.gadgets.hash.SHA256Gadget;

import java.math.BigInteger;

//Gadget for computing commitment over the cryptographic hash of the identity asset using SHA-256 as the commitment scheme.
//This gadget is just a wrapper for SHA-256 gadget, which takes hash and the secret to be committed in BigInteger format
//and provides input to SHA-256 in hex string format
public class CommitmentGadget extends Gadget {

    //inputs
    private BigInteger hashOfIDAsset;
    private BigInteger commitmentSecret;

    //gadget
    private SHA256Gadget commitmentScheme;

    public CommitmentGadget(BigInteger hashOfIDAsset, BigInteger secret, String desc){
        super(desc);
        this.hashOfIDAsset = hashOfIDAsset;
        this.commitmentSecret = secret;
        buildCircuit();
    }

    public void buildCircuit(){
        String hashOfIDAssetInHex = hashOfIDAsset.toString(16);
        String secretInHex = commitmentSecret.toString(16);

        //commitmentScheme = new SHA256Gadget()
    }

    @Override
    public Wire[] getOutputWires() {
        return new Wire[0];
    }
}
