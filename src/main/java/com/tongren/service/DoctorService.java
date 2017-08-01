package com.tongren.service;

import com.github.pagehelper.PageHelper;
import com.tongren.bean.CommonResult;
import com.tongren.bean.Constant;
import com.tongren.bean.Identity;
import com.tongren.pojo.Doctor;
import com.tongren.util.MD5Util;
import com.tongren.util.TokenUtil;
import com.tongren.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DoctorService extends BaseService<Doctor> {

    private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);

    @Autowired
    private PropertyService propertyService;


    /**
     * 查询所有医师
     *
     * @return
     */
    public List<Doctor> queryAllMembers() {

        Example example = new Example(Doctor.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andLike("role", "%医师%");
        return this.getMapper().selectByExample(example);
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





}
