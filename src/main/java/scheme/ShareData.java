package scheme;

import java.math.BigInteger;

/***
 * Shamir's distributed data class
 */
public class ShareData {
    private final int x;
    private final BigInteger data;

    public ShareData(int x, BigInteger data){
        this.x = x;
        this.data = data;
    }

    public int getX() {
        return x;
    }

    public BigInteger getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ShareData{" +
                "x=" + x +
                ", data=" + data +
                '}';
    }
}
