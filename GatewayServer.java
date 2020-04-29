import AnonGateway.AnonGW;
import Application.ConfigValues;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GatewayServer {
    private ServerSocket gatewayServerSocket;
    private String host;
    private int port;
    private List<AnonGW> anonGWS;
    private List<Socket> anonymousGateways;

    public GatewayServer(String host, int port) throws IOException {
        this.anonymousGateways = new ArrayList<Socket>();
        this.host = host;
        this.port = port;
        for (String anonGW_IP : ConfigValues.getOverlayPeers()){
            AnonGW anonGW = new AnonGW(anonGW_IP);
            anonGWS.add(anonGW);
            anonGW.startAnonGw();
            addAnonymousGateway(anonGW_IP, this.port);
        }
    }

    public static void main(String[] args) throws IOException {
        GatewayServer gatewayServer = new GatewayServer(ConfigValues.getGatewayServerIP(), ConfigValues.getGatewayServerPort());
        gatewayServer.startServer();
    }

    public void addAnonymousGateway(String host, int port) throws IOException {
        System.out.println("#### GATEWAY SERVER ####");
        System.out.println("> Connecting to Anonymous Gateway...");
        Socket anonGW = new Socket(host, port);

        if (anonGW.isConnected()) {
            System.out.println("> Connection accepted!");
        }
        anonymousGateways.add(anonGW);
    }

    //aceita pedidos dos clientes
    public void startServer() {
        AnonGW.main(null);
        try {
            this.gatewayServerSocket = new ServerSocket(this.port, 0, InetAddress.getByName(this.host));

            while (true) {
                System.out.println("> GatewayServer is running waiting for a new connection...");
                acceptClients();     //Accept all threads and create threads for the clients
            }
        } catch (IOException e) {
            System.out.println("Error!");
        }
    }

    private void acceptClients(){
        while (true){
            try{
                Socket socket = gatewayServerSocket.accept(); //Accepts a client
                System.out.println("> Client connected!");

                // Give the client a random anon gateway to process his data and give him a thread
                Thread t = new Thread(new GatewayServerWorker(socket, anonymousGateways.get(new Random().nextInt(anonymousGateways.size()))));
                t.start();
            } catch (IOException e) {
                System.out.println("> Accept failed on port " + port + ".");
            }
        }
    }
}
