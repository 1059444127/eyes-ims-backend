package com.tongren.controller;

import com.github.pagehelper.PageInfo;
import com.tongren.bean.CommonResult;
import com.tongren.bean.Constant;
import com.tongren.bean.PageResult;
import com.tongren.bean.rolecheck.RequiredRoles;
import com.tongren.pojo.Record;
import com.tongren.pojo.RecordExtend;
import com.tongren.service.RecordService;
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


    /**
     * 添加员工
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

        if(this.recordService.save(record, surgeries, surgeons, helpers) != 1) {
            return CommonResult.failure("事务错误");
        }

        return CommonResult.success("添加成功");
    }


//    /**
//     * 修改别的手术记录的信息
//     *
//     * @param params
//     * @return
//     */
//    @RequestMapping(method = RequestMethod.PUT)
//    @ResponseBody
//    public CommonResult updateRecord(@RequestBody Map<String, Object> params) {
//
//        Integer recordId = (Integer) params.get("recordId");
//
//        String code = (String) params.get("code");
//        String name = (String) params.get("name");
//        String alias = (String) params.get("alias");
//        Double price = Double.parseDouble((String)params.get("price"));
//        String category = (String) params.get("category");
//        String chargeCode = (String) params.get("chargeCode");
//        String chargeName = (String) params.get("chargeName");
//        Double chargeCount = Double.parseDouble((String)params.get("chargeCount"));
//        Double chargePrice = Double.parseDouble((String)params.get("chargePrice"));
//        Double extraPrice = Double.parseDouble((String)params.get("extraPrice"));
//        String level = (String) params.get("level");
//
//        // 未修改的record
//        Record record = this.recordService.queryById(recordId);
//
//        if (!Validator.checkEmpty(code)) {
//            record.setCode(code);
//        }
//
//        if (!Validator.checkEmpty(name)) {
//            record.setName(name);
//        }
//
//        if (!Validator.checkEmpty(alias)) {
//            record.setAlias(alias);
//        }
//
//        if (!Validator.checkEmpty(category)) {
//            record.setCategory(category);
//        }
//
//        if (!Validator.checkEmpty(chargeCode)) {
//            record.setChargeCode(chargeCode);
//        }
//
//        if (!Validator.checkEmpty(chargeName)) {
//            record.setChargeName(chargeName);
//        }
//
//        if (!Validator.checkNull(price)) {
//            record.setPrice(price);
//        }
//
//        if (!Validator.checkNull(chargeCount)) {
//            record.setChargeCount(chargeCount);
//        }
//
//        if (!Validator.checkNull(chargePrice)) {
//            record.setChargePrice(chargePrice);
//        }
//
//        if (!Validator.checkNull(extraPrice)) {
//            record.setExtraPrice(extraPrice);
//        }
//
//
//        if (!Validator.checkEmpty(level)) {
//            record.setLevel(level);
//        }
//
//        this.recordService.update(record);
//
//        return CommonResult.success("修改成功");
//    }


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

        return CommonResult.success("查询成功", record);
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

        Record record = this.recordService.queryById(recordId);
        if (record == null) {
            return CommonResult.failure("手术记录不存在");
        }

        // this.recordService.deleteById(recordId);
        this.recordService.deleteById(recordId);

        logger.info("删除手术记录：{}", record.getName());

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
