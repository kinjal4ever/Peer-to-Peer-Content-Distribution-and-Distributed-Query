package peer1;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class JAVASwingFormExample {

    private JFrame frame;
    private static JTextField groupIP;
    private JTextField groupPort;
    private JTextField peerID;
    private static JTextField recPort;
    private JTextField question;
    private JTextArea answerBoard;

    InetAddress mcIPAddress;
    private ExecutorService executor;
    private boolean isFirstQuery = true;
    private boolean isFirstSet = true;

    private static final Logger logger = Logger
            .getLogger(JAVASwingFormExample.class.getName());

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    JAVASwingFormExample window = new JAVASwingFormExample();
                    window.frame.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void shutdown() {

        if (executor != null) {
            executor.shutdown();
        }
    }

    /**
     * Create the application.
     */
    public JAVASwingFormExample() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 730, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setResizable(false);

        JLabel lblGroupIP = new JLabel("Group IP");
        lblGroupIP.setBounds(65, 31, 86, 14);
        frame.getContentPane().add(lblGroupIP);

        groupIP = new JTextField();
        groupIP.setBounds(151, 31, 116, 20);
        frame.getContentPane().add(groupIP);
        groupIP.setColumns(10);

        JLabel lblGroupPort = new JLabel("Group Port");
        lblGroupPort.setBounds(65, 71, 86, 14);
        frame.getContentPane().add(lblGroupPort);

        groupPort = new JTextField();
        groupPort.setBounds(156, 71, 86, 20);
        frame.getContentPane().add(groupPort);
        groupPort.setColumns(10);

        JLabel lblPeerID = new JLabel("Peer ID");
        lblPeerID.setBounds(297, 31, 86, 14);
        frame.getContentPane().add(lblPeerID);

        peerID = new JTextField();
        peerID.setBounds(383, 31, 116, 20);
        peerID.setText("Peer1");
        peerID.setEditable(false);
        frame.getContentPane().add(peerID);
        peerID.setColumns(10);

        JLabel lblRPort = new JLabel("Receiption Port");
        lblRPort.setBounds(262, 71, 116, 14);
        frame.getContentPane().add(lblRPort);

        JButton btnSet = new JButton("Set");

        btnSet.setBounds(504, 71, 60, 23);
        frame.getContentPane().add(btnSet);

        recPort = new JTextField();
        recPort.setBounds(398, 71, 86, 17);
        frame.getContentPane().add(recPort);
        recPort.setColumns(10);

        JLabel lblQuestion = new JLabel("Question");
        lblQuestion.setBounds(65, 162, 86, 14);
        frame.getContentPane().add(lblQuestion);

        question = new JTextField();
        question.setBounds(151, 162, 390, 20);
        frame.getContentPane().add(question);
        question.setColumns(10);

        JButton btnQuery = new JButton("Query");

        btnQuery.setBounds(561, 162, 89, 20);
        frame.getContentPane().add(btnQuery);

        JLabel lblAnswer = new JLabel("Answer Board");
        lblAnswer.setBounds(315, 192, 100, 20);
        frame.getContentPane().add(lblAnswer);

        answerBoard = new JTextArea();
        answerBoard.setBounds(65, 222, 600, 450);
        answerBoard.setLineWrap(true);
        answerBoard.setWrapStyleWord(true);
        answerBoard.setEditable(false);
        frame.getContentPane().add(answerBoard);

        btnQuery.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (groupIP.getText().isEmpty() || (groupPort.getText().isEmpty()) || (peerID.getText().isEmpty()) || recPort.getText().isEmpty() || question.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Data Missing");
                } else if (mcIPAddress == null) {
                    JOptionPane.showMessageDialog(null, "You have to set data to join group!!");
                } else {
                    try {
                        //Ask Question
                        DatagramSocket udpSocket = new DatagramSocket();
                        String que = question.getText() + "&RecPort=" + recPort.getText() + "&PeerID=" + peerID.getText();
                        byte[] msg = que.getBytes();
                        DatagramPacket packet = new DatagramPacket(msg, msg.length, mcIPAddress, Integer.valueOf(groupPort.getText()));
                        udpSocket.send(packet);

                        //Receive Answer
                        try {
                            MulticastSocket mcSocket = null;

                            mcIPAddress = InetAddress.getByName(groupIP.getText());
                            mcSocket = new MulticastSocket(Integer.valueOf(recPort.getText()));
                            mcSocket.joinGroup(mcIPAddress);

                            //start thread fir receiving answers continously.
                            if (isFirstQuery) {
                                executor.submit(new AnswerReceiver(mcSocket, answerBoard, frame));
                            }

                            isFirstQuery = false;

                        } catch (Exception ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                    } catch (Exception ex) {
                        logger.log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        btnSet.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (groupIP.getText().isEmpty() || (groupPort.getText().isEmpty()) || (peerID.getText().isEmpty()) || recPort.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Data Missing");
                } else {
                    try {
                        //join group
                        mcIPAddress = InetAddress.getByName(groupIP.getText());
                        MulticastSocket mcSocket = new MulticastSocket(Integer.valueOf(groupPort.getText()));
                        mcSocket.joinGroup(mcIPAddress);

                        JOptionPane.showMessageDialog(null, "Group  Joined");
                        if (isFirstSet) {
                            //start thread for receiving questions.
                            executor = Executors.newFixedThreadPool(3);
                            executor.submit(new QuestionReceiver(mcSocket, peerID.getText(), recPort.getText(), groupIP.getText()));
                        }
                        isFirstSet = false;
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Error occurred:: " + ex.getMessage());
                        logger.log(Level.SEVERE, null, ex);
                    }

                }
            }
        }
        );

    }

}
