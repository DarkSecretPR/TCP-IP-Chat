
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.net.Socket;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

public class TCPClientJFrame extends JFrame {

    private String strTitle;
    private String recIP = "192.168.1.11";
    private boolean finished = true;
    private int firstCon = 0;

    private Socket client;
    private InputStream in;
    private InputStreamReader isReader;
    private BufferedReader bReader;
    private PrintWriter op;

    private JButton btnSetIP;
    private JLabel jLabel1, labConShow;
    private JScrollPane jScrollPane1;
    private JTextArea showText;
    private JTextField textInput;

    TCPClientJFrame() {
        initComponents();
        socketTCPClient();
    }

    private void initComponents() {

        jScrollPane1 = new JScrollPane();
        showText = new JTextArea();
        btnSetIP = new JButton();
        jLabel1 = new JLabel();
        labConShow = new JLabel();
        textInput = new JTextField();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("多人聊天室");

        showText.setBackground(new Color(51, 153, 255));
        showText.setColumns(20);
        showText.setFont(new Font("標楷體", 0, 14));
        showText.setForeground(new Color(0, 0, 0));
        showText.setRows(5);
        showText.setRows(5);
        showText.setEditable(false);
        showText.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(0, 255, 102), new Color(0, 255, 102), new Color(0, 255, 102), new Color(0, 255, 102)));
        jScrollPane1.setViewportView(showText);

        btnSetIP.setFont(new Font("標楷體", 0, 18));
        btnSetIP.setText("IP設定");
        btnSetIP.setFocusPainted(false);
        btnSetIP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                btnSetIPActionPerformed(ae);
            }
        });

        jLabel1.setFont(new Font("標楷體", 0, 14));
        jLabel1.setText("連線狀態：");

        labConShow.setBackground(new Color(0, 255, 0));
        labConShow.setFont(new Font("標楷體", 0, 18));
        labConShow.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(255, 51, 51), new Color(255, 51, 51), new Color(255, 51, 51), new Color(255, 51, 51)));
        labConShow.setOpaque(true);

        textInput.setBackground(new Color(204, 204, 255));
        textInput.setFont(new Font("標楷體", 0, 18));
        textInput.setForeground(new Color(102, 102, 102));
        textInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                textInputActionPerformed(ae);
            }
        });
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSetIP)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel1)
                        .addGap(0, 0, 0)
                        .addComponent(labConShow, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addComponent(textInput)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btnSetIP)
                                .addComponent(jLabel1)
                                .addComponent(labConShow, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 223, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(textInput, GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE))
        );
        pack();
    }

    private void socketTCPClient() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    labConShow.setText("連接中...");
                    client = new Socket(recIP, 1668);
                    in = client.getInputStream();
                    isReader = new InputStreamReader(in);
                    bReader = new BufferedReader(isReader);
                    op = new PrintWriter(client.getOutputStream());
                    strTitle = bReader.readLine();//接收連接至伺服器所屬IP 與 PORT      
                    setTitle("多人聊天室 " + strTitle);
                    System.out.println(strTitle);
                    labConShow.setText("連線成功");

                    String str;
                    while (finished) {
                        str = bReader.readLine();
                        if (str.trim().equals("中斷連線")) {
                            if (showText.getText().isEmpty()) {
                                showText.setText(str);//列出該行的內容
                            } else {
                                showText.setText(showText.getText() + "\r\n" + str);//列出該行的內容
                                showText.setCaretPosition(showText.getDocument().getLength()); //將滾動軸拉到最底
                            }
                        } else if (!str.split(":")[1].trim().equals("")) {
                            if (showText.getText().isEmpty()) {
                                showText.setText(str);//列出該行的內容
                            } else {
                                showText.setText(showText.getText() + "\r\n" + str);//列出該行的內容
                                showText.setCaretPosition(showText.getDocument().getLength()); //將滾動軸拉到最底
                            }
                        }
                    }

                    op.close();
                    socketTCPClient();
                } catch (IOException ex) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException ex1) {
                        System.out.println("SocketTCPClient Sleep : " + ex1);
                    }

                    finished = true;
                    socketTCPClient();
                }
            }
        };
        thread.start();
    }

    private void btnSetIPActionPerformed(ActionEvent evt) {
        String s = (String) JOptionPane.showInputDialog(//彈跳視窗設定
                null, "請輸入對象IP ex:127.0.0.1",
                "IP設定", JOptionPane.PLAIN_MESSAGE, null, null, null);
        if (!s.equals(recIP)) {
            if ((s != null) && (s.length() > 0)) { //不為空 長度大於0         
                if (firstCon != 0) {
                    finished = false;
                    if (op != null) {
                        op.println("連線測試");
                        op.flush();
                    }
                }
                firstCon = 1;
                recIP = s;
            }
        }
    }

    private void textInputActionPerformed(ActionEvent evt) {
        if (!textInput.getText().trim().equals("")) {
            if (showText.getText().isEmpty()) {
                showText.setText("本機 : " + textInput.getText());
            } else {
                showText.setText(showText.getText() + "\r\n本機 : " + textInput.getText());
            }
        }
        op.println(textInput.getText());
        op.flush();
        textInput.setText("");
    }
}
