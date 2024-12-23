package com.liferay.mcv.example.parallel;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import java.lang.management.ManagementFactory;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;

import org.osgi.service.component.annotations.Component;

import com.liferay.mcv.example.parallel.constants.CustomPortletKeys;

/**
 * @author marcialcalvo
 */

@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=Custom",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + CustomPortletKeys.CUSTOM,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class CustomPortlet extends MVCPortlet {

    private static final int THREAD_INCREMENT = 10;
    private static final int MAX_THREADS = 100;
    private static final int QUEUE_CAPACITY = 200; // queue tasks

    private static final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
            MAX_THREADS,        
            MAX_THREADS,     
            60L,               
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(QUEUE_CAPACITY) 
    );

    private static final String MBEAN_NAME = "com.liferay.mcv.example.parallel:type=ThreadPool,name=ParallelThreadsPool";

    @Override
    public void init() throws PortletException {
        super.init();
        registerMBean();
    }

    @Override
    public void destroy() {
        super.destroy();
        executorService.shutdown();
    }

    @Override
    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) {
        submitTasks();
    }

    private void submitTasks() {
        for (int i = 0; i < THREAD_INCREMENT; i++) {
            int threadNum = executorService.getPoolSize() + i + 1;
            try {
                executorService.submit(() -> {
                    long endTime = System.currentTimeMillis() + 30000; // 30 segundos
                    while (System.currentTimeMillis() < endTime) {
                        System.out.println("Thread #" + threadNum + " is running.");
                    }
                });
            } catch (RejectedExecutionException e) {
                System.out.println("Task rejected: " + e.getMessage());
            }
        }
    }

    private void registerMBean() {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName = new ObjectName(MBEAN_NAME);
            ThreadPoolMetrics metrics = new ThreadPoolMetrics(executorService);
            if (!mBeanServer.isRegistered(objectName)) {
                mBeanServer.registerMBean(metrics, objectName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ThreadPoolMetrics implements ThreadPoolMetricsMBean {
        private final ThreadPoolExecutor executor;

        public ThreadPoolMetrics(ThreadPoolExecutor executor) {
            this.executor = executor;
        }

        @Override
        public int getActiveThreads() {
            return executor.getActiveCount();
        }

        @Override
        public int getPoolSize() {
            return executor.getPoolSize();
        }

        @Override
        public int getQueueSize() {
            return executor.getQueue().size();
        }
    }

    public interface ThreadPoolMetricsMBean {
        int getActiveThreads();
        int getPoolSize();
        int getQueueSize();
    }
}
