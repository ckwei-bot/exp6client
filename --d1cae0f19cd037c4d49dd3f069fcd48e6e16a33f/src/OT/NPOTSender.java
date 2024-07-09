package OT;

import java.math.*;
import java.util.*;
import java.io.*;
import java.security.SecureRandom;

import Cipher.Cipher;
import Utils.*;

public class NPOTSender extends Sender {

	private static SecureRandom rnd = new SecureRandom();

	private BigInteger p, q, g, d, r;


	private static final int certainty = 80;

	private final static int qLength = 512; //512;
	private final static int pLength = 15360; //15360;

	//private BigInteger p, q, g, C, r;
//    private BigInteger Cr, gr;
	private BigInteger dr, gr;


	public NPOTSender(int numOfPairs, int msgBitLength, ObjectInputStream in,
					  ObjectOutputStream out) throws Exception {
		super(numOfPairs, msgBitLength, in, out);

		StopWatch.pointTimeStamp("right before NPOT public key generation");
		initialize();
		StopWatch.taskTimeStamp("NPOT public key generation");
	}

	public void execProtocol(BigInteger[][] msgPairs) throws Exception {
		super.execProtocol(msgPairs);
		step1();
	}


	public void openOT() throws Exception {
		step2();
		step3();
	}


	private void initialize() throws Exception {
		File keyfile = new File("NPOTKey");
		if (keyfile.exists()) {
			FileInputStream fin = new FileInputStream(keyfile);
			ObjectInputStream fois = new ObjectInputStream(fin);

			d = (BigInteger) fois.readObject();
			p = (BigInteger) fois.readObject();
			q = (BigInteger) fois.readObject();
			g = (BigInteger) fois.readObject();
			gr = (BigInteger) fois.readObject();
			r = (BigInteger) fois.readObject();
			fois.close();

			oos.writeObject(d);
			oos.writeObject(p);
			oos.writeObject(q);
			oos.writeObject(g);
			oos.writeObject(gr);
			oos.writeInt(msgBitLength);
			oos.flush();

			dr = d.modPow(r, p);
		} else {
			BigInteger pdq;
			q = new BigInteger(qLength, certainty, rnd);//生成可能的素数

			do {
				pdq = new BigInteger(pLength - qLength, rnd);
				pdq = pdq.clearBit(0); //清除最低有效位
				p = q.multiply(pdq).add(BigInteger.ONE);//生成可能的素数
			} while (!p.isProbablePrime(certainty));

			do {
				g = new BigInteger(pLength - 1, rnd);
			} while ((g.modPow(pdq, p)).equals(BigInteger.ONE)
					|| (g.modPow(q, p)).equals(BigInteger.ONE));


			r = (new BigInteger(qLength, rnd)).mod(q);
			gr = g.modPow(r, p);
			d = (new BigInteger(qLength, rnd)).mod(q);

			oos.writeObject(d);
			oos.writeObject(p);
			oos.writeObject(q);
			oos.writeObject(g);
			oos.writeObject(gr);
			oos.writeInt(msgBitLength);
			oos.flush();

			dr = d.modPow(r, p);

			FileOutputStream fout = new FileOutputStream(keyfile);
			ObjectOutputStream foos = new ObjectOutputStream(fout);

			foos.writeObject(d);
			foos.writeObject(p);
			foos.writeObject(q);
			foos.writeObject(g);
			foos.writeObject(gr);
			foos.writeObject(r);

			foos.flush();
			foos.close();
		}
	}


	private void step1() throws Exception {//传输labels也就是电路
		BigInteger[] h0 = (BigInteger[]) ois.readObject();
		BigInteger[] h1 = new BigInteger[numOfPairs];
		BigInteger[][] msg = new BigInteger[numOfPairs][2];


		for (int i = 0; i < numOfPairs; i++) {
			h0[i] = h0[i].modPow(r, p);
			h1[i] = dr.multiply(h0[i].modInverse(p)).mod(p);//not match with jko13
			msg[i][0] = Cipher.encrypt(h0[i], msgPairs[i][0], msgBitLength);
			msg[i][1] = Cipher.encrypt(h1[i], msgPairs[i][1], msgBitLength);
		}

		oos.writeObject(msg);
		System.out.println("sender-step1");
		oos.flush();
	}



	private void step2() throws Exception {
		oos.writeObject(r);
		System.out.println("sender-step2");

		oos.flush();

	}

	private void step3() throws Exception {
		Integer flag = (Integer) ois.readObject();
		int value = flag.intValue();
		if(value != 1){
			throw  new Exception("flag is 0");
		}
		oos.writeObject(msgPairs);
		System.out.println("sender-step3");

		oos.flush();
	}
}
