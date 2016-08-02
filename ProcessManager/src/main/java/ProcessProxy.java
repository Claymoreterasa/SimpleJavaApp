import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Administrator on 2016/7/22.
 */
public class ProcessProxy {
    private String serverIP;
    private int serverPort;
    private Socket client;
    private ProcessContainer container;
    private DataInputStream dis ;
    private DataOutputStream dos;

    public ProcessProxy(String ip,int port){
        this.serverIP=ip;
        this.serverPort=port;
        try {
            this.client=new Socket(serverIP,serverPort);
            this.container=new ProcessContainer();
            this.dis=new DataInputStream(client.getInputStream());
            this.dos=new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void communiteWithServer(){
        try {
            while(true){
               String command = dis.readUTF();
               Object returnValue = executeCommand(command);

                dos.writeUTF(returnValue.toString());
                dos.flush();
            }
        } catch (IOException e) {
            try {
                dis.close();
                dos.close();
                client.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public Object executeCommand(String command){
        return null;
    }


}
