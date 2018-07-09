package client;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/* @author Shibli */

public class Client 
{    
    // Method for user registration 
    public static boolean signUp( String name, String uname, String pass )
    {
        try 
        {
            // Create Client Socket.
            Socket cSocket = new Socket( "localhost", 4444 );
            
            //input & output stream with wrapper.
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
            DataOutputStream outToServer = new DataOutputStream(cSocket.getOutputStream()); 
            
            //Send to Server the Command for Registration.
            outToServer.writeBytes("signup" + '\n');
            
            //Convert the name,username & password into a string & send to server.
            String full_msg =  uname + ":" + pass + ":" + name;
            outToServer.writeBytes(full_msg + '\n');
            
            // Acknowledgement from Server.
            String ack = inFromServer.readLine();
            
            // If acknowledgement matched.
            if( ack.equals("ok") )
            {
                // Print Success message.
                System.out.println("Registration Successful.\n");
                
                // Two thread operations: Send & Receive for one user. 
                ClientRequests client1 = new ClientRequests(cSocket, "send");
                client1.start();
                ClientRequests client2 = new ClientRequests(cSocket, "receive" );
                client2.start();

                return true;
            }
            else
            {
                // Print Unsuccess message.
                System.out.println("Unsuccessful. Please Try Again.\n");
                return false;
            }
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    // Method for user Login.
    public static boolean signIn( String uname, String pass )
    {
        try 
        {
            // Create Client Socket.
            Socket clientSocket = new Socket( "localhost", 4444 );
            
            //input & output stream with wrapper.
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            
            //Send to Server the command for Login.
            outToServer.writeBytes("login" + '\n');
            
            //Convert user name & password into a String and send to server.
            String full_msg = uname + ":" + pass;
            outToServer.writeBytes(full_msg + '\n');
            
            // Acknowledgement from Server.
            String ack = inFromServer.readLine();
            
            // If Acknwledgement Matched.
            if( ack.equals("ok") )
            {
                // Print Success message.
                System.out.println("Login Successful.\n");
                
                // Two thread operations: Send & Receive for one user. 
                ClientRequests client1 = new ClientRequests(clientSocket, "send" );
                client1.start();
                ClientRequests client2 = new ClientRequests(clientSocket, "receive" );
                client2.start();

                return true; 
            }
            else 
            {
                //Print Unsuccessful Message.
                System.out.println("Login Unsuccessful. Try Again\n");
                return false;
            }
        } 
        catch (IOException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static void main(String[] args)
    {
        //User Prompt.
        System.out.println( "Login? type login, or Register? type signup.\n");
        
        // Scanner class to string input.
        Scanner sc = new Scanner( System.in  );
        String ss  = sc.next();
        
        //For login purpose.
        if( ss.equals("login") )
        {
            System.out.print("Your User Name: ");
            String uname = sc.next();
            System.out.println();
            System.out.print("Your Password: ");
            String pass = sc.next();
            System.out.println();
            
            //If fails continue to scan username and password.
            while( !signIn(uname, pass ) )
            {
                System.out.print("Your User Name: ");
                uname = sc.next();
                System.out.println();
                System.out.print("Your Password: ");
                pass = sc.next();
                System.out.println();
            }
        }
        else //For registration purpose.
        {
            
            System.out.print("Your Full Name: ");
            sc.nextLine();
            String name = sc.nextLine();
            System.out.println();
            System.out.print("Your User Name: ");
            String uname = sc.next();
            System.out.println();
            System.out.print("Your Password: ");
            String pass = sc.next();
            System.out.println();
            
            //If fails continue to scan username and password.
            while( !signUp(name, uname, pass ) )
            {
                System.out.print("Your Full Name: ");
                sc.nextLine();
                name = sc.nextLine();
                System.out.println();
                System.out.print("Your UserName: ");
                uname = sc.next();
                System.out.println();
                System.out.print("Your Password: ");
                pass = sc.next();
                System.out.println();
            }
        }    
    }
}