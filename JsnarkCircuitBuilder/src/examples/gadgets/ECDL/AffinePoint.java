package examples.gadgets.ECDL;

import circuit.structure.Wire;

public class AffinePoint {

    private Wire x;
    private Wire y;

    public Wire getX() {
        return x;
    }

    public Wire getY() {
        return y;
    }

    public void setX(Wire x) {
        this.x = x;
    }

    public void setY(Wire y) {
        this.y = y;
    }

    AffinePoint(Wire x) {
        this.x = x;
    }

    AffinePoint(Wire x, Wire y) {
        this.x = x;
        this.y = y;
    }

    AffinePoint(AffinePoint p) {
        this.x = p.x;
        this.y = p.y;
    }
}
