import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Performs Agglomerative clustering on a given data set
 * @author Harry Longwell
 *
 */
public class HW06_Longwell_Harry_Agglomeration {

	/**
	 * Main method
	 * @param args - command line argument
	 */
	public static void main(String[] args){
		//Create FileReader to read in customers and their shopping cart
				FileReader fr = null;
				try {
					fr = new FileReader("HW_09_SHOPPING_CART_V037.csv");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					System.err.println("Failed file read");
				}
				BufferedReader br = null;
				
				//storage of clusters that are created
				ArrayList<Cluster> clusters = new ArrayList<Cluster>();
				
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
				
				//Reads file until end of file, creating Customers
				//,storing them each in their own cluster initially,
				// and storing those clusters in an arraylist
				try {
					while((line = br.readLine()) != null){
						String[] tokens = line.split(",");
						Customer cust = new Customer(Integer.parseInt(tokens[0]),
													Integer.parseInt(tokens[1]),
													Integer.parseInt(tokens[2]),
													Integer.parseInt(tokens[3]),
													Integer.parseInt(tokens[4]),
													Integer.parseInt(tokens[5]),
													Integer.parseInt(tokens[6]),
													Integer.parseInt(tokens[7]),
													Integer.parseInt(tokens[8]),
													Integer.parseInt(tokens[9]),
													Integer.parseInt(tokens[10]),
													Integer.parseInt(tokens[11]),
													Integer.parseInt(tokens[12]));
						Cluster clust = new Cluster();
						clust.addCustomer(cust);
						clusters.add(clust);
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
				
				//Perform Agglomerative clustering
				while(clusters.size() != 3){
					//finds the most similar cluster to the current cluster
					double tempDist = 0, lowestDist = 1000000;
					Cluster lowestClust = null;
					for(int i = 1; i < clusters.size(); i++){
						tempDist = calcDist(clusters.get(0), clusters.get(i));
						//checks if the current distance is lower than the lowest distance
						//if it is, sets current distance as new lowest
						if(tempDist <= lowestDist){
							lowestDist = tempDist;
							lowestClust = clusters.get(i);
						}
					}
					//print size of smaller cluster
					if(clusters.get(0).customer.size() > lowestClust.customer.size()){
						System.out.println("Smaller cluster: " + lowestClust.customer.size());
					}
					else{
						System.out.println("Smaller cluster: " + clusters.get(0).customer.size());
					}
					
					//Merge the clusters, by adding all their customers into one cluster
					Cluster merge = new Cluster();
					for(int i = 0; i < clusters.get(0).customer.size(); i++){
						merge.addCustomer(clusters.get(0).customer.get(i));
					}
					for(int i = 0; i < lowestClust.customer.size(); i++){
						merge.addCustomer(lowestClust.customer.get(i));
					}
					//remove the merged clusters
					clusters.remove(0);
					clusters.remove(lowestClust);
					//add the new merged cluster
					clusters.add(merge);
				}
				//print out the clusters, the ids they contain, and their most prominent attribute
				for(int i = 0; i < clusters.size(); i++){
					System.out.println("Cluster " + i);
					System.out.println("Cluster feature: " + clusters.get(i).highFeature());
					for(int j = 0; j < clusters.get(i).customer.size(); j++){
						System.out.print(clusters.get(i).customer.get(j).id + ", ");
					}
					System.out.println();
				}
	}
	
	/**
	 *Calculates and returns the Euclidean Distance between two clusters
	 * @param clust1 - cluster 1
	 * @param clust2 - cluster 2
	 * @return - the euclidean distance between the two clusters
	 */
	public static double calcDist(Cluster clust1, Cluster clust2){
		double[] temp = new double[11];
		for(int i = 0; i < temp.length; i++){
			temp[i] = (clust1.getCOM(i) - clust2.getCOM(i)) * (clust1.getCOM(i) - clust2.getCOM(i));
		}
		double sum = 0;
		for(int i = 0; i < temp.length; i++){
			sum += temp[i];
		}
		return Math.sqrt(sum);
	}

	/**
	 * class that represent a customer
	 * @author Harry Longwell
	 *
	 */
	public static class Customer{
		int id = 0;
		int[] items = new int[12];
		
		/**
		 * custructor of class
		 * @param id - id of customer
		 * @param milk - milk quantity
		 * @param pet - pet food quantity
		 * @param veg - veggie quantity
		 * @param cereal - cereal quantity
		 * @param nut - nuts quantity
		 * @param rice - rice quantity
		 * @param egg - egg quantity
		 * @param yogurt - yogurt quantity
		 * @param chip - chips quantity
		 * @param beer - beer quantity
		 * @param fruit - fruit quantity
		 */
		public Customer(int id, int milk, int pet, int veg, int cereal, int nut, int rice, int meat, int egg, int yogurt, int chip, int beer, int fruit){
			this.id = id;
			this.items[0] = milk;
			this.items[1] = pet;
			this.items[2] = veg;
			this.items[3] = cereal;
			this.items[4] = nut;
			this.items[5] = rice;
			this.items[6] = meat;
			this.items[7] = egg;
			this.items[8] = yogurt;
			this.items[9] = chip;
			this.items[10] = beer;
			this.items[11] = fruit;
		}
		
		/**
		 * returns the value at the given index
		 * @param index - index to retrieve from
		 * @return - the value at the index
		 */
		public int getItem(int index){
			return items[index];
		}
	}
	
	/**
	 * class that represent a cluster
	 * @author Harry Longwell
	 *
	 */
	public static class Cluster{
		//arraylist that holds all the customer in the cluster
		ArrayList<Customer> customer = new ArrayList<Customer>();
		//holds the average of all customer item values
		double[] COM = new double[12];
		
		/**
		 * cluster constructor
		 */
		public Cluster(){
			
		}
		
		/**
		 * adds a customer to the cluster and recalculates center of mass
		 * @param cust - customer to add
		 */
		public void addCustomer(Customer cust){
			this.customer.add(cust);
			this.calc_COM();
		}
		
		/**
		 * calculates the center of mass of the cluster
		 */
		public void calc_COM(){
			double[] temp = new double[12];
			//gets the sum of each attribute for all the customers in the cluster
			for(int i = 0; i < customer.size(); i++){
				for(int j = 0; j < temp.length; j++){
					temp[j] = temp[j] + (double)(customer.get(i).getItem(j));
				}				
			}
			//averages all values in the array and places them in the COM array
			for(int i = 0; i < COM.length; i++){
				COM[i] = temp[i] / customer.size();
			}
		}
		
		/**
		 * return the value of center of mass at the index
		 * @param index - index to retrieve from
		 * @return - value at the index
		 */
		public double getCOM(int index){
			return COM[index];
		}
		
		/**
		 * returns the most prominent feature
		 * @return - the index that holds the most prominent feature
		 */
		public int highFeature(){
			double highest = 0;
			int index = 0;
			//loops through center of mass, keeping track of the highest value
			for(int i = 0; i < COM.length; i++){
				if(COM[i] > highest){
					highest = COM[i];
					index = i;
				}
			}
			return index;
		}
	}
}
