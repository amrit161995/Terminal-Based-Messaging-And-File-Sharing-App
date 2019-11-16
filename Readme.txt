Assignment 2
Name - Amrit Kataria
Roll Number - 2018201067

TO RUN THE APPLICATION:
	- First set the port variabe in Server.java ,then compile and run the server file:
		javac Server.java
		java Server
	- In Client.java change the variable serverip and serv_port to the listening ip and port of server.
	- To start a client at a particular socket give ip:port as command line argument(This is optional as a random port will be allocated to client if no command line argument is passed.)
	- Compile and run the client file:
		javac Client.java
		java Client

COMMANDS AND SYNTAX:
	- First step is creating user :
		create_user Username
	- To upload a file:
		upload filename
		upload_udp filename
	- To create a folder:
		create_folder foldername
	- To move a file:
		move_file file_path dest_path
	- To create a group:
		create_group groupname
	- To list Groups:
		list_groups
	- To join/leave group:
		join_group groupname
		leave_group groupname
	- To list users and user files and path in group:
		list_detail groupname
	- To share a message in group:
		share_msg 'message_text' groupename
	- To download a file
		get_file groupname/username/filepath
	- To exit:
		Exit

IMPLEMENTAION:
	- Server is multithreaded to accept multiple clients.
	- In Server.java three classes have been defined, Server Class,ClientHandler class and MessageHandler class.
	- Server class runs the server at a given port to accept new client connections.
	- The ClientHandler class handles all the client commands like upload,download etc.
	- The MessageHandler class is for sending messages via share_msg command.
	- Following hashmaps are used at the server:
		1) Registered_users <String,Vector> it stores username as key and a vector of uploaded files by user as value.It is stored in file Registered_Users.ser.
		2) Groups <String,Vector> it stores the groupname as key and a vector of all users in group as value. It is Stored in file Groups.ser
		3) Paths <String,String> it stored filename as key and absolute filepath as value. It is stored in Paths.ser
		4) userIpPort<String,String> it stores the username as key and ip port of the user machine as value for sending messages.

	- 2 threads are created for each client, one for sending commands to server and response, and the second for receiving messages which opens a socket in listening mode whenever the client is runnung.
	- A client may simultaneously upload/download a file and receive messages as different threads are responsible for handling messages.
