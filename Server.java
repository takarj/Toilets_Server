import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;

import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Server{
    
    private static int PORT = 9991;
    
    public static void main(String[] args){
        Server s = new Server();
        s.start();
    }
    
    class Handler implements Runnable{
        Socket socket;
        DatabaseHandler dbHandler;

        public Handler(Socket s){
            socket = s;
            dbHandler = new DatabaseHandler();
        }

        public void run(){
            try{
                String client;
                client = socket.getInetAddress().toString();
                System.out.println("new client:" + client);
                
                InputStream inputToServer = socket.getInputStream();
                OutputStream outputFromServer = socket.getOutputStream();
                
                Scanner scanner = new Scanner(inputToServer, "UTF-8");
                PrintWriter serverPrintOut = new PrintWriter(new OutputStreamWriter(outputFromServer, "UTF-8"), true);
    
                serverPrintOut.println("Hello! Enter disconnect to exit.");
                
                boolean done = false;
                while(!done && scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    
                    if(line.equals("disconnect")) {
                        done = true;
                        serverPrintOut.println("BYEBYE");
                        socket.close();
                    }
                    
                    if(line.startsWith("getToilets")){     // getToilets?val%latTop?val%latBot?val%lngLeft?val%lngRight
                        //serverPrintOut.println("Searching Toilets");
                        System.out.println("Client: " + client + " issued request: " + line);
                        try{
                            String[] params = line.split("\\?val\\%");
                            //System.out.println(params[1]);
                            ArrayList<String[]> toilets = dbHandler.getToiletsInRange(
                                Double.parseDouble(params[1]), Double.parseDouble(params[2]), 
                                Double.parseDouble(params[3]), Double.parseDouble(params[4]));
                            for(String[] t : toilets){
                                serverPrintOut.print("toilet");
                                for(int i = 0; i < t.length; i++){
                                    serverPrintOut.print("?val%" +  t[i]);
                                }
                                serverPrintOut.println();
                            }
                            serverPrintOut.println("done");
                        }catch(Exception e){
                            serverPrintOut.println("bad request");
                        }
                    }
                    
                    if(line.startsWith("submit")){      //submit?val%title?val%lat?val%lng?val%description?val%rating?val%price?val%currency
                        System.out.println("Client: " + client + " issued request: " + line);
                        try{
                            String[] params = line.split("\\?val\\%");
                            
                            dbHandler.insert(params[1], Double.parseDouble(params[2]), Double.parseDouble(params[2]), params[3], Float.parseFloat(params[4]), Float.parseFloat(params[5]), params[6]);
                        }catch(Exception e){
                            serverPrintOut.println("bad request");
                        }
                    }
                    
                    //serverPrintOut.println("Echo from Server: " + line);
                }
            }
            catch(IOException e){}
        }  
    }
    
    
    public Server(){
        
    }
    
    public void start(){
        try{
            final ExecutorService service = Executors.newCachedThreadPool();
            ServerSocket serversock = new ServerSocket(PORT);
            while(true){
                Socket socket = serversock.accept();
                service.submit(new Handler(socket));
            }
        }catch(IOException e){
            
        }
    }
}