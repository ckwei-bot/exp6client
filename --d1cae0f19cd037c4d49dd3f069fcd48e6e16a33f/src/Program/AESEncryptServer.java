package Program;

import java.math.*;

import YaoGC.*;
import LookupTable.*;
import Utils.*;

public class AESEncryptServer extends ProgServer {

	private BigInteger[][] slps, clps,clps1,clps2,clps3,clps4,clps5,clps6,clpsown;

	String commitment;

	AESEncryptCommon aes;

	State outputState;

	public AESEncryptServer(short[] key, int NkIn) {
		aes = new AESEncryptCommon(NkIn);
		aes.initServer(key);
	}

	protected void init() throws Exception {
		System.out.println("send nk");
		AESEncryptCommon.oos0.writeInt(AESEncryptCommon.Nk);//count 4
		AESEncryptCommon.oos0.flush();
		AESEncryptCommon.oos1.writeInt(AESEncryptCommon.Nk);
		AESEncryptCommon.oos1.flush();
		AESEncryptCommon.oos2.writeInt(AESEncryptCommon.Nk);
		AESEncryptCommon.oos2.flush();
		AESEncryptCommon.oos3.writeInt(AESEncryptCommon.Nk);
		AESEncryptCommon.oos3.flush();
		AESEncryptCommon.oos4.writeInt(AESEncryptCommon.Nk);
		AESEncryptCommon.oos4.flush();
		AESEncryptCommon.oos5.writeInt(AESEncryptCommon.Nk);
		AESEncryptCommon.oos5.flush();
//		AESEncryptCommon.oos6.writeInt(AESEncryptCommon.Nk);
//		AESEncryptCommon.oos6.flush();
		AESEncryptCommon.oosown.writeInt(AESEncryptCommon.Nk);
		AESEncryptCommon.oosown.flush();
		System.out.println("send nk-end");

		AESEncryptCommon.initCircuits();

		LookupTableSender.oos0 = AESEncryptCommon.oos0;//应该把stable统一发给两个client
		LookupTableSender.oos1 = AESEncryptCommon.oos1;//应该把stable统一发给两个client
		LookupTableSender.oos2 = AESEncryptCommon.oos2;//应该把stable统一发给两个client
		LookupTableSender.oos3 = AESEncryptCommon.oos3;//应该把stable统一发给两个client
		LookupTableSender.oos4 = AESEncryptCommon.oos4;//应该把stable统一发给两个client
		LookupTableSender.oos5 = AESEncryptCommon.oos5;//应该把stable统一发给两个client
//		LookupTableSender.oos6 = AESEncryptCommon.oos6;//应该把stable统一发给两个client


		LookupTableSender.oosown = AESEncryptCommon.oosown;//应该把stable统一发给两个client

		AESEncryptCommon.agent = new LookupTableSender(AESEncryptCommon.STable);

		System.out.println("Test_init");

		generateLabelPairs();

		super.init();
	}

	private void generateLabelPairs() {
		slps = new BigInteger[(AESEncryptCommon.Nr+1)*128][2];
		clps = new BigInteger[ AESEncryptCommon.Nb*32][2];
		clps1 = new BigInteger[ AESEncryptCommon.Nb*32][2];
		clps2 = new BigInteger[ AESEncryptCommon.Nb*32][2];
		clps3 = new BigInteger[ AESEncryptCommon.Nb*32][2];
		clps4 = new BigInteger[ AESEncryptCommon.Nb*32][2];
		clps5 = new BigInteger[ AESEncryptCommon.Nb*32][2];
//		clps6 = new BigInteger[ AESEncryptCommon.Nb*32][2];

		clpsown = new BigInteger[ AESEncryptCommon.Nb*32][2];


		for (int i = 0; i < slps.length; i++) {
			slps[i] = Wire.newLabelPair();
		}

		for (int i = 0; i < clps.length; i++) {
			clps[i] = Wire.newLabelPair();
			clps1[i] = Wire.newLabelPair();
			clps2[i] = Wire.newLabelPair();
			clps3[i] = Wire.newLabelPair();
			clps4[i] = Wire.newLabelPair();
			clps5[i] = Wire.newLabelPair();
//			clps6[i] = Wire.newLabelPair();

			clpsown[i] = Wire.newLabelPair();


		}
		BigInteger output = BigInteger.ZERO;

		for (int i = 0; i < 128; i++) {
			output = output.setBit(i);
		}

		BigInteger mask = BigInteger.valueOf(255);
		for (int i = 0; i < 16; i++) {
			int temp = output.shiftRight(i*8).and(mask).intValue();
			System.out.print(Integer.toString(temp, 16) + " ");
		}
		System.out.println("garbled result");

	}


	protected void execTransfer() throws Exception {
		int bytelength = (Wire.labelBitLength-1)/8 + 1;
//		System.out.println("byte_length:"+bytelength);//10

		//14080*8
		for (int i = 0; i < slps.length; i++) {//slps.length 1408
			int idx = AESEncryptCommon.testBit(AESEncryptCommon.w, i);
//			System.out.println("slps.length:"+slps.length);

			Utils.writeBigInteger(slps[i][idx], bytelength, AESEncryptCommon.oos0);//10bytes
			Utils.writeBigInteger(slps[i][idx], bytelength, AESEncryptCommon.oos1);
			Utils.writeBigInteger(slps[i][idx], bytelength, AESEncryptCommon.oos2);
			Utils.writeBigInteger(slps[i][idx], bytelength, AESEncryptCommon.oos3);
			Utils.writeBigInteger(slps[i][idx], bytelength, AESEncryptCommon.oos4);
			Utils.writeBigInteger(slps[i][idx], bytelength, AESEncryptCommon.oos5);
//			Utils.writeBigInteger(slps[i][idx], bytelength, AESEncryptCommon.oos6);
			Utils.writeBigInteger(slps[i][idx], bytelength, AESEncryptCommon.oosown);

		}
		System.err.println();
		AESEncryptCommon.oos0.flush();
		AESEncryptCommon.oos1.flush();
		AESEncryptCommon.oos2.flush();
		AESEncryptCommon.oos3.flush();
		AESEncryptCommon.oos4.flush();
		AESEncryptCommon.oos5.flush();
//		AESEncryptCommon.oos6.flush();
		AESEncryptCommon.oosown.flush();
		StopWatch.taskTimeStamp("sending labels for selfs inputs");

		snder0.execProtocol(clps);
		StopWatch.taskTimeStamp("sending labels for peer0s inputs");

		snder1.execProtocol(clps1);
		StopWatch.taskTimeStamp("sending labels for peer1s inputs");

		snder2.execProtocol(clps2);
		StopWatch.taskTimeStamp("sending labels for peer2s inputs");

		snder3.execProtocol(clps3);
		StopWatch.taskTimeStamp("sending labels for peer3s inputs");

		snder4.execProtocol(clps4);
		StopWatch.taskTimeStamp("sending labels for peer4s inputs");

		snder5.execProtocol(clps5);
		StopWatch.taskTimeStamp("sending labels for peer5s inputs");

//		snder6.execProtocol(clps6);
//		StopWatch.taskTimeStamp("sending labels for peer6s inputs");

		snderown.execProtocol(clpsown);
		StopWatch.taskTimeStamp("sending labels for peerowns inputs");
	}

	protected void execCircuit() throws Exception {
		System.out.println("Test1");

		BigInteger[] slbs = new BigInteger[(AESEncryptCommon.Nr+1)*128];
		BigInteger[] clbs = new BigInteger[ AESEncryptCommon.Nb*32];
		BigInteger[] clbs1 = new BigInteger[ AESEncryptCommon.Nb*32];
		BigInteger[] clbs2 = new BigInteger[ AESEncryptCommon.Nb*32];
		BigInteger[] clbs3 = new BigInteger[ AESEncryptCommon.Nb*32];
		BigInteger[] clbs4 = new BigInteger[ AESEncryptCommon.Nb*32];
		BigInteger[] clbs5 = new BigInteger[ AESEncryptCommon.Nb*32];
//		BigInteger[] clbs6 = new BigInteger[ AESEncryptCommon.Nb*32];

		BigInteger[] clbsown = new BigInteger[ AESEncryptCommon.Nb*32];


		for (int i = 0; i < slbs.length; i++)
			slbs[i] = slps[i][0];

		for (int i = 0; i < clbs.length; i++) {
			clbs[i] = clps[i][0];
			clbs1[i] = clps1[i][0];
			clbs2[i] = clps2[i][0];
			clbs3[i] = clps3[i][0];
			clbs4[i] = clps4[i][0];
			clbs5[i] = clps5[i][0];
//			clbs6[i] = clps6[i][0];

			clbsown[i] = clpsown[i][0];

		}

		BigInteger[] mergeclbs = new BigInteger[clbs.length+ clbs1.length+ clbs2.length+ clbs3.length+ clbs4.length+ clbs5.length];
		System.arraycopy(clbs, 0, mergeclbs, 0, clbs.length);
		System.arraycopy(clbs1, 0, mergeclbs, clbs.length, clbs1.length);
		System.arraycopy(clbs2, 0, mergeclbs, clbs.length+clbs1.length, clbs2.length);
		System.arraycopy(clbs3, 0, mergeclbs, clbs.length+clbs1.length+clbs2.length, clbs3.length);
		System.arraycopy(clbs4, 0, mergeclbs, clbs.length+clbs1.length+clbs2.length+clbs3.length, clbs4.length);
		System.arraycopy(clbs5, 0, mergeclbs, clbs.length+clbs1.length+clbs2.length+clbs3.length+clbs4.length, clbs5.length);
//		System.arraycopy(clbs6, 0, mergeclbs, clbs.length+clbs1.length+clbs2.length+clbs3.length+clbs4.length+clbs5.length, clbs6.length);

		System.out.println("Test1-mid");
		outputState = AESEncryptCommon.Cipher(State.fromLabels(slbs), State.fromLabels(mergeclbs));
		System.out.println("Test1-end");

	}


	protected void commit() throws Exception {
		System.out.println("server-commi12t");
		commitment = (String) AESEncryptCommon.oisown.readObject();
//		byte[] ar = commitment.getBytes();
		//64
//		System.out.println("com_length:"+ar.length);
		StopWatch.taskTimeStamp("server-commit");
	}

	protected void openCircuit() throws Exception {
		snder0.openOT();
		snder1.openOT();
		snder2.openOT();
		snder3.openOT();
		snder4.openOT();
		snder5.openOT();
//		snder6.openOT();
		snderown.openOT();

	}

	protected void interpretResult() throws Exception {
		int bytelength = (Wire.labelBitLength-1)/8 + 1;


		// AESEncryptCommon.oosown.flush();
		BigInteger[] slbs = new BigInteger[(AESEncryptCommon.Nr+1)*128];
		BigInteger[] clbs = new BigInteger[ AESEncryptCommon.Nb*32];
		BigInteger[] clbs1 = new BigInteger[ AESEncryptCommon.Nb*32];
		BigInteger[] clbs2 = new BigInteger[ AESEncryptCommon.Nb*32];
		BigInteger[] clbs3 = new BigInteger[ AESEncryptCommon.Nb*32];
		BigInteger[] clbs4 = new BigInteger[ AESEncryptCommon.Nb*32];
		BigInteger[] clbs5 = new BigInteger[ AESEncryptCommon.Nb*32];
//		BigInteger[] clbs6 = new BigInteger[ AESEncryptCommon.Nb*32];
		BigInteger[] clbsown = new BigInteger[ AESEncryptCommon.Nb*32];


		for (int i = 0; i < slbs.length; i++)
			slbs[i] = slps[i][0];

		for (int i = 0; i < clbs.length; i++) {
			clbs[i] = clps[i][0];
//			byte[] re = clbs[i].toByteArray();
//			System.out.println("clbs_length:"+re.length);
			clbs1[i] = clps1[i][0];
			clbs2[i] = clps2[i][0];
			clbs3[i] = clps3[i][0];
			clbs4[i] = clps4[i][0];
			clbs5[i] = clps5[i][0];
//			clbs6[i] = clps6[i][0];
			clbsown[i] = clpsown[i][0];

		}

		BigInteger[] mergeclbs = new BigInteger[clbs.length+ clbs1.length+ clbs2.length+ clbs3.length+ clbs4.length+ clbs5.length];
		System.arraycopy(clbs, 0, mergeclbs, 0, clbs.length);
		System.arraycopy(clbs1, 0, mergeclbs, clbs.length, clbs1.length);
		System.arraycopy(clbs2, 0, mergeclbs, clbs.length+clbs1.length, clbs2.length);
		System.arraycopy(clbs3, 0, mergeclbs, clbs.length+clbs1.length+clbs2.length, clbs3.length);
		System.arraycopy(clbs4, 0, mergeclbs, clbs.length+clbs1.length+clbs2.length+clbs3.length, clbs4.length);
		System.arraycopy(clbs5, 0, mergeclbs, clbs.length+clbs1.length+clbs2.length+clbs3.length+clbs4.length, clbs5.length);
//		System.arraycopy(clbs6, 0, mergeclbs, clbs.length+clbs1.length+clbs2.length+clbs3.length+clbs4.length+clbs5.length, clbs6.length);
		AESEncryptCommon.oosown.writeObject(slbs);
		//448*10+448*11

//		for(BigInteger slb:slbs){
//			byte[] byteArray = slb.toByteArray();
//			System.out.println("slbs_length"+byteArray.length);
//
//		}

		System.out.println("wri1");

		// AESEncryptCommon.oosown.flush();

		AESEncryptCommon.oosown.writeObject(mergeclbs);
		//448*10+448*11
//		for(BigInteger mergeclb:mergeclbs){
//			byte[] byteArray = mergeclb.toByteArray();
//			System.out.println("merge_length"+byteArray.length);
//
//		}
		AESEncryptCommon.oosown.flush();
		System.out.println("wri2");



		BigInteger[] outLabels = (BigInteger[]) AESEncryptCommon.oisown.readObject();
		//64*10+64*11
//		for(BigInteger outlabel:outLabels){
//			byte[] byteArray = outlabel.toByteArray();
//			System.out.println("outlabel_length"+byteArray.length);
//
//		}

		System.out.println("rea-own");

		BigInteger output = BigInteger.ZERO;
		for (int i = 0; i < outLabels.length; i++) {
			if (outputState.wires[i].value != Wire.UNKNOWN_SIG) {
				if (outputState.wires[i].value == 1)
					output = output.setBit(i);
				continue;
			}
			else if (outLabels[i].equals(outputState.wires[i].invd ?
					outputState.wires[i].lbl :
					outputState.wires[i].lbl.xor(Wire.R.shiftLeft(1).setBit(0)))) {
				output = output.setBit(i);
			}
			else if (!outLabels[i].equals(outputState.wires[i].invd ?
					outputState.wires[i].lbl.xor(Wire.R.shiftLeft(1).setBit(0)) :
					outputState.wires[i].lbl))
				throw new Exception("Bad label encountered: i = " + i + "\t" +
						outLabels[i] + " != (" +
						outputState.wires[i].lbl + ", " +
						outputState.wires[i].lbl.xor(Wire.R.shiftLeft(1).setBit(0)) + ")");
		}

		StopWatch.taskTimeStamp("output labels received and interpreted");

		System.out.println("accept");
		BigInteger mask = BigInteger.valueOf(255);
		for (int i = 0; i < 16; i++) {
			int temp = output.shiftRight(i*8).and(mask).intValue();
			System.out.print(Integer.toString(temp, 16) + " ");
		}
		System.out.println();
		for (int i = 0; i < slps.length; i++) {//14080
			int idx = AESEncryptCommon.testBit(AESEncryptCommon.w, i);
			Utils.writeBigInteger(slps[i][idx], bytelength, AESEncryptCommon.oosown);
		}
		AESEncryptCommon.oosown.flush();
		System.err.println();
		State outputState1 = AESEncryptCommon.Cipher(State.fromLabels(slbs), State.fromLabels(mergeclbs));
		System.out.println("cipher");

	}

	protected void verify_result() throws Exception {

	}
}

