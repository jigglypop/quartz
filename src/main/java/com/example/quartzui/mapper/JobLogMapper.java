package com.example.quartzui.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface JobLogMapper {
	void insertLog(@Param("job") String job,
	              @Param("step") String step,
	              @Param("status") String status,
	              @Param("message") String message);
}


