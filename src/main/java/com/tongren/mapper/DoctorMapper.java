package com.tongren.mapper;

import com.tongren.pojo.Doctor;
import com.tongren.pojo.DoctorExtend;
import com.tongren.pojo.RecordExtend1;
import com.tongren.pojo.Record;
import tk.mybatis.mapper.common.Mapper;

import javax.print.Doc;
import java.util.List;
import java.util.Map;

public interface DoctorMapper extends Mapper<Doctor> {

	List<Doctor> selectSurgeonAndHelper();

	List<DoctorExtend> selectByFilters(Map<String, Object> filters);
}