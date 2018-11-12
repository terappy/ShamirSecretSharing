package scheme;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


/**
 * Shamir's Secret Sharing Scheme.<br/>
 * (k,n) Threshold Scheme
 */
public class SecretShare {
    private final int k;
    private final BigInteger p;
    private final int modLength;
    private BigInteger[] polynomial;

    public SecretShare(final int k, final String secret){
        this.k = k;
        this.modLength = new BigInteger(secret.getBytes()).bitLength() + 10;
        this.p = BigInteger.probablePrime(this.modLength, new Random());
        this.polynomial = generatePolynomial(this.k, secret);
    }

    public SecretShare(final int k, final BigInteger secret){
        this.k = k;
        this.modLength = secret.bitLength() + 10;
        this.p = BigInteger.probablePrime(this.modLength, new Random());
        this.polynomial = generatePolynomial(this.k, secret);
    }

    public int getK() {
        return k;
    }

    public BigInteger getP() {
        return p;
    }

    public int getModLength() {
        return modLength;
    }

    public BigInteger[] getPolynomial() {
        return polynomial;
    }

    /***
     * 多項式生成<br/>f(x)= c + a_1 * x_1 ... a_(k-1) * x^(k-1) mod p<br/>の係数部分を生成する
     * @param k しきい値
     * @param secret 秘密情報
     * @return
     */
    private BigInteger[] generatePolynomial(int k, BigInteger secret){
        BigInteger[] polynomial = new BigInteger[k];
        polynomial[0] = secret;
        for(int i=1; i<k; i++){
            polynomial[i] = randomZp();
        }
        return polynomial;
    }

    /***
     * 多項式生成<br/>f(x)= c + a_1 * x_1 ... a_(k-1) * x^(k-1) mod p<br/>の係数部分を生成する
     * @param k しきい値
     * @param secret 秘密情報
     * @return
     */
    private BigInteger[] generatePolynomial(final int k, final String secret){
        return generatePolynomial(k, new BigInteger(secret.getBytes()));
    }

    /***
     * ランダムなZpを生成
     * @return ランダムに生成されたZpの元
     */
    private BigInteger randomZp(){
        BigInteger r;

        do{
            r = new BigInteger(this.modLength, new Random());
        }while (r.compareTo(BigInteger.ZERO) <= 0 || r.compareTo(this.p) >= 0);

        return r;
    }


    /***
     * シェアデータを生成する<br/>f(x)= c + a_1 * x_1 ... a_(k-1) * x^(k-1) mod p
     * @param shareNumber xの値
     * @return シェアデータ: {x, f(x)}
     */
    public ShareData generateShareData(final int shareNumber){
        if(this.polynomial == null){
            return null;
        }

        int polynomialLength = this.polynomial.length;
        BigInteger x = BigInteger.valueOf(shareNumber);
        BigInteger y = BigInteger.valueOf(0);

        for(int i=0; i<polynomialLength; i++){
            // a_i * x^(i) を計算し結果を加算する
            y = y.add(polynomial[i].multiply(x.pow(i)));
        }

        return new ShareData(shareNumber, y.mod(p));
    }

    /***
     * 秘密情報の削除
     */
    public void deletePolynomial(){
        this.polynomial = null;
    }

    public List<ShareData> encrypt(final int n){
        if(n < this.k){
            return null;
        }
        List<ShareData> encryptedDataList = new ArrayList<>();
        for(int i=0; i<n; i++){
            encryptedDataList.add(generateShareData((i+1)));
        }
        return encryptedDataList;
    }

    /***
     * シェアデータから秘密情報を復元する
     * @param encryptedDataList
     * @return 秘密情報
     * @deprecated 割り算の時に誤差が生じてしまうため、別のメソッドに置き換えられました {@link #decrypt(List)}
     */
    public String decrypt_old(final List<ShareData> encryptedDataList){
        final int dataLength = encryptedDataList.size();

        if(dataLength < this.k){
            return null;
        }

        BigInteger result = BigInteger.valueOf(0);

        List<ShareData> dataList = new ArrayList<>(encryptedDataList.subList(0, this.k));
        int interpolation = 2*3*5*7*11*13*17*19;

        for(int i=0; i<this.k; i++){
            int xi = dataList.get(i).getX();
            BigInteger fx = dataList.get(i).getData();

            long numerator = 1; // 分子
            long denominator = 1; // 分母

            // DEBUG
            System.out.print("xi: "+xi+ " || \t");

            for(int l=0; l<this.k; l++){
                if(l == i){
                    continue;
                }
                int xl = dataList.get(l).getX();

                numerator *= xl;
                denominator *= xl-xi;
                // DEBUG
                System.out.print("xl: "+xl+ " | ");
            }


            result = result.add(fx.multiply(BigInteger.valueOf(numerator)).multiply(BigInteger.valueOf(interpolation)).divide(BigInteger.valueOf(denominator)));

            // DEBUG
            System.out.print("\t|| ( "+numerator+ " / "+denominator+ " ) = "+ Float.valueOf(numerator) / denominator + " | ");
            System.out.println(result);
        }

        result = result.divide(BigInteger.valueOf(interpolation)).mod(p);

        // DEBUG
        System.out.println("resultInteger: "+result);

        return new String(result.toByteArray());
    }


    /***
     * シェアデータから秘密情報を復元する
     * @param encryptedDataList
     * @return 秘密情報
     */
    public String decrypt(final List<ShareData> encryptedDataList){
        final int dataLength = encryptedDataList.size();

        if(dataLength < this.k){
            return null;
        }

        BigDecimal result = BigDecimal.valueOf(0);
        List<ShareData> dataList = new ArrayList<>(encryptedDataList.subList(0, this.k));

        for(int i=0; i<this.k; i++){
            int xi = dataList.get(i).getX();
            BigDecimal fx = new BigDecimal(dataList.get(i).getData());

            long numerator = 1; // 分子
            long denominator = 1; // 分母

            // DEBUG
            System.out.print("xi: "+xi+ " || \t");

            for(int l=0; l<this.k; l++){
                if(l == i){
                    continue;
                }
                int xl = dataList.get(l).getX();

                numerator *= xl;
                denominator *= xl-xi;

                // DEBUG
                System.out.print("xl: "+xl+ " | ");
            }

            result = result.add(fx.multiply(BigDecimal.valueOf(numerator)).divide(BigDecimal.valueOf(denominator),3,BigDecimal.ROUND_HALF_UP));

            // DEBUG
            System.out.print("\t|| ( " + numerator + " / " + denominator + " ) = " + Float.valueOf(numerator) / denominator + " | ");
            System.out.println(result);

        }

        BigInteger res = result.setScale(0,BigDecimal.ROUND_UP).toBigInteger().mod(this.p);

        // DEBUG
        System.out.println("resultInteger: "+res);
        System.out.print("[ ");
        for(byte b:res.toByteArray()){
            System.out.print(b+" ");
        }
        System.out.println("]");

        return new String(res.toByteArray());
    }

    /***
     * シェアデータから秘密情報を復元する
     * @param encryptedDataList
     * @return 秘密情報
     */
    public BigInteger decryptToNumber(final List<ShareData> encryptedDataList){
        final int dataLength = encryptedDataList.size();

        if(dataLength < this.k){
            return null;
        }

        BigDecimal result = BigDecimal.valueOf(0);
        List<ShareData> dataList = new ArrayList<>(encryptedDataList.subList(0, this.k));

        for(int i=0; i<this.k; i++){
            int xi = dataList.get(i).getX();
            BigDecimal fx = new BigDecimal(dataList.get(i).getData());

            long numerator = 1; // 分子
            long denominator = 1; // 分母

            // DEBUG
            System.out.print("xi: "+xi+ " || \t");

            for(int l=0; l<this.k; l++){
                if(l == i){
                    continue;
                }
                int xl = dataList.get(l).getX();

                numerator *= xl;
                denominator *= xl-xi;

                // DEBUG
                System.out.print("xl: "+xl+ " | ");
            }

            result = result.add(fx.multiply(BigDecimal.valueOf(numerator)).divide(BigDecimal.valueOf(denominator),3,BigDecimal.ROUND_HALF_UP));

            // DEBUG
            System.out.print("\t|| ( " + numerator + " / " + denominator + " ) = " + Float.valueOf(numerator) / denominator + " | ");
            System.out.println(result);

        }

        return result.setScale(0,BigDecimal.ROUND_UP).toBigInteger().mod(this.p);

    }

    @Override
    public String toString() {
        return "SecretShare{" +
                "k=" + k +
                ", p=" + p +
                ", modLength=" + modLength +
                ", polynomial=" + Arrays.toString(polynomial) +
                '}';
    }
}
