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

public abstract class ProgClient6 extends Program {

    public static String serverIPname = "localhost";             // server IP name
    //    private final int    serverPort   = 23456;                   // server port number
    private final int    ClientPort6   = 23456;
    //    private final int    ClientPort1_3   = 23457;
    private final int    ClientPort6_own   = 23466;
//    private final int    ClientPort0_3   = 23455;



    private ServerSocket       sock6_own         = null;              // original server socket

    private Socket      sock6,clientSocket0_3,clientSocket1_3,clientSocket6_own         = null;                    // Socket object for communicating

    protected int otNumOfPairs;

    //	protected Sender snder;
    protected Sender snder6;

    protected Sender snder6_own;

    //	protected Receiver rcver;
    protected Receiver rcver6;
    protected Receiver rcver6_own;


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
        sock6 = new java.net.Socket(serverIPname, ClientPort6);          // create socket and connect
//        clientSocket0_3 = new java.net.Socket(serverIPname, ClientPort0_3);          // create socket and connect
//        clientSocket1_3 = new java.net.Socket(serverIPname, ClientPort1_3);          // create socket and connect

        try {

            sock6_own = new ServerSocket(ClientPort6_own);
            System.out.println("waiting for client6_own to connect");
            sleep(20);

            clientSocket6_own = sock6_own.accept();
            System.out.println("client5_own has connected");

        } catch (IOException e) {
            // cleanup();
            e.printStackTrace();
        }
//		ProgCommon.oos = new java.io.ObjectOutputStream(sock.getOutputStream());
//		ProgCommon.ois = new java.io.ObjectInputStream(sock.getInputStream());
        System.out.println("socket-mid");



        sleep(500);
        CountingOutputStream cos6_own = new CountingOutputStream(clientSocket6_own.getOutputStream());
        CountingInputStream cis6_own = new CountingInputStream(clientSocket6_own.getInputStream());
        System.out.println("socket-mid1");
//        ProgCommon.oos0 = new java.io.ObjectOutputStream(clientSocket0_3.getOutputStream());
//        ProgCommon.ois0 = new java.io.ObjectInputStream(clientSocket0_3.getInputStream());
//        System.out.println("socket-mid2");
//        ProgCommon.oos1 = new java.io.ObjectOutputStream(clientSocket1_3.getOutputStream());
//        ProgCommon.ois1 = new java.io.ObjectInputStream(clientSocket1_3.getInputStream());
//        System.out.println("socket-mid3");
        ProgCommon.oos6 = new java.io.ObjectOutputStream(sock6.getOutputStream());
        ProgCommon.ois6 = new java.io.ObjectInputStream(sock6.getInputStream());
        System.out.println("socket-mid4");
        ProgCommon.oosown = new ObjectOutputStream(cos6_own);
        ProgCommon.oisown = new ObjectInputStream(cis6_own);
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

        ProgCommon.oos6.close();                                                   // close everything
        ProgCommon.ois6.close();
        sock6.close();

        ProgCommon.oosown.close();                                                   // close everything
        ProgCommon.oisown.close();
        clientSocket6_own.close();
        sock6_own.close();
    }

    protected void createCircuits() throws Exception {
        Circuit.isForGarbling = false;
        Circuit.setIOStream(ProgCommon.ois6, ProgCommon.oos6);
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
        rcver6= new NPOTReceiver(otNumOfPairs, ProgCommon.ois6, ProgCommon.oos6);
        StopWatch.taskTimeStamp("OT preparation");
    }
}