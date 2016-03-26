import java.awt.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class the performs Otsu's method on a data set
 * @author Harry Longwell
 *
 */
public class HW02_Longwell_Harry_program {

	/**
	 * main class that performs the calculations and outputs results of Otsu's method
	 * @param args - command line arguments, not used
	 * @throws IOException, if a data point given was not a double
	 */
	public static void main(String args[]) throws IOException{
		
		//Arraylist to hold the read in data set
		ArrayList<Double> speeds = new ArrayList<Double>();
		
		FileReader fr = new FileReader("UNCLASSIFIED_Speed_Observations_for_128_vehicles.txt");
		BufferedReader br = null;
		int size = 0;
		
		String line;
		
		//Create a BufferedReader and read in the data set one point at a time
		//adding it to the arraylist, and incrementing the size
		try{
			
			br = new BufferedReader(fr);
			
			while( (line = br.readLine()) != null ){

				speeds.add( Double.parseDouble(line) );
				size++;
				
			}
			
		}catch( IOException e ){
			
			e.printStackTrace();
			
		}
		
		fr.close();
		
		double speed = 0;
		
		//Create and array to hold the quantized data set, the bins
		ArrayList<Double> quantized_data = new ArrayList<Double>();
		
		//Loops through the list of speeds, quantizes the data, finds the array
		// index that corresponds with the quantized data and increments its value
		for(int i = 0; i < speeds.size(); i++){
			
			//System.out.println("Speed: " + speeds.get(i));
			speed = ((int) Math.floor((speeds.get(i) / 2))) * 2;
			quantized_data.add(speed);
				
		}
		
		//Sorts data set
		Collections.sort(quantized_data);
		
		double best[] = otsu(quantized_data);
		
		System.out.print("Best Speed to separate: " + best[1] + "\n");
		System.out.print("Best mixed varience: " + best[0] + "\n");
		
	}
	
	public static double[] otsu(ArrayList<Double> data){
		
		double best[] = new double[2];
		double best_thres = 0;
		double best_mixed_var = 1000000;
		ArrayList<Double> variance = new ArrayList<Double>();
		ArrayList<Double> threshold = new ArrayList<Double>();
		ArrayList<Double> under = new ArrayList<Double>();
		ArrayList<Double> over = new ArrayList<Double>();
		
		for(int thr_idx = 0; thr_idx < data.size(); thr_idx++){
			
			double test_thr = data.get(thr_idx);
			
			//Iterates through data set, putting values lower than threshold
			// in a lower arraylist, and the values greater than or equal to
			// in a greater arraylist
			for(int sort_idx = 0; sort_idx < data.size(); sort_idx++){
				
				if(data.get(sort_idx) < test_thr){
					
					under.add(data.get(sort_idx));
					
				}
				else{
					
					over.add(data.get(sort_idx));
					
				}
				
			}
			
			if(under.size() == 0 || over.size() == 0){
				continue;
			}
			
			//gets the fraction of data points under and over the threshold
			double wt_under = (double)under.size() / data.size();
			double wt_over = (double)over.size() / data.size();
			
			//Calculate the mean of the data under the threshold
			double mean_under = 0;
			for(int under_idx = 0; under_idx < under.size(); under_idx++){
				mean_under += under.get(under_idx);
			}
			mean_under = (double)mean_under / under.size();
			
			//Calculate the mean of the data over the threshold
			double mean_over = 0;
			for(int over_idx = 0; over_idx < over.size(); over_idx++){
				mean_over += over.get(over_idx);
			}
			mean_over = (double)mean_over / over.size();
			
			double var_under = 0;
			double var_over = 0;
			
			//Calculates the variance of the data under the threshold
			for(int under_idx = 0; under_idx < under.size(); under_idx++){
				var_under += (under.get(under_idx) - mean_under) * (under.get(under_idx) - mean_under);	
			}
			var_under = var_under / under.size();
			
			//Calculates the variance of the data over the threshold
			for(int over_idx = 0; over_idx < over.size(); over_idx++){
				var_over += (over.get(over_idx) - mean_over) * (over.get(over_idx) - mean_over);	
			}
			var_over = var_over / over.size();
			
			//Calculates the mixed variance of the current threshold
			double current_mixed_var = (wt_under * var_under) + (wt_over * var_over);

			variance.add(current_mixed_var);
			threshold.add(test_thr);
			
			//If the calculated mixed variance is lower or equal to the current
			//best mixed variance, set the the best mixed variance equal to the
			//calculated mixed variance.
			if(current_mixed_var <= best_mixed_var){
				
				best_mixed_var = current_mixed_var;
				best_thres = test_thr;
				
			}
			
			//clear under and over arraylists
			under.clear();
			over.clear();
			
		}		
		
		//places best threshold and variance in array to return
		best[0] = best_mixed_var;
		best[1] = best_thres;
		
		return best;
	}
	
}
