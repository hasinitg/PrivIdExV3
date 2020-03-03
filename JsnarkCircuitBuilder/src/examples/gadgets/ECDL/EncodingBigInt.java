package examples.gadgets.ECDL;

public class EncodingBigInt {

    private AffinePointBigInt part1;
    private AffinePointBigInt part2;

    public EncodingBigInt(AffinePointBigInt p1, AffinePointBigInt p2){
        this.part1 = p1;
        this.part2 = p2;
    }

    public AffinePointBigInt getPart1() {
        return part1;
    }

    public void setPart1(AffinePointBigInt part1) {
        this.part1 = part1;
    }

    public AffinePointBigInt getPart2() {
        return part2;
    }

    public void setPart2(AffinePointBigInt part2) {
        this.part2 = part2;
    }

}
