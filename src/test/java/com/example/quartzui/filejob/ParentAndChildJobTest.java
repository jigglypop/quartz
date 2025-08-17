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

/**
 * 부모 개념을 간단히 흉내: (1) 파일 읽기 전용 자식, (2) 스캔+번호파일생성 자식을 순차 실행
 * 실제 ParentQuartzJob 없이 조합만 검증.
 */
class ParentAndChildJobTest {

    private Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        tempDir = Files.createTempDirectory("parent-child-test");
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
    void parentExecutesReadingOnlyAndThenScanCreate() throws Exception {
        // child1: reading-only (scan)
        JobAtom child1 = FileJobAtoms.scanListAtom(tempDir, "fileList");
        // child2: scan + create next
        JobAtom scan = FileJobAtoms.scanListAtom(tempDir, "fileList");
        JobAtom create = FileJobAtoms.createNextNumberedTextFileAtom(tempDir, "hello-parent");

        // parent: sequential composition of children
        CompositeJobDefinition parent = new CompositeJobDefinition("parent", List.of(child1, scan, create));

        Map<String,Object> ctx = new HashMap<>();
        for (JobAtom atom: parent.getAtoms()) atom.execute(ctx);

        assertThat(Files.exists(tempDir.resolve("1.txt"))).isTrue();
        assertThat(Files.readString(tempDir.resolve("1.txt"))).isEqualTo("hello-parent");
    }
}


