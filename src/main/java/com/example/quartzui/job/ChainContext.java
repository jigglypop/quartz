package com.example.quartzui.job;

import java.util.HashMap;
import java.util.Map;

public final class ChainContext {
	private static final ThreadLocal<Map<String, Object>> CTX = ThreadLocal.withInitial(HashMap::new);

	private ChainContext() {}

	public static Map<String, Object> get() { return CTX.get(); }

	public static void clear() { CTX.remove(); }
}


