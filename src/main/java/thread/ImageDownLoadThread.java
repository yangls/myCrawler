package thread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

/**
 * 单个图片下载的线程
 */
public class ImageDownLoadThread
        implements Runnable {

    private String url;
    private String path = "D:\\pic\\";

    public void setUrl(String url) {
        this.url = url;
    }

    public void run() {
        try {
            System.out.println("#IMG:" + this.url);
            String fileName = this.url.substring(this.url.lastIndexOf("/") + 1);
            HttpClient client = HttpClients.createDefault();
            HttpGet get = new HttpGet(this.url);
            HttpResponse resp = client.execute(get);

            HttpEntity entity = resp.getEntity();
            long length = entity.getContentLength();
            if (length > 30720L)
                if (length > 2147483647L) {
                    System.err.println("##ERRO文件长度超长" + length);
                } else {
                    byte[] bytes = new byte[(int) length];
                    InputStream is = entity.getContent();

                    int count = -1;
                    int num = 0;
                    while ((count = is.read()) != -1) {
                        bytes[(num++)] = (byte) count;
                    }
                    File storeFile = new File(this.path + fileName);

                    FileOutputStream output = new FileOutputStream(storeFile);
                    output.write(bytes);
                    output.flush();
                    is.close();
                    output.close();
                }
        } catch (Exception e) {
            System.out.println(this.url + " is erro!");
        }
    }
}
