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
		AESEncryptCommon.oos0.writeInt(AESEncryptCommon.Nk);
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
		AESEncryptCommon.oos6.writeInt(AESEncryptCommon.Nk);
		AESEncryptCommon.oos6.flush();
		AESEncryptCommon.oosown.writeInt(AESEncryptCommon.Nk);
		AESEncryptCommon.oosown.flush();
		System.out.println("send nk-end");

		AESEncryptCommon.initCircuits();

		LookupTableSender.oos0 = AESEncryptCommon.oos0;//应该把stable统一发给两个client
		LookupTableSender.oos1 = AESEncryptCommon.oos1;//应该把stable统一发给两个client

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
		clpsown = new BigInteger[ AESEncryptCommon.Nb*32][2];


		for (int i = 0; i < slps.length; i++) {
			slps[i] = Wire.newLabelPair();
		}

		for (int i = 0; i < clps.length; i++) {
			clps[i] = Wire.newLabelPair();
			clps1[i] = Wire.newLabelPair();
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

		for (int i = 0; i < slps.length; i++) {
			int idx = AESEncryptCommon.testBit(AESEncryptCommon.w, i);
			Utils.writeBigInteger(slps[i][idx], bytelength, AESEncryptCommon.oos0);
			Utils.writeBigInteger(slps[i][idx], bytelength, AESEncryptCommon.oos1);
			Utils.writeBigInteger(slps[i][idx], bytelength, AESEncryptCommon.oosown);

		}
		System.err.println();
		AESEncryptCommon.oos0.flush();
		AESEncryptCommon.oos1.flush();
		AESEncryptCommon.oosown.flush();
		StopWatch.taskTimeStamp("sending labels for selfs inputs");

		snder0.execProtocol(clps);
		StopWatch.taskTimeStamp("sending labels for peer0s inputs");

		snder1.execProtocol(clps1);
		StopWatch.taskTimeStamp("sending labels for peer1s inputs");

		snderown.execProtocol(clpsown);
		StopWatch.taskTimeStamp("sending labels for peerowns inputs");
	}

	protected void execCircuit() throws Exception {
		System.out.println("Test1");

		BigInteger[] slbs = new BigInteger[(AESEncryptCommon.Nr+1)*128];
		BigInteger[] clbs = new BigInteger[ AESEncryptCommon.Nb*32];
		BigInteger[] clbs1 = new BigInteger[ AESEncryptCommon.Nb*32];
		BigInteger[] clbsown = new BigInteger[ AESEncryptCommon.Nb*32];


		for (int i = 0; i < slbs.length; i++)
			slbs[i] = slps[i][0];

		for (int i = 0; i < clbs.length; i++) {
			clbs[i] = clps[i][0];
			clbs1[i] = clps1[i][0];
			clbsown[i] = clpsown[i][0];

		}

		BigInteger[] mergeclbs = new BigInteger[clbs.length+ clbs1.length];
		System.arraycopy(clbs, 0, mergeclbs, 0, clbs.length);
		System.arraycopy(clbs1, 0, mergeclbs, clbs.length, clbs1.length);
		System.out.println("Test1-mid");
		outputState = AESEncryptCommon.Cipher(State.fromLabels(slbs), State.fromLabels(mergeclbs));
		System.out.println("Test1-end");

	}


	protected void commit() throws Exception {
		System.out.println("server-commi12t");
		commitment = (String) AESEncryptCommon.oisown.readObject();
		StopWatch.taskTimeStamp("server-commit");
	}

	protected void openCircuit() throws Exception {
		snder0.openOT();
		snder1.openOT();
		snderown.openOT();

	}

	protected void interpretResult() throws Exception {
		int bytelength = (Wire.labelBitLength-1)/8 + 1;


		// AESEncryptCommon.oosown.flush();
		BigInteger[] slbs = new BigInteger[(AESEncryptCommon.Nr+1)*128];
		BigInteger[] clbs = new BigInteger[ AESEncryptCommon.Nb*32];
		BigInteger[] clbs1 = new BigInteger[ AESEncryptCommon.Nb*32];
		BigInteger[] clbsown = new BigInteger[ AESEncryptCommon.Nb*32];


		for (int i = 0; i < slbs.length; i++)
			slbs[i] = slps[i][0];

		for (int i = 0; i < clbs.length; i++) {
			clbs[i] = clps[i][0];
			clbs1[i] = clps1[i][0];
			clbsown[i] = clpsown[i][0];

		}

		BigInteger[] mergeclbs = new BigInteger[clbs.length+ clbs1.length];
		System.arraycopy(clbs, 0, mergeclbs, 0, clbs.length);
		System.arraycopy(clbs1, 0, mergeclbs, clbs.length, clbs1.length);
		AESEncryptCommon.oosown.writeObject(slbs);
		System.out.println("wri1");

		// AESEncryptCommon.oosown.flush();

		AESEncryptCommon.oosown.writeObject(mergeclbs);
		AESEncryptCommon.oosown.flush();
		System.out.println("wri2");



		BigInteger[] outLabels = (BigInteger[]) AESEncryptCommon.oisown.readObject();

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
		for (int i = 0; i < slps.length; i++) {
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

