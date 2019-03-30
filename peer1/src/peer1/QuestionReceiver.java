package peer1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author javadev10
 */
public class QuestionReceiver implements Runnable {

    private final MulticastSocket multicastSocket;
    String peer_id;
    String rec_port;
    String group_ip;
    DatagramPacket packet;
    private static final Logger logger = Logger
            .getLogger(AnswerReceiver.class.getName());

    QuestionReceiver(MulticastSocket mcSocket, String peerID, String recPort, String groupIP) {
        multicastSocket = mcSocket;
        peer_id = peerID;
        rec_port = recPort;
        group_ip = groupIP;
    }

    @Override
    public void run() {
        while (true) {

            packet = new DatagramPacket(new byte[1024], 1024);

            try {
                multicastSocket.receive(packet);
            } catch (IOException ex) {
                Logger.getLogger(QuestionReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }

            String msg = new String(packet.getData(), packet.getOffset(),
                    packet.getLength());

            String rec_port1 = msg.substring(msg.indexOf("&RecPort=") + 9, msg.indexOf("&PeerID="));
            String text = msg.substring(0, msg.indexOf("&RecPort="));
            logger.info("Question Received:" + text);
            String pId = msg.substring(msg.indexOf("&PeerID=") + 8);
            
            //if peer is not same who asked question then only it can answer
            if (!pId.equals(peer_id)) {
                
                logger.info("Do You want to answer this question ?(y/n)");
                
                Scanner userResponse = new Scanner(System.in);
                
                String response = userResponse.nextLine();
                
                //if peer want to answer question
                if (response.equals("y")) {
                    
                    logger.info("Write Your answer here::");
                    
                    String answer = userResponse.nextLine();

                    try {
                        
                        int mcPort = Integer.valueOf(rec_port1);
                        String mcIPStr = group_ip;
                        DatagramSocket udpSocket = new DatagramSocket();

                        InetAddress mcIPAddress = InetAddress.getByName(mcIPStr);
                        MulticastSocket mcSocket = new MulticastSocket(mcPort);

                        mcSocket.joinGroup(mcIPAddress);

                        //send messagee
                        answer = peer_id + ":: " + answer + " ( for " + text + " )";
                        byte[] finalanswer = answer.getBytes();
                        DatagramPacket packet1 = new DatagramPacket(finalanswer, finalanswer.length, mcIPAddress, mcPort);
                        udpSocket.send(packet1);
                        
                    } 
                    catch (Exception ex) {
                        
                        logger.log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

}
