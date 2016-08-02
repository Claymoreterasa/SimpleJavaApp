package ProcessTools;

import java.io.*;
import java.util.Map;

/**
 * Created by cwz on 2016/7/13.
 * @author  cwz
 * @description 调用进程执行命令
 */

public class ProcessUtils {
    public static Process executeCommand(String[] command, File workingDir, Map<String, String> extraEnvironment, boolean redirectStderr) {
        ProcessBuilder builder = new ProcessBuilder(command).directory(workingDir);
        Map<String, String> environment = builder.environment();
        for (String key : extraEnvironment.keySet()) {
            environment.put(key, extraEnvironment.get(key));
        }

        Process process = null;
        try {
            process = builder.start();
            if (redirectStderr) {
                String threadName = " stderr for command " + command[0];
                processStreamByLine(threadName, process.getErrorStream(), LineOperators.logLineOperator);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return process;
    }

    //执行进程，stdout输出返回String，stderr输出到log
    public static String executeAndGetOutput(String[] command, File workingDir, Map<String, String> extraEnvironment, boolean redirectStderr) {
        Process process = executeCommand(command, workingDir, extraEnvironment, redirectStderr);
        final StringBuffer output = new StringBuffer();
        String threadName = " read stdout for " + command[0];

        Thread stdoutThread = processStreamByLine(threadName, process.getInputStream(), new LineOperator() {

            public void processLine(String line) {
                output.append(line);
            }
        });

        try {
            int exitCode = process.waitFor();
            stdoutThread.join();
            if (exitCode != 0) {
                System.err.printf("Process %s exited with exitCode %d: %s", command[0], exitCode, output.toString());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return output.toString();
    }

    public static Thread processStreamByLine(String threadName, final InputStream inputStream, final LineOperator lineOperator) {
        Thread t = new Thread(threadName) {
            @Override
            public void run() {
                BufferedInputStream bufferedInputStream = null;
                BufferedReader bufferedReader = null;
                try {
                    bufferedInputStream = new BufferedInputStream(inputStream);
                    bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream, "UTF-8"));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        lineOperator.processLine(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }
            }
        };
        t.setDaemon(true);
        t.start();
        return t;
    }


    public static boolean waitForProcess(Process process, long timeoutMs) {
        boolean terminated = false;
        long startTime = System.currentTimeMillis();
        while (!terminated) {
            try {
                process.exitValue();
                terminated = true;
            } catch (IllegalThreadStateException e) {
                // process not terminated
                if (System.currentTimeMillis() - startTime > timeoutMs) {
                    return false;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    System.err.println(" something bad happen in wait for process");
                }
            }
        }
        return terminated;
    }

    public static String getStderr(Process process, long timeoutMs) {
        boolean terminated = waitForProcess(process, timeoutMs);
        StringBuffer err = new StringBuffer();
        if (terminated) {
            BufferedInputStream bufferedInputStream = null;
            BufferedReader bufferedReader = null;
            try {
                bufferedInputStream = new BufferedInputStream(process.getErrorStream());
                bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream, "UTF-8"));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    err.append(line);
                    err.append(System.lineSeparator());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return err.toString();
        } else {
            return null;
        }
    }

    public static void main(String[] args){
 //       String[] command ={"java","-cp","cwz-1.0-jar-with-dependencies.jar","xidian.ContractAudioFromVideo","E:\\视频\\视频\\罪恶之夜.The.Night.Of.S01E01.720p.HDTV.双语字幕-深影字幕组.mp4","./civilnight.mp3","0:00","1:20"};
//       ProcessBuilder processBuilder = new ProcessBuilder(command);
//        try {
//            Process process=processBuilder.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        String s=executeAndGetOutput(command,new File("."),new HashMap<String, String>(),true);
//        System.out.println(s);
    }
}

