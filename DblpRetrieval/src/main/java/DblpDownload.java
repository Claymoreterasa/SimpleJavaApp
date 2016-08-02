import ProcessTools.ProcessUtils;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/7/18.
 */
public class DblpDownload {
    public static LinkedList<String> downloadUrls = new LinkedList<String>();
    public static String serachBaseUrl = "http://dblp.dagstuhl.de/search/publ/api?q=";
    public static String resultBaseUrl ="http://dblp.dagstuhl.de/rec/ris/";
    public static String dir;
  //  public static ExecutorService executorService = Executors.newCachedThreadPool();

    public static String constructUrlByKeyWord(String keyWord,int startIndex,int step){
        return serachBaseUrl+convertKeyword(keyWord)+"&h="+step+"&f="+startIndex+"&format=xml";
    }


    public static String convertKeyword(String keyword){
        StringBuffer result = new StringBuffer();
        char[] characters=keyword.toCharArray();
        for(char c:characters) {
            if (c == ' ') {
                result.append("%20");
                continue;
            }
            if (c=='|'){
                result.append("%7C");
                continue;
            }

            if(c==':'){
                result.append("%3A");
                continue;
            }
            result.append(c);

        }
        return result.toString();
    }

    public static String convertResultUrl(String resultUrl){
        int recIndex = resultUrl.indexOf("rec");
        return  resultBaseUrl+resultUrl.substring(recIndex+4)+".ris";
    }


    public static void getUrlByNode(NodeList urls){
        for(int i=0;i<urls.getLength();i++){
            Node hit = urls.item(i);
            String url =hit.getTextContent();
            if(url.contains("/")){
                downloadUrls.add(convertResultUrl(url));
            }
        }
    }
    public static void getResulturl(String keyword){
        File directory = new File("./"+convertKeyword(keyword));
        if(!directory.exists()){
            directory.mkdir();
        }
        dir="./"+convertKeyword(keyword);
        StringBuffer xml = new StringBuffer();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document document =dbBuilder.parse(constructUrlByKeyWord(keyword, 0, 1000));

            NodeList nList = document.getElementsByTagName("hits");
            String total = nList.item(0).getAttributes().getNamedItem("total").getNodeValue();
            int totalCount = Integer.parseInt(total);
            int forTimes =(int) Math.ceil(1.0*totalCount/1000);
            getUrlByNode(document.getElementsByTagName("url"));
            for(int i=1;i<forTimes;i++){
                document =dbBuilder.parse(constructUrlByKeyWord(keyword, i * 1000 , 1000));
                getUrlByNode(document.getElementsByTagName("url"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(downloadUrls.size()==0){
            System.out.println("no found");
        }else {
            System.out.println("start download");
            startDownload(1);
        }
        downloadUrls.clear();
    }


    public static void startDownload(int numThreads){
        ExecutorService executorService =Executors.newCachedThreadPool();
        for(int i=1;i<=numThreads;i++){
            DownloadThread downloadThread = new DownloadThread("Thread "+i,dir);
            executorService.execute(downloadThread);
        }
        executorService.shutdown();
        while(!executorService.isTerminated()){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mergeResult();
    }

    public static void mergeResult(){
        String[] command = {"cmd","/c","copy *.ris result.ris"};
        String result= ProcessUtils.executeAndGetOutput(command, new File(dir), new HashMap<String, String>(), false);
        System.out.println(result);
    }

    public static void main(String[] args) {
        String A1[] = {"TODS","TOIS","TKDE","VLDBJ"};
        String A2[] = {"SIGMOD","SIGKDD","SIGIR","VLDB","ICDE"};
        String B1[] = {"TKDD","AEI","DKE","DMKD","EJIS","IPM","IS","JASIST",
        "JWS","KIS","TWEB"};
        String B2[] = {"CIKM","PODS","DASFAA","ECML-PKDD","ISWC","ICDM",
        "ICDT","EDBT","CIDR","SDM","WSDM"};
        String C1[] = {"DPD","I&M","IPL","IR","IJCIS","IJGIS","IJIS","IJKM",
        "IJSWIS","JCIS","JDM","JGITM","JIIS","JSIS"};
        String C2[] ={"DEXA","ECIR","WebDB","ER","MDM","SSDBM","WAIM","SSTD",
        "PAKDD","APWeb","WISE","ESWC"};
        String input = new Scanner(System.in).nextLine();
        String ops = input.substring(input.lastIndexOf(' ')+1);
        if(ops.equals("a1")){
            for(String s : A1){
                String keyword = input.substring(0,input.lastIndexOf(' ')) + " venue:"+s+":";
                getResulturl(keyword);
            }
        }else if(ops.equals("b1")){
            for(String s : B1){
                String keyword = input.substring(0,input.lastIndexOf(' ')) + " venue:"+s+":";
                getResulturl(keyword);
            }
        }else if(ops.equals("c1")){
            for(String s : C1){
                String keyword = input.substring(0,input.lastIndexOf(' ')) + " venue:"+s+":";
                getResulturl(keyword);
            }
        }else if(ops.equals("a2")){
            for(String s : A2){
                String keyword = input.substring(0,input.lastIndexOf(' ')) + " venue:"+s+":";
                getResulturl(keyword);
            }
        }else if(ops.equals("b2")){
            for(String s : B2){
                String keyword = input.substring(0,input.lastIndexOf(' ')) + " venue:"+s+":";
                getResulturl(keyword);
            }
        }else if(ops.equals("c2")){
            for(String s : C2){
                String keyword = input.substring(0,input.lastIndexOf(' ')) + " venue:"+s+":";
                getResulturl(keyword);
            }
        }else{
            getResulturl(input);
        }

       // getResulturl("social.networks influence");
//        System.out.println(downloadUrls.size());
//        if(downloadUrls.size()==0){
//            System.out.println("no found");
//        }else {
//            System.out.println("start download");
//            startDownload(4);
//        }
//
    }



}
