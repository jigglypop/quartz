package com.example.quartzui.filejob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import com.example.quartzui.core.JobAtom;

public final class FileJobAtoms {
	private FileJobAtoms() {}

	public static JobAtom scanListAtom(Path root, String contextKey) {
		return new JobAtom() {
			@Override public String name() { return "scanList:" + root; }
			@Override public void execute(Map<String, Object> context) throws IOException {
				try (var stream = Files.list(root)) {
					var list = stream.filter(Files::isRegularFile)
							.map(p -> p.getFileName().toString())
							.sorted()
							.toList();
					context.put(contextKey, list);
				}
			}
		};
	}

	public static JobAtom scanCountAtom(Path root, String contextKey) {
		return new JobAtom() {
			@Override public String name() { return "scanCount:" + root; }
			@Override public void execute(Map<String, Object> context) throws IOException {
				int count;
				try (var stream = Files.list(root)) {
					count = (int) stream.filter(Files::isRegularFile).count();
				}
				context.put(contextKey, count);
			}
		};
	}

	public static JobAtom createNextNumberedTextFileAtom(Path root, String fallbackContent) {
		return new JobAtom() {
			@Override public String name() { return "createNextNumberedText:" + root; }
			@SuppressWarnings("unchecked")
			@Override public void execute(Map<String, Object> context) throws IOException {
				int count = -1;
				Object listObj = context.get("fileList");
				if (listObj instanceof java.util.List<?> list) {
					count = list.size();
				}
				if (count < 0) {
					try (var stream = Files.list(root)) {
						count = (int) stream.filter(Files::isRegularFile).count();
					}
				}
				int next = count + 1;
				Path target = root.resolve(next + ".txt");
				Files.createDirectories(root);
				String content = String.valueOf(context.getOrDefault("content", fallbackContent != null ? fallbackContent : "generated"));
				Files.writeString(target, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
				// 갱신된 목록을 컨텍스트에 반영
				try (var stream = Files.list(root)) {
					var list = stream.filter(Files::isRegularFile)
							.map(p -> p.getFileName().toString())
							.sorted()
							.toList();
					context.put("fileList", list);
					context.put("fileCount", list.size());
				}
			}
		};
	}

	public static JobAtom readAtom(Path root, String relativePath, String contextKey) {
		return new JobAtom() {
			@Override public String name() { return "read:" + relativePath; }
			@Override public void execute(Map<String, Object> context) throws IOException {
				Path path = root.resolve(relativePath);
				byte[] bytes = Files.readAllBytes(path);
				context.put(contextKey, bytes);
			}
		};
	}

	public static JobAtom writeAtom(Path root, String relativePath, String contextKey) {
		return new JobAtom() {
			@Override public String name() { return "write:" + relativePath; }
			@Override public void execute(Map<String, Object> context) throws IOException {
				Path path = root.resolve(relativePath);
				Files.createDirectories(path.getParent());
				Object data = context.get(contextKey);
				if (data instanceof byte[]) {
					Files.write(path, (byte[]) data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
				} else if (data instanceof CharSequence) {
					Files.writeString(path, data.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
				} else {
					throw new IllegalArgumentException("Unsupported data type for write: " + (data==null?"null":data.getClass()));
				}
			}
		};
	}

	public static JobAtom deleteAtom(Path root, String relativePath) {
		return new JobAtom() {
			@Override public String name() { return "delete:" + relativePath; }
			@Override public void execute(Map<String, Object> context) throws IOException {
				Path path = root.resolve(relativePath);
				Files.deleteIfExists(path);
			}
		};
	}

	public static JobAtom moveAtom(Path root, String from, String to) {
		return new JobAtom() {
			@Override public String name() { return "move:" + from + "->" + to; }
			@Override public void execute(Map<String, Object> context) throws IOException {
				Path src = root.resolve(from);
				Path dst = root.resolve(to);
				Files.createDirectories(dst.getParent());
				Files.move(src, dst, StandardCopyOption.REPLACE_EXISTING);
			}
		};
	}

	public static JobAtom mkdirAtom(Path root, String relativeDir) {
		return new JobAtom() {
			@Override public String name() { return "mkdir:" + relativeDir; }
			@Override public void execute(Map<String, Object> context) throws IOException {
				Files.createDirectories(root.resolve(relativeDir));
			}
		};
	}
}


