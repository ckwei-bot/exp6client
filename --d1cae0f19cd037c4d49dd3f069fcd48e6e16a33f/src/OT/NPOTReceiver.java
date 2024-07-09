package OT;

import java.math.*;
import java.util.*;
import java.io.*;
import java.security.SecureRandom;

import Cipher.Cipher;

public class NPOTReceiver extends Receiver {
	private static SecureRandom rnd = new SecureRandom();


	/*------------------JKO13 */


	private int msgBitLength;
	//    private BigInteger p, q, C;
	private BigInteger p, q, g, d,r;
	public BigInteger[] h0,h1;


	private BigInteger gr,alfa;

	private BigInteger[][] msgPairs;

	//    private BigInteger[] gk, C_over_gk;
	private BigInteger[] galfa, d_over_galfa;

	//	private BigInteger[][] pk;
	private BigInteger[][] h;
	private BigInteger[][] msg;

	private BigInteger[] keys;

	public NPOTReceiver(int numOfChoices,
						ObjectInputStream in, ObjectOutputStream out) throws Exception {
		super(numOfChoices, in, out);

		initialize();
	}

	public void execProtocol(BigInteger choices) throws Exception {
		super.execProtocol(choices);

		step1();
		step2();
		// step3();
	}

	public void openOT() throws Exception {
		step3();
		step4();
	}

	private void initialize() throws Exception {
		d  = (BigInteger) ois.readObject();
		p  = (BigInteger) ois.readObject();
		q  = (BigInteger) ois.readObject();
		g  = (BigInteger) ois.readObject();
		gr = (BigInteger) ois.readObject();
		msgBitLength = ois.readInt();

		galfa = new BigInteger[numOfChoices];
		d_over_galfa = new BigInteger[numOfChoices];
		keys = new BigInteger[numOfChoices];
		for (int i = 0; i < numOfChoices; i++) {
			alfa = (new BigInteger(q.bitLength(), rnd)).mod(q);//随机数，也是私钥
			galfa[i] = g.modPow(alfa, p);//私钥所对应的公钥pk_sigma
			d_over_galfa[i] = d.multiply(galfa[i].modInverse(p)).mod(p);//另一个公钥 pk_1-sigma
			keys[i] = gr.modPow(alfa, p);
		}
	}

	private void step1() throws Exception {
		h0 = new BigInteger[numOfChoices];
		h1 = new BigInteger[numOfChoices];
		h = new BigInteger[numOfChoices][2];
		for (int i = 0; i < numOfChoices; i++) {
			int b = choices.testBit(i) ? 1 : 0;
			h[i][b] = galfa[i];
			h[i][1-b] = d_over_galfa[i];

			// System.out.println("error1");
			h0[i] = h[i][0];//需要传输给sender
			// System.out.println("error2");

		}

		oos.writeObject(h0);
		oos.flush();
	}

	private void step2() throws Exception {
		msg = (BigInteger[][]) ois.readObject();

		BigInteger c2 = gr.modPow(alfa,p);
		c2 = c2.modInverse(p); 
		data = new BigInteger[numOfChoices];
		for (int i = 0; i < numOfChoices; i++) {
			int b = choices.testBit(i) ? 1 : 0;
			data[i] = Cipher.decrypt(keys[i], msg[i][b], msgBitLength);
			// data[i] = msg[i][b].multiply(c2).mod(p);
		}
	}

	private void step3() throws  Exception {
		System.out.println("receiver-step3-first");
		r = (BigInteger) ois.readObject();
		if (!gr.equals(g.modPow(r,p))){
			throw new Exception("r isn't right");
		}
		int flag = 1;
		oos.writeObject(flag);
		System.out.println("receiver-step3-second");

		oos.flush();

	}

	private void step4() throws Exception {
		msgPairs = (BigInteger[][]) ois.readObject();
		System.out.println("receiver-step4-first");

		BigInteger dr =  d.modPow(r, p);
		for (int i = 0; i < numOfChoices; i++) {
			h0[i] = h0[i].modPow(r, p);
			h1[i] = dr.multiply(h0[i].modInverse(p)).mod(p);//not match with jko13
			if(!msg[i][0].equals(Cipher.encrypt(h0[i], msgPairs[i][0], msgBitLength))
					||!msg[i][1].equals(Cipher.encrypt(h1[i], msgPairs[i][1], msgBitLength)))
				throw new Exception("open ot checks fail");
		}
		System.out.println("receiver-step4-second");


	}

	private void chooose() throws Exception{

	}
}
