package com.tongren.service;

import com.github.pagehelper.PageHelper;
import com.tongren.bean.Constant;
import com.tongren.pojo.Surgery;
import com.tongren.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class SurgeryService extends BaseService<Surgery> {

    /**
     * 条件查询医师
     * @param pageNow
     * @param pageSize
     * @param name
     * @param alias
     * @param level
     * @return
     */
    public List<Surgery> querySurgeryList(Integer pageNow, Integer pageSize, String code, String name, String alias, String level) {

        Example example = new Example(Surgery.class);
        Example.Criteria criteria = example.createCriteria();


        if (!Validator.checkEmpty(code)) {
            criteria.andLike("code", "%" + code + "%");
        }

        if (!Validator.checkEmpty(name)) {
            criteria.andLike("name", "%" + name + "%");
        }

        if (!Validator.checkEmpty(alias)) {
            criteria.andLike("alias", "%" + alias + "%");
        }

        if (!Validator.checkEmpty(level)) {
            criteria.andLike("level", "%" + level + "%");
        }

        PageHelper.startPage(pageNow, pageSize);
        return this.getMapper().selectByExample(example);
    }





}
