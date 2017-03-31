package service;

import thread.ParseUrlThread;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 获取地址的业务线程
 */
public class ParseUrlService
        implements Runnable {


    private boolean flag = true;//执行标识

    private static ParseUrlService instance = null;
    private String startUrl = "http://image.baidu.com/";
    public LinkedList<String> pageUrls = new LinkedList();//链表

    public static ParseUrlService getInstance() {//单例，实现线程共享数据
        if (instance == null) {
            instance = new ParseUrlService();
        }
        return instance;
    }

    public void run() {
        System.out.print("flag:" + flag);

        this.pageUrls.add(this.startUrl);
        //开启10个线程池
        ExecutorService pool = Executors.newFixedThreadPool(10);
        while (flag) {
            String url = (String) this.pageUrls.poll();//获得并移除
            if (url == null) {//如果生产者没有获取到链接就等待获取到
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            //消费这个链接（得到图片们和新的下载链接）
            ParseUrlThread pageThread = new ParseUrlThread();
            pageThread.setUrl(url);
            pool.execute(pageThread);
        }
    }

    /**
     * url去重
     *
     * @param pageUrl
     */
    public void addUel(String pageUrl) {
        synchronized (this.pageUrls) {
            if (!this.pageUrls.contains(pageUrl))
                this.pageUrls.add(pageUrl);
        }
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}