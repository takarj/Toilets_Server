import java.net.ServerSocket;
import java.net.Socket;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;

import java.util.Scanner;

public class Server{
    
    private static int PORT = 9991;
    
    public static void main(String[] args){
        Server s = new Server();
    }
    
    
    public Server(){
        try{
            ServerSocket serverSocket = new ServerSocket(PORT);
            Socket connectionSocket = serverSocket.accept();
            
            InputStream inputToServer = connectionSocket.getInputStream();
            OutputStream outputFromServer = connectionSocket.getOutputStream();
            
            Scanner scanner = new Scanner(inputToServer, "UTF-8");
            PrintWriter serverPrintOut = new PrintWriter(new OutputStreamWriter(outputFromServer, "UTF-8"), true);

            serverPrintOut.println("Hello! Enter Peace to exit.");
            
            boolean done = false;
            while(!done && scanner.hasNextLine()) {
                String line = scanner.nextLine();
                serverPrintOut.println("Echo from Server: " + line);
            
                if(line.toLowerCase().trim().equals("peace")) {
                    done = true;
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}