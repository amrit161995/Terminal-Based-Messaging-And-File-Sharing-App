import java.io.*; 
import java.net.*; 
import java.util.Scanner; 
import java.io.File;
// Client class 
public class Client  
{ 
    static int port = 0;
    static String ip_port = "";
    static String ipa = "";
        
    public static void send_file(Socket s,String filename) throws IOException{
        BufferedOutputStream outToClient = new BufferedOutputStream(s.getOutputStream());
        if (outToClient != null) {
            File myFile = new File(filename);
            byte[] mybytearray = new byte[(int) myFile.length()];

            FileInputStream fis = null;

            try {
                fis = new FileInputStream(myFile);
            } catch (FileNotFoundException ex) {
                // Do exception handling
            }
            BufferedInputStream bis = new BufferedInputStream(fis);

            try {
                bis.read(mybytearray, 0, mybytearray.length);
                outToClient.write(mybytearray, 0, mybytearray.length);
                outToClient.flush();
                // outToClient.close();
                // s.close();

                // File sent, exit the main method
                return;
            } catch (IOException ex) {
                // Do exception handling
            }
        }
    }

    public static void send_udp(String c_ip,String s_ip,int c_port,int s_port,String filepath) throws IOException{
        DatagramSocket send = new DatagramSocket(c_port,InetAddress.getByName(c_ip));
    
        File f2 = new File(filepath);
        if(!f2.exists() || f2.isDirectory())
            return;
        FileInputStream bis = new FileInputStream(f2);
        byte[] buf = new byte[63*1024];
        int len;
        
        DatagramPacket pkg = new DatagramPacket(buf, buf.length,InetAddress.getByName(s_ip),s_port);
        while((len=bis.read(buf))!=-1)
        {
            pkg.setLength(len);
            send.send(pkg);
        }
        buf = "end".getBytes();
        DatagramPacket endpkg = new DatagramPacket(buf, buf.length,InetAddress.getByName(s_ip),s_port);
        System.out.println("Send the file.");
        send.send(endpkg);
        bis.close();
        send.close();
    }
    public static void receive_file(Socket s,String filename,long size){
        byte[] aByte = new byte[1024]; // 100
        int bytesRead;

        // Socket clientSocket = null;
        InputStream is = null;

        try {
            // clientSocket = new Socket( serverIP , serverPort );
            is = s.getInputStream();
        } catch (IOException ex) {
            // Do exception handling
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (is != null) {

            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            try {
                fos = new FileOutputStream(filename);
                // bos = new BufferedOutputStream(fos);
                // bytesRead = is.read(aByte, 0, aByte.length);
                // size++;
                while (size>0 && (bytesRead = is.read(aByte, 0,(int) Math.min(aByte.length,size)))!=-1) { // size>100
                        fos.write(aByte,0,bytesRead);
                        // bytesRead = is.read(aByte);
                        size = size - bytesRead; // size - 100
                }
                // System.out.println(bytesRead);
                // baos.write(aByte);
                // bos.write(baos.toByteArray());
                fos.close();
                // System.out.println("x");
                // bos.flush();
                // System.out.println("y");
                // bos.close();
                // System.out.println("z");
                // clientSocket.close();
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
                // Do exception handling
            }
        }
    }

    public static void main(String[] args) throws IOException  
    { 
        if(args.length == 0){
            port = 5000 + (int)(Math.random() * ((20000 - 5000) + 1));
            InetAddress localhost = InetAddress.getLocalHost(); 
            ipa = (localhost.getHostAddress()).trim();
            try{
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("google.com", 80));
                ipa = socket.getLocalAddress().toString();
                ipa = ipa.replace("/", "");
                socket.close();
            }
            catch(Exception e){
                
            }
            ip_port = ipa + ":" + Integer.toString(port);
        }
        else{
            // ipa = 
            ip_port = args[0];
            ipa = ip_port.split("\\:")[0];
            if(ip_port.split("\\:").length >=2)
                port = Integer.parseInt(ip_port.split("\\:")[1]);
            else{
                port = 5000 + (int)(Math.random() * ((20000 - 5000) + 1));
                ip_port = ip_port + ":" + Integer.toString(port);
            }
        }
             
        Thread thread1 = new Thread() {
            public void run() {
                try
                { 
                    Scanner scn = new Scanner(System.in); 
                    
                    // getting localhost ip 
                    String serverip = "10.1.33.196";
                    InetAddress ip = InetAddress.getByName(serverip); 
            
                    // establish the connection with server port 5056 
                    int serv_port = 5056;
                    Socket s = new Socket(ip, serv_port); 
                    String current_user = "";
                    // obtaining input and out streams 
                    DataInputStream dis = new DataInputStream(s.getInputStream()); 
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
                    
                    dos.writeUTF(ip_port); 
                    while (true)  
                    { 
                        System.out.println(dis.readUTF()); 
                        String tosend = scn.nextLine(); 
                        // if
                        String[] tokens = tosend.split("\\s");

                        
                        if(tokens[0].equals("upload")){
                            long file_size = 0;
                            try {
                                File file = new File(tokens[1]);
                                if(!file.exists() || file.isDirectory()){
                                    System.out.println("File " + tokens[1] + " does not exist");
                                    dos.writeUTF(tosend + " 0");
                                    continue;
                                }
                                file_size = file.length();
                                // System.out.println(file.length());
                            } catch (Exception e) {
                            }
                            tosend= tosend + " " + Long.toString(file_size);
                        }                
                        dos.writeUTF(tosend); 
                        
                        if(tosend.toLowerCase().equals("exit")) 
                        { 
                            System.out.println("Closing this connection : " + s); 
                            s.close(); 
                            System.out.println("Connection closed"); 
                            System.exit(0);
                        }
                       
                        // If client sends exit,close this connection  
                        // and then break from the while loop 
                    
                        if(tokens[0].equals("create_user")){
                            if(current_user.equals(""))
                                current_user = tokens[1];
                            else{
                                System.out.println("Logged in as " + current_user + "..Use Exit Command First");
                                continue;
                            }
                        }
                        else if(current_user.equals("")){
                            System.out.println("Use create_user command");
                            continue;
                        }
                        else if(tokens[0].equals("upload")) 
                        { 
                            // dis1 = dis;
                            // dos1 = dos;
                            System.out.println("Sending File"); 
                            send_file(s,tokens[1]);
                            System.out.println("File Sent"); 
                            // break;
                            // s = new Socket(ip, 5056);
                            // dis = new DataInputStream(s.getInputStream()); 
                            // dos = new DataOutputStream(s.getOutputStream()); 
                    
                            // dos = dos1;
                            // dis = dis1;
                        }
                        else if(tokens[0].equals("get_file")){
                            String size = dis.readUTF();
                            long file_size = Long.parseLong(size);
                            String [] tokens1 = tokens[1].split("\\/");
                            if(!size.equals("0")){
                                System.out.println("Receiving file............");
                                receive_file(s, tokens1[tokens1.length - 1], file_size);
                                System.out.println("File Received");
                            }
                        }
                        else if(tokens[0].equals("upload_udp")){
                            System.out.println("Sending File"); 
                            send_udp(ipa, serverip, port,serv_port , tokens[1]);
                            System.out.println("File Sent"); 
                        }

                        // printing date or time as requested by client 
                        String received = dis.readUTF(); 
                        System.out.println(received); 
                        // System.out.println("Sending File"); 
                        // send_file(s);
                        // System.out.println("File Sent"); 
                        // break;
                        
                        
                    } 
                    
                    // closing resources 
                    // scn.close(); 
                    // dis.close(); 
                    // dos.close(); 
                }catch(Exception e){ 
                    e.printStackTrace(); 
                }
            }
        };
        
        Thread thread2 = new Thread() {
            ServerSocket ss =null;
            public void run() {
                    try{
                        InetAddress addr = InetAddress.getByName(ipa);
                        ss = new ServerSocket(port,50,addr); 
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                    while(true){
                        try{
                            Socket s = ss.accept(); 
                        
                        
                            DataInputStream dis = new DataInputStream(s.getInputStream()); 
                            DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
                        
                            System.out.println(dis.readUTF()); 
                        }
                        catch(Exception e){
                            // ss.close();
                            e.printStackTrace();
                        } 
                    }
                    
            }
        };
        try{
            thread1.start();
            thread2.start();

            // Wait for them both to finish
            thread1.join();
            thread2.join();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    } 
} 