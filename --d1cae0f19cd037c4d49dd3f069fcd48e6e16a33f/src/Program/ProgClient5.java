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

public abstract class ProgClient5 extends Program {

    public static String serverIPname = "localhost";             // server IP name
    //    private final int    serverPort   = 23456;                   // server port number
    private final int    ClientPort5   = 23455;
    //    private final int    ClientPort1_3   = 23457;
    private final int    ClientPort5_own   = 23465;
//    private final int    ClientPort0_3   = 23455;



    private ServerSocket       sock5_own         = null;              // original server socket

    private Socket      sock5,clientSocket0_3,clientSocket1_3,clientSocket5_own         = null;                    // Socket object for communicating

    protected int otNumOfPairs;

    //	protected Sender snder;
    protected Sender snder5;

    protected Sender snder5_own;

    //	protected Receiver rcver;
    protected Receiver rcver5;
    protected Receiver rcver5_own;


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
        sock5 = new java.net.Socket(serverIPname, ClientPort5);          // create socket and connect
//        clientSocket0_3 = new java.net.Socket(serverIPname, ClientPort0_3);          // create socket and connect
//        clientSocket1_3 = new java.net.Socket(serverIPname, ClientPort1_3);          // create socket and connect

        try {

            sock5_own = new ServerSocket(ClientPort5_own);
            System.out.println("waiting for client5_own to connect");
            sleep(20);

            clientSocket5_own = sock5_own.accept();
            System.out.println("client5_own has connected");

        } catch (IOException e) {
            // cleanup();
            e.printStackTrace();
        }
//		ProgCommon.oos = new java.io.ObjectOutputStream(sock.getOutputStream());
//		ProgCommon.ois = new java.io.ObjectInputStream(sock.getInputStream());
        System.out.println("socket-mid");



        sleep(500);
        CountingOutputStream cos5_own = new CountingOutputStream(clientSocket5_own.getOutputStream());
        CountingInputStream cis5_own = new CountingInputStream(clientSocket5_own.getInputStream());
        System.out.println("socket-mid1");
//        ProgCommon.oos0 = new java.io.ObjectOutputStream(clientSocket0_3.getOutputStream());
//        ProgCommon.ois0 = new java.io.ObjectInputStream(clientSocket0_3.getInputStream());
//        System.out.println("socket-mid2");
//        ProgCommon.oos1 = new java.io.ObjectOutputStream(clientSocket1_3.getOutputStream());
//        ProgCommon.ois1 = new java.io.ObjectInputStream(clientSocket1_3.getInputStream());
//        System.out.println("socket-mid3");
        ProgCommon.oos5 = new java.io.ObjectOutputStream(sock5.getOutputStream());
        ProgCommon.ois5 = new java.io.ObjectInputStream(sock5.getInputStream());
        System.out.println("socket-mid4");
        ProgCommon.oosown = new ObjectOutputStream(cos5_own);
        ProgCommon.oisown = new ObjectInputStream(cis5_own);
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

        ProgCommon.oos5.close();                                                   // close everything
        ProgCommon.ois5.close();
        sock5.close();

        ProgCommon.oosown.close();                                                   // close everything
        ProgCommon.oisown.close();
        clientSocket5_own.close();
        sock5_own.close();
    }

    protected void createCircuits() throws Exception {
        Circuit.isForGarbling = false;
        Circuit.setIOStream(ProgCommon.ois5, ProgCommon.oos5);
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
        rcver5 = new NPOTReceiver(otNumOfPairs, ProgCommon.ois5, ProgCommon.oos5);
        StopWatch.taskTimeStamp("OT preparation");
    }
}