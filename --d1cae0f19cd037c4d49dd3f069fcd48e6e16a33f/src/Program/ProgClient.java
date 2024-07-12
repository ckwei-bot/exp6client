package Program;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

import Utils.*;
import OT.*;
import YaoGC.*;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.CountingOutputStream;

import static java.lang.Thread.sleep;

public abstract class ProgClient extends Program {

	public static String serverIPname = "localhost";             // server IP name
	//    private final int    serverPort   = 23456;                   // server port number
	private final int    ClientPort0   = 23450;
//	private final int    ClientPort0_1   = 23454;
	private final int    ClientPort0_own   = 23460;


	private ServerSocket sock0_1,sock0_own;
	private Socket      sock0, clientSocket0_1,clientSocket0_own        = null;                    // Socket object for communicating

	protected int otNumOfPairs;

	protected Receiver rcver0;


	public void run() throws Exception {
		create_socket_and_connect();
		StopWatch.pointTimeStamp("running program");
		super.run();
		cleanup();
	}

	protected void init() throws Exception {
		System.out.println(Program.iterCount);
		ProgCommon.oos0.writeInt(Program.iterCount);
		ProgCommon.oos0.flush();

		super.init();
	}

	private void create_socket_and_connect() throws Exception {
//		sock = new java.net.Socket(serverIPname, serverPort);          // create socket and connect
		sock0 = new java.net.Socket(serverIPname, ClientPort0);          // create socket and connect
		try {
//			sock0_1 = new ServerSocket(ClientPort0_1);
//			System.out.println("waiting for client0_1 to connect");

			sock0_own = new ServerSocket(ClientPort0_own);
			System.out.println("waiting for client0_own to connect");
			sleep(20);
//			clientSocket0_1 = sock0_1.accept();
			System.out.println("client0_1 has connected");

			clientSocket0_own = sock0_own.accept();
			System.out.println("client0_own has connected");

		} catch (IOException e) {
			e.printStackTrace();
		}
//		ProgCommon.oos = new java.io.ObjectOutputStream(sock.getOutputStream());
//		ProgCommon.ois = new java.io.ObjectInputStream(sock.getInputStream());
//		System.out.println("socket-mid");
//		CountingOutputStream cos0_1 = new CountingOutputStream(clientSocket0_1.getOutputStream());
//		CountingInputStream cis0_1 = new CountingInputStream(clientSocket0_1.getInputStream());
		System.out.println("socket-mid1");

//		System.out.println("socket-mid4");
		ProgCommon.oos0 = new java.io.ObjectOutputStream(sock0.getOutputStream());
		ProgCommon.ois0 = new java.io.ObjectInputStream(sock0.getInputStream());
		System.out.println("socket-mid5");
		CountingOutputStream cos0_own = new CountingOutputStream(clientSocket0_own.getOutputStream());
		CountingInputStream cis0_own = new CountingInputStream(clientSocket0_own.getInputStream());
//		ProgCommon.oos1 = new ObjectOutputStream(cos0_1);
//		ProgCommon.ois1 = new ObjectInputStream(cis0_1);
		System.out.println("socket-mid6");
		ProgCommon.oosown = new ObjectOutputStream(cos0_own);
		ProgCommon.oisown = new ObjectInputStream(cis0_own);
		System.out.println("socket-end");
	}

	private void cleanup() throws Exception {
		ProgCommon.oos0.close();                                                   // close everything
		ProgCommon.ois0.close();
		sock0.close();

//		ProgCommon.oos1.close();                                                   // close everything
//		ProgCommon.ois1.close();
//		clientSocket0_1.close();
//		sock0_1.close();


		ProgCommon.oosown.close();                                                   // close everything
		ProgCommon.oisown.close();
		clientSocket0_own.close();
		sock0_own.close();
	}

	protected void createCircuits() throws Exception {
		Circuit.isForGarbling = false;
//		Circuit.setIOStream(ProgCommon.ois0, ProgCommon.oos0);
		// for (int i = 0; i < ProgCommon.ccs.length; i++) {
		// 	ProgCommon.ccs[i].build();
		// }
		// for (int i = 0; i < ProgCommon.ccsin.length; i++) {
		// 	ProgCommon.ccsin[i].build();
		// }

		StopWatch.taskTimeStamp("circuit preparation");
	}

	protected void initializeOT() throws Exception {
		ProgCommon.oos0.writeInt(otNumOfPairs);
		ProgCommon.oos0.flush();

		rcver0 = new NPOTReceiver(otNumOfPairs, ProgCommon.ois0, ProgCommon.oos0);
		StopWatch.taskTimeStamp("OT preparation");
	}
}