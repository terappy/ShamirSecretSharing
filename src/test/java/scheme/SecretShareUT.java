package scheme;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class SecretShareUT {
    private static final int K = 2;
    private static final int N = 3;
    private static final String SECRET = "secret";
    private static BigInteger SECRET_INTEGER;


    @Before
    public void setUp() throws Exception {
        SECRET_INTEGER = new BigInteger(SECRET.getBytes());
    }


    @Test
    public void testGeneratePolynomial(){
        Method method;
        try {
            method = SecretShare.class.getDeclaredMethod("generatePolynomial", int.class, BigInteger.class);
            method.setAccessible(true);
            SecretShare secretShare = new SecretShare(K,SECRET);
            BigInteger[] actual = (BigInteger[])method.invoke(secretShare, K, SECRET_INTEGER);
            assertThat(actual[0], is(SECRET_INTEGER));
            assertThat(actual.length, is(2));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGenerateShareData() {
        SecretShare secretShare = new SecretShare(K,SECRET);
        int x = 1;
        ShareData actual = secretShare.generateShareData(x);

        assertThat(actual.getX(), is(x));
        assertThat(actual.getData(), instanceOf(BigInteger.class));
    }

    @Test
    public void testDeletePolynomial() {
        SecretShare secretShare = new SecretShare(K, SECRET);
        assertThat(secretShare.getPolynomial(), notNullValue());
        secretShare.deletePolynomial();
        assertThat(secretShare.getPolynomial(), nullValue());
    }

    @Test
    public void testEncrypt() {
        SecretShare secretShare = new SecretShare(K, SECRET);
        List<ShareData> actual = secretShare.encrypt(N);

        assertThat(actual.size(), is(N));
        assertThat(actual.get(0).getX(), is(1));
        assertThat(actual.get(1).getX(), is(2));
        assertThat(actual.get(2).getX(), is(3));
    }

    @Test
    public void testDecrypt() {
        SecretShare secretShare = new SecretShare(K, SECRET);
        List<ShareData> encryptedData = secretShare.encrypt(N);

        assertThat(secretShare.decryptToString(encryptedData), is(SECRET));
    }

}
