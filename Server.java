import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 
import java.util.HashMap; 
import java.util.Map; 
import java.io.*;
import java.net.*;
import java.io.ByteArrayOutputStream;
import java.util.StringTokenizer;
// Server class 
public class Server  
{ 
    static HashMap <String,String> userIpPort = new HashMap<>();
    public static void main(String[] args) throws IOException  
    { 
        // server is listening on port 5056 
        // InetAddress addr = InetAddress.getByName("10.1.33.196");
        // System.out.println(addr);
        // ServerSocket ss = new ServerSocket(5056);
        int port = 5056; 
        ServerSocket ss = new ServerSocket(port); 
        // running infinite loop for getting 
        // client request 
        System.out.println("Server Started");
        while (true)  
        { 
            Socket s = null; 
              
            try 
            { 
                // socket object to receive incoming client requests 
                s = ss.accept(); 
                  
                System.out.println("A new client is connected : " + s); 
                  
                // obtaining input and out streams 
                DataInputStream dis = new DataInputStream(s.getInputStream()); 
                DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
                  
                System.out.println("Assigning new thread for this client"); 
  
                // create a new thread object 
                Thread t = new ClientHandler(s, dis, dos,userIpPort); 
  
                // Invoking the start() method 
                t.start(); 
                  
            } 
            catch (Exception e){ 
                s.close(); 
                e.printStackTrace(); 
            } 
        } 
    } 
} 


class MessageHandler extends Thread  
{ 
    final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket s; 
    String msg = "";
    // Constructor 
    public MessageHandler(Socket s, DataInputStream dis, DataOutputStream dos,String message)  
    { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos; 
        msg = message;
    } 
  
    @Override
    public void run()  
    { 
       
        try { 
  
                // String message = "hello";
                dos.writeUTF(msg); 
                  
                 
            } catch (IOException e) { 
                e.printStackTrace(); 
            } 
         
          
       
    } 

   
} 

@SuppressWarnings({"unchecked"})
class ClientHandler extends Thread  
{ 
    // DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd"); 
    // DateFormat fortime = new SimpleDateFormat("hh:mm:ss"); 
    DataInputStream dis; 
    DataOutputStream dos; 
    Socket s; 
    HashMap<String, Vector> map = new HashMap<>(); 
    HashMap<String, Socket> online = new HashMap<>(); 
    HashMap<String, Vector > groups = new HashMap<>(); 
    HashMap<String, String > userIpPort = new HashMap<>(); 
    String current_user = "";
    /////////////////////////////////////////////////////////////////////////////////
    public void persist_data(HashMap map,String file){
        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();
            fos.close();
        }catch(Exception ioe)
        {
            ioe.printStackTrace();
        }
    }
    /////////////////////////////////////////////////////////////////////////////////
      
    public HashMap<String,Vector> load_data(){
        HashMap<String, Vector> map = new HashMap<>();
        File f = new File("Registered_Users.ser");
        if(f.exists() && !f.isDirectory()){
            try
            {
            FileInputStream fis = new FileInputStream("Registered_Users.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (HashMap<String,Vector>) ois.readObject();
            ois.close();
            fis.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return map;
    }

    /////////////////////////////////////////////////////////////////////////////////
    
    public HashMap<String, Vector > load_groups(){
        HashMap<String, Vector > groups = new HashMap<>(); 
        File f = new File("Groups.ser");
        if(f.exists() && !f.isDirectory()){
            try
            {
            FileInputStream fis = new FileInputStream("Groups.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            groups = (HashMap<String,Vector>) ois.readObject();
            ois.close();
            fis.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return groups;
    
    }

    /////////////////////////////////////////////////////////////////////////////////
    
    public HashMap<String, String > load_paths(){
        HashMap<String, String > paths = new HashMap<>(); 
        File f = new File("Paths.ser");
        if(f.exists() && !f.isDirectory()){
            try
            {
            FileInputStream fis = new FileInputStream("Paths.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            paths = (HashMap<String,String>) ois.readObject();
            ois.close();
            fis.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return paths;
    
    }

    /////////////////////////////////////////////////////////////////////////////////
    
    public void receive_file(Socket s,String filename,long size){
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

    /////////////////////////////////////////////////////////////////////////////////
    
    public String create_user(String Username,HashMap<String, Vector> map){
        map = load_data();
        if(map.containsKey(Username)){
            System.out.println("User Already Exists.....Connected User " + Username);
            return "User Already Exists............Connected as " + Username;
        }
        // else
        Vector v = new Vector();
        map.put(Username, v);
        try
        {
            FileOutputStream fos = new FileOutputStream("Registered_Users.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();
            fos.close();
        }catch(Exception ioe)
        {
            ioe.printStackTrace();
        }
            System.out.println("New User has been Created..........Connected User " + Username);
            return "New User has been Created................Connected as " + Username;

    }

    /////////////////////////////////////////////////////////////////////////////////
    
    public int create_folder(String foldername){
        
        File theDir = new File(foldername);
        if (!theDir.exists()){
            new File(foldername).mkdirs();
            return 1;
        }
        else{
            return 0;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////
    
    public String move_file(String source,String dest,String Username){
        // File file = new File(source); 
        String[] tokens_s = source.split("\\/");
        HashMap<String,Vector> users = load_data();
        Vector v = users.get(Username);
        if(tokens_s[tokens_s.length - 1].contains(Username)){
            String filen = tokens_s[tokens_s.length - 1].replace(Username + "_","");
            tokens_s[tokens_s.length - 1] = filen;
            // System.out.println(uname);
        }
        if(!v.contains(tokens_s[tokens_s.length - 1])){
            System.out.println("File " + tokens_s[tokens_s.length - 1] + " does not belong to user " + Username); 
            return "File " + tokens_s[tokens_s.length - 1] + " does not belong to user " + Username;
        }
        // if(!tokens_s[tokens_s.length - 1].contains(Username))
        tokens_s[tokens_s.length - 1] = Username + "_" + tokens_s[tokens_s.length - 1];
        String[] tokens_d = dest.split("\\/");
        String[] last_s = tokens_s[tokens_s.length - 1].split("\\.");
        String[] last_d = tokens_d[tokens_d.length - 1].split("\\.");

        if(!last_d[last_d.length - 1].equals(last_s[last_s.length - 1]))
            dest = dest + "/" + tokens_s[tokens_s.length - 1];
        // System.out.println(tokens[tokens.length - 1]);
        HashMap<String,String> paths = load_paths();
        // String new_source = "";
        if(!paths.containsKey(tokens_s[tokens_s.length - 1])){
            System.out.println("File does not exist");
            return "File does not exist";
        }
        File file = new File(paths.get(tokens_s[tokens_s.length - 1])); 
        // renaming the file and moving it to a new location 
        if(file.renameTo(new File(dest))) 
        { 
            // if file copied successfully then delete the original file 
            // HashMap paths = load_paths();
            System.out.println(tokens_s[tokens_s.length - 1]);
            if(paths.containsKey(tokens_s[tokens_s.length - 1])){
                paths.replace(tokens_s[tokens_s.length - 1], (new File(dest)).getAbsolutePath());
            }
            persist_data(paths, "Paths.ser");
            file.delete(); 
            System.out.println("File moved successfully");
            return "File moved successfully"; 
        } 
        else
        { 
            System.out.println("Failed to move the file"); 
            return "Failed to move the file";
        } 
    }

    /////////////////////////////////////////////////////////////////////////////////
    
    public int create_group(String GroupName){
        HashMap<String, Vector> groups = load_groups();
        if(groups.containsKey(GroupName)){
            System.out.println("Group Cannot be Created");
            return 0;       
        }
        else{
            Vector v = new Vector(); 
            groups.put(GroupName, v);
            try
            {
                FileOutputStream fos = new FileOutputStream("Groups.ser");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(groups);
                oos.close();
                fos.close();
                System.out.println("Group " + GroupName + " Created");
                return 1;
            }catch(Exception ioe)
            {
                ioe.printStackTrace();
            }
        }
        return 0;
    }

    /////////////////////////////////////////////////////////////////////////////////
    
    public String list_groups(){
        HashMap<String, Vector> groups = load_groups();
        String res = "";
        for (String name : groups.keySet())  
            res = res + name + "\n";
        
        if(res.equals(""))
            return "No Groups Exist";
        return res;
    }
    /////////////////////////////////////////////////////////////////////////////////
    
    public int find(HashMap<String, Vector> groups,String Username,String Groupname){
        // String Groupname = "";
        Vector v = groups.get(Groupname);
        // for (Map.Entry<String,Vector> entry : groups.entrySet()) {
            // v = entry.getValue();
            if(v.contains(Username))
                return 1;
        // }
        return 0;
    }

    /////////////////////////////////////////////////////////////////////////////////
    
    public String join_group(String Groupname,String Username){
        HashMap<String, Vector> groups = load_groups();
        if(!groups.containsKey(Groupname)){
            return "Group " + Groupname + " does not exist";
        }
        else if(find(groups, Username,Groupname) == 1){
            return "User "+ Username + " Already exists in group " + Groupname;
        }
        else{
            groups.get(Groupname).add(Username);

            try
            {
                FileOutputStream fos = new FileOutputStream("Groups.ser");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(groups);
                oos.close();
                fos.close();
                
            }catch(Exception ioe)
            {
                ioe.printStackTrace();
            }
            System.out.println("User " + Username + " Joined Group " + Groupname);
            return "User "+ Username + " Successfully added in group " + Groupname;

        }
        // if(find(groups, Username).equals(anObject))
    }

    /////////////////////////////////////////////////////////////////////////////////
    
    public String leave_Group(String Groupname,String Username){
        HashMap<String, Vector> groups = load_groups();
        if(!groups.containsKey(Groupname)){
            return "Group " + Groupname + " does not exist";
        }
        else if(find(groups, Username,Groupname) == 1){
            groups.get(Groupname).remove(Username);

            try
            {
                FileOutputStream fos = new FileOutputStream("Groups.ser");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(groups);
                oos.close();
                fos.close();
                
            }catch(Exception ioe)
            {
                ioe.printStackTrace();
            }
            System.out.println("User " + Username + " Left Group " + Groupname);
            return "User "+ Username + " left group " + Groupname;

        }
        else{
            return "User "+ Username + " does not exist in group " + Groupname;
        }
        // if(find(groups, Username).equals(anObject))
    }

    /////////////////////////////////////////////////////////////////////////////////
    
    public String listDetail(String Groupname){
        String res = "";
        HashMap<String, Vector> groups = load_groups();
        HashMap<String, Vector> user_files = load_data();
        
        Vector users = groups.get(Groupname);
        HashMap paths = load_paths();
        for(int index = 0; index < users.size(); index++) {
            String uname = users.get(index).toString();
            Vector files = user_files.get(uname);
            res = res + "User " + uname + "\n";
            if(files.size() > 0){
                res = res + "->Files for user " + uname + "----------\n";
                for(int j = 0; j < files.size(); j++){
                    res = res +"--"+ files.get(j) + " (PATH = " + paths.get(uname + "_" + files.get(j)) + ") \n";
                }
            }
        }

        if(res == ""){
            return "No Users Exist";
        }
        else{
            return res;
        }
        // if(user_files.get())
    }

    /////////////////////////////////////////////////////////////////////////////////
    
    public String share_msg(String message,String Groupname,HashMap<String,String> userIpPort,String current_user){
        String msg = "";
        try{
            groups = load_groups();
            Vector users  = groups.get(Groupname);
            if(!groups.containsKey(Groupname)){
                msg = "Group " + Groupname + " does not exist";
                return msg;
            }
            if(!users.contains(current_user)){
                msg = "User " + current_user + " does not exist in group " + Groupname;
                return msg;
            }
            for(int i=0;i<users.size();i++){
                String uname = users.get(i).toString();
                if(uname.equals(current_user))
                    continue;
                if(userIpPort.containsKey(uname)){
                    // System.out.println(userIpPort.get(uname));
                    String [] tokens = userIpPort.get(uname).split("\\:");
                    InetAddress ip = InetAddress.getByName(tokens[0]); 
                    Socket s = new Socket(ip,Integer.parseInt(tokens[1])); 
                    DataInputStream dis = new DataInputStream(s.getInputStream()); 
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
                    // message =  message;
                    Thread t = new MessageHandler(s, dis, dos,"Message from " + current_user + " -> " + message); 
                    t.start();
                }
            }
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        msg = "Message Sent";
        return msg;
    }

    /////////////////////////////////////////////////////////////////////////////////
    
    public void send_file(Socket s,String filename) throws IOException{
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

    /////////////////////////////////////////////////////////////////////////////////
    
    public String get_file(String file_data,DataOutputStream dos) throws IOException{
        String [] tokens = file_data.split("\\/");
        HashMap<String,Vector> groups = load_groups();
        String fileSize = "0";
        
        if(!groups.containsKey(tokens[0])){
            System.out.println("Group " + tokens[0] + " does not exist");
            dos.writeUTF(fileSize);
            return "Group " + tokens[0] + " does not exist";
        }
        Vector v = groups.get(tokens[0]);
        if(!v.contains(tokens[1])){
            System.out.println("Group " + tokens[0] + " does not contain user " + tokens[1]);
            dos.writeUTF(fileSize);
            return "Group " + tokens[0] + " does not contain user " + tokens[1];
        }

        HashMap <String,String> paths = load_paths();
        String file_name = "";
        if(!tokens[tokens.length - 1].contains(tokens[1]))
            file_name = tokens[1] + "_" + tokens[tokens.length - 1];
        else    
            file_name = tokens[tokens.length - 1];
        if(!paths.containsKey(file_name)){
            System.out.println("User " + tokens[1] + " has not uploaded file " + tokens[tokens.length - 1]);
            dos.writeUTF(fileSize);
            return "User " + tokens[1] + " has not uploaded file " + tokens[tokens.length - 1];
        }
        File file = new File(paths.get(file_name));
        long size = file.length();
        fileSize = Long.toString(size);
        dos.writeUTF(fileSize);
        send_file(s, paths.get(file_name));
        System.out.println("File Sent By Server");
        return "File Sent By Server";
    }

    /////////////////////////////////////////////////////////////////////////////////
    
    public String upload_udp(int port,String filepath) throws IOException{
        DatagramSocket dsoc = new DatagramSocket(port);
        // HashMap<String,String> paths = load
        File f1 = new File(filepath);
        //f1.createNewFile();
        // if(!f1.exists() || f1.isDirectory())
        //     return  "File does not exist";
        FileOutputStream bos = new FileOutputStream(f1);
        byte[] buf = new byte[63*1024];
        DatagramPacket pkg = new DatagramPacket(buf, buf.length);
        
        while(true)
        {
            dsoc.receive(pkg);
            if (new String(pkg.getData(), 0, pkg.getLength()).equals("end"))
            {
                System.out.println("Documents received");
                bos.close();
                dsoc.close();
             
                return "Documents received by server";
                // break;
            }
            bos.write(pkg.getData(), 0, pkg.getLength());
            bos.flush();
        }

        // return "";
        // bos.close();
        // dsoc.close();

    
    }
    // Constructor 
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos,HashMap<String, String > userIpPort)  
    { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos; 
        this.userIpPort = userIpPort;
    } 
  
    @Override
    public void run()  
    { 
        String received; 
        String toreturn; 
        String ip_port = "";
        try{
            ip_port = dis.readUTF(); 
        }
        catch(Exception e){
            e.printStackTrace();
        }

        while (true)  
        { 
            try { 
  
                // Ask user what he wants
                // dos.writeUTF("Enter Command:\n"); 
                 
                dos.writeUTF("Enter Command:"); 
                
                // receive the answer from client 
                received = dis.readUTF(); 
                String[] tokens = received.split("\\s");
                String msg = "";
                // int 
                if(tokens[0].toLowerCase().equals("exit")){
                    userIpPort.remove(current_user);
                    System.out.println("Client " + current_user + " left");
                    
                }
                if(tokens[0].equals("create_user") && tokens.length>=2) 
                {  
                    if(!current_user.equals(""))
                        continue;
                    msg = create_user(tokens[1], map);
                    // if(msg == 0)
                    current_user = tokens[1];
                    userIpPort.put(current_user, ip_port);
                    dos.writeUTF(msg);
                    // else
                    //     dos.writeUTF("User Already Exists");
                    // break;
                } 
                else if(current_user.equals("")){
                    // msg = "Use create_user Command";
                    // dos.writeUTF(msg);
                    continue;
                }
                else if(tokens[0].equals("upload") && tokens.length>=2 ){
                    // DataInputStream dis1 = dis;
                    // DataOutputStream dos1 = dos;
                    // System.out.println(current_user);
                    // System.out.println(tokens[2]);
                    String [] tokens2 =  tokens[1].split("\\/");
                    String newFileName = current_user + "_" + tokens2[tokens2.length-1];
                    long size = Long.parseLong(tokens[2]); 
                    if(size == 0)
                        continue;
                    System.out.println("Receiving file");
                    receive_file(s,newFileName,size); 
                    System.out.println("File Received"); 
                    msg = "File Received by Server";
                    // dis = new DataInputStream(s.getInputStream()); 
                    // dos = new DataOutputStream(s.getOutputStream()); 
                    HashMap<String,Vector> map = load_data();
                    String [] tokens1 = tokens[1].split("\\/");
                    if(!map.get(current_user).contains(tokens1[tokens1.length - 1]))
                        map.get(current_user).add(tokens1[tokens1.length - 1]);
                    HashMap <String,String> paths = new HashMap<>();
                    paths = load_paths();
                    paths.put(newFileName, System.getProperty("user.dir") + "/" + newFileName);
                    persist_data(paths, "Paths.ser");
                    persist_data(map, "Registered_Users.ser");
                    dos.writeUTF(msg);
                }

                else if(tokens.length == 2 && tokens[0].equals("create_folder")){
                    if(create_folder(tokens[1]) == 1){
                        System.out.println("Folder Created"); 
                        msg = "Folder Created at Server";
                    }
                    else{
                        System.out.println("Folder Already Exists"); 
                        msg = "Folder Already Exists at Server";
                    
                    }
                    // msg = "File Received by Server";
                    dos.writeUTF(msg);
                }

                else if(tokens.length == 3 && tokens[0].equals("move_file")){
                    // if(move_file(tokens[1], tokens[2]) == 1){
                    //     msg = "File Moved";
                    // }
                    // else{
                    //     msg = "Cannot Move File";
                    // }
                    // HashMap <String,String> paths = load_paths();
                    
                    msg = move_file(tokens[1], tokens[2], current_user);
                    dos.writeUTF(msg);
                }
                else if(tokens[0].equals("create_group") && tokens.length>=2 ){
                    if(create_group(tokens[1]) == 1)
                        msg = "Group " + tokens[1] + " Created";
                    else
                        msg = "Group " + tokens[1] + " cannot be created as it already exists";
                    dos.writeUTF(msg);
                }
                else if(tokens[0].equals("list_groups")){
                    msg = list_groups();
                    dos.writeUTF(msg);
                }
                else if(tokens[0].equals("join_group") && tokens.length>=2 ){
                    msg = join_group(tokens[1], current_user);
                    dos.writeUTF(msg);
                }
                else if(tokens[0].equals("leave_group") && tokens.length>=2 ){
                    msg = leave_Group(tokens[1],current_user);
                    dos.writeUTF(msg);
                }

                else if(tokens[0].equals("list_detail") && tokens.length>=2 ){
                    msg = listDetail(tokens[1]);
                    dos.writeUTF(msg);
                }
                else if(tokens[0].equals("share_msg") && tokens.length>2 ){
                    msg = "";
                    String[] tokens1 = received.split("\\'");
                    msg = share_msg(tokens1[1], tokens[tokens.length - 1], userIpPort,current_user);
                    dos.writeUTF(msg);
                }
                else if(tokens[0].equals("get_file") && tokens.length>=2 ){
                    msg = get_file(tokens[1], dos);
                    dos.writeUTF(msg);
                }
                else if(tokens[0].equals("upload_udp") && tokens.length>=2 ){
                    String [] tokens2 =  tokens[1].split("\\/");
                    String newFileName = current_user + "_" + tokens2[tokens2.length-1];
                    // System.out.println(s.getPort());
                    msg = upload_udp(5056, newFileName);
                    HashMap<String,Vector> map = load_data();
                    String [] tokens1 = tokens[1].split("\\/");
                    if(!map.get(current_user).contains(tokens1[tokens1.length - 1]))
                        map.get(current_user).add(tokens1[tokens1.length - 1]);
                    HashMap <String,String> paths = new HashMap<>();
                    paths = load_paths();
                    paths.put(newFileName, System.getProperty("user.dir") + "/" + newFileName);
                    persist_data(paths, "Paths.ser");
                    try
                    {
                        FileOutputStream fos = new FileOutputStream("Registered_Users.ser");
                        ObjectOutputStream oos = new ObjectOutputStream(fos);
                        oos.writeObject(map);
                        oos.close();
                        fos.close();
                    }catch(Exception ioe)
                    {
                        ioe.printStackTrace();
                    }
                    dos.writeUTF(msg);
                
                }
                else{
                    msg = "Invalid Command";
                    dos.writeUTF(msg);
                }
                // System.out.println("Receiving file");
                // receive_file(s); 
                // System.out.println("File Received"); 
                
            } catch (IOException e) { 
                // e.printStackTrace(); 
            } 
        } 
          
        // try
        // { 
        //     // closing resources 
        //     this.dis.close(); 
        //     this.dos.close(); 
              
        // }catch(IOException e){ 
        //     e.printStackTrace(); 
        // } 
    } 
} 