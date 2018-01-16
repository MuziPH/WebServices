package branch.checks;

import org.quartz.CronScheduleBuilder;

import org.quartz.Job;

import org.quartz.JobBuilder;

import org.quartz.JobDetail;

import org.quartz.JobExecutionContext;

import org.quartz.JobExecutionException;

import org.quartz.Scheduler;

import org.quartz.SchedulerException;

import org.quartz.Trigger;

import org.quartz.TriggerBuilder;

import org.quartz.impl.StdSchedulerFactory;



public class VAFScheduler {
	
	public static void main(String[] args) throws SchedulerException {

        //Define a job and tie it to our job class

        JobDetail MorningCheckJob = JobBuilder.newJob(MorningCheck.class).build();
        
        JobDetail EndDayCheckJob = JobBuilder.newJob(EndDayCheck.class).build();
        
        // Create triggers for when the Job need to run

        Trigger morningCheckTrigger = TriggerBuilder.newTrigger()

                .withIdentity("CronTriggerSOD")

                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/2 * 1/1 * ? *"))

                .build();
        
        Trigger eodCheckTrigger = TriggerBuilder.newTrigger()

                .withIdentity("CronTriggerEOD")

                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/2 * 1/1 * ? *"))

                .build();

              
        Scheduler sc = StdSchedulerFactory.getDefaultScheduler();

        sc.start();

       

        //Actual scheduling of jobs with triggers defined above

        sc.scheduleJob(MorningCheckJob, morningCheckTrigger);
        
        //sc.scheduleJob(EndDayCheckJob, eodCheckTrigger);

 

    }



}
