package com.tongren.service;

import com.tongren.mapper.RecordSurgeryMapper;
import com.tongren.pojo.RecordSurgery;
import com.tongren.pojo.RecordSurgeryExtend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordSurgeryService extends BaseService<RecordSurgery> {


	@Autowired
	private RecordSurgeryMapper recordSurgeryMapper;

	public List<RecordSurgeryExtend> queryByRecordId(Integer recordId) {

		return this.recordSurgeryMapper.selectByRecordId(recordId);
	}
}
