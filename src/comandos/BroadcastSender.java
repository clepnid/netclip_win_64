package comandos;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadcastSender {

    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            String message = "Hola";
            byte[] buffer = message.getBytes();

            // Dirección de broadcast (por ejemplo, 255.255.255.255 para alcance global o 192.168.1.255 para red local)
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
            int port = 8888; // Puedes usar un puerto específico para escuchar el mensaje

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, port);
                socket.send(packet);
                System.out.println("Mensaje enviado: " + message);

                // Pausa entre mensajes (1 segundo en este caso)
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
