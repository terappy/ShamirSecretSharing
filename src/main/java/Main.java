import scheme.SecretShare;
import scheme.ShareData;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args){
        int k = 3;
        int n = 5;
        String secret = "secret sharing scheme for java";

        System.out.println(secret.getBytes());
        SecretShare ss = new SecretShare(k,secret);

        List<ShareData> dataList = ss.encrypt(n);

        System.out.println(secret);
        System.out.println(new BigInteger(secret.getBytes()));
        System.out.println(ss);
        System.out.println(dataList);
        System.out.println("================");

        printResult(ss.decrypt_old(dataList),secret);
        System.out.println("---");
        printResult(ss.decrypt(dataList),secret);


        System.out.println("================");
        System.out.println("Shuffled!");

        Collections.shuffle(dataList);
        printResult(ss.decrypt_old(dataList),secret);
        System.out.println("---");
        printResult(ss.decrypt(dataList),secret);


        if(k==2){
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
        }

        BigInteger secret2 = BigInteger.valueOf(123);
        SecretShare ss2 = new SecretShare(k,secret2);

        List<ShareData> dataList2 = ss2.encrypt(n);

        System.out.println(secret2);
        System.out.println(ss2);
        System.out.println(dataList2);
        System.out.println("================");

        printResult(ss2.decryptToNumber(dataList2),secret2);

        System.out.println("================");
        System.out.println("Shuffled!");

        Collections.shuffle(dataList2);
        printResult(ss2.decryptToNumber(dataList2),secret2);




    }



    private static void printResult(String actual, String expected){
        System.out.println("Actual: "+actual+" | Expected: "+expected);
        System.out.println("####################################");
        System.out.print("# Result -> \t");
        if(actual.equals(expected)){
            System.out.println("Match!!!!!!");
        }else{
            System.out.println("MissMatch..........");
        }
        System.out.println("####################################");

    }
    private static void printResult(BigInteger actual, BigInteger expected){
        System.out.println("Actual: "+actual+" | Expected: "+expected);
        System.out.println("####################################");
        System.out.print("# Result -> \t");
        if(actual.equals(expected)){
            System.out.println("Match!!!!!!");
        }else{
            System.out.println("MissMatch..........");
        }
        System.out.println("####################################");

    }

}
