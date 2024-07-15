package LookupTable;

import java.io.*;
import java.math.*;

import Cipher.Cipher;
import YaoGC.*;
import Utils.*;

public class LookupTableSender extends LookupTableAgent {
	public static ObjectOutputStream oos0,oos1,oos2,oos3,oos4,oos5,oos6,oosown;

	private int[][] table;

	private BigInteger[][] EGTable;

	private BigInteger[] outputLabelPairs;

	public LookupTableSender(int[][] tab) {
		table = tab;

		init();
	}

	public BigInteger[] execute(BigInteger[] riBitPairs, BigInteger[] ciBitPairs) {
		if (riBitPairs.length != nRIBits || ciBitPairs.length != nCIBits) {
			(new Exception("index bit length unmatch: " +
					riBitPairs.length + " != " + nRIBits + "\t" +
					ciBitPairs.length + " != " + nCIBits)).printStackTrace();
			System.exit(1);
		}

		generateOutputLabelPairs();

		if (!extCase) {
			garbleEGTable();
			encryptEGTable(riBitPairs, ciBitPairs);
			permuteEGTable(riBitPairs, ciBitPairs);
			sendEGTable();
		}
		else {
			garbleEGTable_EXT();
			encryptEGTable_EXT(riBitPairs, ciBitPairs);
			permuteEGTable(riBitPairs, ciBitPairs);
			sendEGTable_EXT();
		}

		return outputLabelPairs;
	}

	private void init() {
		nRows = table.length;
		nRIBits = bitLength(nRows-1);
		nCols = table[0].length;
		nCIBits = bitLength(nCols-1);

		int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
		for (int i = 0; i < nRows; i++)
			for (int j = 0; j < nCols; j++) {
				if (table[i][j] < min)
					min = table[i][j];
				if (table[i][j] > max)
					max = table[i][j];
			}
		nBits = bitLength(max-min+1);

		if (nRows != (1<<nRIBits) || nCols != (1<<nCIBits)) {
			nRows = 1<<nRIBits;
			nCols = 1<<nCIBits;
			extCase = true;
		}

		EGTable = new BigInteger[nRows][nCols];
		outputLabelPairs = new BigInteger[nBits];

		try {
			sendParams();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void sendParams() throws Exception {
		oos0.writeInt(nRows);
		oos0.writeInt(nRIBits);
		oos0.writeInt(nCols);
		oos0.writeInt(nCIBits);
		oos0.writeInt(nBits);
		oos0.writeBoolean(extCase);
		oos0.flush();
		oos1.writeInt(nRows);
		oos1.writeInt(nRIBits);
		oos1.writeInt(nCols);
		oos1.writeInt(nCIBits);
		oos1.writeInt(nBits);
		oos1.writeBoolean(extCase);
		oos1.flush();
		oos2.writeInt(nRows);
		oos2.writeInt(nRIBits);
		oos2.writeInt(nCols);
		oos2.writeInt(nCIBits);
		oos2.writeInt(nBits);
		oos2.writeBoolean(extCase);
		oos2.flush();
		oos3.writeInt(nRows);
		oos3.writeInt(nRIBits);
		oos3.writeInt(nCols);
		oos3.writeInt(nCIBits);
		oos3.writeInt(nBits);
		oos3.writeBoolean(extCase);
		oos3.flush();
		oos4.writeInt(nRows);
		oos4.writeInt(nRIBits);
		oos4.writeInt(nCols);
		oos4.writeInt(nCIBits);
		oos4.writeInt(nBits);
		oos4.writeBoolean(extCase);
		oos4.flush();
		oos5.writeInt(nRows);
		oos5.writeInt(nRIBits);
		oos5.writeInt(nCols);
		oos5.writeInt(nCIBits);
		oos5.writeInt(nBits);
		oos5.writeBoolean(extCase);
		oos5.flush();
//		oos6.writeInt(nRows);
//		oos6.writeInt(nRIBits);
//		oos6.writeInt(nCols);
//		oos6.writeInt(nCIBits);
//		oos6.writeInt(nBits);
//		oos6.writeBoolean(extCase);
//		oos6.flush();

		oosown.writeInt(nRows);
		oosown.writeInt(nRIBits);
		oosown.writeInt(nCols);
		oosown.writeInt(nCIBits);
		oosown.writeInt(nBits);
		oosown.writeBoolean(extCase);
		oosown.flush();
	}

	private void generateOutputLabelPairs() {
		for (int i = 0; i < nBits; i++) {
			BigInteger[] lp = Wire.newLabelPair();
			outputLabelPairs[i] = lp[0];
		}
	}

	private void garbleEGTable() {
		for (int i = 0; i < nRows; i++)
			for (int j = 0; j < nCols; j++) {
				EGTable[i][j] = garble(table[i][j], nBits, outputLabelPairs);
			}
	}

	private void garbleEGTable_EXT() {
		for (int i = 0; i < nRows; i++)
			for (int j = 0; j < nCols; j++) {
				if (i < table.length && j < table[0].length)
					EGTable[i][j] = garble(table[i][j], nBits, outputLabelPairs);
				else
					EGTable[i][j] = null;
			}
	}

	private void encryptEGTable(BigInteger[] ribp, BigInteger[] cibp) {
		for (int i = 0; i < nRows; i++) {
			BigInteger rowKey = garble(i, nRIBits, ribp);
			for (int j = 0; j < nCols; j++) {
				BigInteger colKey = garble(j, nCIBits, cibp);
				BigInteger key = rowKey.shiftLeft(nCIBits*Wire.labelBitLength).xor(colKey);
				BigInteger msg = garble(table[i][j], nBits, outputLabelPairs);

				EGTable[i][j] = Cipher.encrypt(key, msg, nBits*Wire.labelBitLength);
			}
		}
	}

	private void encryptEGTable_EXT(BigInteger[] ribp, BigInteger[] cibp) {
		for (int i = 0; i < nRows; i++) {
			if (i < table.length) {
				BigInteger rowKey = garble(i, nRIBits, ribp);
				for (int j = 0; j < nCols; j++) {
					if (j < table[0].length) {
						BigInteger colKey = garble(j, nCIBits, cibp);
						BigInteger key = rowKey.shiftLeft(nCIBits*Wire.labelBitLength).xor(colKey);
						BigInteger msg = garble(table[i][j], nBits, outputLabelPairs);

						EGTable[i][j] = Cipher.encrypt(key, msg, nBits*Wire.labelBitLength);
					}
				}
			}
		}
	}

	/*
	 * assume nRow and nCol are powers of 2.
	 */
	private void permuteEGTable(BigInteger[] ribp, BigInteger[] cibp) {
		for (int i = 0; i < nRIBits; i++) {
			if (ribp[i].testBit(0)) {
				swapMultRows(i);
			}
		}

		for (int i = 0; i < nCIBits; i++) {
			if (cibp[i].testBit(0)) {
				swapMultCols(i);
			}
		}
	}

	private void sendEGTable() {
		int nBytes = (nBits-1)/8 + 1;

		try {
			for (int i = 0; i < nRows; i++) {

				for (int j = 0; j < nCols; j++){
					Utils.writeBigInteger(EGTable[i][j], nBytes*Wire.labelBitLength, oos0);
					oos0.flush();
					Utils.writeBigInteger(EGTable[i][j], nBytes*Wire.labelBitLength, oos1);
					oos1.flush();
					Utils.writeBigInteger(EGTable[i][j], nBytes*Wire.labelBitLength, oos2);
					oos2.flush();
					Utils.writeBigInteger(EGTable[i][j], nBytes*Wire.labelBitLength, oos3);
					oos3.flush();
					Utils.writeBigInteger(EGTable[i][j], nBytes*Wire.labelBitLength, oos4);
					oos4.flush();
					Utils.writeBigInteger(EGTable[i][j], nBytes*Wire.labelBitLength, oos5);
					oos5.flush();
//					Utils.writeBigInteger(EGTable[i][j], nBytes*Wire.labelBitLength, oos6);
//					oos6.flush();


					Utils.writeBigInteger(EGTable[i][j], nBytes*Wire.labelBitLength, oosown);
					oosown.flush();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void sendEGTable_EXT() {
		int nBytes = (nBits-1)/8 + 1;

		try {
			for (int i = 0; i < nRows; i++)
				for (int j = 0; j < nCols; j++)
					if (EGTable[i][j] != null) {
						oos0.writeBoolean(true);
						Utils.writeBigInteger(EGTable[i][j], nBytes*Wire.labelBitLength, oos0);
						oos1.writeBoolean(true);
						Utils.writeBigInteger(EGTable[i][j], nBytes*Wire.labelBitLength, oos1);
						oos2.writeBoolean(true);
						Utils.writeBigInteger(EGTable[i][j], nBytes*Wire.labelBitLength, oos2);
						oos3.writeBoolean(true);
						Utils.writeBigInteger(EGTable[i][j], nBytes*Wire.labelBitLength, oos3);
						oos4.writeBoolean(true);
						Utils.writeBigInteger(EGTable[i][j], nBytes*Wire.labelBitLength, oos4);
						oos5.writeBoolean(true);
						Utils.writeBigInteger(EGTable[i][j], nBytes*Wire.labelBitLength, oos5);
//						oos6.writeBoolean(true);
//						Utils.writeBigInteger(EGTable[i][j], nBytes*Wire.labelBitLength, oos6);


						oosown.writeBoolean(true);
						Utils.writeBigInteger(EGTable[i][j], nBytes*Wire.labelBitLength, oosown);
					}
					else {
						oos0.writeBoolean(false);
						oos0.flush();
						oos1.writeBoolean(false);
						oos1.flush();
						oos2.writeBoolean(false);
						oos2.flush();
						oos3.writeBoolean(false);
						oos3.flush();
						oos4.writeBoolean(false);
						oos4.flush();
						oos5.writeBoolean(false);
						oos5.flush();
//						oos6.writeBoolean(false);
//						oos6.flush();

						oosown.writeBoolean(false);
						oosown.flush();
					}
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static BigInteger garble(int number, int bitlen, BigInteger[] lblpairs) {
		BigInteger res = BigInteger.ZERO;

		for (int k = bitlen-1; k >= 0; k--) {
			res = res.shiftLeft(Wire.labelBitLength);
			if ((number & (1 << k)) == 0)
				res = res.xor(lblpairs[k]);
			else
				res = res.xor(Wire.conjugate(lblpairs[k]));
		}

		return res;
	}

	private static int insertBit(int n, int bitPos, int bitVal) { // point and permute
		int mask = (1<<bitPos)-1;
		int lowBits  = n & mask;
		mask = ~mask;
		int highBits = n & mask;

		return (highBits<<1) | (bitVal<<bitPos) | lowBits;
	}

	/*
	 * assume nRows is a power of 2.
	 */
	private void swapMultRows(int n) {
		int bound = nRows >> 1;

		for (int i = 0; i < bound; i++) {
			int x = insertBit(i, n, 0);
			int y = insertBit(i, n, 1);
			swapRows(x, y);
		}
	}

	/*
	 * assume nCols is a power of 2.
	 */
	private void swapMultCols(int n) {
		int bound = nCols >> 1;

		for (int i = 0; i < bound; i++) {
			int x = insertBit(i, n, 0);
			int y = insertBit(i, n, 1);
			swapCols(x, y);
		}
	}

	private void swapRows(int x, int y) {
		BigInteger[] temp = EGTable[x];
		EGTable[x] = EGTable[y];
		EGTable[y] = temp;
	}

	private void swapCols(int x, int y) {
		for (int i = 0; i < nRows; i++) {
			BigInteger temp = EGTable[i][x];
			EGTable[i][x] = EGTable[i][y];
			EGTable[i][y] = temp;
		}
	}

	private static int bitLength(int x) {
		return BigInteger.valueOf(x).bitLength();
	}

}