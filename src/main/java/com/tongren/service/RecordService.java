package com.tongren.service;

import com.github.pagehelper.PageHelper;
import com.tongren.bean.Constant;
import com.tongren.mapper.RecordMapper;
import com.tongren.pojo.*;
import com.tongren.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecordService extends BaseService<Record> {

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private RecordSurgeryService recordSurgeryService;

    @Autowired
    private RecordDoctorService recordDoctorService;

    @Autowired
    private DoctorService doctorService;


    public List<RecordExtend> queryRecordList(Integer pageNow,
                                              Integer pageSize,
                                              Map<String, Object> filters) {

        PageHelper.startPage(pageNow, pageSize);
        return this.recordMapper.selectByFilters(filters);
    }

    /**
     * 添加手术记录（记录-手术项、记录-术者/助手）
     * @param record
     * @param surgeries
     * @param surgeons
     * @param helpers
     * @return
     */
    public Integer save(Record record,
                        ArrayList<HashMap<String, Object>> surgeries,
                        ArrayList<HashMap<String, Object>> surgeons,
                        ArrayList<HashMap<String, Object>> helpers) {

        //首要添加记录
        this.save(record);

        Integer recordId = record.getId();
        Date date = record.getDate();

        //构造手术记录-所做手术对象
        if(!Validator.checkNull(surgeries)) {

            for(HashMap<String, Object> surgery : surgeries) {

                RecordSurgery recordSurgery = new RecordSurgery();
                recordSurgery.setRecordId(recordId);
                recordSurgery.setSurgeryId(Integer.parseInt((String)surgery.get("key")));
                this.recordSurgeryService.save(recordSurgery);

                //TODO:每轮要比较下最高级别手术
            }
        }

        //TODO:统计surgeries里面最高级别的手术，方便之后统计术者/助手的工作量用

        //构造手术记录-术者对象
        if(!Validator.checkNull(surgeons)) {

            for(HashMap<String, Object> surgeon : surgeons) {

                Integer surgeonId = Integer.parseInt((String)surgeon.get("key"));

                RecordDoctor recordDoctor = new RecordDoctor();
                recordDoctor.setRecordId(recordId);
                recordDoctor.setDoctorId(surgeonId);
                recordDoctor.setDate(date);

                //获取术者当前的信息
                Doctor doctor = this.doctorService.queryById(surgeonId);
                recordDoctor.setDoctorType("术者");
                recordDoctor.setDoctorName(doctor.getName());
                recordDoctor.setDoctorSalaryNum(doctor.getSalaryNum());
                recordDoctor.setDoctorLevel(doctor.getLevel());

                //TODO:根据level和本场最高级别手术计算乘积得到工作量积分score
                recordDoctor.setDoctorScore(5);

                this.recordDoctorService.save(recordDoctor);
            }
        }

        //构造手术记录-助手对象
        if(!Validator.checkNull(helpers)) {

            for(HashMap<String, Object> helper : helpers) {

                Integer helperId = Integer.parseInt((String)helper.get("key"));

                RecordDoctor recordDoctor = new RecordDoctor();
                recordDoctor.setRecordId(recordId);
                recordDoctor.setDoctorId(helperId);
                recordDoctor.setDate(date);

                //获取术者当前的信息
                Doctor doctor = this.doctorService.queryById(helperId);
                recordDoctor.setDoctorType("助手");
                recordDoctor.setDoctorName(doctor.getName());
                recordDoctor.setDoctorSalaryNum(doctor.getSalaryNum());
                recordDoctor.setDoctorLevel(doctor.getLevel());

                //TODO:根据level和本场最高级别手术计算乘积得到工作量score
                recordDoctor.setDoctorScore(5);

                this.recordDoctorService.save(recordDoctor);
            }
        }

        return Constant.CRUD_SUCCESS;
    }


    /**
     * 修改手术记录（记录-手术项、记录-术者/助手）
     * @param record
     * @param surgeries
     * @param surgeons
     * @param helpers
     * @return
     */
    public Integer update(Record record,
                        ArrayList<HashMap<String, Object>> surgeries,
                        ArrayList<HashMap<String, Object>> surgeons,
                        ArrayList<HashMap<String, Object>> helpers) {

        //修改记录本身
        this.update(record);

        Integer recordId = record.getId();
        Date date = record.getDate();


        //删除手术记录-所做手术 原来的信息
        RecordSurgery recordSurgery = new RecordSurgery();
        recordSurgery.setRecordId(recordId);
        this.recordSurgeryService.deleteByWhere(recordSurgery);

        //删除手术记录-术者/助手 原来的信息
        RecordDoctor recordDoctor = new RecordDoctor();
        recordDoctor.setRecordId(recordId);
        this.recordDoctorService.deleteByWhere(recordDoctor);


        //构造手术记录-所做手术对象
        if(!Validator.checkNull(surgeries)) {

            for(HashMap<String, Object> surgery : surgeries) {

                recordSurgery = new RecordSurgery();
                recordSurgery.setRecordId(recordId);
                recordSurgery.setSurgeryId(Integer.parseInt((String)surgery.get("key")));
                this.recordSurgeryService.save(recordSurgery);

                //TODO:每轮要比较下最高级别手术
            }
        }

        //TODO:统计surgeries里面最高级别的手术，方便之后统计术者/助手的工作量用

        //构造手术记录-术者对象
        if(!Validator.checkNull(surgeons)) {

            for(HashMap<String, Object> surgeon : surgeons) {

                Integer surgeonId = Integer.parseInt((String)surgeon.get("key"));

                recordDoctor = new RecordDoctor();
                recordDoctor.setRecordId(recordId);
                recordDoctor.setDoctorId(surgeonId);
                recordDoctor.setDate(date);

                //获取术者当前的信息
                Doctor doctor = this.doctorService.queryById(surgeonId);
                recordDoctor.setDoctorType("术者");
                recordDoctor.setDoctorName(doctor.getName());
                recordDoctor.setDoctorSalaryNum(doctor.getSalaryNum());
                recordDoctor.setDoctorLevel(doctor.getLevel());

                //TODO:根据level和本场最高级别手术计算乘积得到工作量积分score
                recordDoctor.setDoctorScore(5);

                this.recordDoctorService.save(recordDoctor);
            }
        }

        //构造手术记录-助手对象
        if(!Validator.checkNull(helpers)) {

            for(HashMap<String, Object> helper : helpers) {

                Integer helperId = Integer.parseInt((String)helper.get("key"));

                recordDoctor = new RecordDoctor();
                recordDoctor.setRecordId(recordId);
                recordDoctor.setDoctorId(helperId);
                recordDoctor.setDate(date);

                //获取术者当前的信息
                Doctor doctor = this.doctorService.queryById(helperId);
                recordDoctor.setDoctorType("助手");
                recordDoctor.setDoctorName(doctor.getName());
                recordDoctor.setDoctorSalaryNum(doctor.getSalaryNum());
                recordDoctor.setDoctorLevel(doctor.getLevel());

                //TODO:根据level和本场最高级别手术计算乘积得到工作量score
                recordDoctor.setDoctorScore(5);

                this.recordDoctorService.save(recordDoctor);
            }
        }

        return Constant.CRUD_SUCCESS;
    }


}
