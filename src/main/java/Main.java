import scheme.SecretShare;
import scheme.ShareData;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
    static int missCount = 0;
    static int missCountForNum = 0;

    public static void main(String[] args){
        int k = 4;
        int n = 6;
        String secret = "secret sharing";

        // secretを出力
        System.out.println(secret);
        System.out.println(new BigInteger(secret.getBytes()));

        System.out.print("[ ");
        for(byte b:new BigInteger(secret.getBytes()).toByteArray()){
            System.out.print(b+" ");
        }
        System.out.println("]");


        SecretShare ss = new SecretShare(k,secret);

        List<ShareData> dataList = ss.encrypt(n);

        System.out.println(ss);
        System.out.println(dataList);
        System.out.println("================");

        printResult(ss.decryptToString(dataList),secret);

        System.out.println("================");
        System.out.println("Shuffled!");

        Collections.shuffle(dataList);
        printResult(ss.decryptToString(dataList),secret);

        System.out.println("================");
        System.out.println("================");
        System.out.println("================");

        if(k==2){
            for(int i=0; i<n; i++){
                for (int j=i+1; j<n; j++){
                    List<ShareData> targetPair = Arrays.asList(dataList.get(i), dataList.get(j));
                    System.out.println(targetPair);
                    printResult(ss.decryptToString(targetPair),secret);
                    System.out.println("================");

                }
            }
        }

        System.out.println("================");
        System.out.println("================");

        if(missCount == 0){
            System.out.println("ALL OK!!");
        }else{
            System.out.println("failed : " + missCount);
        }


        System.out.println("-------------------------------------------------------");


        BigInteger secret2 = BigInteger.valueOf(1234567890);
        SecretShare ss2 = new SecretShare(k,secret2);

        List<ShareData> dataList2 = ss2.encrypt(n);

        System.out.println(secret2);
        System.out.println(ss2);
        System.out.println(dataList2);
        System.out.println("================");

        printResult(ss2.decrypt(dataList2),secret2);

        System.out.println("================");
        System.out.println("Shuffled!");

        Collections.shuffle(dataList2);
        printResult(ss2.decrypt(dataList2),secret2);

        if(missCountForNum == 0){
            System.out.println("ALL OK!!");
        }else{
            System.out.println("failed : " + missCountForNum);
        }


    }



    private static void printResult(String actual, String expected){
        System.out.println(new BigInteger(actual.getBytes()));
        System.out.println("Actual: "+actual+" | Expected: "+expected);
        System.out.println("####################################");
        System.out.print("# Result -> \t");
        if(actual.equals(expected)){
            System.out.println("Match!!!!!!");
        }else{
            System.out.println("MissMatch..........");
            missCount++;
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
            missCountForNum++;
        }
        System.out.println("####################################");
    }
}
