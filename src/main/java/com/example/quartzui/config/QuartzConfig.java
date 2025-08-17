package com.example.quartzui.config;

import javax.sql.DataSource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

// Listener optional: remove if not needed

@Configuration
public class QuartzConfig {

	@Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource, ApplicationContext applicationContext) {
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		factory.setDataSource(dataSource);
		factory.setOverwriteExistingJobs(true);
		factory.setApplicationContextSchedulerContextKey("applicationContext");
		java.util.Properties props = new java.util.Properties();
		props.setProperty("org.quartz.jobStore.class", "org.springframework.scheduling.quartz.LocalDataSourceJobStore");
		props.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
		props.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");
		props.setProperty("org.quartz.threadPool.threadCount", "5");
		factory.setQuartzProperties(props);
		factory.setApplicationContext(applicationContext);
		return factory;
	}
	
	@Bean
	public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean) throws SchedulerException {
		return schedulerFactoryBean.getScheduler();
	}
}


