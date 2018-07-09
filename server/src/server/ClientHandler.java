package server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static server.Server.cLists;

/* @author Shibli */

public class ClientHandler extends Thread
{
    public  Socket cSocket;
    public  String cName;
    public  String uname;
    
    //Constructor for ClientHandler class.
    public ClientHandler( Socket cSocket, String cName, String uname )
    {
        this.cSocket = cSocket;
        this.cName = cName;
        this.uname = uname;
    }
    // Mehthod to write the accepted friends name in text file
    void addFriends( String frdname )throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter("friends.txt", true));
        writer.write(uname + ":" + frdname );
        writer.newLine();
        writer.flush();
        writer.close();
    }
    
    //Method to store friend requests
    void addFrinedRequest( String frdname )throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter("requests.txt", true));
        writer.write("send:" + uname + ":" + frdname );
        writer.newLine();
        writer.flush();
        writer.close();
    }
    
    //Method to send Confirmation
    void requestConfirmation( String name ) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter("notifications.txt", true));
        writer.write("send:" + name + ":" + uname + " is your friend now.");
        writer.newLine();
        writer.flush();
        writer.close();
    }
    
    //Method for messaging 
     void giveMSG( String name, String msg )throws IOException
     {
        BufferedWriter writer = new BufferedWriter(new FileWriter("message.txt", true));
        StringTokenizer tokens = new StringTokenizer(name,":");
        while (tokens.hasMoreTokens()) 
        {
            String next = tokens.nextToken();
            if( !checkFriendship(uname, next ) )continue;
            writer.write("send:" + uname + ":" + next + ":" + msg  );
            writer.newLine(); 
        } 
        writer.flush();
        writer.close();
    }
    
     //Method for Checking Friendship
    boolean checkFriendship( String user1, String user2 )throws IOException
    {       
        FileReader inputFile = null;
        inputFile = new FileReader("friends.txt");
        Scanner parser = new Scanner(inputFile);
        
        while (parser.hasNextLine())
        {
            String line = parser.nextLine();
            if( "".equals(line) )continue;
            StringTokenizer tokens = new StringTokenizer( line, ":" );
            String f = tokens.nextToken();           
            String s = tokens.nextToken(); 
            if( f.equals(user1) && s.equals(user2) )return true;   
            else if( s.equals(user1) && f.equals(user2) )return true;
        }
        return false;
    }
    // Method for broadcast messaging.
    void broadcasting(String msg )throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter("message.txt", true));
        FileReader inputFile = null;
        inputFile = new FileReader("friends.txt");
        Scanner parser = new Scanner(inputFile);
        
        while (parser.hasNextLine())
        {
            String line = parser.nextLine();
            if( "".equals(line) )continue;
            StringTokenizer tokens = new StringTokenizer( line, ":" );
            String f = tokens.nextToken();           
            String s = tokens.nextToken(); 
            if( f.equals(uname) )
            {
                writer.write("send:" + f + ":" + s + ":" + msg  );
                writer.newLine();                
            }
            else if( s.equals(uname) )
            {
                writer.write("send:" + s + ":" + f + ":" + msg  );
                writer.newLine();
            }
        }
        writer.flush();
        writer.close();
    }
    
    public void delUser()
    {
        for( ClientHandler i: cLists )
        {
            if( i.uname.equals(uname) )
            {
                cLists.remove(i);
                break;
            }
        }
    }
    
    //Run Method for the thread.
    public void run()
    {      
        try
        {
           while( true )
           {
               DataOutputStream outToClient = new DataOutputStream(cSocket.getOutputStream() );
               BufferedReader inFromClient = new BufferedReader( new InputStreamReader(cSocket.getInputStream()));
               String cmd = inFromClient.readLine();

               if( "Online User Lists".equals(cmd))
               {
                   outToClient.writeBytes("Online User Lists" + '\n' );
                   ArrayList<String> allUsers = new ArrayList<String>();
                   for( ClientHandler i: cLists )
                   {
                       String names =  "Name : " + i.cName + ", UserName : " + i.uname ;                    
                       allUsers.add( names );
                   }     
                   ObjectOutputStream objectOutput = new ObjectOutputStream(cSocket.getOutputStream());
                   objectOutput.writeObject(allUsers);
               }
               else if("Accept Request".equals(cmd) )
               {
                   String name = inFromClient.readLine();              
                   addFriends( name );
                   outToClient.writeBytes("New Friendship with " + name + '\n' );
                   requestConfirmation( name );
               }
               else if( "Send Request".equals(cmd) )
               {
                   String name = inFromClient.readLine();
                   addFrinedRequest( name );
               }
               else if( "Unicast".equals(cmd) )
               {
                   String user = inFromClient.readLine();
                   String msg = inFromClient.readLine();
                   giveMSG( user, msg );
               }
               else if( "Multicast".equals(cmd) )
               {
                   String user = inFromClient.readLine();
                   String msg = inFromClient.readLine();               
                   giveMSG( user, msg );             
               }
               else if( "Broadcast".equals(cmd) )
               {
                   String msg = inFromClient.readLine();
                   broadcasting( msg );
               }
               else if( "Log out".equals(cmd) )
                { 
                    delUser();
                    outToClient.writeBytes("Logged out of the system."  + '\n' );   
                    break;  
                }
             }
           } 
        catch (IOException ex)
        {
               Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}