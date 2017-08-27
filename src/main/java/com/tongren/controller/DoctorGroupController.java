package com.tongren.controller;

import com.github.pagehelper.PageInfo;
import com.tongren.bean.CommonResult;
import com.tongren.bean.Constant;
import com.tongren.bean.Identity;
import com.tongren.bean.PageResult;
import com.tongren.bean.rolecheck.RequiredRoles;
import com.tongren.pojo.Doctor;
import com.tongren.pojo.DoctorGroup;
import com.tongren.pojo.User;
import com.tongren.service.DoctorGroupService;
import com.tongren.service.DoctorService;
import com.tongren.service.PropertyService;
import com.tongren.service.UserService;
import com.tongren.util.MD5Util;
import com.tongren.util.Validator;
import ken.searcher.PinyinSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * DoctorController
 */
@Controller
@RequestMapping("doctor_group")
public class DoctorGroupController {

    private static final Logger logger = LoggerFactory.getLogger(DoctorGroupController.class);

    @Autowired
    private DoctorGroupService doctorGroupService;

    @Autowired
    private DoctorService doctorService;

    /**
     * 添加医师组
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public CommonResult addDoctorGroup(@RequestBody Map<String, Object> params) {

        String name = (String) params.get(Constant.NAME);

        DoctorGroup doctorGroup = new DoctorGroup();

        if (Validator.checkEmpty(name)) {
            return CommonResult.failure("添加失败，信息不完整");
        } else {
            doctorGroup.setName(name);
        }

        this.doctorGroupService.save(doctorGroup);

        return CommonResult.success("添加成功");
    }


    /**
     * 修改医师组的信息
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult updateDoctorGroup(@RequestBody Map<String, Object> params) {

        Integer doctorGroupId = (Integer) params.get("doctorGroupId");
        String name = (String) params.get(Constant.NAME);

        // 未修改的doctor
        DoctorGroup doctorGroup = this.doctorGroupService.queryById(doctorGroupId);

        if (!Validator.checkEmpty(name)) {
            doctorGroup.setName(name);
        }

        this.doctorGroupService.update(doctorGroup);

        return CommonResult.success("修改成功");
    }


    /**
     * 查询医师组信息
     *
     * @param doctorGroupId
     * @return
     */
    @RequestMapping(value = "{doctorGroupId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult queryById(@PathVariable("doctorGroupId") Integer doctorGroupId) {

        DoctorGroup doctorGroup = this.doctorGroupService.queryById(doctorGroupId);
        if (doctorGroup == null) {
            return CommonResult.failure("医师组不存在");
        }

        return CommonResult.success("查询成功", doctorGroup);
    }


    /**
     * 删除医师组
     *
     * @param doctorGroupId
     * @return
     */
    @RequestMapping(value = "{doctorGroupId}", method = RequestMethod.DELETE)
    @ResponseBody
    @RequiredRoles(roles = {"系统管理员"})
    public CommonResult deleteById(@PathVariable("doctorGroupId") Integer doctorGroupId) {

        DoctorGroup doctorGroup = this.doctorGroupService.queryById(doctorGroupId);
        if (doctorGroup == null) {
            return CommonResult.failure("医师组不存在");
        }

        //判断该医师组旗下是否还有医师，若有不允许删除
        if(!this.doctorService.isAnyDoctorUnderTheGroup(doctorGroupId)) {

            this.doctorGroupService.deleteById(doctorGroupId);
        } else {

            return CommonResult.failure("该医师组下存在医师，无法删除");
        }

        logger.info("删除医师组：{}", doctorGroup.getName());

        return CommonResult.success("删除成功");
    }


    /**
     * 分页查询所有医师组
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult queryDoctorGroups(@RequestBody Map<String, Object> params, HttpSession session) {

        Integer pageNow = (Integer) params.get(Constant.PAGE_NOW);
        Integer pageSize = (Integer) params.get(Constant.PAGE_SIZE);

        List<DoctorGroup> doctorGroupList = this.doctorGroupService.queryDoctorGroupList(pageNow, pageSize);
        PageResult pageResult = new PageResult(new PageInfo<>(doctorGroupList));

        logger.info("pageNow: {}, pageSize: {}", pageNow, pageSize);

        return CommonResult.success("查询成功", pageResult);
    }

    /**
     * 查询所有的医师组
     * @param session
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult queryAllDoctorGroups(HttpSession session) {

        List<DoctorGroup> doctorGroupList = this.doctorGroupService.queryAll();
        return CommonResult.success("查询成功", doctorGroupList);
    }

}
