import java.util.*;
import java.net.*;
import java.io.*;

class GossipData implements Serializable{
  int nodeNumber;
  int average;
  int highValue;
  int lowValue;
  int size;
  String userString;

  public GossipData(int nodeNumber){
  	Random rand = new Random();
  	int randomInt1 = rand.nextInt(99);
  	int randomInt2 = rand.nextInt(99);
  	this.lowValue = (randomInt1<randomInt2)?randomInt1:randomInt2;
  	this.highValue = (randomInt1+randomInt2) - this.lowValue;//subtracting lowValue From totalValue to get HighValue
  	this.size = 1;
  	this.userString = "";
  	this.nodeNumber = nodeNumber;
  	this.average = 0;
  }


}//Gossip Class end



public class Gossip{

	public static int nodeNumber;
	public static int fixedPortNo = 48100;
    

	
		
	public static int processDatagramThread = 1;
	public static int listenConsoleInputThread = 1;

    public static DatagramSocket ds;
    public static GossipData gp;

	public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) throws Exception {

		//When The Program Runs For The First Time..
		//Validation
		if(args.length < 1 || args.length >2){
			System.out.println("INVALID NO OF ARGUMENTS.. \n USAGE: <nodeNumber>");
			return;
		}//if end
		else{
			nodeNumber = Integer.parseInt(args[0]);
			if(nodeNumber<0 || nodeNumber > 999){System.out.println("INVALID NODE NUMBER (must be 0 to 999)");return;}//if end
			
			//Updating the fixedPortNo based on the nodenumber give by the user.
			fixedPortNo = fixedPortNo+nodeNumber;
            System.out.println(fixedPortNo);
			ds = new DatagramSocket(fixedPortNo);
			System.out.println("Gossip Node Sarted.. Listening on port:"+(fixedPortNo));
			System.out.println("Node Number ->"+nodeNumber);
			System.out.println("Commands Can Now Be Enterred...");
			gp = new GossipData(nodeNumber);

		}//else end
		

		//Thread Which Processes Datagrams and Processes Them. 
		Runnable threadDatagramProcess = new Runnable(){
			public void run(){
			try{
                while(processDatagramThread==1){
            
                    byte[] receivedBytes = new byte[2048];
                    DatagramPacket packetReceived = new DatagramPacket(receivedBytes,receivedBytes.length);
                    ds.receive(packetReceived);
                    //deserializing the received object..
                    ByteArrayInputStream byteArIpStream = new ByteArrayInputStream(packetReceived.getData());
                    ObjectInputStream objInputStream = new ObjectInputStream(byteArIpStream);

                    GossipData deserializedObject = (GossipData) objInputStream.readObject();

                    String userStr = deserializedObject.userString;
                    
                    System.out.println("RECEIVED COMMAND "+userStr);
                }//while end                    
            }catch(Exception e){}
			
			}//run end
		};//threadDatagramProcess end
		


		//Thread Which Listens For Console Input
		Runnable threadConsoleInput = new Runnable(){
			public void run(){
                try{
                    while(listenConsoleInputThread==1){
                        String command = reader.readLine();
                        handleCommands(command);

                    }//while end
                }catch(Exception e){}

			}//run end

		};//threadConsoleInput

        new Thread(threadDatagramProcess).start();
        new Thread(threadConsoleInput).start();

	}//main method end


	private static void handleCommands(String command){
        try{
                    String[] parts = command.split(" ");
        String cmd = parts[0];
        switch (cmd) {
            case "t":
                displayCommands();
                break;
            case "l":
                displayLocalValues(nodeNumber);
                break;
            case "p":
                pingNeighbors();
                break;
            case "m":
                calculateMinMax();
                break;
            case "a":
                calculateAverage();
                break;
            case "z":
                calculateNetworkSize();
                break;
            case "v":
                createNewValues();
                break;
            case "d":
                deleteNode();
                break;
            case "k":
                killNetwork();
                break;
            case "y":
                displayCycleNumber();
                break;
            case "n":
                setGossipMessages(parts);
                break;
            default:
                System.out.println("Invalid command. Type 't' to see available commands.");
                break;
        }//switch end
        }catch(Exception e){}


	}//handleCommands end


	private static void displayCommands(){
        System.out.println("Available commands:");
        System.out.println("t - Displays all the commands");
        System.out.println("l - Displays the local values of this node");
        System.out.println("p - Ping neighboring nodes");
        System.out.println("m - Display the minimum and maximum values in the network");
        System.out.println("a - Calculate and display the average value of the nodes");
        System.out.println("z - Calculate and display the current network size");
        System.out.println("v - Create new random local values throughout the network");
        System.out.println("d - Delete the current node");
        System.out.println("k - Kill the entire network");
        System.out.println("y - Display the number of cycles since the beginning of time on every node");
        System.out.println("n <numMessages> - Set the number of gossip messages for the entire network");
	}//displayCommands end

    public static void displayLocalValues(int nodeNumber) {
        // Implement logic to display local values
        System.out.println("Local values for Node " + nodeNumber + ":");
        
    }//displayLocalValues end

    public static void pingNeighbors() throws Exception {
    	//Serialize Object 
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	ObjectOutputStream oos = new ObjectOutputStream(baos);
    	oos.writeObject(gp);
    	oos.close();
        sendData(baos.toByteArray(),fixedPortNo+1);
        sendData(baos.toByteArray(),fixedPortNo-1);
    }//pingNeighbors end

    public static void calculateMinMax() {
        int min = gp.lowValue;
        int max = gp.highValue;
        System.out.println("Minimum value: " + min);
        System.out.println("Maximum value: " + max);
    }

    public static void calculateAverage() {
        gp.average = (gp.lowValue + gp.highValue) / 2;
        System.out.println("Average value: " + gp.average);
    }

    public static void calculateNetworkSize() {
        // Implement logic to calculate and display network size
    }

    public static void createNewValues() {
        Random rand = new Random();
        int randomInt1 = rand.nextInt(99);
        int randomInt2 = rand.nextInt(99);
        gp.lowValue = (randomInt1 < randomInt2) ? randomInt1 : randomInt2;
        gp.highValue = (randomInt1 + randomInt2) - gp.lowValue;
        System.out.println("New values generated: Low: " + gp.lowValue + ", High: " + gp.highValue);
    }

    public static void deleteNode() {
        System.out.println("Node " + nodeNumber + " deleted.");
        // Implement logic to gracefully delete the current node
    }

    public static void killNetwork() {
        System.out.println("Network terminated.");
        // Implement logic to kill the entire network
    }


    public static void displayCycleNumber() {
        // Implement logic to display cycle number
    }//displayCycleNumber end

    public static void setGossipMessages(String[] parts) {
 
    }//setGossipMessages end


    public static void sendData(byte[] dataToSend,int portNoToBeSent) throws Exception{
    	
        InetAddress InetAddr = InetAddress.getByName("127.0.0.1");
        DatagramPacket dp = new DatagramPacket(dataToSend,dataToSend.length,InetAddr,portNoToBeSent);
    	ds.send(dp);
    }//sendData end


}//Gossip class end