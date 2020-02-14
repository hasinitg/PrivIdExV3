package examples.gadgets.ECDL;

public class Encoding {

    private AffinePoint encodingPart1;
    private AffinePoint encodingPart2;

    public Encoding(AffinePoint part1, AffinePoint part2){
        this.encodingPart1 = part1;
        this.encodingPart2 = part2;
    }

    public AffinePoint getEncodingPart1() {
        return encodingPart1;
    }

    public void setEncodingPart1(AffinePoint encodingPart1) {
        this.encodingPart1 = encodingPart1;
    }

    public AffinePoint getEncodingPart2() {
        return encodingPart2;
    }

    public void setEncodingPart2(AffinePoint encodingPart2) {
        this.encodingPart2 = encodingPart2;
    }
}
