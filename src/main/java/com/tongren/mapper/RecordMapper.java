package com.tongren.mapper;


import com.tongren.pojo.Record;
import com.tongren.pojo.RecordExtend;
import tk.mybatis.mapper.common.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RecordMapper extends Mapper<Record> {


	List<RecordExtend> selectByFilters(Map<String, Object> filters);

}