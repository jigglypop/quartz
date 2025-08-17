package com.example.quartzui.filejob;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.quartzui.core.CompositeJobDefinition;
import com.example.quartzui.core.JobAtom;

class FileJobIntegrationTest {

    private Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        tempDir = Files.createTempDirectory("filejob-test");
    }

    @AfterEach
    void tearDown() throws Exception {
        if (tempDir != null) {
            try (var s = Files.walk(tempDir)) {
                s.sorted((a,b) -> b.getNameCount() - a.getNameCount()).forEach(p -> {
                    try { Files.deleteIfExists(p); } catch (Exception ignored) {}
                });
            }
        }
    }

    @Test
    void scanAndCreateNumberedFile() throws Exception {
        // 1) 루트 경로 파일 목록 읽기
        JobAtom scan = FileJobAtoms.scanListAtom(tempDir, "fileList");
        // 2) 파일 개수 + 1 의 제목으로 텍스트 파일 생성
        JobAtom create = FileJobAtoms.createNextNumberedTextFileAtom(tempDir, "hello");

        CompositeJobDefinition def = new CompositeJobDefinition("scan-create", List.of(scan, create));

        Map<String,Object> ctx = new HashMap<>();
        for (JobAtom atom: def.getAtoms()) {
            atom.execute(ctx);
        }

        assertThat(Files.exists(tempDir.resolve("1.txt"))).isTrue();
        String content = Files.readString(tempDir.resolve("1.txt"));
        assertThat(content).isEqualTo("hello");
    }
}


