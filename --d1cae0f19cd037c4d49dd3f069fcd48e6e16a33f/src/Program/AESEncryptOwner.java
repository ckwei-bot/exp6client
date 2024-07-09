package Program;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import YaoGC.*;
import LookupTable.*;
import Utils.*;


public class AESEncryptOwner extends ProgOwner {

    private BigInteger[] slbs,slbs0,slbs1,slbs2, clbs,clbs0,clbs1,clbs2,clbs3,clbs4,clbs5,clbs6,clbsown;
    private short[] msg,msg1,msg2,msg3,msg4,msg5,msg6;

    AESEncryptCommon aes;

    State outputState;

    public AESEncryptOwner(short[] msgIn, int NkIn) {
        msg = msgIn;
        aes = new AESEncryptCommon(4);
    }
    //		public AESEncryptClient(short[] msgIn, short[] msgIn1,int NkIn) {
//		msg = msgIn;
//		msg1 = msgIn1;
//		aes = new AESEncryptCommon(4);
//	}
    public AESEncryptOwner(short[] msgIn, short[] msgIn1, short[] msgIn2,int NkIn) {
        msg = msgIn;
        msg1 = msgIn1;
        msg2 = msgIn2;
        aes = new AESEncryptCommon(4);
    }

    protected void init() throws Exception {
        AESEncryptCommon.Nk = AESEncryptCommon.oisown.readInt();
        AESEncryptCommon.Nr = AESEncryptCommon.Nk + 6;

        AESEncryptCommon.initCircuits();

        LookupTableReceiver.ois = AESEncryptCommon.oisown;
        AESEncryptCommon.agent = new LookupTableReceiver();

        otNumOfPairs = AESEncryptCommon.Nb*32;

        System.out.println("Test_init");

        super.init();
    }


    //owner此时应该接收到相应的k'
    protected void execTransfer() throws Exception{
        int bytelength = (Wire.labelBitLength-1)/8 + 1;

        slbs = new BigInteger[(AESEncryptCommon.Nr+1)*128];
        for (int i = 0; i < slbs.length; i++) {//三个client得到一样的
            slbs[i] = Utils.readBigInteger(bytelength, AESEncryptCommon.oisown);
        }
        StopWatch.taskTimeStamp("receiving labels for peer's inputs");
        clbs0 = (BigInteger[]) AESEncryptCommon.ois0.readObject();
        clbs1 = (BigInteger[]) AESEncryptCommon.ois1.readObject();
        clbs2 = (BigInteger[]) AESEncryptCommon.ois2.readObject();
        clbs3 = (BigInteger[]) AESEncryptCommon.ois3.readObject();
        clbs4 = (BigInteger[]) AESEncryptCommon.ois4.readObject();
        clbs5 = (BigInteger[]) AESEncryptCommon.ois5.readObject();
        clbs6 = (BigInteger[]) AESEncryptCommon.ois6.readObject();
        BigInteger m = BigInteger.ZERO;

        for (int i = 0; i < 16; i++)
            m = m.shiftLeft(8).xor(BigInteger.valueOf(msg[15-i]));

        rcverown.execProtocol(m);//每个client都需要执行
        clbsown = rcverown.getData();
        StopWatch.taskTimeStamp("receiving labels for self's inputs");


    }



    protected void execCircuit() throws Exception {//owner activity
//		System.out.println("Test1");
        clbs = new BigInteger[clbs0.length + clbs1.length+ clbs2.length+ clbs3.length+ clbs4.length+ clbs5.length+ clbs6.length];
//		System.out.println("Test1-mid");
        System.arraycopy(clbs0,0,clbs,0,clbs0.length);
        System.arraycopy(clbs1,0,clbs,clbs0.length,clbs1.length);
        System.arraycopy(clbs2,0,clbs,clbs0.length+clbs1.length,clbs2.length);
        System.arraycopy(clbs3,0,clbs,clbs0.length+clbs1.length+clbs2.length,clbs3.length);
        System.arraycopy(clbs4,0,clbs,clbs0.length+clbs1.length+clbs2.length+clbs3.length,clbs4.length);
        System.arraycopy(clbs5,0,clbs,clbs0.length+clbs1.length+clbs2.length+clbs3.length+clbs4.length,clbs5.length);
        System.arraycopy(clbs6,0,clbs,clbs0.length+clbs1.length+clbs2.length+clbs3.length+clbs4.length+clbs5.length,clbs6.length);



        outputState = AESEncryptCommon.Cipher(State.fromLabels(slbs), State.fromLabels(clbs));
//		System.out.println("Test1-end");
//        BigInteger[] temp = outputState.toLabels();
//        for(int i=0;i<128;i++){
//            System.out.println("label-" + i +":"+temp[i]);
//        }


    }


    protected void commit() throws Exception {//owner activity
//		System.out.println("Test2");

        try {
            // 将BigInteger数组转换为字节数组
            ByteBuffer byteBuffer = ByteBuffer.allocate(outputState.toLabels().length * Integer.BYTES * outputState.toLabels().length);
            for (BigInteger bi : outputState.toLabels()) {
                byteBuffer.put(bi.toByteArray());
            }
            byte[] byteArray = byteBuffer.array();

            System.out.println("Test3");
            // 计算SHA-256哈希值
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(byteArray);

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            String sha256Hash = hexString.toString();
            System.out.println("SHA-256 哈希值： " + sha256Hash);
            AESEncryptCommon.oosown.writeObject(sha256Hash);
            AESEncryptCommon.oosown.flush();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        StopWatch.taskTimeStamp("client-commit");

    }

    protected void openCircuit() throws Exception {
        rcverown.openOT();
    }

    protected void interpretResult() throws Exception {// evaluation

        int bytelength = (Wire.labelBitLength-1)/8 + 1;


        System.out.println("re1");
        BigInteger[] slbs1 = (BigInteger[]) AESEncryptCommon.oisown.readObject();
        System.out.println("re2");

        BigInteger[] mergeclbs1 = (BigInteger[]) AESEncryptCommon.oisown.readObject();
        System.out.println("re3");


        AESEncryptCommon.oosown.writeObject(outputState.toLabels());
        AESEncryptCommon.oosown.flush();
        System.out.println("end");
        Circuit.isForGarbling = true;
        slbs = new BigInteger[(AESEncryptCommon.Nr+1)*128];
        for (int i = 0; i < slbs.length; i++) {//三个client得到一样的
            slbs[i] = Utils.readBigInteger(bytelength, AESEncryptCommon.oisown);
        }
        State outputState1 = AESEncryptCommon.Cipher(State.fromLabels(slbs1), State.fromLabels(mergeclbs1));

        BigInteger[] temp1 = outputState1.toLabels();
        for(int i=0;i<128;i++) {
            System.out.println("label--1-" + i + ":" + temp1[i]);
        }

//        if(!outputState.toLabels().equals(outputState1.toLabels())) System.out.println("err");
        System.out.println("cipher");

        BigInteger[] outLabels = outputState.toLabels();

        System.out.println("rea-own");

        BigInteger[] temp = outputState.toLabels();
        for(int i=0;i<128;i++) {
            System.out.println("label-" + i + ":" + temp[i]);
        }
        // for (int i = 2; i < outLabels.length; i=i+2) {
        //     if(temp[i].equals(temp1[i]) || temp[i+1].equals(temp1[i+1])){
        //         continue;
        //     }
        //     else{
        //         System.out.println("wrong pairs:"+i);
        //     }
        // }
        System.out.println("cipher end");




    }

    protected void verify_result() throws Exception {

    }
}

