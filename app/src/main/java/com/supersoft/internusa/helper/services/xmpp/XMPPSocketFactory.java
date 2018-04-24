package com.supersoft.internusa.helper.services.xmpp;

/**
 * Created by itclub21 on 1/24/2018.
 */

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.SocketFactory;

public class XMPPSocketFactory extends SocketFactory {
    private static SocketFactory sDefaultFactory = SocketFactory.getDefault();
    private static XMPPSocketFactory sInstance;

    private Socket socket;

    public static XMPPSocketFactory getInstance() {
        if (sInstance == null) sInstance = new XMPPSocketFactory();
        return sInstance;
    }

    @Override
    public Socket createSocket() throws IOException {
        socket = sDefaultFactory.createSocket();
        setSockOpt(socket);
        return socket;
    }

    @Override
    public Socket createSocket(String arg0, int arg1) throws IOException {
        socket = sDefaultFactory.createSocket(arg0, arg1);
        setSockOpt(socket);
        return socket;
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        socket = sDefaultFactory.createSocket(host, port);
        setSockOpt(socket);
        return socket;
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
            throws IOException {
        socket = sDefaultFactory.createSocket(host, port, localHost, localPort);
        setSockOpt(socket);
        return socket;
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress,
                               int localPort) throws IOException {
        socket = sDefaultFactory.createSocket(address, port, localAddress, localPort);
        setSockOpt(socket);
        return socket;
    }

    private static void setSockOpt(Socket socket) throws IOException {
        socket.setKeepAlive(false);
        // Set the Socket timeout to PING_INTERVAL_SECONDS + 10 minutes
        socket.setSoTimeout((XMPPPingManager.PING_INTERVAL_SECONDS + (10 * 60)) * 1000);
        socket.setTcpNoDelay(false);
    }
}
