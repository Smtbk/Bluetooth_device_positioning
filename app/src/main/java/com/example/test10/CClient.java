package com.example.test10;

import java.io.*;
import java.net.Socket;

public class CClient implements Runnable
{
    private static String ipHost = "10.244.0.244";
    private static Integer port = 1337;
    private Socket socket;
    private String message;

    CClient(String m, String IP, Integer PORT)
    {
        this.message = m;
        this.ipHost = IP;
        this.port = PORT;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(ipHost, port);
            try {
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream()
                        ),
                        true);
                out.println(message+'\n');
                out.flush();
                out.close();
            } catch(Exception e) {
                System.out.println(port.toString());
            }
        } catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

}
