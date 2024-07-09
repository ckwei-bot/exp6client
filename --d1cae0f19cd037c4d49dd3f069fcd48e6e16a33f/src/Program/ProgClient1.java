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

public abstract class ProgClient1 extends Program {

    public static String serverIPname = "localhost";             // server IP name
    //    private final int    serverPort   = 23456;                   // server port number
    private final int    ClientPort1   = 23451;
    private final int    ClientPort1_own   = 23458;
    // private final int    ClientPort0_1   = 23454;

    private ServerSocket       sock1_own         = null;              // original server socket

    private Socket      sock1,clientSocket0_1,clientSocket1_own         = null;                    // Socket object for communicating

    protected int otNumOfPairs;


    protected Receiver rcver1;



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
        sock1 = new java.net.Socket(serverIPname, ClientPort1);          // create socket and connect
//        clientSocket0_1 = new java.net.Socket(serverIPname, ClientPort0_1);          // create socket and connect
        try {

            sock1_own = new ServerSocket(ClientPort1_own);
            System.out.println("waiting for client1_own to connect");
            sleep(20);

            clientSocket1_own = sock1_own.accept();
            System.out.println("client1_own has connected");

        } catch (IOException e) {
            cleanup();
            e.printStackTrace();
        }
//		ProgCommon.oos = new java.io.ObjectOutputStream(sock.getOutputStream());
//		ProgCommon.ois = new java.io.ObjectInputStream(sock.getInputStream());

        System.out.println("socket-mid");

        CountingOutputStream cos1_own = new CountingOutputStream(clientSocket1_own.getOutputStream());
        CountingInputStream cis1_own = new CountingInputStream(clientSocket1_own.getInputStream());
        System.out.println("socket-mi2");

//        ProgCommon.oos0 = new java.io.ObjectOutputStream(clientSocket0_1.getOutputStream());
//        ProgCommon.ois0 = new java.io.ObjectInputStream(clientSocket0_1.getInputStream());
        System.out.println("socket-mid3");
        ProgCommon.oos1 = new java.io.ObjectOutputStream(sock1.getOutputStream());
        ProgCommon.ois1 = new java.io.ObjectInputStream(sock1.getInputStream());
        System.out.println("socket-mi4");

        ProgCommon.oosown = new ObjectOutputStream(cos1_own);
        ProgCommon.oisown = new ObjectInputStream(cis1_own);
        System.out.println("socketend");
    }

    private void cleanup() throws Exception {
//        ProgCommon.oos0.close();                                                   // close everything
//        ProgCommon.ois0.close();
//        clientSocket0_1.close();

        ProgCommon.oos1.close();                                                   // close everything
        ProgCommon.ois1.close();
        sock1.close();


        ProgCommon.oosown.close();                                                   // close everything
        ProgCommon.oisown.close();
        clientSocket1_own.close();
        sock1_own.close();
    }

    protected void createCircuits() throws Exception {
        Circuit.isForGarbling = false;
//        Circuit.setIOStream(ProgCommon.ois1, ProgCommon.oos1);
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
        rcver1 = new NPOTReceiver(otNumOfPairs, ProgCommon.ois1, ProgCommon.oos1);
        StopWatch.taskTimeStamp("OT preparation");
    }
}