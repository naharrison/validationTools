package lundTools;

import org.jlab.physics.io.LundReader;

public class Try {

 	public static void main(String[] args) {
 		
 		LundReader lr = new LundReader();
 		lr.addFile("/Users/harrison/Documents/workspace/validationTools/src/lundTools/testfile.dat");
 		lr.open();
 		//lr.next();
 		System.out.println(lr.next());
 		System.out.println(lr.getEvent().toLundStringGenerated());
 		System.out.println("");
 		System.out.println("");
 		System.out.println(lr.getEvent().toLundString());
 		System.out.println("");
 		System.out.println("");
 		System.out.println("");
 		System.out.println("");
 		
 		int count = 0;
 		while(lr.next())
 		{
 		System.out.print(lr.getEvent().toLundString());
 		count++;
 		}
 		System.out.println("");
 		System.out.println("");
 		System.out.println(count);
 		
 	}
 	
}
