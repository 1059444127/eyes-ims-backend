package com.tongren.controller;

import com.github.pagehelper.PageInfo;
import com.tongren.bean.CommonResult;
import com.tongren.bean.Constant;
import com.tongren.bean.PageResult;
import com.tongren.bean.rolecheck.RequiredRoles;
import com.tongren.pojo.*;
import com.tongren.service.RecordDoctorService;
import com.tongren.service.RecordService;
import com.tongren.service.RecordSurgeryService;
import com.tongren.util.TimeUtil;
import com.tongren.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * RecordController
 */
@Controller
@RequestMapping("record")
public class RecordController {

    private static final Logger logger = LoggerFactory.getLogger(RecordController.class);

    @Autowired
    private RecordService recordService;

    @Autowired
    private RecordSurgeryService recordSurgeryService;

    @Autowired
    private RecordDoctorService recordDoctorService;

    /**
     * 添加手术记录
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public CommonResult addRecord(@RequestBody Map<String, Object> params) {


        //手术记录的基本信息
        Date date = TimeUtil.parseTime((String) params.get("date"));
        String type = (String) params.get("type");
        String historyNum = (String) params.get("historyNum");
        String name = (String) params.get("name");
        String sex = (String) params.get("sex");
        Integer age = (Integer) params.get("age");
        String eye = (String) params.get("eye");
        String place = (String) params.get("place");

        //手术记录的详细信息
        //手术数组、术者数组、助手数组
        ArrayList<HashMap<String, Object>> surgeries = (ArrayList<HashMap<String,Object>>) params.get("surgeries");
        ArrayList<HashMap<String, Object>> surgeons = (ArrayList<HashMap<String,Object>>) params.get("surgeons");
        ArrayList<HashMap<String, Object>> helpers = (ArrayList<HashMap<String,Object>>) params.get("helpers");

        logger.info("date={} type={} historyNum={} name={} sex={} age={} eye={}", date, type, historyNum, name, sex, age, eye);

        //校验数据
        if(Validator.checkEmpty(type)
                || Validator.checkEmpty(historyNum)
                || Validator.checkEmpty(name)
                || Validator.checkEmpty(sex)
                || Validator.checkEmpty(eye)
                || Validator.checkNull(date)
                || Validator.checkNull(age)){

            return CommonResult.failure("信息不完整");
        }

        //构造手术记录对象
        Record record = new Record();
        record.setName(name);
        record.setSex(sex);
        record.setAge(age);
        record.setEye(eye);
        record.setHistoryNum(historyNum);
        record.setType(type);
        record.setDate(date);
        record.setPlace(place);

        if(this.recordService.save(record, surgeries, surgeons, helpers) != Constant.CRUD_SUCCESS) {
            return CommonResult.failure("事务错误");
        }

        return CommonResult.success("添加成功");
    }


    /**
     * 修改手术记录
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult updateRecord(@RequestBody Map<String, Object> params) {

        Integer recordId = (Integer) params.get("recordId");
        Record record = this.recordService.queryById(recordId);
        if(record == null) {
            return CommonResult.failure("不存在该手术记录");
        }

        //手术记录的基本信息
        Date date = TimeUtil.parseTime((String) params.get("date"));
        String type = (String) params.get("type");
        String historyNum = (String) params.get("historyNum");
        String name = (String) params.get("name");
        String sex = (String) params.get("sex");
        Integer age = (Integer) params.get("age");
        String eye = (String) params.get("eye");
        String place = (String) params.get("place");

        //手术记录的详细信息
        //手术数组、术者数组、助手数组
        ArrayList<HashMap<String, Object>> surgeries = (ArrayList<HashMap<String,Object>>) params.get("surgeries");
        ArrayList<HashMap<String, Object>> surgeons = (ArrayList<HashMap<String,Object>>) params.get("surgeons");
        ArrayList<HashMap<String, Object>> helpers = (ArrayList<HashMap<String,Object>>) params.get("helpers");

        logger.info("date={} type={} historyNum={} name={} sex={} age={} eye={}", date, type, historyNum, name, sex, age, eye);

        //校验数据
        if(Validator.checkEmpty(type)
                || Validator.checkEmpty(historyNum)
                || Validator.checkEmpty(name)
                || Validator.checkEmpty(sex)
                || Validator.checkEmpty(eye)
                || Validator.checkNull(date)
                || Validator.checkNull(age)){

            return CommonResult.failure("信息不完整");
        }

        //构造手术记录对象
        record.setName(name);
        record.setSex(sex);
        record.setAge(age);
        record.setEye(eye);
        record.setHistoryNum(historyNum);
        record.setType(type);
        record.setDate(date);
        record.setPlace(place);

        if(this.recordService.update(record, surgeries, surgeons, helpers) != Constant.CRUD_SUCCESS) {
            return CommonResult.failure("事务错误");
        }

        return CommonResult.success("修改成功");
    }


    /**
     * 查询手术记录信息
     *
     * @param recordId
     * @return
     */
    @RequestMapping(value = "{recordId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult queryById(@PathVariable("recordId") Integer recordId) {

        Record record = this.recordService.queryById(recordId);
        if (record == null) {
            return CommonResult.failure("手术记录不存在");
        }

        List<RecordSurgeryExtend> surgeries = this.recordSurgeryService.queryByRecordId(recordId);
        List<RecordDoctor> surgeons = this.recordDoctorService.queryByRecordIdAndDoctorType(recordId, "术者");
        List<RecordDoctor> helpers = this.recordDoctorService.queryByRecordIdAndDoctorType(recordId, "助手");

        Map<String, Object> result = new HashMap<>();
        result.put("record", record);
        result.put("surgeries", surgeries);
        result.put("surgeons", surgeons);
        result.put("helpers", helpers);
        return CommonResult.success("查询成功", result);
    }


    /**
     * 删除手术记录
     * @param recordId
     * @return
     */
    @RequestMapping(value = "{recordId}", method = RequestMethod.DELETE)
    @ResponseBody
    @RequiredRoles(roles = {"系统管理员"})
    public CommonResult deleteById(@PathVariable("recordId") Integer recordId) {


        if(this.recordService.deleteById(recordId) != Constant.CRUD_SUCCESS) {
            return CommonResult.failure("删除失败");
        }

        logger.info("删除手术记录：{}", recordId);

        return CommonResult.success("删除成功");
    }






    /**
     * 条件分页查询手术记录
     * 会员member、职员employee
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult queryRecords(@RequestBody Map<String, Object> params, HttpSession session) {

        Integer pageNow = (Integer) params.get(Constant.PAGE_NOW);
        Integer pageSize = (Integer) params.get(Constant.PAGE_SIZE);

        Date beginTime = TimeUtil.parseTime((String) params.get("beginTime"));
        Date endTime = TimeUtil.parseTime((String) params.get("endTime"));
        params.put("beginTime", beginTime);
        params.put("endTime", endTime);

        List<RecordExtend> recordList = this.recordService.queryRecordList(pageNow, pageSize, params);
        PageResult pageResult = new PageResult(new PageInfo<>(recordList));

        return CommonResult.success("查询成功", pageResult);
    }

    
}
