package Program;

import java.net.*;
import java.io.*;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.io.input.CountingInputStream;

import Utils.*;
import OT.*;
import YaoGC.*;

public abstract class ProgServer extends Program {

//	final private  int   serverPort   = 23454;             // server port number
	private final int    ClientPort0   = 23450;
	private final int    ClientPort1   = 23451;
	private final int    ClientPort2   = 23452;
	private final int    ClientPort3   = 23453;
	private final int    ClientPort4   = 23454;
	private final int    ClientPort5   = 23455;
	private final int    ClientPort6   = 23456;

	private final int    OwnerPort   = 23457;

	private ServerSocket       socksvr,sock0,sock1,sock2,sock3,sock4,sock5,sock6,sockown         = null;              // original server socket
	private Socket             clientSocket0,clientSocket1,clientSocket2,clientSocket3,clientSocket4,clientSocket5,clientSocket6,ownerSocket = null;              // socket created by accept

	//	protected Sender snder;
	protected Sender snder0;
	protected Sender snder1;
	protected Sender snder2;
	protected Sender snder3;
	protected Sender snder4;
	protected Sender snder5;
//	protected Sender snder6;
	protected Sender snderown;

	protected int otNumOfPairs;
	protected int otMsgBitLength = Wire.labelBitLength;

	public void run() throws Exception {
		create_socket_and_listen();
		StopWatch.pointTimeStamp("running program");

		super.run();

		cleanup();
	}

	protected void init() throws Exception {
		Program.iterCount = ProgCommon.ois0.readInt();
		System.out.println(Program.iterCount);

		super.init();
	}

	private void create_socket_and_listen() throws Exception {
		sock0 = new ServerSocket(ClientPort0);            // create socket and bind to port
		System.out.println("waiting for client0 to connect");
		sock1 = new ServerSocket(ClientPort1);            // create socket and bind to port
		System.out.println("waiting for client1 to connect");
		sock2 = new ServerSocket(ClientPort2);            // create socket and bind to port
		System.out.println("waiting for client2 to connect");
		sock3 = new ServerSocket(ClientPort3);            // create socket and bind to port
		System.out.println("waiting for client3 to connect");
		sock4 = new ServerSocket(ClientPort4);            // create socket and bind to port
		System.out.println("waiting for client4 to connect");
		sock5 = new ServerSocket(ClientPort5);            // create socket and bind to port
		System.out.println("waiting for client5 to connect");
//		sock6 = new ServerSocket(ClientPort6);            // create socket and bind to port
//		System.out.println("waiting for client6 to connect");

		sockown = new ServerSocket(OwnerPort);            // create socket and bind to port
		System.out.println("waiting for owner to connect");
		try{
			clientSocket0 = sock0.accept();
			System.out.println("client0 has connected");
			clientSocket1 = sock1.accept();                   // wait for client to connect
			System.out.println("client1 has connected");
			clientSocket2 = sock2.accept();
			System.out.println("client2 has connected");
			clientSocket3 = sock3.accept();                   // wait for client to connect
			System.out.println("client3 has connected");
			clientSocket4 = sock4.accept();
			System.out.println("client4 has connected");
			clientSocket5 = sock5.accept();                   // wait for client to connect
			System.out.println("client5 has connected");
//			clientSocket6 = sock6.accept();
//			System.out.println("client6 has connected");
			ownerSocket = sockown.accept();                   // wait for client to connect
			System.out.println("owner has connected");

			CountingOutputStream cos = new CountingOutputStream(clientSocket0.getOutputStream());
			CountingInputStream cis = new CountingInputStream(clientSocket0.getInputStream());
			CountingOutputStream cos1 = new CountingOutputStream(clientSocket1.getOutputStream());
			CountingInputStream cis1 = new CountingInputStream(clientSocket1.getInputStream());
			CountingOutputStream cos2 = new CountingOutputStream(clientSocket2.getOutputStream());
			CountingInputStream cis2 = new CountingInputStream(clientSocket2.getInputStream());
			CountingOutputStream cos3 = new CountingOutputStream(clientSocket3.getOutputStream());
			CountingInputStream cis3 = new CountingInputStream(clientSocket3.getInputStream());
			CountingOutputStream cos4 = new CountingOutputStream(clientSocket4.getOutputStream());
			CountingInputStream cis4 = new CountingInputStream(clientSocket4.getInputStream());
			CountingOutputStream cos5 = new CountingOutputStream(clientSocket5.getOutputStream());
			CountingInputStream cis5 = new CountingInputStream(clientSocket5.getInputStream());
//			CountingOutputStream cos6 = new CountingOutputStream(clientSocket6.getOutputStream());
//			CountingInputStream cis6 = new CountingInputStream(clientSocket6.getInputStream());
			CountingOutputStream cosown = new CountingOutputStream(ownerSocket.getOutputStream());
			CountingInputStream cisown = new CountingInputStream(ownerSocket.getInputStream());

			ProgCommon.oos0 = new ObjectOutputStream(cos);
			ProgCommon.ois0 = new ObjectInputStream(cis);
			ProgCommon.oos1 = new ObjectOutputStream(cos1);
			ProgCommon.ois1 = new ObjectInputStream(cis1);
			ProgCommon.oos2 = new ObjectOutputStream(cos2);
			ProgCommon.ois2 = new ObjectInputStream(cis2);
			ProgCommon.oos3 = new ObjectOutputStream(cos3);
			ProgCommon.ois3 = new ObjectInputStream(cis3);
			ProgCommon.oos4 = new ObjectOutputStream(cos4);
			ProgCommon.ois4 = new ObjectInputStream(cis4);
			ProgCommon.oos5 = new ObjectOutputStream(cos5);
			ProgCommon.ois5 = new ObjectInputStream(cis5);
//			ProgCommon.oos6 = new ObjectOutputStream(cos6);
//			ProgCommon.ois6 = new ObjectInputStream(cis6);


			ProgCommon.oosown = new ObjectOutputStream(cosown);
			ProgCommon.oisown = new ObjectInputStream(cisown);

			StopWatch.cos = cos;
			StopWatch.cis = cis;
			System.out.println("own_s_byte:"+cosown.getByteCount());
		} catch (IOException e) {
			e.printStackTrace();
		}



	}

	private void cleanup() throws Exception {
		ProgCommon.oos0.close();                          // close everything
		ProgCommon.ois0.close();

		ProgCommon.oos1.close();                          // close everything
		ProgCommon.ois1.close();

		ProgCommon.oos2.close();                          // close everything
		ProgCommon.ois2.close();

		ProgCommon.oos3.close();                          // close everything
		ProgCommon.ois3.close();

		ProgCommon.oos4.close();                          // close everything
		ProgCommon.ois4.close();

		ProgCommon.oos5.close();                          // close everything
		ProgCommon.ois5.close();

//		ProgCommon.oos6.close();                          // close everything
//		ProgCommon.ois6.close();


		ProgCommon.oosown.close();                          // close everything
		ProgCommon.oisown.close();

		clientSocket0.close();
		sock0.close();

		clientSocket1.close();
		sock1.close();

		clientSocket2.close();
		sock2.close();

		clientSocket3.close();
		sock3.close();

		clientSocket4.close();
		sock4.close();

		clientSocket5.close();
		sock5.close();

//		clientSocket6.close();
//		sock6.close();

		ownerSocket.close();
		sockown.close();
	}

	protected void initializeOT() throws Exception {
		otNumOfPairs = ProgCommon.ois0.readInt();
		snder0 = new NPOTSender(otNumOfPairs, otMsgBitLength, ProgCommon.ois0, ProgCommon.oos0);
		snder1 = new NPOTSender(otNumOfPairs, otMsgBitLength, ProgCommon.ois1, ProgCommon.oos1);
		snder2 = new NPOTSender(otNumOfPairs, otMsgBitLength, ProgCommon.ois2, ProgCommon.oos2);
		snder3 = new NPOTSender(otNumOfPairs, otMsgBitLength, ProgCommon.ois3, ProgCommon.oos3);
		snder4 = new NPOTSender(otNumOfPairs, otMsgBitLength, ProgCommon.ois4, ProgCommon.oos4);
		snder5 = new NPOTSender(otNumOfPairs, otMsgBitLength, ProgCommon.ois5, ProgCommon.oos5);
//		snder6 = new NPOTSender(otNumOfPairs, otMsgBitLength, ProgCommon.ois6, ProgCommon.oos6);
		snderown = new NPOTSender(otNumOfPairs, otMsgBitLength, ProgCommon.oisown, ProgCommon.oosown);
		StopWatch.taskTimeStamp("OT preparation");
	}

	protected void createCircuits() throws Exception {
		Circuit.isForGarbling = true;
		Circuit.setIOStream(ProgCommon.oisown, ProgCommon.oosown);
		for (int i = 0; i < ProgCommon.ccs.length; i++) {
			ProgCommon.ccs[i].build();
		}
		for (int i = 0; i < ProgCommon.ccsin.length; i++) {
			ProgCommon.ccsin[i].build();
		}
		StopWatch.taskTimeStamp("circuit preparation");
	}


}