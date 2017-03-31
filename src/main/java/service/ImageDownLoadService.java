package service;

import thread.ImageDownLoadThread;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 数据处理线程，
 * 这里是拿到了想要的图片链接并下载
 */
public class ImageDownLoadService
        implements Runnable {
    private static ImageDownLoadService instance = null;
    private static LinkedList<String> imgUrls = new LinkedList();//下载的图片列表
    private static ParseUrlService parseUrlService = ParseUrlService.getInstance();
    public static ImageDownLoadService getinstance() {//单例
        if (instance == null) {
            instance = new ImageDownLoadService();
        }
        return instance;
    }

    public void run() {
        ExecutorService pool = Executors.newFixedThreadPool(20);
        while (true) {
            String url = (String) imgUrls.poll();
            if (url == null) {
                try {
                    System.out.println("ImageDownLoadService WAIT()");
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                continue;
            }
            if(imgUrls.size()>100){
                System.out.println("达到100最大就不执行搜索了");
                parseUrlService.setFlag(false);
            }else{
                parseUrlService.setFlag(true);
            }
            System.out.println("IMG\t" + url);
            //消费这个图片
            ImageDownLoadThread imgThread = new ImageDownLoadThread();
            imgThread.setUrl(url);
            pool.execute(imgThread);

        }
    }

    /**
     * 去重
     * @param imgUrl
     */
    public void addImg(String imgUrl) {
        synchronized (imgUrls) {
            if (!imgUrls.contains(imgUrl))
                imgUrls.add(imgUrl);
        }
    }
}
