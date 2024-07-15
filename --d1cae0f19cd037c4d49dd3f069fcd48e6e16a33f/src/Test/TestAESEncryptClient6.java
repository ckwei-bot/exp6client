//package Test;
//
//import java.security.SecureRandom;
//
//import jargs.gnu.CmdLineParser;
//
//import Utils.*;
//import Program.*;
//
//class TestAESEncryptClient6 {
//
//    static boolean autogen;
//    static short[] msg6 = {0x32, 0x43, 0xf6, 0xa8,
//            0x88, 0x5a, 0x30, 0x8d,
//            0x31, 0x31, 0x98, 0xa2,
//            0xe0, 0x37, 0x07, 0x34};
//
//
//
//    static SecureRandom rnd6 = new SecureRandom();
//
//
//
//    private static void printUsage() {
//        System.out.println("Usage: java TestAESEncryptClient [{-a, --autogen}] [{-s, --server} servername]");
//    }
//
//    private static void process_cmdline_args(String[] args) {
//        CmdLineParser parser = new CmdLineParser();
//        CmdLineParser.Option optionServerIPname = parser.addStringOption('s', "server");
//        CmdLineParser.Option optionAuto = parser.addBooleanOption('a', "autogen");
//        CmdLineParser.Option optionIterCount = parser.addIntegerOption('r', "iteration");
//
//        try {
//            parser.parse(args);
//        }
//        catch (CmdLineParser.OptionException e) {
//            System.err.println(e.getMessage());
//            printUsage();
//            System.exit(2);
//        }
//
//        autogen = (Boolean) parser.getOptionValue(optionAuto, false);
//        ProgClient.serverIPname = (String) parser.getOptionValue(optionServerIPname, new String("localhost"));
//        Program.iterCount = ((Integer) parser.getOptionValue(optionIterCount, new Integer(1))).intValue();
//    }
//
//    static void generateData() throws Exception {
//        //输入是使用random种子生成的
//        msg6 = new short[16];
//
//        for (int i = 0; i < 16; i++) {
//            msg6[i] = (short) rnd6.nextInt(0xff);
//
//        }
//    }
//
//    public static void main(String[] args) throws Exception {
//        StopWatch.pointTimeStamp("Starting program");
//        process_cmdline_args(args);
//
//        if (autogen)
//            generateData();
//
////	AESEncryptClient aesEncryptClient = new AESEncryptClient(msg, 4);
//        AESEncryptClient6 aesEncryptClient6 = new AESEncryptClient6(msg6, 4);
//        aesEncryptClient6.run();
//    }
//}