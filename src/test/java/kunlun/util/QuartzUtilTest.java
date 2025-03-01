//package kunlun.util;
//
//import org.junit.Test;
//import org.quartz.CronTrigger;
//import org.quartz.JobDetail;
//import org.quartz.Scheduler;
//import org.quartz.TriggerKey;
//import org.quartz.impl.JobDetailImpl;
//import org.quartz.impl.triggers.SimpleTriggerImpl;
//
//import java.util.Date;
//
//public class QuartzUtilTest {
//
//    @Test
//    public void test0() throws Exception {
//        Scheduler scheduler = QuartzUtil.getScheduler();
//        QuartzUtil.startScheduler(scheduler);
//
//        JobDetailImpl jobDetail = QuartzUtil.createJobDetail();
//        jobDetail.setJobClass(TaskDemo.class);
//        jobDetail.setName("job1");
//
//        SimpleTriggerImpl trigger = QuartzUtil.createSimpleTrigger();
//        trigger.setName("trigger1");
//        trigger.setRepeatCount(10);
//        trigger.setRepeatInterval(1000);
//        trigger.setStartTime(new Date(new Date().getTime() + 1000));
//
//        QuartzUtil.scheduleJob(scheduler, jobDetail, trigger);
//        Thread.sleep(30000);
//        QuartzUtil.shutdownScheduler(scheduler);
//    }
//
//    @Test
//    public void test1() throws Exception {
//        Scheduler scheduler = QuartzUtil.getScheduler();
//        QuartzUtil.startScheduler(scheduler);
//        JobDetail job = QuartzUtil.createJobBuilder(TaskDemo.class).build();
//
//        SimpleTriggerImpl trigger = QuartzUtil.createSimpleTrigger();
//        trigger.setName("trigger1");
//        trigger.setRepeatCount(10);
//        trigger.setRepeatInterval(1000);
//        trigger.setStartTime(new Date(new Date().getTime() + 1000));
//
//        QuartzUtil.scheduleJob(scheduler, job, trigger);
//        Thread.sleep(30000);
//        QuartzUtils.shutdownScheduler(scheduler);
//    }
//
//    @Test
//    public void test2() throws Exception {
//        Scheduler scheduler = QuartzUtil.getScheduler();
//        QuartzUtil.startScheduler(scheduler);
//
//        JobDetail job = QuartzUtil.createJobBuilder(TaskDemo.class).build();
//        CronTrigger trigger = QuartzUtil.createCronTriggerBuilder("0/5 * * * * ?").build();
//
//        QuartzUtil.scheduleJob(scheduler, job, trigger);
//        Thread.sleep(30000);
//        QuartzUtil.shutdownScheduler(scheduler);
//    }
//
//    @Test
//    public void test3() throws Exception {
//        Scheduler scheduler = QuartzUtil.getScheduler();
//        QuartzUtil.startScheduler(scheduler);
//        TriggerKey triggerKey = new TriggerKey("trigger");
//
//        JobDetail job = QuartzUtil.createJobBuilder(TaskDemo.class).build();
//        CronTrigger trigger = QuartzUtil
//                .createCronTriggerBuilder("0/8 * * * * ?")
//                .withIdentity(triggerKey).build();
//
//        QuartzUtil.scheduleJob(scheduler, job, trigger);
//        Thread.sleep(10000);
//
//        CronTrigger newTrigger = QuartzUtil
//                .createCronTriggerBuilder("0/1 * * * * ?")
//                .withIdentity(triggerKey).build();
//        if (scheduler.checkExists(triggerKey)) {
//            scheduler.rescheduleJob(triggerKey, newTrigger);
//        }
//        Thread.sleep(30000);
//        QuartzUtil.shutdownScheduler(scheduler);
//    }
//
//    @Test
//    public void test4() throws Exception {
//        Scheduler scheduler = QuartzUtil.getScheduler();
//        QuartzUtil.startScheduler(scheduler);
//        TriggerKey triggerKey = QuartzUtil.createTriggerKey("trigger");
//
//        JobDetail job = QuartzUtil.createJobBuilder(TaskDemo.class).build();
//        CronTrigger trigger = QuartzUtil
//                .createCronTriggerBuilder("0/8 * * * * ?")
//                .withIdentity(triggerKey).build();
//
//        QuartzUtil.scheduleJob(scheduler, job, trigger);
//        Thread.sleep(10000);
//
//        CronTrigger newTrigger = QuartzUtil
//                .createCronTriggerBuilder("0/1 * * * * ?")
//                .withIdentity(triggerKey).build();
//        QuartzUtil.rescheduleJob(scheduler, triggerKey, newTrigger);
//
//        Thread.sleep(30000);
//        QuartzUtil.shutdownScheduler(scheduler);
//    }
//
//}
