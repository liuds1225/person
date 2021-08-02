package com.cn.person.batch;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

/**
 * 动态批处理启动类型
 * 
 * @author ADMIN
 *
 */
@Configuration
@EnableScheduling // 定时任务注解
public class DynamicScheduleTask implements SchedulingConfigurer {
	private static final Logger log = LoggerFactory.getLogger(DynamicScheduleTask.class);
	
	@Autowired
	private TransactionControl batchTransactionControl;
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		for(int i = 7; i <= 2; i++) {
			String expression = "*/" + (5 + i ) + " * * * * ?";
			String index = i + "";
			// 定时任务要执行的方法
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					log.info("定时任务开启{}：============",index);
					String taskCode = "";
					String className = "";
					String methodStr = "";
					String value = "";
					try {
						batchTransactionControl.transactionControl(className, methodStr, value);
					} catch (Exception e) {
						log.info("{}{}定时任务处理失败", taskCode, e);
					}
				}
			};
			// 调度实现的时间控制
			Trigger trigger = new Trigger() {
				@Override
				public Date nextExecutionTime(TriggerContext triggerContext) {
					return new CronTrigger(expression).nextExecutionTime(triggerContext);
				}
			};
			taskRegistrar.addTriggerTask(runnable, trigger);
		}
	}

}
