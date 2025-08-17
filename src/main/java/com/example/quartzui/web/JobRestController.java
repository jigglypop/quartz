package com.example.quartzui.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Job Management", description = "Quartz Job 관리 API")
public class JobRestController {

	private final Scheduler scheduler;

	public JobRestController(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@GetMapping
	@Operation(summary = "전체 Job 목록 조회", description = "등록된 모든 Job과 Trigger 정보를 조회합니다")
	@ApiResponse(responseCode = "200", description = "조회 성공")
	public ResponseEntity<List<Map<String, Object>>> getAllJobs() throws SchedulerException {
		List<Map<String, Object>> jobs = new ArrayList<>();
		
		for (String groupName : scheduler.getJobGroupNames()) {
			Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));
			for (JobKey jobKey : jobKeys) {
				Map<String, Object> jobInfo = new HashMap<>();
				JobDetail jobDetail = scheduler.getJobDetail(jobKey);
				
				jobInfo.put("name", jobKey.getName());
				jobInfo.put("group", jobKey.getGroup());
				jobInfo.put("description", jobDetail.getDescription());
				jobInfo.put("jobClass", jobDetail.getJobClass().getName());
				jobInfo.put("durability", jobDetail.isDurable());
				jobInfo.put("shouldRecover", jobDetail.requestsRecovery());
				
				List<Map<String, Object>> triggers = new ArrayList<>();
				List<? extends Trigger> jobTriggers = scheduler.getTriggersOfJob(jobKey);
				for (Trigger trigger : jobTriggers) {
					Map<String, Object> triggerInfo = new HashMap<>();
					triggerInfo.put("name", trigger.getKey().getName());
					triggerInfo.put("group", trigger.getKey().getGroup());
					triggerInfo.put("state", scheduler.getTriggerState(trigger.getKey()).name());
					triggerInfo.put("type", trigger.getClass().getSimpleName());
					triggerInfo.put("startTime", trigger.getStartTime());
					triggerInfo.put("endTime", trigger.getEndTime());
					triggerInfo.put("nextFireTime", trigger.getNextFireTime());
					triggerInfo.put("previousFireTime", trigger.getPreviousFireTime());
					triggerInfo.put("priority", trigger.getPriority());
					triggers.add(triggerInfo);
				}
				jobInfo.put("triggers", triggers);
				
				jobs.add(jobInfo);
			}
		}
		
		return ResponseEntity.ok(jobs);
	}
	
	@GetMapping("/{group}/{name}")
	@Operation(summary = "Job 상세 정보 조회", description = "특정 Job의 상세 정보를 조회합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "404", description = "Job을 찾을 수 없음")
	})
	public ResponseEntity<Map<String, Object>> getJobDetail(
			@Parameter(description = "Job 그룹명") @PathVariable String group, 
			@Parameter(description = "Job 이름") @PathVariable String name) throws SchedulerException {
		JobKey jobKey = new JobKey(name, group);
		
		if (!scheduler.checkExists(jobKey)) {
			return ResponseEntity.notFound().build();
		}
		
		JobDetail jobDetail = scheduler.getJobDetail(jobKey);
		Map<String, Object> jobInfo = new HashMap<>();
		
		jobInfo.put("name", jobKey.getName());
		jobInfo.put("group", jobKey.getGroup());
		jobInfo.put("description", jobDetail.getDescription());
		jobInfo.put("jobClass", jobDetail.getJobClass().getName());
		jobInfo.put("durability", jobDetail.isDurable());
		jobInfo.put("shouldRecover", jobDetail.requestsRecovery());
		jobInfo.put("jobDataMap", jobDetail.getJobDataMap().getWrappedMap());
		
		List<Map<String, Object>> triggers = new ArrayList<>();
		List<? extends Trigger> jobTriggers = scheduler.getTriggersOfJob(jobKey);
		for (Trigger trigger : jobTriggers) {
			Map<String, Object> triggerInfo = new HashMap<>();
			triggerInfo.put("name", trigger.getKey().getName());
			triggerInfo.put("group", trigger.getKey().getGroup());
			triggerInfo.put("state", scheduler.getTriggerState(trigger.getKey()).name());
			triggerInfo.put("type", trigger.getClass().getSimpleName());
			triggerInfo.put("description", trigger.getDescription());
			triggerInfo.put("calendarName", trigger.getCalendarName());
			triggerInfo.put("startTime", trigger.getStartTime());
			triggerInfo.put("endTime", trigger.getEndTime());
			triggerInfo.put("nextFireTime", trigger.getNextFireTime());
			triggerInfo.put("previousFireTime", trigger.getPreviousFireTime());
			triggerInfo.put("priority", trigger.getPriority());
			triggerInfo.put("misfireInstruction", trigger.getMisfireInstruction());
			triggerInfo.put("jobDataMap", trigger.getJobDataMap().getWrappedMap());
			triggers.add(triggerInfo);
		}
		jobInfo.put("triggers", triggers);
		
		return ResponseEntity.ok(jobInfo);
	}
	
	@DeleteMapping("/{group}/{name}")
	@Operation(summary = "Job 삭제", description = "특정 Job을 삭제합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "삭제 성공"),
		@ApiResponse(responseCode = "404", description = "Job을 찾을 수 없음")
	})
	public ResponseEntity<Map<String, Object>> deleteJob(
			@Parameter(description = "Job 그룹명") @PathVariable String group, 
			@Parameter(description = "Job 이름") @PathVariable String name) throws SchedulerException {
		JobKey jobKey = new JobKey(name, group);
		
		if (!scheduler.checkExists(jobKey)) {
			return ResponseEntity.notFound().build();
		}
		
		boolean deleted = scheduler.deleteJob(jobKey);
		
		Map<String, Object> result = new HashMap<>();
		result.put("deleted", deleted);
		result.put("jobKey", jobKey.toString());
		
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/{group}/{name}/pause")
	@Operation(summary = "Job 일시정지", description = "특정 Job을 일시정지합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "일시정지 성공"),
		@ApiResponse(responseCode = "404", description = "Job을 찾을 수 없음")
	})
	public ResponseEntity<Map<String, Object>> pauseJob(
			@Parameter(description = "Job 그룹명") @PathVariable String group, 
			@Parameter(description = "Job 이름") @PathVariable String name) throws SchedulerException {
		JobKey jobKey = new JobKey(name, group);
		
		if (!scheduler.checkExists(jobKey)) {
			return ResponseEntity.notFound().build();
		}
		
		scheduler.pauseJob(jobKey);
		
		Map<String, Object> result = new HashMap<>();
		result.put("status", "paused");
		result.put("jobKey", jobKey.toString());
		
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/{group}/{name}/resume")
	@Operation(summary = "Job 재개", description = "일시정지된 Job을 재개합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "재개 성공"),
		@ApiResponse(responseCode = "404", description = "Job을 찾을 수 없음")
	})
	public ResponseEntity<Map<String, Object>> resumeJob(
			@Parameter(description = "Job 그룹명") @PathVariable String group, 
			@Parameter(description = "Job 이름") @PathVariable String name) throws SchedulerException {
		JobKey jobKey = new JobKey(name, group);
		
		if (!scheduler.checkExists(jobKey)) {
			return ResponseEntity.notFound().build();
		}
		
		scheduler.resumeJob(jobKey);
		
		Map<String, Object> result = new HashMap<>();
		result.put("status", "resumed");
		result.put("jobKey", jobKey.toString());
		
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/{group}/{name}/trigger")
	@Operation(summary = "Job 즉시 실행", description = "특정 Job을 즉시 실행합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "실행 성공"),
		@ApiResponse(responseCode = "404", description = "Job을 찾을 수 없음")
	})
	public ResponseEntity<Map<String, Object>> triggerJob(
			@Parameter(description = "Job 그룹명") @PathVariable String group, 
			@Parameter(description = "Job 이름") @PathVariable String name,
			@Parameter(description = "Job 실행 시 전달할 데이터") @RequestBody(required = false) Map<String, Object> jobData) throws SchedulerException {
		JobKey jobKey = new JobKey(name, group);
		
		if (!scheduler.checkExists(jobKey)) {
			return ResponseEntity.notFound().build();
		}
		
		JobDataMap dataMap = new JobDataMap();
		if (jobData != null) {
			dataMap.putAll(jobData);
		}
		
		scheduler.triggerJob(jobKey, dataMap);
		
		Map<String, Object> result = new HashMap<>();
		result.put("status", "triggered");
		result.put("jobKey", jobKey.toString());
		result.put("timestamp", new Date());
		
		return ResponseEntity.ok(result);
	}
	
	// Legacy chain trigger endpoint removed. Use /api/file-jobs/schedule instead.
}

@RestController
@RequestMapping("/api/scheduler")
@Tag(name = "Scheduler Management", description = "Quartz Scheduler 관리 API")
class SchedulerRestController {
	
	private final Scheduler scheduler;
	
	public SchedulerRestController(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	@GetMapping("/info")
	@Operation(summary = "스케줄러 정보 조회", description = "Quartz 스케줄러의 상태 및 메타 정보를 조회합니다")
	@ApiResponse(responseCode = "200", description = "조회 성공")
	public ResponseEntity<Map<String, Object>> getSchedulerInfo() throws SchedulerException {
		Map<String, Object> info = new HashMap<>();
		
		info.put("schedulerName", scheduler.getSchedulerName());
		info.put("schedulerInstanceId", scheduler.getSchedulerInstanceId());
		info.put("schedulerClass", scheduler.getClass().getName());
		info.put("isStarted", scheduler.isStarted());
		info.put("isInStandbyMode", scheduler.isInStandbyMode());
		info.put("isShutdown", scheduler.isShutdown());
		info.put("jobGroupNames", scheduler.getJobGroupNames());
		info.put("triggerGroupNames", scheduler.getTriggerGroupNames());
		info.put("pausedTriggerGroups", scheduler.getPausedTriggerGroups());
		
		Map<String, Object> metaData = new HashMap<>();
		metaData.put("version", scheduler.getMetaData().getVersion());
		metaData.put("jobStoreClass", scheduler.getMetaData().getJobStoreClass());
		metaData.put("threadPoolClass", scheduler.getMetaData().getThreadPoolClass());
		metaData.put("threadPoolSize", scheduler.getMetaData().getThreadPoolSize());
		metaData.put("numberOfJobsExecuted", scheduler.getMetaData().getNumberOfJobsExecuted());
		metaData.put("runningSince", scheduler.getMetaData().getRunningSince());
		metaData.put("schedulerRemote", scheduler.getMetaData().isSchedulerRemote());
		metaData.put("jobStoreSupportsPersistence", scheduler.getMetaData().isJobStoreSupportsPersistence());
		metaData.put("jobStoreClustered", scheduler.getMetaData().isJobStoreClustered());
		
		info.put("metaData", metaData);
		
		return ResponseEntity.ok(info);
	}
	
	@PostMapping("/start")
	@Operation(summary = "스케줄러 시작", description = "Quartz 스케줄러를 시작합니다")
	@ApiResponse(responseCode = "200", description = "시작 성공")
	public ResponseEntity<Map<String, Object>> startScheduler() throws SchedulerException {
		scheduler.start();
		
		Map<String, Object> result = new HashMap<>();
		result.put("status", "started");
		result.put("timestamp", new Date());
		
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/standby")
	@Operation(summary = "스케줄러 대기 모드", description = "Quartz 스케줄러를 대기 모드로 전환합니다")
	@ApiResponse(responseCode = "200", description = "대기 모드 전환 성공")
	public ResponseEntity<Map<String, Object>> standbyScheduler() throws SchedulerException {
		scheduler.standby();
		
		Map<String, Object> result = new HashMap<>();
		result.put("status", "standby");
		result.put("timestamp", new Date());
		
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/shutdown")
	@Operation(summary = "스케줄러 종료", description = "Quartz 스케줄러를 종료합니다")
	@ApiResponse(responseCode = "200", description = "종료 성공")
	public ResponseEntity<Map<String, Object>> shutdownScheduler(
			@Parameter(description = "Job 종료를 기다릴지 여부") @RequestParam(defaultValue = "false") boolean waitForJobsToComplete) throws SchedulerException {
		scheduler.shutdown(waitForJobsToComplete);
		
		Map<String, Object> result = new HashMap<>();
		result.put("status", "shutdown");
		result.put("waitForJobsToComplete", waitForJobsToComplete);
		result.put("timestamp", new Date());
		
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/clear")
	@Operation(summary = "스케줄러 초기화", description = "모든 Job과 Trigger를 삭제합니다")
	@ApiResponse(responseCode = "200", description = "초기화 성공")
	public ResponseEntity<Map<String, Object>> clearScheduler() throws SchedulerException {
		scheduler.clear();
		
		Map<String, Object> result = new HashMap<>();
		result.put("status", "cleared");
		result.put("timestamp", new Date());
		
		return ResponseEntity.ok(result);
	}
}


