
import java.net.*;
import java.io.*;
import java.util.Arrays;


public class ActionServerThread extends Thread {

	
  private Socket actionSocket = null;
  private SharedActionState mySharedActionStateObject;
  private String myActionServerThreadName;
  private double mySharedVariable;
   
  //Setup the thread
  	public ActionServerThread(Socket actionSocket, String ActionServerThreadName, SharedActionState SharedObject) {
	
//	  super(ActionServerThreadName);
	  this.actionSocket = actionSocket;
	  mySharedActionStateObject = SharedObject;
	  myActionServerThreadName = ActionServerThreadName;
	}

  public void run() {
    try {
      System.out.println(myActionServerThreadName + "initialising.");
      PrintWriter out = new PrintWriter(actionSocket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(actionSocket.getInputStream()));
      String inputLine, outputLine;

      while ((inputLine = in.readLine()) != null) {
    	  // Get a lock first
    	  try {
              String[] command = inputLine.split("\\(");
              String[] params = command[1].split(",");

              // This nightmare line removes the last ) closing parenthesis from the last parameter
              params[params.length - 1] = params[params.length - 1].substring(0, params[params.length - 1].length() - 1);

              System.out.println("Command: " + command[0]);
              System.out.println("Params: " + Arrays.toString(params));

              switch (command[0]){
                  case "Add_money":
                        mySharedActionStateObject.acquireLock(params[0]);
                        outputLine = mySharedActionStateObject.processInput("Add_money", new String[] {params[0]}, params[1]);
                        out.println(outputLine);
                        mySharedActionStateObject.releaseLock(params[0]);
                        break;
                  case "Subtract_money":
                      mySharedActionStateObject.acquireLock(params[0]);
                        outputLine = mySharedActionStateObject.processInput("Subtract_money", new String[] {params[0]}, params[1]);
                        out.println(outputLine);
                        mySharedActionStateObject.releaseLock(params[0]);
                        break;
                  case "Transfer_money":
                        mySharedActionStateObject.acquireLock(params[0]);
                        mySharedActionStateObject.acquireLock(params[1]);
                        outputLine = mySharedActionStateObject.processInput("Transfer_money", new String[] {params[0], params[1]}, params[2]);
                        out.println(outputLine);
                        mySharedActionStateObject.releaseLock(params[0]);
                        mySharedActionStateObject.releaseLock(params[1]);
                        break;
              }
    	  }
    	  catch(InterruptedException e) {
    		  System.err.println("Failed to get lock when reading:"+e);
    	  }
      }

       out.close();
       in.close();
       actionSocket.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}