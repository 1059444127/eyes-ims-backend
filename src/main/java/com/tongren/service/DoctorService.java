package com.tongren.service;

import com.github.pagehelper.PageHelper;
import com.tongren.bean.Constant;
import com.tongren.mapper.DoctorMapper;
import com.tongren.mapper.UserMapper;
import com.tongren.pojo.Doctor;
import com.tongren.pojo.DoctorExtend;
import com.tongren.pojo.User;
import com.tongren.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class DoctorService extends BaseService<Doctor> {

    private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);


    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PropertyService propertyService;

    /**
     * 添加医师，同时添加用户
     * @param doctor
     * @param user
     * @return
     */
    public Integer save(Doctor doctor, User user) {

        //添加医师
        this.doctorMapper.insert(doctor);

        //同步添加用户
        user.setDoctorId(doctor.getId());
        this.userMapper.insert(user);

        return Constant.CRUD_SUCCESS;
    }


    /**
     * 条件查询医师
     * @param pageNow
     * @param pageSize
     * @param filters
     * @return
     */
    public List<DoctorExtend> queryDoctorList(Integer pageNow, Integer pageSize, Map<String, Object> filters) {

        PageHelper.startPage(pageNow, pageSize);
        return this.doctorMapper.selectByFilters(filters);
    }

    /**
     * 查询所有职称为医师的术者/助手
     * @return
     */
    public List<Doctor> querySurgeonAndHelperList() {

        return this.doctorMapper.selectSurgeonAndHelper();
    }

    /**
     *  检查医师组旗下是否存在任一医师
     * @param doctorGroupId
     * @return
     */
    public boolean isAnyDoctorUnderTheGroup(Integer doctorGroupId) {

        Doctor doctor = new Doctor();
        doctor.setGroupId(doctorGroupId);
        List<Doctor> doctorList = this.getMapper().select(doctor);
        return doctorList != null && doctorList.size() > 0;
    }

}
