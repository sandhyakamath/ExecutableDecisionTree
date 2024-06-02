package Helper;

import Lexer.TOKEN;

public class Utils {
    private static String interpret(int offset,String args) {
        String [] splitted = args.split(",");


        if ( offset < splitted.length ) {
            return splitted[offset];
        }

        return "";
    }

    public static String cmd_get_s(double offset, String ARGS) {

        return interpret((int)offset,ARGS);
    }

    public static int cmd_get_i(double offset, String ARGS) {
        String ret = interpret((int)offset,ARGS);

        try {
            return Integer.parseInt(ret);
        }
        catch(Exception e) {
            return 0;
        }
    }

    public static double cmd_get_d(double offset, String ARGS) {
        String ret = interpret((int)offset,ARGS);

        try {
            return Double.parseDouble(ret);
        }
        catch(Exception e) {

            return 0.0;
        }



    }

    public static boolean cmd_get_b(double offset, String ARGS) {
        //System.out.println(offset + " BOOLEAN  " + ARGS);
        return true;
    }





    public static boolean compareInt(int a, int b, String op) {
        boolean val = false;
        switch (op) {
            case "EQ":
                val = (a == b);
                break;
            case "GT" :
                val = (a > b);
                break;
            case "GTE":
                val = (a >= b);
                break;
            case "LT":
                val = (a < b);
                break;
            case "LTE":
                val = (a <= b);
                break;
            case "NEQ":
                val = (a != b);
                break;
        }
        return val;
    }
}
