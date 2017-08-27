package com.tongren.service;

import com.github.pagehelper.PageHelper;
import com.tongren.bean.Constant;
import com.tongren.mapper.DoctorMapper;
import com.tongren.mapper.UserMapper;
import com.tongren.pojo.Doctor;
import com.tongren.pojo.DoctorGroup;
import com.tongren.pojo.User;
import com.tongren.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class DoctorGroupService extends BaseService<DoctorGroup> {

    private static final Logger logger = LoggerFactory.getLogger(DoctorGroupService.class);

    public List<DoctorGroup> queryDoctorGroupList(Integer pageNow, Integer pageSize) {

        PageHelper.startPage(pageNow, pageSize);
        return this.getMapper().selectAll();
    }

}
