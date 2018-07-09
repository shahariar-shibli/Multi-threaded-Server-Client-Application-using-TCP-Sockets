package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/* @author Shibli */

public class Server 
{
    // Arraylist to store the online users
    public static ArrayList<ClientHandler> cLists = new ArrayList<ClientHandler>();
    
    public static void main(String[] args) 
    {
        try 
        {
            // Create Server Socket.
            ServerSocket sSocket = new ServerSocket(4444);
            
            //This thread will always keep checking
            ContinuousHandler obj = new ContinuousHandler();
            obj.start();
             
            // Continuously accept new client if get one.
            while( true )
            {
                Socket connectionSocket = sSocket.accept();
                IntroHandler newlogIn = new IntroHandler(connectionSocket);
                newlogIn.start();
            }
        } 
        catch (IOException ex)
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
