package com.tongren.controller;

import com.github.pagehelper.PageInfo;
import com.tongren.bean.CommonResult;
import com.tongren.bean.Constant;
import com.tongren.bean.Identity;
import com.tongren.bean.PageResult;
import com.tongren.bean.rolecheck.RequiredRoles;
import com.tongren.pojo.Doctor;
import com.tongren.pojo.RecordExtend1;
import com.tongren.pojo.Record;
import com.tongren.pojo.User;
import com.tongren.service.DoctorService;
import com.tongren.service.PropertyService;
import com.tongren.service.UserService;
import com.tongren.util.MD5Util;
import com.tongren.util.TimeUtil;
import com.tongren.util.Validator;
import ken.searcher.PinyinSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * DoctorController
 */
@Controller
@RequestMapping("doctor")
public class DoctorController {

    private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserService userService;

    @Autowired
    private PropertyService propertyService;
    /**
     * 添加员工
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public CommonResult addDoctor(@RequestBody Map<String, Object> params) {

        String name = (String) params.get(Constant.NAME);
        String salaryNum = (String) params.get(Constant.SALARY_NUM);
        String level = (String) params.get(Constant.LEVEL);

        Doctor doctor = new Doctor();
        User user = new User();

        if (Validator.checkEmpty(name) || Validator.checkEmpty(salaryNum) || Validator.checkEmpty(level)) {
            return CommonResult.failure("添加失败，信息不完整");
        } else if(this.userService.isExist(salaryNum)) {
            return CommonResult.failure("该工资号已存在");
        } else {

            doctor.setName(name);
            doctor.setSalaryNum(salaryNum);
            doctor.setLevel(level);

            user.setName(name);
            user.setUsername(salaryNum);
            user.setRole(Constant.DOCTOR);
            try {
                user.setPassword(MD5Util.generate(Constant.DEFAULT_PASSWORD));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            user.setAvatar("avatar_default.png"); // 默认头像
        }

        this.doctorService.save(doctor, user);

        return CommonResult.success("添加成功");
    }


    /**
     * 修改别的用户的信息
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult updateDoctor(@RequestBody Map<String, Object> params) {

        Integer doctorId = (Integer) params.get("doctorId");
        // 修改别的用户的时候不能修改name和phone
        String name = (String) params.get(Constant.NAME);
        String level = (String) params.get(Constant.LEVEL);

        // 未修改的doctor
        Doctor doctor = this.doctorService.queryById(doctorId);

        if (!Validator.checkEmpty(name)) {
            doctor.setName(name);
        }

        if (!Validator.checkEmpty(level)) {
            doctor.setLevel(level);
        }

        this.doctorService.update(doctor);

        return CommonResult.success("修改成功");
    }


    /**
     * 查询用户信息
     *
     * @param doctorId
     * @return
     */
    @RequestMapping(value = "{doctorId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult queryById(@PathVariable("doctorId") Integer doctorId) {

        Doctor doctor = this.doctorService.queryById(doctorId);
        if (doctor == null) {
            return CommonResult.failure("用户不存在");
        }

        return CommonResult.success("查询成功", doctor);
    }


    /**
     * 删除用户
     * role改为已删除，doctorname加上_deleted的后缀
     *
     * @param doctorId
     * @return
     */
    @RequestMapping(value = "{doctorId}", method = RequestMethod.DELETE)
    @ResponseBody
    @RequiredRoles(roles = {"系统管理员"})
    public CommonResult deleteById(@PathVariable("doctorId") Integer doctorId) {

        Doctor doctor = this.doctorService.queryById(doctorId);
        if (doctor == null) {
            return CommonResult.failure("用户不存在");
        }

        // this.doctorService.deleteById(doctorId);
        this.doctorService.deleteById(doctorId);

        logger.info("删除用户：{}", doctor.getName());

        return CommonResult.success("删除成功");
    }






    /**
     * 条件分页查询用户
     * 会员member、职员employee
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult queryDoctors(@RequestBody Map<String, Object> params, HttpSession session) {

        Integer pageNow = (Integer) params.get(Constant.PAGE_NOW);
        Integer pageSize = (Integer) params.get(Constant.PAGE_SIZE);

        String name = (String) params.get(Constant.NAME);
        String salaryNum = (String) params.get(Constant.SALARY_NUM);
        String level = (String) params.get(Constant.LEVEL);

        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);

        List<Doctor> doctorList = this.doctorService.queryDoctorList(pageNow, pageSize, name, salaryNum, level);
        PageResult pageResult = new PageResult(new PageInfo<>(doctorList));

        logger.info("pageNow: {}, pageSize: {}, role: {}, phone: {}, name: {}", pageNow, pageSize, name, salaryNum, level);

        return CommonResult.success("查询成功", pageResult);
    }

    @RequestMapping(value = "list_keyword", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult queryDoctorsByKeyword(@RequestBody Map<String, Object> params, HttpSession session) {

        String keyword = (String) params.get("keyword");
        List<Doctor> doctorList = this.doctorService.querySurgeonAndHelperList();

        //构造待查集合
        List<Object> selectedList = new ArrayList<>();
        Iterator iterator = doctorList.iterator();
        while(iterator.hasNext()) selectedList.add(iterator.next());

        //筛选出符合keyword的项
        try {
            selectedList = new PinyinSearcher().match(keyword, selectedList, "name");
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.failure("查询失败");
        }


        return  CommonResult.success("查询成功", selectedList);
    }


    /**
     * 查询医师对应的级别系数
     * @return
     */
    @RequestMapping(value = "level", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult queryDoctorLevel() {

        Map<String, Integer> levels = propertyService.readIntegers(Constant.LEVEL_PROPERTIES_FILE_PATH);
        if(levels == null) {

            return CommonResult.failure("查询失败");
        }

        return CommonResult.success("查询成功", levels);
    }


    /**
     * 更新医师对应的级别系数
     * @return
     */
    @RequestMapping(value = "level", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult updateDoctorLevel(@RequestBody Map<String, Object> params) {

        if(propertyService.update(Constant.LEVEL_PROPERTIES_FILE_PATH, params) != Constant.CRUD_SUCCESS) {
            return CommonResult.success("更新失败");
        }

        return CommonResult.success("更新成功");
    }

}
