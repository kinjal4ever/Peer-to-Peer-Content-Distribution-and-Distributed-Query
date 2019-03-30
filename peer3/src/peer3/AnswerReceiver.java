package peer3;

import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 *
 * @author javadev10
 */
public class AnswerReceiver implements Runnable {

    private final MulticastSocket multicastSocket;
    DatagramPacket packet1;

    private JTextArea answerBoard;
    private JFrame frame;
    private static final Logger logger = Logger
            .getLogger(AnswerReceiver.class.getName());

    public AnswerReceiver(MulticastSocket mcSocket, JTextArea answerBoard, JFrame frame) {

        multicastSocket = mcSocket;
        this.answerBoard = answerBoard;
        this.frame = frame;
    }

    @Override
    public void run() {

        while (true) {

            try {

                packet1 = new DatagramPacket(new byte[1024], 1024);

                //receive answer
                multicastSocket.receive(packet1);

                String msg1 = new String(packet1.getData(), packet1.getOffset(),
                        packet1.getLength());

                if (!msg1.isEmpty()) {
                    String answer = answerBoard.getText();
                    answer = answer + msg1 + "\n";
                    answerBoard.setText(answer);
                    answerBoard.update(answerBoard.getGraphics());
                    frame.setVisible(true);

                }
            } catch (Exception ex) {
                 logger.log(Level.SEVERE, null, ex);
            }
        }
    }

}
