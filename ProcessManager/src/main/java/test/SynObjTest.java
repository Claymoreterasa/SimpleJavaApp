package test;

import java.io.BufferedReader;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/7/22.
 */
public class SynObjTest {
    public Integer i=1000;
    public Integer addOne(){
        return  i++;
    }
    public Integer minusOne(){
        return  i--;
    }
    public static void main(String[] args){
        SynObjTest x =new SynObjTest();
        new Thread(new AddThread(x,"Thread-1")).start();
        new Thread(new AddThread(x,"Thread-2")).start();
        for(int i=0;i<=10000;i++){
            synchronized (x){
                System.out.println( "Main:" + x.minusOne());
            }
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(x.i);

        InputStream inputStream;

    }
}

class AddThread implements Runnable{
    private SynObjTest synObjTest;
    private String name;
    public AddThread(SynObjTest synObjTest,String name){
        this.synObjTest=synObjTest;
        this.name=name;
    }

    public void run(){
            for (int j = 0; j < 1000; j++) {
                synchronized (synObjTest) {
                System.out.println(name + ":" + synObjTest.addOne());
            }
        }
    }
}
