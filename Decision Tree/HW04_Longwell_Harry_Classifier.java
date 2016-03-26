import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
/**
 * 
 * @author Harry Longwell
 *
 */
public class HW04_Longwell_Harry_Classifier {
	/**
	 * main driver method
	 * @param args - 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		//Create FileReader to read in file of speeds and recklessness
		FileReader fr = null;
		//Takes command line argument as file to read in
		File file = new File(args[0]);
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("Failed file read");
		}
		BufferedReader br = null;
		
		//Arraylists of attribute 1, 2, and 3
		ArrayList<Double> attr1 = new ArrayList<Double>();
		ArrayList<Double> attr2 = new ArrayList<Double>();
		ArrayList<Double> attr3 = new ArrayList<Double>();
		
		String line;
		
		//Read in and store data
		br = new BufferedReader(fr);
		
		//grabs first line of file, and discards it
		try {
			line = br.readLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//Reads file until end of file, storing data in the appropriate array
		try {
			while((line = br.readLine()) != null){
				String[] tokens = line.split(",;");
				attr1.add(Double.parseDouble(tokens[0]));
				attr2.add(Double.parseDouble(tokens[1]));
				attr3.add(Double.parseDouble(tokens[2]));
			}
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fr.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		int classifier = 0;
		//create the file to output to
		File fout = new File("HW04_Longwell_Harry_MyClassifications.txt");
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		//write header line of output file
		bw.write("Attr1, Attr2, Attr3, Class");
		bw.newLine();
		//loops through data deciding which class each line belongs to
		for(int data_idx = 0; data_idx < attr1.size(); data_idx++){
			if ( attr3.get(data_idx) >= 1.5 )
				if( attr2.get(data_idx) >= 3 ){
					classifier = 1;
				}
				else{
					classifier = 0;
				}
			else{
				if ( attr1.get(data_idx) >= 4 ){
					classifier = 1;
				}
				else{
					classifier = 0;
				}
			}
			//print out the class value for each line of the test data file.
			bw.write(attr1.get(data_idx) + ", " + attr2.get(data_idx) + ", " + attr3.get(data_idx) + ", " + classifier);
			bw.newLine();
		}
		bw.close();
	}
}
