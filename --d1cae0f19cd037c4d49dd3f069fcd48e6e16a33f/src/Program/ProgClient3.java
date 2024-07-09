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

public abstract class ProgClient3 extends Program {

    public static String serverIPname = "localhost";             // server IP name
    //    private final int    serverPort   = 23456;                   // server port number
    private final int    ClientPort3   = 23453;
//    private final int    ClientPort1_3   = 23457;
    private final int    ClientPort3_own   = 23463;
//    private final int    ClientPort0_3   = 23455;



    private ServerSocket       sock3_own         = null;              // original server socket

    private Socket      sock3,clientSocket0_3,clientSocket1_3,clientSocket3_own         = null;                    // Socket object for communicating

    protected int otNumOfPairs;

    //	protected Sender snder;
    protected Sender snder3;

    protected Sender snder3_own;

    //	protected Receiver rcver;
    protected Receiver rcver3;
    protected Receiver rcver3_own;


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
        sock3 = new java.net.Socket(serverIPname, ClientPort3);          // create socket and connect
//        clientSocket0_3 = new java.net.Socket(serverIPname, ClientPort0_3);          // create socket and connect
//        clientSocket1_3 = new java.net.Socket(serverIPname, ClientPort1_3);          // create socket and connect

        try {

            sock3_own = new ServerSocket(ClientPort3_own);
            System.out.println("waiting for client3_own to connect");
            sleep(20);

            clientSocket3_own = sock3_own.accept();
            System.out.println("client3_own has connected");

        } catch (IOException e) {
            // cleanup();
            e.printStackTrace();
        }
//		ProgCommon.oos = new java.io.ObjectOutputStream(sock.getOutputStream());
//		ProgCommon.ois = new java.io.ObjectInputStream(sock.getInputStream());
        System.out.println("socket-mid");



        sleep(500);
        CountingOutputStream cos3_own = new CountingOutputStream(clientSocket3_own.getOutputStream());
        CountingInputStream cis3_own = new CountingInputStream(clientSocket3_own.getInputStream());
        System.out.println("socket-mid1");
//        ProgCommon.oos0 = new java.io.ObjectOutputStream(clientSocket0_3.getOutputStream());
//        ProgCommon.ois0 = new java.io.ObjectInputStream(clientSocket0_3.getInputStream());
//        System.out.println("socket-mid2");
//        ProgCommon.oos1 = new java.io.ObjectOutputStream(clientSocket1_3.getOutputStream());
//        ProgCommon.ois1 = new java.io.ObjectInputStream(clientSocket1_3.getInputStream());
//        System.out.println("socket-mid3");
        ProgCommon.oos3 = new java.io.ObjectOutputStream(sock3.getOutputStream());
        ProgCommon.ois3 = new java.io.ObjectInputStream(sock3.getInputStream());
        System.out.println("socket-mid4");
        ProgCommon.oosown = new ObjectOutputStream(cos3_own);
        ProgCommon.oisown = new ObjectInputStream(cis3_own);
        System.out.println("socketend");

    }

    private void cleanup() throws Exception {
//        ProgCommon.oos0.close();                                                   // close everything
//        ProgCommon.ois0.close();
//        clientSocket0_3.close();
//
//        ProgCommon.oos1.close();                                                   // close everything
//        ProgCommon.ois1.close();
//        clientSocket1_3.close();

        ProgCommon.oos3.close();                                                   // close everything
        ProgCommon.ois3.close();
        sock3.close();

        ProgCommon.oosown.close();                                                   // close everything
        ProgCommon.oisown.close();
        clientSocket3_own.close();
        sock3_own.close();
    }

    protected void createCircuits() throws Exception {
        Circuit.isForGarbling = false;
        Circuit.setIOStream(ProgCommon.ois3, ProgCommon.oos3);
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
        rcver3 = new NPOTReceiver(otNumOfPairs, ProgCommon.ois3, ProgCommon.oos3);
        StopWatch.taskTimeStamp("OT preparation");
    }
}