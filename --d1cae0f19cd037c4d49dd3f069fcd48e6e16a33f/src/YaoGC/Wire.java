// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

import java.math.*;
import java.util.*;
import java.security.SecureRandom;

public class Wire extends TransitiveObservable {
    public static final int UNKNOWN_SIG = -1;

    // These four fields are for garbling
    public static int K = 0;
    private static SecureRandom rnd = new SecureRandom();//生成label的seed，verify发送给prover
    public static final int labelBitLength = 80;

    public static final BigInteger R = new BigInteger(labelBitLength-1, rnd);

    public final int serialNum;//电线序列号
    public int value = UNKNOWN_SIG;//值
    public BigInteger lbl;//标签
    public boolean invd = false;

    public Wire() {
	serialNum = K++;
	lbl = new BigInteger(labelBitLength, rnd);
    }

	//生成一对label值
    public static BigInteger[] newLabelPair() {
	BigInteger[] res = new BigInteger[2];
	//生成随机数
	res[0] = new BigInteger(labelBitLength, rnd);
	res[1] = conjugate(res[0]);
	return res;
    }
    
    public static BigInteger conjugate(BigInteger label) {
	if (label == null)
	    return null;
	//将R左移1位，设置最低位为1，再将得到的数字与label异或
	return label.xor(R.shiftLeft(1).setBit(0));
    }

    public void setLabel(BigInteger label) {
	lbl = label;
    }

    public void setReady() {
	setChanged();
 	notifyObservers();
    }

    public void connectTo(Wire[] ws, int idx) {
	Wire w = ws[idx];

	for (int i = 0; i < w.observers.size(); i++) {
	    TransitiveObserver ob = w.observers.get(i);
	    TransitiveObservable.Socket s = w.exports.get(i);
	    this.addObserver(ob, s);
	    s.updateSocket(this);
	}

	w.deleteObservers();
	ws[idx] = this;
    }

    public void fixWire(int v) {
	this.value = v;
	
	for (int i = 0; i < this.observers.size(); i++) {
	    Circuit c = (Circuit) this.observers.get(i);
	    c.inDegree--;
	    if (c.inDegree == 0) {
		c.compute();
		for (int j = 0; j < c.outDegree; j++)
		    c.outputWires[j].fixWire(c.outputWires[j].value);
	    }
	}
    }

    protected static int getLSB(BigInteger lp) {
	return lp.testBit(0) ? 1 : 0;
    }
}
