import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;


/**
 * Program that reads in data, quantize it, and finds the lowest misclassification rate
 *Takes data and plots it
 * @author Harry Longwell
 *
 */
public class HW03_Longwell_Harry_program {

	/**
	 * Main driver class, performs all the calculations and plots data
	 * @param args
	 */
	public static void main(String args[]){
		
		//Create FileReader to read in file of speeds and recklessness
		FileReader fr = null;
		try {
			fr = new FileReader("CLASSIFIED_TRAINING_SET_FOR_RECKLESS_DRIVERS.csv");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("Failed file read");
		}
		BufferedReader br = null;
		
		//Array of speeds
		ArrayList<Double> speeds = new ArrayList<Double>();
		
		//Array of recklessness
		ArrayList<Integer> reckless = new ArrayList<Integer>();
		
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
		
		//Reads file until end of file, creating Objects that hold both attributes
		//and stores them in a list
		try {
			while((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				speeds.add(Double.parseDouble(tokens[0]));
				reckless.add(Integer.parseInt(tokens[1]));
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
		
		//ArrayList of data holding objects
		ArrayList<Speed> data = new ArrayList<Speed>();
		
		//Loops through speeds, quantizing them rounded to the nearest half mph
		//Creates a Speed object containing new speed and recklessness and
		//stores them in an array
		for(int speed_idx = 0; speed_idx < speeds.size(); speed_idx++){
			double speed = 0.5 * Math.round(speeds.get(speed_idx) / 0.5);
			Speed speed_obj = new Speed(speed, reckless.get(speed_idx));
			data.add(speed_obj);
		}
		
		//Sorting variables
		int count = 0;
		int pos = 0;
		
		//Loops until the the entire list of data objects is sorted from
		//lowest to highest
		while(count != data.size() - 1){
			//checks if the current speed is greater than the next
			//if so swaps them and resets loop, else moves on
			if(data.get(pos).getSpeed() > data.get(pos + 1).getSpeed()){
				Speed temp = data.get(pos);
				data.set(pos, data.get(pos + 1));
				data.set(pos + 1, temp);
				pos = 0;
				count = 0;
			}
			else{
				pos++;
				count++;
			}
		}
		
		//Initialize variables for use during the calculations
		double curr_misclass = 0;
		double best_misclass = 1000000000;
		double best_thres = 0;
		double TP = 0;
		double FP = 0;
		double FN = 0;
		double TN = 0;
		double best_FP = 0;
		DecimalFormat df = new DecimalFormat("0.###");
		
		//ArrayList to contain Misclass objects
		ArrayList<Misclass> misclass_list = new ArrayList<Misclass>();
		
		//Loop through all the data objects, sets the current threshold
		//to test with, finds number of Hits, False Alarms, Misses
		//and Correct Rejections, calculates the misclassification rate
		//Stores the better misclassification rate
		for(int thres_idx = 0; thres_idx < data.size(); thres_idx++){
			
			//Set current test threshold
			double test_thres = data.get(thres_idx).getSpeed();
			//Reset variables
			TP = 0;
			FP = 0;
			FN = 0;
			TN = 0;
			//Loop through data, comparing to current threshold
			for(int data_idx = 0; data_idx < data.size(); data_idx++){
				//Checks if the current speed is greater than the current threshold
				if(data.get(data_idx).getSpeed() > test_thres){
					//If reckless is 1, adds one to True Positives, else, adds one to False Positives
					if(data.get(data_idx).getReckless() == 1){
						TP++;
					}
					else{
						FP++;
					}
				}
				else{
					//If reckless is 1, adds one to False Negative, else adds one to True Rejections
					if(data.get(data_idx).getReckless() == 1){
						FN++;
					}
					else{
						TN++;
					}
				}
			}
			//Calculate current misclassification rate
			curr_misclass = (FP + FN) / (TP + FP + FN + TN);
			
			//Create new Misclass object, and store misclassification rate, 
			//and current threshold, and add it to the list
			Misclass misclass = new Misclass(curr_misclass, test_thres);
			misclass_list.add(misclass);
			
			//if current threshold is less or equal to best, then checks if current is equal to best
			//if they are equal, it assigns the misclassification rate that has the
			//lower amount of False Positives as the best threshold
			if(curr_misclass <= best_misclass){
				if(curr_misclass == best_misclass){
					if(FP < best_FP){
						best_misclass = curr_misclass;
						best_FP = FP;
						best_thres = test_thres;
					}
				}
				else{
					best_misclass = curr_misclass;
					best_FP = FP;
					best_thres = test_thres;
				}
			}
		}
		
		//Creates an instance of the plotting class and displays
		final LineChart graph = new LineChart("HW03", misclass_list);
		graph.pack();
		RefineryUtilities.centerFrameOnScreen(graph);
		graph.setVisible(true);
		
		//Prints out best results
		System.out.println("Best Threshold: " + best_thres);
		System.out.println("Best Miclassification Rate: " + df.format(best_misclass));
	}
	
	/**
	 * Class that uses the imported API to plot the misclassification as a
	 * function of the threshold
	 * @author Harry Longwell
	 *
	 */
	public static class LineChart extends ApplicationFrame{

		//Constructor of the class
		public LineChart(final String title, ArrayList<Misclass> misclass_list) {
			super(title);
			
			// TODO Auto-generated constructor stub
			
			//Creates a empty collection of data
			XYSeriesCollection collection = new XYSeriesCollection();
			//Creates a new series to hold the dataset 
			XYSeries series = new XYSeries("Misclassification Rate");
			//Loops through and adds the misclassification rates and thresholds 
			//as points to be plotted on the graph
			for(int data_idx = 0; data_idx < misclass_list.size(); data_idx++){
				series.add(misclass_list.get(data_idx).getThreshold(), misclass_list.get(data_idx).getMisclass());
			}
			collection.addSeries(series);
			XYDataset dataset = collection;
			
			//Creates an instance of the charting object
			JFreeChart chart = ChartFactory.createXYLineChart(
					"HW03 Misclassification Rates",
					"Threshold",
					"Misclassification Rate",
					dataset,
					PlotOrientation.VERTICAL,
					true,
					true,
					false);
			
			ChartPanel chartPanel = new ChartPanel(chart);
			chartPanel.setPreferredSize(new java.awt.Dimension(650, 500));
			setContentPane(chartPanel);
		}
		
	}
	/**
	 * A class that holds the speed and recklessness
	 * @author Harry Longwell
	 *
	 */
	public static class Speed {

		double speed = 0;
		int reckless = 0;
		/**
		 * Constructor of class
		 * @param speed - speed to be stored
		 * @param reckless - recklessness to be stored
		 */
		public Speed(Double speed, Integer reckless){
			this.speed = speed;
			this.reckless = reckless;
		}
		/**
		 * Return speed
		 * @return speed of object
		 */
		public double getSpeed(){
			return this.speed;
		}
		/**
		 * returns recklessness
		 * @return recklessness of object
		 */
		public int getReckless(){
			return this.reckless;
		}
	}
	
	/**
	 * Class that hold the misclassification rate and related threshold
	 * @author Harry Longwell
	 *
	 */
	public static class Misclass {

		double misclass = 0;
		double thres = 0;

		/**
		 * Constructor of class
		 * @param misclass - misclassification rate
		 * @param thres - threshold
		 */
		public Misclass(double misclass, double thres){
			this.misclass = misclass;
			this.thres = thres;
		}
		/**
		 * Returns misclassification rate
		 * @return misclassification rate
		 */
		public double getMisclass(){
			return this.misclass;
		}
		/**
		 * Returns threshold
		 * @return threshold
		 */
		public double getThreshold(){
			return this.thres;
		}
	}
}