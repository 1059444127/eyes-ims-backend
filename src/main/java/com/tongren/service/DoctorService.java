package com.tongren.service;

import com.github.pagehelper.PageHelper;
import com.tongren.bean.Constant;
import com.tongren.mapper.DoctorMapper;
import com.tongren.mapper.UserMapper;
import com.tongren.pojo.Doctor;
import com.tongren.pojo.User;
import com.tongren.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

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
     * @param name
     * @param salaryNum
     * @param level
     * @return
     */
    public List<Doctor> queryDoctorList(Integer pageNow, Integer pageSize, String name, String salaryNum, String level) {

        Example example = new Example(Doctor.class);
        Example.Criteria criteria = example.createCriteria();


        if (!Validator.checkEmpty(name)) {
            criteria.andLike(Constant.NAME, "%" + name + "%");
        }

        if (!Validator.checkEmpty(salaryNum)) {
            criteria.andLike(Constant.SALARY_NUM, "%" + salaryNum + "%");
        }

        if (!Validator.checkEmpty(level)) {
            criteria.andLike(Constant.LEVEL, "%" + level + "%");
        }

        PageHelper.startPage(pageNow, pageSize);
        return this.getMapper().selectByExample(example);
    }

    /**
     * 查询所有职称为医师的术者/助手
     * @return
     */
    public List<Doctor> querySurgeonAndHelperList() {

        Example example = new Example(Doctor.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andLike(Constant.LEVEL, "%医师%");
        return this.getMapper().selectByExample(example);
    }

}
