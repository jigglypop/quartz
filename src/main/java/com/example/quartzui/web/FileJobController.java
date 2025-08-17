package com.example.quartzui.web;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quartzui.core.CompositeJobDefinition;
import com.example.quartzui.core.JobAtom;
import com.example.quartzui.filejob.FileJobAtoms;
import com.example.quartzui.service.SchedulerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/file-jobs")
@Tag(name = "File Composite Jobs")
public class FileJobController {

    private final Scheduler scheduler;
    private final SchedulerService schedulerService;
    public FileJobController(Scheduler scheduler, SchedulerService schedulerService) { this.scheduler = scheduler; this.schedulerService = schedulerService; }

    public static class ScheduleRequest {
        public String jobName;
        public String rootDir;
        public List<Map<String, String>> steps; // {type: read|write|delete|move|mkdir, path:..., to:..., ctxKey:..., content:...}
        public Long startEpochMillis; // optional: immediate if null
    }

    @PostMapping("/schedule")
    @Operation(summary = "파일 작업 컴포지트 잡 등록")
    public ResponseEntity<Map<String, Object>> schedule(@RequestBody ScheduleRequest req) throws SchedulerException {
        Path root = Path.of(req.rootDir);
        // Build atoms
        List<JobAtom> atoms = new java.util.ArrayList<>();
        Map<String, Object> presetContext = new HashMap<>();
        for (Map<String, String> step : req.steps) {
            String type = step.get("type");
            switch (type) {
                case "read":
                    atoms.add(FileJobAtoms.readAtom(root, step.get("path"), step.getOrDefault("ctxKey", "content")));
                    break;
                case "write":
                    // preload content if given
                    String content = step.get("content");
                    if (content != null) {
                        presetContext.put(step.getOrDefault("ctxKey", "content"), content);
                    }
                    atoms.add(FileJobAtoms.writeAtom(root, step.get("path"), step.getOrDefault("ctxKey", "content")));
                    break;
                case "delete":
                    atoms.add(FileJobAtoms.deleteAtom(root, step.get("path")));
                    break;
                case "move":
                    atoms.add(FileJobAtoms.moveAtom(root, step.get("path"), step.get("to")));
                    break;
                case "mkdir":
                    atoms.add(FileJobAtoms.mkdirAtom(root, step.get("path")));
                    break;
                case "create-next-numbered-text":
                    atoms.add(FileJobAtoms.createNextNumberedTextFileAtom(root, step.get("content")));
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported step type: " + type);
            }
        }

        CompositeJobDefinition def = new CompositeJobDefinition(req.jobName, atoms);
        String jobKey = schedulerService.scheduleComposite(def, req.startEpochMillis, presetContext);
        Map<String, Object> res = new HashMap<>();
        res.put("scheduled", true);
        res.put("jobKey", jobKey);
        return ResponseEntity.ok(res);
    }
}


