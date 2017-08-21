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

    @Autowired
    private SurgeryService surgeryService;

    @Autowired
    private PropertyService propertyService;

    /**
     * 管理员查询手术记录（含工作量）
     * @param pageNow
     * @param pageSize
     * @param filters
     * @return
     */
    public List<RecordExtend> queryRecordListForAdmin(Integer pageNow,
                                              Integer pageSize,
                                              Map<String, Object> filters) {

        PageHelper.startPage(pageNow, pageSize);
        return this.recordMapper.selectByFiltersForAdmin(filters);
    }

    public List<RecordExtend> queryRecordListForAdmin(Map<String, Object> filters) {

        return this.recordMapper.selectByFiltersForAdmin(filters);
    }

    /**
     * 其它角色查询手术记录（不含工作量）
     * @param pageNow
     * @param pageSize
     * @param filters
     * @return
     */
    public List<RecordExtend> queryRecordListForOthers(Integer pageNow,
                                                      Integer pageSize,
                                                      Map<String, Object> filters) {

        PageHelper.startPage(pageNow, pageSize);
        return this.recordMapper.selectByFiltersForOthers(filters);
    }

    public List<RecordExtend> queryRecordListForOthers(Map<String, Object> filters) {

        return this.recordMapper.selectByFiltersForOthers(filters);
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

        //获取手术级别、医师级别系数列表
        Map<String, Integer> levels = this.propertyService.readIntegers(Constant.LEVEL_PROPERTIES_FILE_PATH);


        //本场手术的最大级别系数
        Integer maxSurgeryLevel = 1;

        //构造手术记录-所做手术对象
        if(!Validator.checkNull(surgeries)) {

            for(HashMap<String, Object> surgery : surgeries) {

                RecordSurgery recordSurgery = new RecordSurgery();
                Integer surgeryId = Integer.parseInt((String)surgery.get("key"));
                recordSurgery.setRecordId(recordId);
                recordSurgery.setSurgeryId(surgeryId);
                this.recordSurgeryService.save(recordSurgery);

                //每轮比较找出最高级别手术
                Integer level = levels.get(this.surgeryService.queryById(surgeryId).getLevel());
                maxSurgeryLevel = level > maxSurgeryLevel ? level : maxSurgeryLevel;
            }
        }

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
                recordDoctor.setDoctorScore(this.calDoctorScore(doctor.getLevel(), maxSurgeryLevel, levels));

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
                recordDoctor.setDoctorScore(this.calDoctorScore(doctor.getLevel(), maxSurgeryLevel, levels));

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

        //获取手术级别、医师级别系数列表
        Map<String, Integer> levels = this.propertyService.readIntegers(Constant.LEVEL_PROPERTIES_FILE_PATH);


        //本场手术的最大级别系数
        Integer maxSurgeryLevel = 1;


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
                Integer surgeryId = Integer.parseInt((String)surgery.get("key"));
                recordSurgery.setRecordId(recordId);
                recordSurgery.setSurgeryId(surgeryId);
                this.recordSurgeryService.save(recordSurgery);

                //每轮比较找出最高级别手术
                Integer level = levels.get(this.surgeryService.queryById(surgeryId).getLevel());
                maxSurgeryLevel = level > maxSurgeryLevel ? level : maxSurgeryLevel;
            }
        }


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
                recordDoctor.setDoctorScore(this.calDoctorScore(doctor.getLevel(), maxSurgeryLevel, levels));

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
                recordDoctor.setDoctorScore(this.calDoctorScore(doctor.getLevel(), maxSurgeryLevel, levels));

                this.recordDoctorService.save(recordDoctor);
            }
        }

        return Constant.CRUD_SUCCESS;
    }

    /**
     * 计算医师的工作量积分
     * @param doctorLevel
     * @param maxSurgeryLevel
     * @param levels
     * @return
     */
    private Integer calDoctorScore(String doctorLevel, Integer maxSurgeryLevel, Map<String, Integer> levels) {

        //根据level和本场最高级别手术计算乘积得到工作量积分score
        Integer score = 0;
        if(doctorLevel.indexOf(Constant.DOCTOR_VICEHEAD) != -1) {

            score = levels.get(Constant.DOCTOR_VICEHEAD) * maxSurgeryLevel;

        } else if(doctorLevel.indexOf(Constant.DOCTOR_HEAD) != -1) {

            score = levels.get(Constant.DOCTOR_HEAD) * maxSurgeryLevel;

        } else if(doctorLevel.indexOf(Constant.DOCTOR_TREAT) != -1) {

            score = levels.get(Constant.DOCTOR_TREAT) * maxSurgeryLevel;

        } else if(doctorLevel.indexOf(Constant.DOCTOR_RESIDENT) != -1) {

            score = levels.get(Constant.DOCTOR_RESIDENT) * maxSurgeryLevel;
        }

        return score;
    }

}
