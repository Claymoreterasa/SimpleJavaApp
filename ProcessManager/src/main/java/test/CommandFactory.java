package test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/7/25.
 */
public class CommandFactory {
    private String baseDir = "./jars";
    private List<String> commandBase;
    private HashMap<String,String> name2jar=new HashMap<String,String>();
    private HashMap<String,List<String>> name2args=new HashMap<String,List<String>>();

    public CommandFactory(){
        commandBase=new ArrayList<String>();
        commandBase.add("java");
        commandBase.add("-cp");

        name2jar.put("video2studio", "cwz-1.0-jar-with-dependencies.jar");
        name2jar.put("video2pic", "cwz-1.0-jar-with-dependencies.jar");
        ArrayList video2studioArgs = new ArrayList();
        video2studioArgs.add("xidian.ContractAudioFromVideo");
        video2studioArgs.add("E:/1.mp4");
        video2studioArgs.add("./test1.mp3");
        video2studioArgs.add("0:00");
        video2studioArgs.add("2:00");

        name2args.put("video2studio",video2studioArgs);
    }

    public List<String> getCommand(String name){
        ArrayList<String> command = new ArrayList<String>();
        command.addAll(commandBase);
        command.add(name2jar.get(name));
        command.addAll(name2args.get(name));
        return  command;
    }

    public List<String> getCommand(String name,String args){
        ArrayList<String> command = new ArrayList<String>();
        command.addAll(commandBase);
        command.add(name2jar.get(name));
        for(String arg:args.split(" ")){
            command.add(arg);
        }
        System.out.println(command);
        return  command;
    }
}
