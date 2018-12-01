package scheme;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


/**
 * Shamir's Secret Sharing Scheme.<br/>
 * (k,n) Threshold Scheme
 */
public class SecretShare {
    private final int k;                // しきい値
    private final BigInteger p;         // 素数
    private final int modLength;        // ビット長
    private BigInteger[] polynomial;    // 多項式の係数群


    public SecretShare(final int k, final BigInteger secret){
        this.k = k;
        this.modLength = secret.bitLength();
        this.p = getPrimeNumber(secret);
        this.polynomial = generatePolynomial(this.k, secret);
    }

    public SecretShare(final int k, final int secret){
        this(k, BigInteger.valueOf(secret));
    }

    public SecretShare(final int k, final String secret){
        this(k, new BigInteger(secret.getBytes()));
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
    private BigInteger[] generatePolynomial(final int k, final BigInteger secret){
        BigInteger[] polynomial = new BigInteger[k];
        polynomial[0] = secret;
        for(int i=1; i<k; i++){
            polynomial[i] = randomZp();
        }
        return polynomial;
    }

    private BigInteger getPrimeNumber(final BigInteger secret){
        BigInteger r;
        do{
            r = BigInteger.probablePrime(secret.bitLength(), new Random());
        }while(r.compareTo(BigInteger.ZERO) <= 0 || r.compareTo(secret) <= 0);

        return r;
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

        final int polynomialLength = this.polynomial.length;
        final BigInteger x = BigInteger.valueOf(shareNumber);
        BigInteger y = BigInteger.valueOf(0);

        for(int i=0; i<polynomialLength; i++){
            // a_i * x^(i) を計算し結果を加算する
            y = y.add(polynomial[i].multiply(x.pow(i)).mod(this.p)).mod(this.p);
        }

        return new ShareData(shareNumber, y.mod(p));
    }

    /***
     * 秘密情報の削除
     */
    public void deletePolynomial(){
        this.polynomial = null;
    }

    /***
     * 秘密情報を秘密分散する
     * @param n
     * @return
     */
    public List<ShareData> encrypt(final int n){
        if(n < this.k){
            return null;
        }
        List<ShareData> encryptedDataList = new ArrayList<>();
        for(int i=0; i<n; i++){
//            final int x = calcChebyshevNode(i+1);
            final int x = (i+1);
            encryptedDataList.add(generateShareData(x));
        }
        return encryptedDataList;
    }

    /***
     * シェアデータから秘密情報を復元する
     * @param encryptedDataList
     * @return 秘密情報
     */
    public BigInteger decrypt(final List<ShareData> encryptedDataList){
        final int dataLength = encryptedDataList.size();

        if(dataLength < this.k){
            return null;
        }

        BigInteger result = BigInteger.valueOf(0);
        List<ShareData> dataList = new ArrayList<>(encryptedDataList.subList(0, this.k));

        for(int i=0; i<this.k; i++){
            final int xi = dataList.get(i).getX();
            final BigInteger fx = dataList.get(i).getData();

            BigInteger numerator = BigInteger.ONE; // 分子
            BigInteger denominator = BigInteger.ONE; // 分母

            for(int l=0; l<this.k; l++){
                if(l == i){
                    continue;
                }
                final int xl = dataList.get(l).getX();

                numerator = numerator.multiply(BigInteger.valueOf(xl).negate()).mod(this.p);
                denominator = denominator.multiply(BigInteger.valueOf(xi-xl)).mod(this.p);
            }

            result = result.add(fx.multiply(numerator).mod(this.p).multiply(denominator.modInverse(this.p)).mod(this.p)).mod(this.p);
        }

        System.out.println(result);

        return result;
    }

    /***
     * シェアデータから秘密情報を復元し、文字列に変換する
     * @param encryptedDataList
     * @return 秘密情報
     */
    public String decryptToString(final List<ShareData> encryptedDataList){
        return new String(decrypt(encryptedDataList).toByteArray());
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
