import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Program that creates a decision tree based on the gini index
 * @author Harry Longwell
 *
 */
public class HW04_Longwell_Harry_Trainer {
	/**
	 * Main driver method
	 * @param args
	 */
	public static void main(String[] args){
		//Create FileReader to read in file of speeds and recklessness
				FileReader fr = null;
				try {
					fr = new FileReader("HW04_Training_Data__v010.csv");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					System.err.println("Failed file read");
				}
				BufferedReader br = null;
				
				//Arraylists of attribute 1, 2, and 3
				ArrayList<Double> attr1 = new ArrayList<Double>();
				ArrayList<Double> attr2 = new ArrayList<Double>();
				ArrayList<Double> attr3 = new ArrayList<Double>();
				
				//Array of class
				ArrayList<Double> classify = new ArrayList<Double>();
				
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
				
				//Reads file until end of file, putting each data point into its appropriate array
				try {
					while((line = br.readLine()) != null){
						String[] tokens = line.split(",");
						attr1.add(Double.parseDouble(tokens[0]));
						attr2.add(Double.parseDouble(tokens[1]));
						attr3.add(Double.parseDouble(tokens[2]));
						classify.add(Double.parseDouble(tokens[3]));
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
				
				//Loops through data, quantizing them rounded to the nearest half
				for(int data_idx = 0; data_idx < attr1.size(); data_idx++){
					double data1 = 0.5 * Math.round(attr1.get(data_idx) / 0.5);
					attr1.set(data_idx, data1);
					double data2 = 0.5 * Math.round(attr2.get(data_idx) / 0.5);
					attr2.set(data_idx, data2);
					double data3 = 0.5 * Math.round(attr3.get(data_idx) / 0.5);
					attr3.set(data_idx, data3);
				}
				//Calls the threshold finding method on each attribute list
				double thres1 = findThreshold(attr1, classify);
				double thres2 = findThreshold(attr2, classify);
				double thres3 = findThreshold(attr3, classify);
				//outputs threshold values
				System.out.println("Attribute 1 Threshold: " + thres1);
				System.out.println("Attribute 2 Threshold: " + thres2);
				System.out.println("Attribute 3 Threshold: " + thres3);
	}
	/**
	 * method that finds the best splitting value for the attribute
	 * @param data - array of attribute values
	 * @param classify - array of class values
	 * @return - threshold value
	 */
	public static double findThreshold(ArrayList<Double> data, ArrayList<Double> classify){
		//set variables to keep track of best gini and threshold
		double best_gini = 1000000;
		double best_thres = 0;
		//loops through the attribute values setting the current value as the threshold to test
		for(int thres_idx = 0; thres_idx < data.size(); thres_idx++){
			//Set current test threshold
			double test_thres = data.get(thres_idx);
			//set variables to keep track of class counts
			double above_yes = 0;
			double above_no = 0;
			double below_yes = 0;
			double below_no = 0;
			double above_count = 0;
			double below_count = 0;

			//Loop through attribute data, comparing to current threshold
			for(int data_idx = 0; data_idx < data.size(); data_idx++){
				//Checks if the current data is greater than the current threshold
				if(data.get(data_idx) > test_thres){
					//increment above total
					above_count++;
					if(classify.get(data_idx) == 1){
						above_yes++;
					}
					else{
						above_no++;
					}
				}
				else{
					//increment below count
					below_count++;
					if(classify.get(data_idx) == 1){
						below_yes++;
					}
					else{
						below_no++;
					}
				}
			}
			double size = data.size();
			//Calculate current gini
			double above_gini = 1 - ((above_yes / above_count) * (above_yes / above_count)) - ((above_no / above_count) * (above_no / above_count));
			double below_gini = 1 - ((below_yes / below_count) * (below_yes / below_count)) - ((below_no / below_count) * (below_no / below_count));
			double curr_gini = ((above_count / size) * above_gini) + ((below_count / size) * below_gini);
			
			//skips if the gini results in zero
			if(curr_gini == 0.0){
				continue;
			}
			//if current gini is less or equal to best, then checks if current is equal to best
			//if they are equal, it assigns the gini that has the
			//lower threshold value as the best gini
			if(curr_gini <= best_gini){
				if(curr_gini == best_gini){
					System.out.println("TIE");
					//if there was a tie, chooses the larger threshold
					if(test_thres > best_thres){
						best_thres = test_thres;
						best_gini = curr_gini;
					}
				}
				else{
					best_thres = test_thres;
					best_gini = curr_gini;
				}
			}
		}
		System.out.println("Best Gini: " + best_gini);
		return best_thres;
	}
}
