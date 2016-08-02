import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Administrator on 2016/7/18.
 */
public class DownloadThread implements Runnable{
    private String name;
    private String dir;
    public DownloadThread(String name,String dir){
        this.name=name;
        this.dir=dir;
    }
    public String getName(){
        return this.name;
    }

    public synchronized static String getUrl(){
        return DblpDownload.downloadUrls.poll();
    }
    public void run(){
        String url;
        while((url=getUrl())!=null){
            download(url);
            System.out.println(name + ":" + url);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void download(String url){
        try {
            URL downloadUrl = new URL(url);
            URLConnection conn = downloadUrl.openConnection();
            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream(dir+"/"+url.substring(url.lastIndexOf("/")+1),true);

            byte[] buffer = new byte[1204];
            int byteread = 0;
            while ((byteread = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
