import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2016/7/18.
 */

public class ScriptTest {
    public static void main(String[] args){
        WebClient webClient = new WebClient();
        webClient.getOptions().setCssEnabled(true);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.waitForBackgroundJavaScript(60*1000);

        try {
            HtmlPage retrievalPage = webClient.getPage("http://dblp.uni-trier.de/search?q=vehicle%20predict");
            String javaScript = "scrollTo(0,document.body.scrollHeight)";

            ScriptResult result = retrievalPage.executeJavaScript(javaScript);
            webClient.waitForBackgroundJavaScript(60*1000);
            System.out.println(result.getJavaScriptResult());
            System.out.println(result.toString());
            HtmlPage page = (HtmlPage)result.getNewPage();
            page.save(new File("./x.html"));

            //  System.out.println(page.asXml());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
