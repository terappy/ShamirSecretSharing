import scheme.SecretShare;
import scheme.ShareData;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args){
        int k = 2;
        int n = 7;
        String secret = "secret";

        System.out.println(secret.getBytes());
        SecretShare ss = new SecretShare(k,secret);

        List<ShareData> dataList = ss.encrypt(n);

        System.out.println(secret);
        System.out.println(new BigInteger(secret.getBytes()));
        System.out.println(ss);
        System.out.println(dataList);
        System.out.println("================");

//        System.out.println(ss.decrypt_old(dataList));
//        System.out.println(ss.decrypt(dataList));
//
//
//        System.out.println("================");
//        System.out.println("Shuffled!");
//
//        Collections.shuffle(dataList);
//        System.out.println(ss.decrypt_old(dataList));
//        System.out.println(ss.decrypt(dataList));

        for(int i=0; i<n; i++){
            for (int j=i+1; j<n; j++){
                List<ShareData> targetPair = Arrays.asList(dataList.get(i), dataList.get(j));
                System.out.println(targetPair);
                printResult(ss.decrypt_old(targetPair), secret);
                System.out.println("---");
                printResult(ss.decrypt(targetPair),secret);
                System.out.println("================");

            }
        }
        for(byte b:BigInteger.valueOf(126879297332596L).toByteArray()){
            System.out.print(b+" ");
        }
        System.out.println();
        System.out.println(BigInteger.valueOf(126879297332596L).toByteArray());

    }
    private static void printResult(String actual, String expected){
        System.out.println("actual: "+actual+" | expected: "+expected);
        if(actual.equals(expected)){
            System.out.println("Match!!!!!!");
        }else{
            System.out.println("MissMatch..........");
        }
    }
}
