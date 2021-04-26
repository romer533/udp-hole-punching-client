import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;

public class UDPHolePunchingClient {

    public static void main(String[] args) throws Exception {

        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName("metricon.hivecompany.com");
        int port = 14023;
        int timeout = 30000;
        int maxSizePackage = 8;
        byte[] buf;

        writeOnFile(LocalDateTime.now() + ", " + "Client start");
        ping(maxSizePackage, address, port, socket, timeout, 1);

        for (int i = 2; ; i++) {


            try {

                byte[] messageGetNotify = "received".getBytes();
                buf = new byte[maxSizePackage];
                DatagramPacket packetNotifyGetToServer = new DatagramPacket(messageGetNotify, messageGetNotify.length,
                        address, port);
                DatagramPacket packetNotifyFromServer = new DatagramPacket(buf, buf.length);
                socket.setSoTimeout(timeout);
                System.out.println("Я жду нотифай");
                socket.receive(packetNotifyFromServer);
                System.out.println("Я получил нотифай");
                i++;
                System.out.println(LocalDateTime.now() + ", Server: " + new String(packetNotifyFromServer.getData()) + ", i = " + i);
                writeOnFile(LocalDateTime.now() + ", Server: " + new String(packetNotifyFromServer.getData()) + ", i = " + i);
                System.out.println("Я отправил гет");
                socket.send(packetNotifyGetToServer);

//                    TimeUnit.SECONDS.sleep(30);

            } catch (Exception e) {
//                writeOnFile(LocalDateTime.now() + ", " + e.toString() + ", i = " + i);
//                e.printStackTrace();
                ping(maxSizePackage, address, port, socket, timeout, i);
            }

        }


    }

    private static void writeOnFile(String str) {
        try (FileWriter myWriter = new FileWriter("log.csv", true)) {
            myWriter.write(str + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void ping(int maxSizePackage, InetAddress address, int port, DatagramSocket socket, int timeout, int i) {
        try {
            byte[] messagePing = "ping".getBytes();
            byte[] buf = new byte[maxSizePackage];
            DatagramPacket packetPingToServer = new DatagramPacket(messagePing, messagePing.length,
                    address, port);
            DatagramPacket packetPongFromServer = new DatagramPacket(buf, buf.length);
            System.out.println("Я отправил пинг");
            socket.send(packetPingToServer);
            socket.setSoTimeout(timeout);
            System.out.println("Я жду понг");
            socket.receive(packetPongFromServer);
            System.out.println("Я получил понг");
//                    TimeUnit.SECONDS.sleep(30);

            System.out.println(LocalDateTime.now() + ", Server: " + new String(packetPongFromServer.getData()) + ", i = " + i);
            writeOnFile(LocalDateTime.now() + ", Server: " + new String(packetPongFromServer.getData()) + ", i = " + i);

        } catch (Exception e) {
//            writeOnFile(LocalDateTime.now() + ", " + e.toString() + ", i = " + i);
//            e.printStackTrace();
        }
    }

}
