package com.example.quartzui.service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import static org.quartz.TriggerBuilder.newTrigger;
import org.springframework.stereotype.Service;

import com.example.quartzui.core.CompositeJobDefinition;
import com.example.quartzui.quartz.CompositeQuartzJob;

@Service
public class SchedulerService {

    private final Scheduler scheduler;
    public SchedulerService(Scheduler scheduler) { this.scheduler = scheduler; }

    public String scheduleComposite(CompositeJobDefinition def, Long startEpochMillis, Map<String, Object> presetContext) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("definition", def);
        if (presetContext != null) data.put("presetContext", presetContext);

        JobDetail jd = JobBuilder.newJob(CompositeQuartzJob.class)
                .withIdentity("composite-" + def.getJobName(), "composite")
                .usingJobData(data)
                .storeDurably()
                .build();

        Trigger trigger = newTrigger()
                .withIdentity("composite-trigger-" + def.getJobName(), "composite")
                .startAt(startEpochMillis == null ? Date.from(Instant.now()) : new Date(startEpochMillis))
                .forJob(jd)
                .build();

        if (scheduler.checkExists(jd.getKey())) {
            scheduler.deleteJob(jd.getKey());
        }
        scheduler.scheduleJob(jd, Set.of(trigger), true);
        return jd.getKey().toString();
    }
}


