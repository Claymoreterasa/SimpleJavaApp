import java.util.Date;

/**
 * Created by Administrator on 2016/7/22.
 */
public class ProcessStatus {
    private String name;
    private String stdout;
    private String stderr;
    private String status;

    public ProcessStatus(String name){
        this.name=name;
        this.stdout="";
        this.stderr="";
        this.status="0";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void updateStatus(String stdout,String stderr,String status){
        this.stdout+=stdout;
        this.stderr+=stderr;
        this.status=status;
    }

    public void updateStatusByFlag(int flag,String line){
        if(flag==1){
            this.stdout+=line;
            this.stdout+=System.lineSeparator();
        }else if(flag==2){
            this.stderr+=line;
            this.stderr+=System.lineSeparator();
        }
    }

    public void clearStatus(){
        this.stdout="";
        this.stderr="";
    }

    @Override
    public String toString() {
        return "ProcessStatus{" +
                "stdout='" + stdout + '\'' +
                ", stderr='" + stderr + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public ProcessStatus clone(){
        ProcessStatus processStatus =new ProcessStatus(this.name);
        processStatus.setStdout(this.stdout);
        processStatus.setStderr(this.stderr);
        processStatus.setStatus(this.status);
        return  processStatus;
    }
}
