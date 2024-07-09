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

public abstract class ProgClient2 extends Program {

    public static String serverIPname = "localhost";             // server IP name
    //    private final int    serverPort   = 23456;                   // server port number
    private final int    ClientPort2   = 23452;
//    private final int    ClientPort1_2   = 23457;
    private final int    ClientPort2_own   = 23462;
//    private final int    ClientPort0_2   = 23455;



    private ServerSocket       sock2_own         = null;              // original server socket

    private Socket      sock2,clientSocket0_2,clientSocket1_2,clientSocket2_own         = null;                    // Socket object for communicating

    protected int otNumOfPairs;

    //	protected Sender snder;
    protected Sender snder2;

    protected Sender snder2_own;

    //	protected Receiver rcver;
    protected Receiver rcver2;
    protected Receiver rcver2_own;


    public void run() throws Exception {
        create_socket_and_connect();
        StopWatch.pointTimeStamp("running program");


        super.run();

        cleanup();
    }

    protected void init() throws Exception {
        System.out.println("CLient1-itercount:"+Program.iterCount);
        super.init();
    }

    private void create_socket_and_connect() throws Exception {
        sock2 = new java.net.Socket(serverIPname, ClientPort2);          // create socket and connect
//        clientSocket0_2 = new java.net.Socket(serverIPname, ClientPort0_2);          // create socket and connect
//        clientSocket1_2 = new java.net.Socket(serverIPname, ClientPort1_2);          // create socket and connect

        try {

            sock2_own = new ServerSocket(ClientPort2_own);
            System.out.println("waiting for client2_own to connect");
            sleep(20);

            clientSocket2_own = sock2_own.accept();
            System.out.println("client2_own has connected");

        } catch (IOException e) {
            // cleanup();
            e.printStackTrace();
        }
//		ProgCommon.oos = new java.io.ObjectOutputStream(sock.getOutputStream());
//		ProgCommon.ois = new java.io.ObjectInputStream(sock.getInputStream());
        System.out.println("socket-mid");



        sleep(500);
        CountingOutputStream cos2_own = new CountingOutputStream(clientSocket2_own.getOutputStream());
        CountingInputStream cis2_own = new CountingInputStream(clientSocket2_own.getInputStream());
        System.out.println("socket-mid1");
//        ProgCommon.oos0 = new java.io.ObjectOutputStream(clientSocket0_2.getOutputStream());
//        ProgCommon.ois0 = new java.io.ObjectInputStream(clientSocket0_2.getInputStream());
//        System.out.println("socket-mid2");
//        ProgCommon.oos1 = new java.io.ObjectOutputStream(clientSocket1_2.getOutputStream());
//        ProgCommon.ois1 = new java.io.ObjectInputStream(clientSocket1_2.getInputStream());
//        System.out.println("socket-mid3");
        ProgCommon.oos2 = new java.io.ObjectOutputStream(sock2.getOutputStream());
        ProgCommon.ois2 = new java.io.ObjectInputStream(sock2.getInputStream());
        System.out.println("socket-mid4");
        ProgCommon.oosown = new ObjectOutputStream(cos2_own);
        ProgCommon.oisown = new ObjectInputStream(cis2_own);
        System.out.println("socketend");

    }

    private void cleanup() throws Exception {
//        ProgCommon.oos0.close();                                                   // close everything
//        ProgCommon.ois0.close();
//        clientSocket0_2.close();
//
//        ProgCommon.oos1.close();                                                   // close everything
//        ProgCommon.ois1.close();
//        clientSocket1_2.close();

        ProgCommon.oos2.close();                                                   // close everything
        ProgCommon.ois2.close();
        sock2.close();

        ProgCommon.oosown.close();                                                   // close everything
        ProgCommon.oisown.close();
        clientSocket2_own.close();
        sock2_own.close();
    }

    protected void createCircuits() throws Exception {
        Circuit.isForGarbling = false;
        Circuit.setIOStream(ProgCommon.ois2, ProgCommon.oos2);
        //every client has different label
        // for (int i = 0; i < ProgCommon.ccs.length; i++) {
        //     ProgCommon.ccs[i].build();
        // }
        // for (int i = 0; i < ProgCommon.ccsin.length; i++) {
        //     ProgCommon.ccsin[i].build();
        // }
        StopWatch.taskTimeStamp("circuit preparation");
    }

    protected void initializeOT() throws Exception {
        rcver2 = new NPOTReceiver(otNumOfPairs, ProgCommon.ois2, ProgCommon.oos2);
        StopWatch.taskTimeStamp("OT preparation");
    }
}