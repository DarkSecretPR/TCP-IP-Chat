
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class ServerThread extends Thread {

    private final Socket sc;
    private final JTextArea showText, showUser;
    private JLabel labNumber;
    private PrintWriter op;
    private InputStream in;
    private BufferedReader bReader;
    private static ArrayList<PrintWriter> opArr = new ArrayList<>();
    private ArrayList<String> userArr = new ArrayList<>();
    private String tmpCust;
    private static int onlineNumber = 0;

    ServerThread(Socket sc, JTextArea showText, JTextArea showUser, JLabel labNumber, PrintWriter op) {
        this.sc = sc;
        this.showText = showText;
        this.showUser = showUser;
        this.labNumber = labNumber;
        this.op = op;
    }

    @Override
    public synchronized void run() {
        try {
            showUser.setText(showUser.getText() + "\r\n客戶端連線 " + ((sc.getRemoteSocketAddress()).toString().split("/")[1]));
            showUser.setCaretPosition(showUser.getDocument().getLength()); //將滾動軸拉到最底
            tmpCust = (sc.getRemoteSocketAddress()).toString().split("/")[1];
            //取得對客戶端的資料流
            op = new PrintWriter(sc.getOutputStream());//送出
            in = sc.getInputStream();
            bReader = new BufferedReader(new InputStreamReader(in));//接收
            op.println(tmpCust);
            op.flush();            
            labNumber.setText("");  
            userArr.add(tmpCust);            
            opArr.add(op);
            
            onlineNumber++;   
            labNumber.setText(String.valueOf(onlineNumber));
            String str;
            int length;
            while (true) {
                str = bReader.readLine();
                if(str.trim().equals("連線測試")){
                    op.println("中斷連線");
                    op.flush();
                }
                if (!str.trim().equals("")) {
                    if (showText.getText().isEmpty()) {
                        showText.setText(tmpCust + " : " + str);
                    } else {
                        showText.setText(showText.getText() + "\r\n" + tmpCust + " : " + str);//接收來自使用者資料 顯示於伺服器端textarea
                        showText.setCaretPosition(showText.getDocument().getLength()); //將滾動軸拉到最底
                    }
                }

                length = opArr.size();//目前有幾位連線使用者                    
                if (length >= 2) { //判斷2人以上 才能成立多人聊天室
                    for (int i = 0; i < opArr.size(); i++) {//取得所有連線使用者
                        if (opArr.get(i) == op) { //判斷是否為某使用者輸入資料
                            for (int j = 0; j < opArr.size(); j++) {//取得所有連線使用者 第二次判斷
                                if (i != j) { //除了目前使用者外 發送給其他使用者資料
                                    opArr.get(j).println(tmpCust + " : " + str);
                                    opArr.get(j).flush();                                    
                                }
                            }
                        }
                    }
                }                             
            }
        } catch (IOException | NullPointerException ex) {
            showUser.setText(showUser.getText() + "\r\n" + tmpCust + "離開連線");
            showUser.setCaretPosition(showUser.getDocument().getLength()); //將滾動軸拉到最底
            System.out.println(tmpCust + "離開連線");            
            labNumber.setText("");                
            onlineNumber--;  
            labNumber.setText(String.valueOf(onlineNumber));                           
        }
    }

    public ArrayList getOP() {
        return opArr;
    }
}
