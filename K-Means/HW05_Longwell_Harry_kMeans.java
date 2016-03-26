import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class HW05_Longwell_Harry_kMeans {

	public static void main(String[] args){
		
		//Create FileReader to read in file of speeds and recklessness
		FileReader fr = null;
		try {
			fr = new FileReader("HW_KMEANS_DATA_v015.csv");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("Failed file read");
		}
		BufferedReader br = null;
		
		//Arraylists for the coordinates
		ArrayList<Position> points = new ArrayList<Position>();
		
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
				Position p = new Position(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]));
				points.add(p);
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
		
		//array lists to represent the clusters being formed
		ArrayList<Position> cluster1 = new ArrayList<Position>();
		ArrayList<Position> cluster2 = new ArrayList<Position>();
		ArrayList<Position> cluster3 = new ArrayList<Position>();
		ArrayList<Position> cluster4 = new ArrayList<Position>();
		ArrayList<Position> cluster5 = new ArrayList<Position>();
		
		//Arraylists that in the end will hold the clusters with the best SSE
		ArrayList<Position> best_cluster1 = null;
		ArrayList<Position> best_cluster2 = null;
		ArrayList<Position> best_cluster3 = null;
		ArrayList<Position> best_cluster4 = null;
		ArrayList<Position> best_cluster5 = null;
		
		//random seed
		Random rn = new Random();
		
		//temporary storing variables
		double tempX = 0;
		double tempY = 0;
		double best_SSE = 100000;
		double temp_SSE = 0;
		
		//array of booleans for the clusters, will be changed to true if the corresponding cluster didn't move with a new point
		boolean stayed[] = new boolean[5];
		Arrays.fill(stayed, false);
		
		for(int i = 0; i < 10; i++){
			System.out.println("Loop " + i);
			//Randomly chooses five centroids to form clusters with
			int choice = rn.nextInt(points.size());
			cluster1.add(points.get(choice));
			points.remove(choice);
			choice = rn.nextInt(points.size());
			cluster2.add(points.get(choice));
			points.remove(choice);
			choice = rn.nextInt(points.size());
			cluster3.add(points.get(choice));
			points.remove(choice);
			choice = rn.nextInt(points.size());
			cluster4.add(points.get(choice));
			points.remove(choice);
			choice = rn.nextInt(points.size());
			cluster5.add(points.get(choice));
			points.remove(choice);
			
			//variable to hold the centroid positions
			Position curr_centroid1 = null;
			Position curr_centroid2 = null;
			Position curr_centroid3 = null;
			Position curr_centroid4 = null;
			Position curr_centroid5 = null;
			
			//fills a temporary arraylist with all the points
			ArrayList<Position> tempPoints = new ArrayList<Position>();
			for(int j = 0; j < points.size(); j++){
				tempPoints.add(points.get(j));
			}
			
			//while all the clusters have not stopped moving, performs k-means
			while(stayed[0] == false && stayed[1] == false && stayed[2] == false && stayed[3] == false && stayed[4] == false){
				if(tempPoints.size() == 0){
					break;
				}
				//Assign centroid for first cluster
				curr_centroid1 = calcCentroid(cluster1);
				
				//Assign centroid for second cluster
				curr_centroid2 = calcCentroid(cluster2);
				
				//Assign centroid for third cluster
				curr_centroid3 = calcCentroid(cluster3);
				
				//Assign centroid for fourth cluster
				curr_centroid4 = calcCentroid(cluster4);
				
				//Assign centroid for fifth cluster
				curr_centroid5 = calcCentroid(cluster5);
				
				//finds the new point for the first cluster and checks if the cluster moved
				if(stayed[0] == false){
					Position lowest1 = newPoint(curr_centroid1, tempPoints);
					cluster1.add(lowest1);
					tempPoints.remove(lowest1);
					if(lowest1.getX() == curr_centroid1.getX() && lowest1.getY() == curr_centroid1.getY()){
						stayed[0] = true;
					}
				}
				//finds the new point for the second cluster and checks if the cluster moved
				if(stayed[1] == false){
					Position lowest2 = newPoint(curr_centroid2, tempPoints);
					if(lowest2 == null){
						break;
					}
					cluster2.add(lowest2);
					tempPoints.remove(lowest2);
					
					if(lowest2.getX() == curr_centroid2.getX() && lowest2.getY() == curr_centroid2.getY()){
						stayed[1] = true;
					}
				}
				//finds the new point for the third cluster and checks if the cluster moved
				if(stayed[2] == false){
					Position lowest3 = newPoint(curr_centroid3, tempPoints);
					cluster3.add(lowest3);
					tempPoints.remove(lowest3);
					if(lowest3.getX() == curr_centroid3.getX() && lowest3.getY() == curr_centroid3.getY()){
						stayed[2] = true;
					}
				}
				//finds the new point for the fourth cluster and checks if the cluster moved
				if(stayed[3] == false){
					Position lowest4 = newPoint(curr_centroid4, tempPoints);
					cluster4.add(lowest4);
					tempPoints.remove(lowest4);
					if(lowest4.getX() == curr_centroid4.getX() && lowest4.getY() == curr_centroid4.getY()){
						stayed[3] = true;
					}
				}
				//finds the new point for the fifth cluster and checks if the cluster moved
				if(stayed[4] == false){
					Position lowest5 = newPoint(curr_centroid5, tempPoints);
					cluster5.add(lowest5);
					tempPoints.remove(lowest5);
					if(lowest5.getX() == curr_centroid5.getX() && lowest5.getY() == curr_centroid5.getY()){
						stayed[4] = true;
					}
				}
			}
			//Calculate the SSE for the current clustering
			temp_SSE = 0;
			double dist1 = sumDistance(curr_centroid1, cluster1);
			double dist2 = sumDistance(curr_centroid2, cluster1);
			double dist3 = sumDistance(curr_centroid3, cluster1);
			double dist4 = sumDistance(curr_centroid4, cluster1);
			double dist5 = sumDistance(curr_centroid5, cluster1);
			temp_SSE = dist1 + dist2 + dist3 + dist4 + dist5;
			//checks if the current SSE is lower than the previous SSE
			if(temp_SSE < best_SSE){
				best_SSE = temp_SSE;
				best_cluster1 = new ArrayList<Position>();
				for(int k = 0; k < cluster1.size(); k++){
					best_cluster1.add(cluster1.get(k));
				}
				best_cluster2 = new ArrayList<Position>();
				for(int k = 0; k < cluster2.size(); k++){
					best_cluster2.add(cluster2.get(k));
				}
				best_cluster3 = new ArrayList<Position>();
				for(int k = 0; k < cluster3.size(); k++){
					best_cluster3.add(cluster3.get(k));
				}
				best_cluster4 = new ArrayList<Position>();
				for(int k = 0; k < cluster4.size(); k++){
					best_cluster4.add(cluster4.get(k));
				}
				best_cluster5 = new ArrayList<Position>();
				for(int k = 0; k < cluster5.size(); k++){
					best_cluster5.add(cluster5.get(k));
				}
			}
			System.out.println("SSE: " + best_SSE);
			//clears all clusters
			/*
			cluster1.clear();
			cluster2.clear();
			cluster3.clear();
			cluster4.clear();
			cluster5.clear();*/
		}
		System.out.println("Cluster 1 Centroid: X = " + calcCentroid(best_cluster1).getX() + ", Y = " + calcCentroid(best_cluster1).getY());
		System.out.println("Cluster 2 Centroid: X = " + calcCentroid(best_cluster2).getX() + ", Y = " + calcCentroid(best_cluster2).getY());
		System.out.println("Cluster 3 Centroid: X = " + calcCentroid(best_cluster3).getX() + ", Y = " + calcCentroid(best_cluster3).getY());
		System.out.println("Cluster 4 Centroid: X = " + calcCentroid(best_cluster4).getX() + ", Y = " + calcCentroid(best_cluster4).getY());
		System.out.println("Cluster 5 Centroid: X = " + calcCentroid(best_cluster5).getX() + ", Y = " + calcCentroid(best_cluster5).getY());
		
		System.out.println("Cluster 1 size: " + best_cluster1.size());
		System.out.println("Cluster 2 size: " + best_cluster2.size());
		System.out.println("Cluster 3 size: " + best_cluster3.size());
		System.out.println("Cluster 4 size: " + best_cluster4.size());
		System.out.println("Cluster 5 size: " + best_cluster5.size());
	}

	/**
	 * finds the centroid to use
	 * @param cluster - cluster of points
	 * @return - centroid
	 */
	public static Position calcCentroid(ArrayList<Position> cluster){
		double tempX = 0;
		double tempY = 0;
		//for each point, sums the x and y coordinates
		for(int j = 0; j < cluster.size(); j++){
			tempX += cluster.get(j).getX();
			tempY += cluster.get(j).getY();
		}
		//averages the x's and y's, and returns a point with those coordinates
		tempX = tempX / cluster.size();
		tempY = tempY / cluster.size();
		Position curr_centroid = new Position(tempX, tempY);
		return curr_centroid;
	}
	
	/**
	 * Finds the sum of all the distances of the points  from the centroid
	 * @param curr_centroid - centroid
	 * @param cluster - cluster of points
	 * @return - sum of distances for that cluster
	 */
	public static double sumDistance(Position curr_centroid, ArrayList<Position> cluster){
		double temp_dist = 0;
		double total_dist = 0;
		//find point with the sum of euclidean distances
		for(int j = 0; j < cluster.size(); j++){
			temp_dist = 0;
			temp_dist += (curr_centroid.getX() - curr_centroid.getY()) * (curr_centroid.getX() - curr_centroid.getY());
			temp_dist += (cluster.get(j).getX() - cluster.get(j).getY()) * (cluster.get(j).getX() - cluster.get(j).getY());
			temp_dist = Math.sqrt(temp_dist);
			total_dist += temp_dist;
		}
		return total_dist;
	}
	
	/**
	 * finds the coodinate closest to the current centroid
	 * @param curr_centroid - current centroid
	 * @param points - all the points to compare with
	 * @return - the closest point
	 */
	public static Position newPoint(Position curr_centroid, ArrayList<Position> points){
		double temp_dist = 100000;
		Position lowest = null;
		double lowest_dist = 100000;
		//find point with the lowest euclidean distance from the centroid
		for(int j = 0; j < points.size(); j++){
			temp_dist += (curr_centroid.getX() - curr_centroid.getY()) * (curr_centroid.getX() - curr_centroid.getY());
			temp_dist += (points.get(j).getX() - points.get(j).getY()) * (points.get(j).getX() - points.get(j).getY());
			temp_dist = Math.sqrt(temp_dist);
			//checks if the current distance is lower than the current lowest
			if(temp_dist < lowest_dist){
				lowest_dist = temp_dist;
				lowest = points.get(j);
			}
		}
		return lowest;
	}
	
	/**
	 * Object that holds the represents a coordinate
	 * @author Harry Longwell
	 *
	 */
	public static class Position{
		private double x;
		private double y;
		
		/**
		 * constructor method
		 * @param x - x coordinate
		 * @param y - y coordinate
		 */
		public Position(double x, double y){
			this.x = x;
			this.y = y;
		}
		
		/**
		 * returns the x coordinate
		 * @return
		 */
		public double getX(){
			return this.x;
		}
		
		/**
		 * returns the y coordinate
		 * @return
		 */
		public double getY(){
			return this.y;
		}
	}
}
