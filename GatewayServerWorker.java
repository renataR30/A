import java.io.*;
import java.net.Socket;

public class GatewayServerWorker implements Runnable {
    private final Socket clientSocket;
    private final Socket anonGW_Socket;

    public GatewayServerWorker(Socket client, Socket anonGW) {
        this.clientSocket = client;
        this.anonGW_Socket = anonGW;
    }

    public void run() {
        try {
            //Criar canais de comunicação com o cliente
            BufferedReader inClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter outClient = new PrintWriter(clientSocket.getOutputStream());

            String linhaRecebida = "";
            String output = "";
            4,02,16
                    4,12,18

            while ((linhaRecebida = inClient.readLine()) != null) {
                output = parseAndExecute(linhaRecebida);
                if (!output.equals("error")) {
                    outClient.println(output);
                    outClient.flush();
                    System.out.println("> Reply with: " + output);
                }
            }

            System.out.println("> Client disconnected. Connection is closed.\n");

            //fechar sockets
            clientSocket.shutdownOutput();
            clientSocket.shutdownInput();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Erro aqui.");
        }
    }

    /**
     * Método responsável por fazer o parse da linha recebida através de socket e fazer
     * a execução da operação respetiva.
     * @param linhaRecebida Linha recebida do socket a fazer parse para verificar a operação a executar.
     * @return String O que vai ser enviado através de socket para o lado do cliente.
     */
    private String parseAndExecute(String linhaRecebida) throws IOException {
        String[] parts = linhaRecebida.toLowerCase().split(" ");
        StringBuilder output = new StringBuilder("error");

        switch (parts[0]) {
            case "teste": {
                output = new StringBuilder();

                //Criar canais de comunicação com o AnonGW
                BufferedReader inAnonGW = new BufferedReader(new InputStreamReader(anonGW_Socket.getInputStream()));
                PrintWriter outAnonGW = new PrintWriter(anonGW_Socket.getOutputStream());

                outAnonGW.println("teste ");
                outAnonGW.flush();

                String response;
                while ((response = inAnonGW.readLine()) != null) {
                    output.append("worked");
                    return output.toString();
                }

                break;
            }
            case "wget": {
                break;
            }
            default:{
                return output.toString();
            }
        }
        return output.toString();
    }

    /**
     * Método responsável por enviar o pedido ao Anonymous Gateway.
     * @param header Header do pedido.
     * @return String Contém todas as músicas numa única string.
     */
    private String wget(String header){
        return null;
    }
}