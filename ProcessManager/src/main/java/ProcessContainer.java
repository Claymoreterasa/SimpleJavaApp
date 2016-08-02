import test.CommandFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/22.
 */
public class ProcessContainer {
    private HashMap<String,Process> nameToProcess = new HashMap<String,Process>();
    private HashMap<String,ProcessStatus> nameToStatus = new HashMap<String,ProcessStatus>();
    private HashMap<String,String> nameToDate = new HashMap<String,String>();
    private CommandFactory commandFactory = new CommandFactory();
    public  int count=0;

    public boolean shutdownProcess(String name){
        if(nameToProcess.containsKey(name)){
            Process process=nameToProcess.get(name);
            process.destroy();
            nameToProcess.remove(name);
            nameToStatus.remove(name);
        }
        return true;
    }

    public boolean startProcess(String name){
        List<String> command = commandFactory.getCommand(name);
        ProcessStatus processStatus = new ProcessStatus(name);

        Process process = executeCommand(command, new File("."), new HashMap<String, String>(), processStatus);
        nameToProcess.put(name,process);
        nameToStatus.put(name,processStatus);
        nameToDate.put(name,new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()));
        return true;
    }

    public boolean startProcess(String name,String args){
        List<String> command = commandFactory.getCommand(name,args);
        ProcessStatus processStatus = new ProcessStatus(name);
        Process process = executeCommand(command, new File("."), new HashMap<String, String>(), processStatus);
        nameToProcess.put(name,process);
        nameToStatus.put(name,processStatus);
        nameToDate.put(name,new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()));
        return true;
    }

    public ProcessStatus getProcessStatus(String name){
        if(!nameToProcess.containsKey(name)){
            ProcessStatus result = new ProcessStatus("name");
            result.setStatus("0");
            return result;
        }

        ProcessStatus returnStatus;
        Process process =nameToProcess.get(name);
        ProcessStatus processStatus = nameToStatus.get(name);
        synchronized (processStatus){
            processStatus.setStatus("0");
        }
        try {
            process.exitValue();
        } catch (IllegalThreadStateException e) {
            synchronized (processStatus){
                processStatus.setStatus("1");
            }
        }
        synchronized (processStatus){
            writeStatusToFile(name,processStatus);
            returnStatus=processStatus.clone();
            processStatus.clearStatus();
        }

        if(processStatus.getStatus().equals("0")){
            nameToProcess.remove(name);
            nameToStatus.remove(name);
        }
        return returnStatus;
    }

    public  Process executeCommand(List<String> command, File workingDir, Map<String, String> extraEnvironment,ProcessStatus processStatus) {
        ProcessBuilder builder = new ProcessBuilder(command).directory(workingDir);
        Map<String, String> environment = builder.environment();
        for (String key : extraEnvironment.keySet()) {
            environment.put(key, extraEnvironment.get(key));
        }

        Process process = null;
        try {
            process = builder.start();
            int outFlag=1;
            processStreamByLine(outFlag, process.getInputStream(), processStatus);

            int errFlag=2;
            processStreamByLine(errFlag, process.getErrorStream(), processStatus);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return process;
    }

    public  Thread processStreamByLine(final int flag, final InputStream inputStream, final ProcessStatus processStatus) {
        Thread t = new Thread(){
            @Override
            public void run() {
                BufferedInputStream bufferedInputStream = null;
                BufferedReader bufferedReader = null;
                try {
                    bufferedInputStream = new BufferedInputStream(inputStream);
                    bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream, "UTF-8"));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        //System.out.println(line);
                        count++;
                        synchronized (processStatus){
                            processStatus.updateStatusByFlag(flag,line);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        t.setDaemon(true);
        t.start();
        return t;
    }

    public void writeStatusToFile(String name,ProcessStatus processStatus){
        String dirName = "./"+name+nameToDate.get(name);
        File dir = new File(dirName);
        if(!dir.exists()){
            dir.mkdir();
        }
        try {

            BufferedWriter stdoutWriter = new BufferedWriter(new FileWriter(dirName+"/stdout.txt",true));
            BufferedWriter stderrWriter = new BufferedWriter(new FileWriter(dirName+"/stderr.txt",true));

            stdoutWriter.write(processStatus.getStdout());
            stderrWriter.write(processStatus.getStderr());
            stdoutWriter.flush();
            stderrWriter.flush();
            stdoutWriter.close();
            stderrWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) throws Exception{
        ProcessContainer processContainer = new ProcessContainer();
        processContainer.startProcess("video2studio");
        processContainer.startProcess("video2pic","xidian.ContractPicsFromVideo E:/1.mp4 ./pics 1:00 3:00 1000");
        while(true){
            Thread.sleep(3000);
            ProcessStatus status=processContainer.getProcessStatus("video2studio");
            ProcessStatus status2=processContainer.getProcessStatus("video2pic");
            System.out.println(status);
            System.out.println(status2);
            if(status.getStatus().equals("0")&&status2.getStatus().equals("0")){
                break;
            }

        }

        System.out.println(processContainer.count);
    }
}
