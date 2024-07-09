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


public class AESEncryptClient3 extends ProgClient3 {

    private BigInteger[] slbs, clbs,clbs_own;
    private short[] msg;

    AESEncryptCommon aes;

    State outputState;

    public AESEncryptClient3(short[] msgIn,int NkIn) {
        msg = msgIn;
        aes = new AESEncryptCommon(4);
    }


    protected void init() throws Exception {
        AESEncryptCommon.Nk = AESEncryptCommon.ois3.readInt();
        AESEncryptCommon.Nr = AESEncryptCommon.Nk + 6;

        // AESEncryptCommon.initCircuits();

        LookupTableReceiver.ois = AESEncryptCommon.ois3;
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
            slbs[i] = Utils.readBigInteger(bytelength, AESEncryptCommon.ois3);
        }
        StopWatch.taskTimeStamp("receiving labels for peer's inputs");

        BigInteger m = BigInteger.ZERO;


        for (int i = 0; i < 16; i++)
            m = m.shiftLeft(8).xor(BigInteger.valueOf(msg[15-i]));



        rcver3.execProtocol(m);//每个client都需要执行
        clbs = rcver3.getData();
        StopWatch.taskTimeStamp("receiving labels for self's inputs");
        clbs_own = rcver3.getData();
        AESEncryptCommon.oosown.writeObject(clbs_own);
        AESEncryptCommon.oosown.flush();
        StopWatch.taskTimeStamp("send labels from client2's inputs to owner");




    }

    protected void execCircuit() throws Exception {//owner activity


    }

    protected void commit() throws Exception{

    }


    protected void openCircuit() throws Exception {
        rcver3.openOT();
    }

    protected void interpretResult() throws Exception {// evaluation
//		AESEncryptCommon.oos.writeObject(outputState.toLabels());
//		AESEncryptCommon.oos.flush();
    }

    protected void verify_result() throws Exception {

    }
}

