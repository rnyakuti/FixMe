package main.java.fixme.Router;

import java.net.*;
import java.io.*;

class MyServer
{
    public static void main(String args[]) throws Exception
    {
        ServerSocket ss = new ServerSocket(5000);
        Socket s = ss.accept();

        DataInputStream datain = new DataInputStream(s.getInputStream());
        DataOutputStream dataout = new DataOutputStream(s.getOutputStream());

        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));

        String str = "";
        String str2 = "";

        while(!str.equals("stop"))
        {
            str = datain.readUTF();
            System.out.println("client says: " + str);
            str2 = buffer.readLine();
            dataout.writeUTF(str2);
            dataout.flush();
        }

        din.close();
        s.close();
        ss.close();
    }
}