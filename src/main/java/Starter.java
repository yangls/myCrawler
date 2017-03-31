import service.ImageDownLoadService;
import service.ParseUrlService;

import java.io.File;

public class Starter {
    public static void main(String[] args) {
        Starter starter = new Starter();
        starter.run();
    }

    /**
     * 爬虫和业务处理必然是多线程对多线程的product-customer 关系
     */
    public void run() {
        File file = new File("D:\\pic\\");
        if (!file.exists())
            file.mkdir();
        //登录判断
        //url,生产者，获取要去查的链接并在页面获取所需的数据（爬虫流程）
        ParseUrlService parseUrl = ParseUrlService.getInstance();
        Thread parseThread = new Thread(parseUrl);
        parseThread.start();
        //消费者，拿到图的链接去下载（业务处理流程）
        ImageDownLoadService imageDownLoad = ImageDownLoadService.getinstance();
        Thread imgDownThread = new Thread(imageDownLoad);
        imgDownThread.start();
    }
}
