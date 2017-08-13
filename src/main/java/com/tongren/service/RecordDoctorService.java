package com.tongren.service;

import com.tongren.pojo.RecordDoctor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordDoctorService extends BaseService<RecordDoctor> {

	public List<RecordDoctor> queryByRecordIdAndDoctorType(Integer recordId, String doctorType) {

		RecordDoctor recordDoctor = new RecordDoctor();
		recordDoctor.setRecordId(recordId);
		recordDoctor.setDoctorType(doctorType);

		return this.getMapper().select(recordDoctor);
	}
}
