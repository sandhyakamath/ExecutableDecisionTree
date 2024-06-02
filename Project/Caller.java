import java.lang.*;
import Helper.*;
public class Caller
{
	public static void main(String [] args ){
		String rs = "";
		for(int c = 0; c < args.length; ++c )
			rs += args[c]+",";
		System.out.println("Command Line :" + rs );
		String rst = DATASET.predict(rs);
		System.out.println("Predicted: " + rst);
	}


}