package com.hdtx.androidsdk.androidsdk.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xiaoqing.zhou on 2018/8/21.
 */

public class ThreadPoolManager {
    private ExecutorService service;
    private static ThreadPoolManager manager;

    private ThreadPoolManager() {
        int num = Runtime.getRuntime().availableProcessors();
        this.service = Executors.newFixedThreadPool(num * 2);
    }

    public static ThreadPoolManager getInstance() {
        if (manager == null) {
            synchronized (ThreadPoolManager.class) {
                if (manager == null) {
                    manager = new ThreadPoolManager();
                }
            }
        }
        return manager;
    }

    public void addTask(Runnable runnable) {
        this.service.submit(runnable);
    }
}
