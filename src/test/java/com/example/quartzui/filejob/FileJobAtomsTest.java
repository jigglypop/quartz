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

class FileJobAtomsTest {

    private Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        tempDir = Files.createTempDirectory("file-atoms-test");
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
    void scanTwiceAndCreateTwoNumberedFiles() throws Exception {
        JobAtom scan1 = FileJobAtoms.scanListAtom(tempDir, "fileList");
        JobAtom create1 = FileJobAtoms.createNextNumberedTextFileAtom(tempDir, "hello-1");
        JobAtom scan2 = FileJobAtoms.scanListAtom(tempDir, "fileList");
        JobAtom create2 = FileJobAtoms.createNextNumberedTextFileAtom(tempDir, "hello-2");

        CompositeJobDefinition def = new CompositeJobDefinition("scan-create-two", List.of(scan1, create1, scan2, create2));
        Map<String,Object> ctx = new HashMap<>();
        for (JobAtom atom: def.getAtoms()) atom.execute(ctx);

        assertThat(Files.exists(tempDir.resolve("1.txt"))).isTrue();
        assertThat(Files.exists(tempDir.resolve("2.txt"))).isTrue();
        assertThat(Files.readString(tempDir.resolve("1.txt"))).isEqualTo("hello-1");
        assertThat(Files.readString(tempDir.resolve("2.txt"))).isEqualTo("hello-2");
    }

    @Test
    void deleteTwiceIsIdempotent() throws Exception {
        Path file = tempDir.resolve("foo.txt");
        Files.writeString(file, "x");
        assertThat(Files.exists(file)).isTrue();

        JobAtom delete1 = FileJobAtoms.deleteAtom(tempDir, "foo.txt");
        JobAtom delete2 = FileJobAtoms.deleteAtom(tempDir, "foo.txt");
        Map<String,Object> ctx = new HashMap<>();
        delete1.execute(ctx);
        delete2.execute(ctx);

        assertThat(Files.exists(file)).isFalse();
    }

    @Test
    void scanCountAndCreateNext() throws Exception {
        Files.writeString(tempDir.resolve("a.txt"), "a");
        Files.writeString(tempDir.resolve("b.txt"), "b");

        JobAtom count = FileJobAtoms.scanCountAtom(tempDir, "fileCount");
        JobAtom create = FileJobAtoms.createNextNumberedTextFileAtom(tempDir, "hello");

        Map<String,Object> ctx = new HashMap<>();
        count.execute(ctx);
        assertThat(ctx.get("fileCount")).isEqualTo(2);
        create.execute(ctx);

        assertThat(Files.exists(tempDir.resolve("3.txt"))).isTrue();
        assertThat(Files.readString(tempDir.resolve("3.txt"))).isEqualTo("hello");
    }
}


