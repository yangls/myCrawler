package thread;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import service.ImageDownLoadService;
import service.ParseUrlService;
import util.HttpsUtils;

/**
 * 对单个url的处理过程
 * target:1.生产url
 *        2.获取页面所需信息（图片们）
 */
public class ParseUrlThread
        implements Runnable {

    public static String HTTP="http://";
    public static String HTTPS="https://";

    public static int THR_URL = 1;
    public static int THE_IMAGE = 2;

    private static ParseUrlService parseUrlService;//解析url的业务
    private static ImageDownLoadService downLoadService = ImageDownLoadService.getinstance();//静态实例，处理数据的业务

    HttpClient httpClient = HttpClients.createDefault();
    private String url = null;

    static {
        parseUrlService = ParseUrlService.getInstance();
    }

    public void setUrl(String url) {
        this.url = url;
    }
/*

    public static void main(String[] args) {
        ParseUrlThread thread = new ParseUrlThread();
        thread.url = "http://image.baidu.com/";
        thread.run();
    }
*/

    public void run() {
        System.out.println("#PAGE:" + this.url);
        //域名获取（从0开始，在第一次出现//这字符之后后2为开始遇到/为止的字符）
        String start = this.url.substring(0, this.url.indexOf("/", this.url.indexOf("//") + 2));
        StringBuilder sb = new StringBuilder();

        if(start.contains("https")){
            httpClient = HttpsUtils.createSSLClientDefault();
        }
        HttpGet httpget =  new HttpGet(this.url);
        httpget.setHeader("Accept-Encoding", "identity");
        try {
            HttpResponse resp = this.httpClient.execute(httpget);
            HttpEntity entity = resp.getEntity();

            InputStream is = entity.getContent();
            int count = -1;
            while ((count = is.read()) != -1) {
                sb.append((char) count);
            }
            System.out.println(new String (sb.toString().getBytes("iso-8859-1"),"gbk"));
            //正则获取
            String urlReg = "<a.*href=\"(.*?)\"";
            List<String> urls = getAllUrl(sb.toString(), urlReg, start,THR_URL);
            //获取要爬的链接
            for (String pageUrl : urls) {
                parseUrlService.addUel(pageUrl);
            }
            //图片们，要处理的数据
            String imgReg = "<img.*src=\"(.*?)\"";
            List<String> imgs = getAllUrl(sb.toString(), imgReg, start,THE_IMAGE);

            for (String img : imgs) {
                downLoadService.addImg(img);
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取页面上的链接
     * @param content 网页内容
     * @param reg 正则
     * @param start 域名
     * @return 该页面上的匹配正则的链接
     */
    public List<String> getAllUrl(String content, String reg, String start, int urlKind) {
        List urls = new ArrayList();

        Matcher matcher = Pattern.compile(reg).matcher(content);
        while (matcher.find()) {
            String url = matcher.group(1);
            if (!url.startsWith("http")&& urlKind==THR_URL) {
                url = start + url;
            }
            if(!url.startsWith("http")&& urlKind==THE_IMAGE){
                url = HTTPS + url;
            }
            urls.add(url);
        }
        return urls;
    }
}