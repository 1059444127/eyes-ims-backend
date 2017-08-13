package com.tongren.mapper;

import com.tongren.pojo.RecordSurgery;
import com.tongren.pojo.RecordSurgeryExtend;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RecordSurgeryMapper extends Mapper<RecordSurgery> {

	List<RecordSurgeryExtend> selectByRecordId(Integer recordId);
}