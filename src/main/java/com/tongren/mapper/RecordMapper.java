package com.tongren.mapper;


import com.tongren.pojo.Record;
import com.tongren.pojo.RecordExtend;
import com.tongren.pojo.RecordExtend1;
import tk.mybatis.mapper.common.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RecordMapper extends Mapper<Record> {


	List<RecordExtend> selectByFiltersForAdmin(Map<String, Object> filters);

	List<RecordExtend> selectByFiltersForOthers(Map<String, Object> filters);

	List<RecordExtend1> selectDetailByFiltersForAdmin(Map<String, Object> filters);

	List<Record> selectDetailByFiltersForOthers(Map<String, Object> filters);

	Integer selectTotalScore(Map<String, Object> filters);

}