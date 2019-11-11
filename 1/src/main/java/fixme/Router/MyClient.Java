package main.java.fixme.Router;

import java.io.*;
import java.net.*;

class MyClient
{
    public static void main(String args[]) throws Exception
    {
        Socket s = new Socket("localhost", 5000);

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
    
    }
}