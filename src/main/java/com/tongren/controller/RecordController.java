package com.tongren.controller;

import com.github.pagehelper.PageInfo;
import com.tongren.bean.CommonResult;
import com.tongren.bean.Constant;
import com.tongren.bean.Identity;
import com.tongren.bean.PageResult;
import com.tongren.bean.rolecheck.RequiredRoles;
import com.tongren.pojo.*;
import com.tongren.service.RecordDoctorService;
import com.tongren.service.RecordService;
import com.tongren.service.RecordSurgeryService;
import com.tongren.util.TimeUtil;
import com.tongren.util.Validator;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
        ArrayList<HashMap<String, Object>> surgeries = (ArrayList<HashMap<String, Object>>) params.get("surgeries");
        ArrayList<HashMap<String, Object>> surgeons = (ArrayList<HashMap<String, Object>>) params.get("surgeons");
        ArrayList<HashMap<String, Object>> helpers = (ArrayList<HashMap<String, Object>>) params.get("helpers");

        logger.info("date={} type={} historyNum={} name={} sex={} age={} eye={}", date, type, historyNum, name, sex, age, eye);

        //校验数据
        if (Validator.checkEmpty(type)
                || Validator.checkEmpty(historyNum)
                || Validator.checkEmpty(name)
                || Validator.checkEmpty(sex)
                || Validator.checkEmpty(eye)
                || Validator.checkNull(date)
                || Validator.checkNull(age)) {

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

        if (this.recordService.save(record, surgeries, surgeons, helpers) != Constant.CRUD_SUCCESS) {
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
        if (record == null) {
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
        ArrayList<HashMap<String, Object>> surgeries = (ArrayList<HashMap<String, Object>>) params.get("surgeries");
        ArrayList<HashMap<String, Object>> surgeons = (ArrayList<HashMap<String, Object>>) params.get("surgeons");
        ArrayList<HashMap<String, Object>> helpers = (ArrayList<HashMap<String, Object>>) params.get("helpers");

        logger.info("date={} type={} historyNum={} name={} sex={} age={} eye={}", date, type, historyNum, name, sex, age, eye);

        //校验数据
        if (Validator.checkEmpty(type)
                || Validator.checkEmpty(historyNum)
                || Validator.checkEmpty(name)
                || Validator.checkEmpty(sex)
                || Validator.checkEmpty(eye)
                || Validator.checkNull(date)
                || Validator.checkNull(age)) {

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

        if (this.recordService.update(record, surgeries, surgeons, helpers) != Constant.CRUD_SUCCESS) {
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
     *
     * @param recordId
     * @return
     */
    @RequestMapping(value = "{recordId}", method = RequestMethod.DELETE)
    @ResponseBody
    @RequiredRoles(roles = {"系统管理员"})
    public CommonResult deleteById(@PathVariable("recordId") Integer recordId) {


        if (this.recordService.deleteById(recordId) != Constant.CRUD_SUCCESS) {
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

        //根据角色查询不同的信息（仅管理员能查询工作量）
        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);
        String role = identity.getRole();
        List<RecordExtend> recordList = null;
        if (Constant.ADMIN.equals(role)) {

            recordList = this.recordService.queryRecordListForAdmin(pageNow, pageSize, params);
        } else if (Constant.INPUTER.equals(role) || Constant.DOCTOR.equals(role)) {

            recordList = this.recordService.queryRecordListForOthers(pageNow, pageSize, params);
        }


        PageResult pageResult = new PageResult(new PageInfo<>(recordList));
        return CommonResult.success("查询成功", pageResult);
    }


    @RequestMapping(value = "export", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult exportRecords(@RequestBody Map<String, Object> params, HttpSession session) {


        Date beginTime = TimeUtil.parseTime((String) params.get("beginTime"));
        Date endTime = TimeUtil.parseTime((String) params.get("endTime"));
        params.put("beginTime", beginTime);
        params.put("endTime", endTime);

        //根据角色查询不同的信息（仅管理员能查询工作量）
        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);
        List<RecordExtend> recordList = this.recordService.queryRecordListForAdmin(params);


        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet inputSheet = workbook.createSheet("手术记录");
        inputSheet.setDefaultColumnWidth(20);
        inputSheet.setDefaultRowHeight((short) (1.6 * 256));

        // 第一行，6个单元格合并，检查亚类
        {
            XSSFRow firstRow = inputSheet.createRow((short) 0);
            XSSFCell firstRowCell = firstRow.createCell((short) 0);
            firstRowCell.setCellValue("手术记录");

            XSSFFont firstFont = workbook.createFont();
            firstFont.setColor(XSSFFont.COLOR_RED); // 红色
            firstFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD); // 加粗
            firstFont.setFontHeightInPoints((short) 14);

            XSSFCellStyle firstStyle = workbook.createCellStyle();
            firstStyle.setFont(firstFont);
            firstStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);

            firstRowCell.setCellStyle(firstStyle);

            inputSheet.addMergedRegion(new CellRangeAddress(
                    0, //first firstRow (0-based)
                    0, //last firstRow (0-based)
                    0, //first column (0-based)
                    9 //last column (0-based)
            ));
        }

        // 第二行表头
        {
            XSSFRow secondRow = inputSheet.createRow((short) 1);

            XSSFFont boldFont = workbook.createFont();
            boldFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD); // 加粗

            XSSFCellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);


            XSSFCell cell = secondRow.createCell((short) 0);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("病历号");

            cell = secondRow.createCell((short) 1);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("类型");

            cell = secondRow.createCell((short) 2);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("姓名");

            cell = secondRow.createCell((short) 3);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("性别");

            cell = secondRow.createCell((short) 4);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("年龄");

            cell = secondRow.createCell((short) 5);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("眼别");

            cell = secondRow.createCell((short) 6);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("地点");

            cell = secondRow.createCell((short) 7);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("日期");

            cell = secondRow.createCell((short) 8);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("所做手术 / 级别 / 价格");

            cell = secondRow.createCell((short) 9);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("术者 / 级别 / 工作量");

            cell = secondRow.createCell((short) 10);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("助手 / 级别 / 工作量");
        }

        {
            int rowIndex = 2;
            for (RecordExtend record : recordList) {
                XSSFRow row = inputSheet.createRow((short) rowIndex);

                XSSFCell cell = row.createCell((short) 0);
                cell.setCellValue(record.getHistoryNum());

                cell = row.createCell((short) 1);
                cell.setCellValue(record.getType());

                cell = row.createCell((short) 2);
                cell.setCellValue(record.getName());

                cell = row.createCell((short) 3);
                cell.setCellValue(record.getSex());

                cell = row.createCell((short) 4);
                cell.setCellValue(record.getAge());

                cell = row.createCell((short) 5);
                cell.setCellValue(record.getEye());

                cell = row.createCell((short) 6);
                cell.setCellValue(record.getPlace());

                cell = row.createCell((short) 7);
                cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(record.getDate()));

                cell = row.createCell((short) 8);
                cell.setCellValue(record.getSurgeries() != null ? record.getSurgeries().replace(",", "\n") : "");

                cell = row.createCell((short) 9);
                cell.setCellValue(record.getSurgeons() != null ? record.getSurgeons().replace(",", "\n") : "");

                cell = row.createCell((short) 10);
                cell.setCellValue(record.getHelpers() != null ? record.getHelpers().replace(",", "\n") : "");

                rowIndex++;
            }
        }

        String userId = identity.getId();
        String fileName = Constant.FILE_PATH + "record/手术记录_" + userId + ".xlsx";

        try {
            FileOutputStream out = new FileOutputStream(new File(fileName));
            // OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return CommonResult.failure("下载失败");
        }

        return CommonResult.success("下载成功", "/record/手术记录_" + userId + ".xlsx");

    }


    /**
     * 条件分页查询某医生的详细手术记录
     * 会员member、职员employee
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "detail", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult queryDetails(@RequestBody Map<String, Object> params, HttpSession session) {

        Integer pageNow = (Integer) params.get(Constant.PAGE_NOW);
        Integer pageSize = (Integer) params.get(Constant.PAGE_SIZE);

        Date beginTime = TimeUtil.parseTime((String) params.get("beginTime"));
        Date endTime = TimeUtil.parseTime((String) params.get("endTime"));
        params.put("beginTime", beginTime);
        params.put("endTime", endTime);

        //根据角色查询不同的信息（仅管理员能查询工作量）
        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);
        String role = identity.getRole();
        if (Constant.ADMIN.equals(role)) {

            List<RecordExtend1> detailList = this.recordService.queryDetailListForAdmin(pageNow, pageSize, params);
            PageResult pageResult = new PageResult(new PageInfo<>(detailList));
            return CommonResult.success("查询成功", pageResult);

        } else if (Constant.DOCTOR.equals(role)) {

            List<Record> detailList = this.recordService.queryDetailListForOthers(pageNow, pageSize, params);
            PageResult pageResult = new PageResult(new PageInfo<>(detailList));
            return CommonResult.success("查询成功", pageResult);
        } else {

            return CommonResult.success("查询失败");
        }
    }


    @RequestMapping(value = "total_score", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult queryTotalScore(@RequestBody Map<String, Object> params, HttpSession session) {

        Date beginTime = TimeUtil.parseTime((String) params.get("beginTime"));
        Date endTime = TimeUtil.parseTime((String) params.get("endTime"));
        params.put("beginTime", beginTime);
        params.put("endTime", endTime);

        Integer totalScore = this.recordService.queryTotalScore(params);
        if (totalScore == null) {
            return CommonResult.success("查询失败");
        }

        return CommonResult.success("查询成功", totalScore);
    }

    @RequestMapping(value = "export_detail", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult exportDetail(@RequestBody Map<String, Object> params, HttpSession session) {


        Date beginTime = TimeUtil.parseTime((String) params.get("beginTime"));
        Date endTime = TimeUtil.parseTime((String) params.get("endTime"));
        params.put("beginTime", beginTime);
        params.put("endTime", endTime);

        //根据角色查询不同的信息（仅管理员能查询工作量）
        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);
        List<RecordExtend1> detailList = this.recordService.queryDetailListForAdmin(params);


        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet inputSheet = workbook.createSheet("医师手术记录");
        inputSheet.setDefaultColumnWidth(20);
        inputSheet.setDefaultRowHeight((short) (1.6 * 256));

        // 第一行，6个单元格合并，检查亚类
        {
            XSSFRow firstRow = inputSheet.createRow((short) 0);
            XSSFCell firstRowCell = firstRow.createCell((short) 0);
            firstRowCell.setCellValue("医师手术记录");

            XSSFFont firstFont = workbook.createFont();
            firstFont.setColor(XSSFFont.COLOR_RED); // 红色
            firstFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD); // 加粗
            firstFont.setFontHeightInPoints((short) 14);

            XSSFCellStyle firstStyle = workbook.createCellStyle();
            firstStyle.setFont(firstFont);
            firstStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);

            firstRowCell.setCellStyle(firstStyle);

            inputSheet.addMergedRegion(new CellRangeAddress(
                    0, //first firstRow (0-based)
                    0, //last firstRow (0-based)
                    0, //first column (0-based)
                    7 //last column (0-based)
            ));
        }

        // 第二行表头： 累计积分
        {
            XSSFRow firstRow = inputSheet.createRow((short) 1);
            XSSFFont boldFont = workbook.createFont();
            boldFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD); // 加粗
            XSSFCellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);

            XSSFCell cell = firstRow.createCell((short) 8);
            cell.setCellStyle(boldStyle);
            boldFont.setColor(XSSFFont.COLOR_RED); // 红色
            Integer totalScore = this.recordService.queryTotalScore(params);
            cell.setCellValue("累计工作量：" + (totalScore == null ? 0 : totalScore));
        }

        // 第三行：表头
        {
            XSSFRow secondRow = inputSheet.createRow((short) 2);

            XSSFFont boldFont = workbook.createFont();
            boldFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD); // 加粗

            XSSFCellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);


            XSSFCell cell = secondRow.createCell((short) 0);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("病历号");

            cell = secondRow.createCell((short) 1);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("类型");

            cell = secondRow.createCell((short) 2);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("姓名");

            cell = secondRow.createCell((short) 3);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("性别");

            cell = secondRow.createCell((short) 4);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("年龄");

            cell = secondRow.createCell((short) 5);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("眼别");

            cell = secondRow.createCell((short) 6);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("地点");

            cell = secondRow.createCell((short) 7);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("日期");

            cell = secondRow.createCell((short) 8);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("本场工作量");
        }

        //第四行：数据
        {
            int rowIndex = 3;
            for (RecordExtend1 detail : detailList) {
                XSSFRow row = inputSheet.createRow((short) rowIndex);

                XSSFCell cell = row.createCell((short) 0);
                cell.setCellValue(detail.getHistoryNum());

                cell = row.createCell((short) 1);
                cell.setCellValue(detail.getType());

                cell = row.createCell((short) 2);
                cell.setCellValue(detail.getName());

                cell = row.createCell((short) 3);
                cell.setCellValue(detail.getSex());

                cell = row.createCell((short) 4);
                cell.setCellValue(detail.getAge());

                cell = row.createCell((short) 5);
                cell.setCellValue(detail.getEye());

                cell = row.createCell((short) 6);
                cell.setCellValue(detail.getPlace());

                cell = row.createCell((short) 7);
                cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(detail.getDate()));

                cell = row.createCell((short) 8);
                cell.setCellValue(detail.getDoctorScore());

                rowIndex++;
            }
        }

        String userId = identity.getId();
        String fileName = Constant.FILE_PATH + "doctor_detail/医师手术记录_" + userId + ".xlsx";

        try {
            FileOutputStream out = new FileOutputStream(new File(fileName));
            workbook.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return CommonResult.failure("下载失败");
        }

        return CommonResult.success("下载成功", "/doctor_detail/医师手术记录_" + userId + ".xlsx");


    }
}
