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

public abstract class ProgOwner extends Program {

    public static String serverIPname = "localhost";             // server IP name
    //    private final int    serverPort   = 23456;                   // server port number
    private final int    OwnerPort   = 23457;
    private final int    ClientPort0_own   = 23460;
    private final int    ClientPort1_own   = 23461;
    private final int    ClientPort2_own   = 23462;
    private final int    ClientPort3_own   = 23463;
    private final int    ClientPort4_own   = 23464;
    private final int    ClientPort5_own   = 23465;
    private final int    ClientPort6_own   = 23466;



    private Socket      sockown,clientSocket0_own,clientSocket1_own,clientSocket2_own,clientSocket3_own,clientSocket4_own,clientSocket5_own,clientSocket6_own        = null;                    // Socket object for communicating

    protected int otNumOfPairs;

    protected Receiver rcverown;




    public void run() throws Exception {
        create_socket_and_connect();
        StopWatch.pointTimeStamp("running program");

        super.run();

        cleanup();
    }

    protected void init() throws Exception {
        System.out.println("Owner-itercount:"+Program.iterCount);
        super.init();
    }

    private void create_socket_and_connect() throws Exception {
        sockown = new java.net.Socket(serverIPname, OwnerPort);          // create socket and connect
        clientSocket0_own = new java.net.Socket(serverIPname, ClientPort0_own);          // create socket and connect
        clientSocket1_own = new java.net.Socket(serverIPname, ClientPort1_own);          // create socket and connect
        clientSocket2_own = new java.net.Socket(serverIPname, ClientPort2_own);          // create socket and connect
        clientSocket3_own = new java.net.Socket(serverIPname, ClientPort3_own);          // create socket and connect
        clientSocket4_own = new java.net.Socket(serverIPname, ClientPort4_own);          // create socket and connect
        clientSocket5_own = new java.net.Socket(serverIPname, ClientPort5_own);          // create socket and connect
        clientSocket6_own = new java.net.Socket(serverIPname, ClientPort6_own);          // create socket and connect


//		ProgCommon.oos = new java.io.ObjectOutputStream(sock.getOutputStream());
//		ProgCommon.ois = new java.io.ObjectInputStream(sock.getInputStream());
        ProgCommon.oos0 = new java.io.ObjectOutputStream(clientSocket0_own.getOutputStream());
        ProgCommon.ois0 = new java.io.ObjectInputStream(clientSocket0_own.getInputStream());
        ProgCommon.oos1 = new java.io.ObjectOutputStream(clientSocket1_own.getOutputStream());
        ProgCommon.ois1 = new java.io.ObjectInputStream(clientSocket1_own.getInputStream());
        ProgCommon.oos2 = new java.io.ObjectOutputStream(clientSocket2_own.getOutputStream());
        ProgCommon.ois2 = new java.io.ObjectInputStream(clientSocket2_own.getInputStream());
        ProgCommon.oos3 = new java.io.ObjectOutputStream(clientSocket3_own.getOutputStream());
        ProgCommon.ois3 = new java.io.ObjectInputStream(clientSocket3_own.getInputStream());
        ProgCommon.oos4 = new java.io.ObjectOutputStream(clientSocket4_own.getOutputStream());
        ProgCommon.ois4 = new java.io.ObjectInputStream(clientSocket4_own.getInputStream());
        ProgCommon.oos5 = new java.io.ObjectOutputStream(clientSocket5_own.getOutputStream());
        ProgCommon.ois5 = new java.io.ObjectInputStream(clientSocket5_own.getInputStream());
        ProgCommon.oos6 = new java.io.ObjectOutputStream(clientSocket6_own.getOutputStream());
        ProgCommon.ois6 = new java.io.ObjectInputStream(clientSocket6_own.getInputStream());

        ProgCommon.oosown = new java.io.ObjectOutputStream(sockown.getOutputStream());
        ProgCommon.oisown = new java.io.ObjectInputStream(sockown.getInputStream());
        System.out.println("socket-mid");

    }

    private void cleanup() throws Exception {
        ProgCommon.oos0.close();                                                   // close everything
        ProgCommon.ois0.close();
        clientSocket0_own.close();

        ProgCommon.oos1.close();                                                   // close everything
        ProgCommon.ois1.close();
        clientSocket1_own.close();

        ProgCommon.oos2.close();                                                   // close everything
        ProgCommon.ois2.close();
        clientSocket2_own.close();

        ProgCommon.oos3.close();                                                   // close everything
        ProgCommon.ois3.close();
        clientSocket3_own.close();

        ProgCommon.oos4.close();                                                   // close everything
        ProgCommon.ois4.close();
        clientSocket4_own.close();

        ProgCommon.oos5.close();                                                   // close everything
        ProgCommon.ois5.close();
        clientSocket5_own.close();

        ProgCommon.oos6.close();                                                   // close everything
        ProgCommon.ois6.close();
        clientSocket6_own.close();

        ProgCommon.oosown.close();                                                   // close everything
        ProgCommon.oisown.close();
        sockown.close();
    }

    protected void createCircuits() throws Exception {
        Circuit.isForGarbling = false;
        Circuit.setIOStream(ProgCommon.oisown, ProgCommon.oosown);
        //every client has different label

        for (int i = 0; i < ProgCommon.ccs.length; i++) {
            ProgCommon.ccs[i].build();
        }
        for (int i = 0; i < ProgCommon.ccsin.length; i++) {
            ProgCommon.ccsin[i].build();
        }
        StopWatch.taskTimeStamp("circuit preparation");
    }

    protected void initializeOT() throws Exception {
        rcverown = new NPOTReceiver(otNumOfPairs, ProgCommon.oisown, ProgCommon.oosown);
        StopWatch.taskTimeStamp("OT preparation");
    }
}