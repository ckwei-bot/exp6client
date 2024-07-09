package Program;

import java.math.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import YaoGC.*;
import LookupTable.*;
import Utils.*;


public class AESEncryptClient1 extends ProgClient1 {

    private BigInteger[] slbs, clbs,clbs_own;
    private short[] msg;

    AESEncryptCommon aes;

    State outputState;

    public AESEncryptClient1(short[] msgIn,int NkIn) {
        msg = msgIn;
        aes = new AESEncryptCommon(4);
    }


    protected void init() throws Exception {
        AESEncryptCommon.Nk = AESEncryptCommon.ois1.readInt();
        AESEncryptCommon.Nr = AESEncryptCommon.Nk + 6;

//        AESEncryptCommon.initCircuits();

        LookupTableReceiver.ois = AESEncryptCommon.ois1;
        AESEncryptCommon.agent = new LookupTableReceiver();

        otNumOfPairs = AESEncryptCommon.Nb*32;

        System.out.println("Test_init");

        super.init();
    }

    //client do
    protected void execTransfer() throws Exception {
        int bytelength = (Wire.labelBitLength-1)/8 + 1;

        slbs = new BigInteger[(AESEncryptCommon.Nr+1)*128];
        for (int i = 0; i < slbs.length; i++) {//三个client得到一样的
            slbs[i] = Utils.readBigInteger(bytelength, AESEncryptCommon.ois1);
        }
        StopWatch.taskTimeStamp("receiving labels for peer's inputs");

        BigInteger m = BigInteger.ZERO;


        for (int i = 0; i < 16; i++)
            m = m.shiftLeft(8).xor(BigInteger.valueOf(msg[15-i]));


        rcver1.execProtocol(m);//每个client都需要执行
        clbs = rcver1.getData();
        StopWatch.taskTimeStamp("receiving labels for self's inputs");
        clbs_own = rcver1.getData();
        AESEncryptCommon.oosown.writeObject(clbs_own);
        AESEncryptCommon.oosown.flush();
        StopWatch.taskTimeStamp("send labels from client1's inputs to owner");


    }

    protected void execCircuit() throws Exception {//owner activity

    }

    protected void commit() throws Exception{

    }


    protected void openCircuit() throws Exception {
        rcver1.openOT();
    }

    protected void interpretResult() throws Exception {// evaluation

    }

    protected void verify_result() throws Exception {

    }
}

