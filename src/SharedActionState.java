import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

public class SharedActionState {

    private SharedActionState mySharedObj;
    private String myThreadName;
    private double mySharedVariable;

    private double account_a_balance;
    private double account_b_balance;
    private double account_c_balance;

    private boolean account_a_lock = false;
    private boolean account_b_lock = false;
    private boolean account_c_lock = false;


    //	private boolean accessing=false; // true a thread has a lock, false otherwise
    private int threadsWaiting = 0; // number of waiting writers

// Constructor	

    SharedActionState(double account_a_start, double account_b_start, double account_c_start) {
//		mySharedVariable = SharedVariable;
        account_a_balance = account_a_start;
        account_b_balance = account_b_start;
        account_c_balance = account_c_start;
    }

//Attempt to aquire a lock

    public synchronized void acquireLock(String accountNum) throws InterruptedException {
        Thread me = Thread.currentThread(); // get a ref to the current thread
        System.out.println(me.getName() + " is attempting to acquire a lock!");
        ++threadsWaiting;

        switch (accountNum) {
            case "a":
                while (account_a_lock) {
                    System.out.println(me.getName() + " - waiting to get a lock as someone else is accessing.");
                    wait();
                }
                --threadsWaiting;
                account_a_lock = true;
                System.out.println(me.getName() + " got a lock!");
                break;
            case "b":
                while (account_b_lock) {
                    System.out.println(me.getName() + " - waiting to get a lock as someone else is accessing.");
                    wait();
                }
                --threadsWaiting;
                account_b_lock = true;
                System.out.println(me.getName() + " got a lock!");
                break;
            case "c":
                while (account_c_lock) {
                    System.out.println(me.getName() + " - waiting to get a lock as someone else is accessing.");
                    wait();
                }
                --threadsWaiting;
                account_c_lock = true;
                System.out.println(me.getName() + " got a lock!");
                break;
            default:
                System.out.println("Invalid account number");
                break;
        }
    }

    public synchronized void releaseLock(String accountNum) {
        //release the lock and tell everyone
        switch (accountNum) {
            case "a":
                account_a_lock = false;
                break;
            case "b":
                account_b_lock = false;
                break;
            case "c":
                account_c_lock = false;
                break;
            default:
                System.out.println("Invalid account number");
                break;
        }

//		      accessing = false;
        notifyAll();
        Thread me = Thread.currentThread(); // get a ref to the current thread
        System.out.println(me.getName() + " released a lock!");
    }

    /* Add Money Method */
    public synchronized String addMoney(String accountNum, double amount){
        String theOutput = null;

        switch (accountNum) {
            case "a":
                account_a_balance += amount;
                theOutput = "Account a balance: " + account_a_balance;
                break;
            case "b":
                account_b_balance += amount;
                theOutput = "Account b balance: " + account_b_balance;
                break;
            case "c":
                account_c_balance += amount;
                theOutput = "Account c balance: " + account_c_balance;
                break;
            default:
                System.out.println("Invalid account number");
                break;
        }

        return theOutput;
    }

    public synchronized String subtractMoney(String accountNum, double amount){
        String theOutput = null;

        switch (accountNum) {
            case "a":
                account_a_balance -= amount;
                theOutput = "Account a balance: " + account_a_balance;
                break;
            case "b":
                account_b_balance -= amount;
                theOutput = "Account b balance: " + account_b_balance;
                break;
            case "c":
                account_c_balance -= amount;
                theOutput = "Account c balance: " + account_c_balance;
                break;
            default:
                System.out.println("Invalid account number");
                break;
        }

        return theOutput;
    }

    /* Transfer Money Method */
    public synchronized String transferMoney(String[] accounts, double amount){
        String theOutput = null;
        String sending_account = accounts[0];
        String receiving_account = accounts[1];
        

        switch (sending_account) {
            case "a":
                account_a_balance -= amount;
                break;
            case "b":
                account_b_balance -= amount;
                break;
            case "c":
                account_c_balance -= amount;
                break;
            default:
                theOutput = "Invalid sending account number";
                break;
        }

        switch (receiving_account) {
            case "a":
                account_a_balance += amount;
                theOutput = "Account a balance: " + account_a_balance;
                break;
            case "b":
                account_b_balance += amount;
                theOutput = "Account b balance: " + account_b_balance;
                break;
            case "c":
                account_c_balance += amount;
                theOutput = "Account c balance: " + account_c_balance;
                break;
            default:
                System.out.println("Invalid recieving account number");
                break;
        }

        return theOutput;
    }


    /* The processInput method */

    public synchronized String processInput(String command, String[] targets, String amount) {
        String theOutput = null;

        if(command.equals("Add_money")){
            theOutput = addMoney(targets[0], Double.parseDouble(amount));
        }else if (command.equals("Subtract_money")){
            theOutput = subtractMoney(targets[0], Double.parseDouble(amount));
        }else if (command.equals("Transfer_money")) {
            theOutput = transferMoney(targets, Double.parseDouble(amount));
        }else{
            theOutput = "Invalid command";
        }

        //Return the output message to the ActionServer
        System.out.println(theOutput);
        return theOutput;
    }
}

