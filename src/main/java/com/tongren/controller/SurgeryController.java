package com.tongren.controller;

import com.github.pagehelper.PageInfo;
import com.tongren.bean.CommonResult;
import com.tongren.bean.Constant;
import com.tongren.bean.Identity;
import com.tongren.bean.PageResult;
import com.tongren.bean.rolecheck.RequiredRoles;
import com.tongren.pojo.Surgery;
import com.tongren.service.SurgeryService;
import com.tongren.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * SurgeryController
 */
@Controller
@RequestMapping("surgery")
public class SurgeryController {

    private static final Logger logger = LoggerFactory.getLogger(SurgeryController.class);

    @Autowired
    private SurgeryService surgeryService;

    /**
     * 添加员工
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public CommonResult addSurgery(@RequestBody Map<String, Object> params) {

        String code = (String) params.get("code");
        String name = (String) params.get("name");
        String alias = (String) params.get("alias");
        Double price = Double.parseDouble((String)params.get("price"));
        String category = (String) params.get("category");
        String chargeCode = (String) params.get("chargeCode");
        String chargeName = (String) params.get("chargeName");
        Double chargeCount = Double.parseDouble((String)params.get("chargeCount"));
        Double chargePrice = Double.parseDouble((String)params.get("chargePrice"));
        Double extraPrice = Double.parseDouble((String)params.get("extraPrice"));
        String level = (String) params.get("level");

        Surgery surgery = new Surgery();

        if (Validator.checkEmpty(code)
                || Validator.checkEmpty(name)
                || Validator.checkEmpty(alias)
                || Validator.checkNull(price)
                || Validator.checkEmpty(category)
                || Validator.checkEmpty(chargeCode)
                || Validator.checkEmpty(chargeName)
                || Validator.checkNull(chargePrice)
                || Validator.checkNull(extraPrice)
                || Validator.checkNull(chargeCount)
                || Validator.checkEmpty(level)) {
            return CommonResult.failure("添加失败，信息不完整");
        } else {
            surgery.setCode(code);
            surgery.setName(name);
            surgery.setAlias(alias);
            surgery.setPrice(price);
            surgery.setCategory(category);
            surgery.setChargeCode(chargeCode);
            surgery.setChargeName(chargeName);
            surgery.setChargeCount(chargeCount);
            surgery.setChargePrice(chargePrice);
            surgery.setExtraPrice(extraPrice);
            surgery.setLevel(level);
        }


        this.surgeryService.save(surgery);
        return CommonResult.success("添加成功");
    }


    /**
     * 修改别的手术医嘱的信息
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult updateSurgery(@RequestBody Map<String, Object> params) {

        Integer surgeryId = (Integer) params.get("surgeryId");

        String code = (String) params.get("code");
        String name = (String) params.get("name");
        String alias = (String) params.get("alias");
        Double price = Double.parseDouble((String)params.get("price"));
        String category = (String) params.get("category");
        String chargeCode = (String) params.get("chargeCode");
        String chargeName = (String) params.get("chargeName");
        Double chargeCount = Double.parseDouble((String)params.get("chargeCount"));
        Double chargePrice = Double.parseDouble((String)params.get("chargePrice"));
        Double extraPrice = Double.parseDouble((String)params.get("extraPrice"));
        String level = (String) params.get("level");

        // 未修改的surgery
        Surgery surgery = this.surgeryService.queryById(surgeryId);

        if (!Validator.checkEmpty(code)) {
            surgery.setCode(code);
        }

        if (!Validator.checkEmpty(name)) {
            surgery.setName(name);
        }

        if (!Validator.checkEmpty(alias)) {
            surgery.setAlias(alias);
        }

        if (!Validator.checkEmpty(category)) {
            surgery.setCategory(category);
        }

        if (!Validator.checkEmpty(chargeCode)) {
            surgery.setChargeCode(chargeCode);
        }

        if (!Validator.checkEmpty(chargeName)) {
            surgery.setChargeName(chargeName);
        }

        if (!Validator.checkNull(price)) {
            surgery.setPrice(price);
        }

        if (!Validator.checkNull(chargeCount)) {
            surgery.setChargeCount(chargeCount);
        }

        if (!Validator.checkNull(chargePrice)) {
            surgery.setChargePrice(chargePrice);
        }

        if (!Validator.checkNull(extraPrice)) {
            surgery.setExtraPrice(extraPrice);
        }


        if (!Validator.checkEmpty(level)) {
            surgery.setLevel(level);
        }

        this.surgeryService.update(surgery);

        return CommonResult.success("修改成功");
    }


    /**
     * 查询手术医嘱信息
     *
     * @param surgeryId
     * @return
     */
    @RequestMapping(value = "{surgeryId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult queryById(@PathVariable("surgeryId") Integer surgeryId) {

        Surgery surgery = this.surgeryService.queryById(surgeryId);
        if (surgery == null) {
            return CommonResult.failure("手术医嘱不存在");
        }

        return CommonResult.success("查询成功", surgery);
    }


    /**
     * 删除手术医嘱
     * @param surgeryId
     * @return
     */
    @RequestMapping(value = "{surgeryId}", method = RequestMethod.DELETE)
    @ResponseBody
    @RequiredRoles(roles = {"系统管理员"})
    public CommonResult deleteById(@PathVariable("surgeryId") Integer surgeryId) {

        Surgery surgery = this.surgeryService.queryById(surgeryId);
        if (surgery == null) {
            return CommonResult.failure("手术医嘱不存在");
        }

        // this.surgeryService.deleteById(surgeryId);
        this.surgeryService.deleteById(surgeryId);

        logger.info("删除手术医嘱：{}", surgery.getName());

        return CommonResult.success("删除成功");
    }






    /**
     * 条件分页查询手术医嘱
     * 会员member、职员employee
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult querySurgerys(@RequestBody Map<String, Object> params, HttpSession session) {

        Integer pageNow = (Integer) params.get(Constant.PAGE_NOW);
        Integer pageSize = (Integer) params.get(Constant.PAGE_SIZE);

        String code = (String) params.get("code");
        String name = (String) params.get("name");
        String alias = (String) params.get("alias");
        String level = (String) params.get("level");

        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);

        List<Surgery> surgeryList = this.surgeryService.querySurgeryList(pageNow, pageSize, code, name, alias, level);
        PageResult pageResult = new PageResult(new PageInfo<>(surgeryList));

        logger.info("pageNow: {}, pageSize: {}, role: {}, phone: {}, name: {}", pageNow, pageSize, code, name, alias, level);

        return CommonResult.success("查询成功", pageResult);
    }


}
