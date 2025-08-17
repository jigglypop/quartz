package com.example.quartzui.core;

import java.util.Map;

public interface JobAtom {
	String name();
	void execute(Map<String, Object> context) throws Exception;
}


