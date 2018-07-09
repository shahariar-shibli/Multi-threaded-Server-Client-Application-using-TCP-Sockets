package client;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/* @author Shibli */

public class ClientRequests extends Thread 
{
    public Socket cSocket;
    String task;
    
    //Constructor for the class.
    public ClientRequests(Socket cSocket, String task)
    {
        this.cSocket = cSocket;
        this.task = task;
    }
    
    //Run method for the thread.
    public void run()
    {
        if( "send".equals(task) )
        { 
            while( true )
            {
                try 
                {
                    //input & output stream with wrapper.
                    BufferedReader inFromClient = new BufferedReader( new InputStreamReader( System.in ) );
                    DataOutputStream outToServer = new DataOutputStream(cSocket.getOutputStream());
                    
                    //Show Task list.
                    System.out.println("Tasks you can do:");
                     System.out.println("1. Online User Lists");
                      System.out.println("2. Accept Request");
                       System.out.println("3. Send Request");
                        System.out.println("4. Unicast");
                         System.out.println("5. Multicast");
                          System.out.println("6. Broadcast");
                           System.out.println("7. Log out");
                         
                    //Command from Client.
                    String coomand = inFromClient.readLine();
                    System.out.println("");

                    String k = "";
                    if( coomand.equals("Online User Lists")) //online user lists
                    {
                        k = "Online User Lists" + '\n';
                        outToServer.writeBytes(k); // send command to server.
                    }
                    else if( coomand.equals("Accept Request") ) //accept request
                    {
                        k = "Accept Request" + '\n';
                        outToServer.writeBytes(k); // send command to server.
                        
                        System.out.print("Accept Request of the user: ");
                        String name = inFromClient.readLine();
                        System.out.println();
                        outToServer.writeBytes(name + '\n'); //send name to server.
 
                    }
                    else if( coomand.equals("Send Request") ) //send request
                    {
                        k = "Send Request" + '\n';
                        outToServer.writeBytes(k); //send command to the server.
                        
                        System.out.print("Send Request to the user: ");
                        String name = inFromClient.readLine();
                        System.out.println();
                        outToServer.writeBytes(name + '\n'); //send name to the server.
                        
                    }
                    else if( coomand.equals("Unicast") ) //unicast
                    {
                        k = "Unicast" + '\n';
                        outToServer.writeBytes(k);
                        
                        System.out.print("Recipient: ");
                        String name = inFromClient.readLine();
                        System.out.println("");
                        outToServer.writeBytes(name + '\n');
                        
                        System.out.print("Message: ");
                        String msg = inFromClient.readLine();
                        System.out.println("");
                        outToServer.writeBytes(msg + '\n');
                          
                    }
                    else if(coomand.equals("Multicast") ) //multicast
                    {
                        k = "Multicast" + '\n';
                        outToServer.writeBytes(k);
                        
                        System.out.print("Recipient: ");
                        String name = inFromClient.readLine();
                        System.out.println("");
                        outToServer.writeBytes(name + '\n');
                        
                        System.out.print("Message: ");
                        String msg = inFromClient.readLine();
                        System.out.println("");
                        outToServer.writeBytes(msg + '\n');
                        
                    }
                    else if(coomand.equals("Broadcast") ) //broadcast
                    {
                        k = "Broadcast" + '\n';
                        outToServer.writeBytes(k);
                        
                        System.out.print("Message: ");
                        String msg = inFromClient.readLine();
                        System.out.println("");
                        outToServer.writeBytes(msg + '\n');
                    }
                    else if( "Log out".equals( coomand ) )
                    {
                        k = "Log out" + '\n';
                        outToServer.writeBytes(k);
                        break;
                    }
                }
                catch (IOException ex)
                {
                    Logger.getLogger(ClientRequests.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else if( "receive".equals(task) )
        { 
            try
            {
                while( true )
                {
                    //input stream & object input stream for arraylist passing.
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));   
                    
                    // Server sends command.
                    String sentence = inFromServer.readLine();
                    
                    // Time to show the online user lists.
                    if( sentence.equals("Online User Lists") )
                    {
                        ObjectInputStream objectInput = new ObjectInputStream(cSocket.getInputStream());
                        try
                        {
                            Object object = objectInput.readObject();
                            ArrayList<String> listusers = new ArrayList<String>();
                            listusers =  (ArrayList<String>) object;
                            for( String i: listusers )System.out.println(i);
                        } 
                        catch (ClassNotFoundException ex)
                        {
                            Logger.getLogger(ClientRequests.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else System.out.println(sentence);
                }
            }
            catch (IOException ex) 
            {
               Logger.getLogger(ClientRequests.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    } 
}
