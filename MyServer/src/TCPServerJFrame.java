
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class TCPServerJFrame extends JFrame {

    private PrintWriter op;
    private ServerThread st;
    private ArrayList<PrintWriter> opArr = new ArrayList<>();

    private JLabel jLabel1, jLabel2;
    private JScrollPane jScrollPane1, jScrollPane2;
    private JLabel labConnect, labNumber;
    private JTextArea showText, showUser;
    private JTextField textInput;

    TCPServerJFrame() {
        initComponents();
        socketAccept();
    }

    private void initComponents() {
        textInput = new JTextField();
        jScrollPane1 = new JScrollPane();
        showText = new JTextArea();
        jScrollPane2 = new JScrollPane();
        showUser = new JTextArea();
        labConnect = new JLabel();
        labNumber = new JLabel();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("多人聊天伺服器");
        setResizable(false);
        setBackground(new Color(51, 51, 255));
        setCursor(new java.awt.Cursor(Cursor.DEFAULT_CURSOR));

        textInput.setBackground(new Color(0, 204, 255));
        textInput.setFont(new Font("標楷體", 0, 18));
        textInput.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae) {
                textInputActionPerformed(ae);
            }          
        });
        showText.setBackground(new Color(0, 51, 153));
        showText.setColumns(20);
        showText.setFont(new Font("標楷體", 0, 14));
        showText.setForeground(new Color(255, 255, 255));
        showText.setRows(5);
        showText.setEditable(false);
        jScrollPane1.setViewportView(showText);

        showUser.setBackground(new Color(0, 153, 255));
        showUser.setColumns(20);
        showUser.setFont(new Font("標楷體", 0, 14));
        showUser.setRows(5);
        showUser.setEditable(false);
        showUser.setCaretColor(new Color(153, 153, 255));
        showUser.setSelectedTextColor(new Color(0, 0, 0));
        jScrollPane2.setViewportView(showUser);

        labConnect.setFont(new Font("標楷體", 0, 14));
        labConnect.setText("連線狀態　在線人數：");

        labNumber.setText("0");

        jLabel2.setFont(new Font("標楷體", 0, 14));
        jLabel2.setText("聊天視窗");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(textInput)
                .addGroup(layout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(labConnect, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(labNumber, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE))
                                .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 273, GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addContainerGap())
                                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 367, GroupLayout.PREFERRED_SIZE))))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(labConnect)
                                .addComponent(labNumber)
                                .addComponent(jLabel2))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                                .addComponent(jScrollPane2))
                        .addGap(0, 0, 0)
                        .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 0, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(textInput, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE))
        );
        pack();
    }

    private void socketAccept() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    ServerSocket ss = new ServerSocket(1668);
                    showUser.setText("伺服端正常啟動");
                    while (true) {
                        Socket sc = ss.accept();
                        st = new ServerThread(sc, showText, showUser, labNumber, op);
                        st.start();
                        opArr = st.getOP();
                    }
                } catch (Exception e) {
                    System.out.println("SocketAccept : " + e);
                    showUser.setText("伺服器啟動失敗");
                }
            }
        };
        thread.start();
    }

    private void textInputActionPerformed(ActionEvent evt) {
        if (!textInput.getText().trim().equals("")) {
            if (showText.getText().isEmpty()) {
                showText.setText("本機 : " + textInput.getText());
            } else {
                showText.setText(showText.getText() + "\r\n本機 : " + textInput.getText());
            }
        }
        for (PrintWriter opArr1 : opArr) {
            opArr1.println("伺服器端 : " + textInput.getText());
            opArr1.flush();
        }
        textInput.setText("");
    }
}
