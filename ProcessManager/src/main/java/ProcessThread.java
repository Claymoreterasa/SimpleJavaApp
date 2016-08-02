

/**
 * Created by Administrator on 2016/7/22.
 */
public class ProcessThread implements Runnable{
    private String processName;
    private String processCommand;
    private ProcessStatus ProcessStatus;
    public ProcessThread (String processName,String processCommand,ProcessStatus processStatus){
        this.processName=processName;
        this.processCommand=processCommand;
        this.ProcessStatus=processStatus;
    }

    public void run(){

    }
}
