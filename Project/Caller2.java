import java.lang.*;
import Helper.*;
import java.io.*;
import java.util.ArrayList;


public class Caller2 {

    public static void main(String [] args ){
        if (args.length <= 1) {
            System.out.println("We need more command line arguments\n" );
        }
        if (args[0].equals("S")) {
            String rst = singleInstance(args);
            System.out.println("Predicted: " + rst);
        } else {
            try {
                multipleInstance(args);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public static String singleInstance(String [] args) {
        String rs = "";
        for(int c = 1; c < args.length; ++c )
            rs += args[c]+",";
      //  System.out.println("Command Line :" + rs );
        return DATASET.predict(rs);
    }

    public static String singleInstance2(String [] args) {
        String rs = "";
        for(int c = 0; c < args.length; ++c )
            rs += args[c]+",";
        //  System.out.println("Command Line :" + rs );
        return DATASET.predict(rs);
    }

    public static void multipleInstance(String [] args) throws Exception {
        ArrayList<String> prediction = new ArrayList<String>();
        String rs = "";
        for(int c = 1; c < args.length; ++c ) {
            rs += args[c] + ",";
        }

        String fs = args[1];
        if ( fs == null) {
            System.out.println("Could not load Code Template File");
            return;
        }
        BufferedReader reader = new BufferedReader(new FileReader(fs));
        String line;
        int count = 0;
        int total = 0;
        while ((line = reader.readLine()) != null) {
            String[] arr = line.split(",");
            int size = arr.length;
            rs = singleInstance2(arr);
            prediction.add(rs);
           // System.out.println(arr[size-1]);
           // System.out.println(rs);
           // System.out.println("******");
            String x = "'"+rs+"'";
            if (arr[size-1].equals(x)) {
                count++;
            } else {
                System.out.println(arr[0] + " "+arr[size-1] + " "+ x);
            }
            total++;

        }

        System.out.println(count);
        reader.close();
    }
}
