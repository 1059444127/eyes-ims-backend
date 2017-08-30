package com.tongren.service;

import com.github.pagehelper.PageHelper;
import com.tongren.bean.Constant;
import com.tongren.bean.Identity;
import com.tongren.mapper.RecordMapper;
import com.tongren.pojo.*;
import com.tongren.util.Validator;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RecordService extends BaseService<Record> {

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private RecordSurgeryService recordSurgeryService;

    @Autowired
    private RecordDoctorService recordDoctorService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DoctorGroupService doctorGroupService;

    @Autowired
    private SurgeryService surgeryService;

    @Autowired
    private PropertyService propertyService;

    /**
     * 管理员查询手术记录（含工作量）
     * @param pageNow
     * @param pageSize
     * @param filters
     * @return
     */
    public List<RecordExtend> queryRecordListForAdmin(Integer pageNow,
                                              Integer pageSize,
                                              Map<String, Object> filters) {

        PageHelper.startPage(pageNow, pageSize);
        return this.recordMapper.selectByFiltersForAdmin(filters);
    }

    public List<RecordExtend> queryRecordListForAdmin(Map<String, Object> filters) {

        return this.recordMapper.selectByFiltersForAdmin(filters);
    }

    /**
     * 其它角色查询手术记录（不含工作量）
     * @param pageNow
     * @param pageSize
     * @param filters
     * @return
     */
    public List<RecordExtend> queryRecordListForOthers(Integer pageNow,
                                                      Integer pageSize,
                                                      Map<String, Object> filters) {

        PageHelper.startPage(pageNow, pageSize);
        return this.recordMapper.selectByFiltersForOthers(filters);
    }

    public List<RecordExtend> queryRecordListForOthers(Map<String, Object> filters) {

        return this.recordMapper.selectByFiltersForOthers(filters);
    }

    /**
     * 添加手术记录（记录-手术项、记录-术者/助手）
     * @param record
     * @param surgeries
     * @param surgeons
     * @param helpers
     * @return
     */
    public Integer save(Record record,
                        ArrayList<HashMap<String, Object>> surgeries,
                        ArrayList<HashMap<String, Object>> surgeons,
                        ArrayList<HashMap<String, Object>> helpers) {

        //首要添加记录
        this.save(record);

        Integer recordId = record.getId();
        Date date = record.getDate();

        //获取手术级别、医师级别系数列表
        Map<String, Double> levels = this.propertyService.readDoubles(Constant.LEVEL_PROPERTIES_FILE_PATH);


        //本场手术的最大级别系数
        Double maxSurgeryLevel = 0.0;

        //构造手术记录-所做手术对象
        if(!Validator.checkNull(surgeries)) {

            for(HashMap<String, Object> surgery : surgeries) {

                RecordSurgery recordSurgery = new RecordSurgery();
                Integer surgeryId = Integer.parseInt((String)surgery.get("key"));
                recordSurgery.setRecordId(recordId);
                recordSurgery.setSurgeryId(surgeryId);
                this.recordSurgeryService.save(recordSurgery);

                //每轮比较找出最高级别手术
                Double level = levels.get(this.surgeryService.queryById(surgeryId).getLevel());
                maxSurgeryLevel = level > maxSurgeryLevel ? level : maxSurgeryLevel;
            }
        }

        //构造手术记录-术者对象
        if(!Validator.checkNull(surgeons)) {

            for(HashMap<String, Object> surgeon : surgeons) {

                Integer surgeonId = Integer.parseInt((String)surgeon.get("key"));

                RecordDoctor recordDoctor = new RecordDoctor();
                recordDoctor.setRecordId(recordId);
                recordDoctor.setDoctorId(surgeonId);
                recordDoctor.setDate(date);

                //获取术者当前的信息
                Doctor doctor = this.doctorService.queryById(surgeonId);
                DoctorGroup group = this.doctorGroupService.queryById(doctor.getGroupId());
                recordDoctor.setDoctorType("术者");
                recordDoctor.setDoctorName(doctor.getName());
                recordDoctor.setDoctorSalaryNum(doctor.getSalaryNum());
                recordDoctor.setDoctorGroupId(group.getId());
                recordDoctor.setDoctorGroupName(group.getName());
                recordDoctor.setDoctorLevel(doctor.getLevel());
                recordDoctor.setDoctorScore(this.calDoctorScore(doctor.getLevel(), maxSurgeryLevel, levels));

                this.recordDoctorService.save(recordDoctor);
            }
        }

        //构造手术记录-助手对象
        if(!Validator.checkNull(helpers)) {

            for(HashMap<String, Object> helper : helpers) {

                Integer helperId = Integer.parseInt((String)helper.get("key"));

                RecordDoctor recordDoctor = new RecordDoctor();
                recordDoctor.setRecordId(recordId);
                recordDoctor.setDoctorId(helperId);
                recordDoctor.setDate(date);

                //获取术者当前的信息
                Doctor doctor = this.doctorService.queryById(helperId);
                DoctorGroup group = this.doctorGroupService.queryById(doctor.getGroupId());
                recordDoctor.setDoctorType("助手");
                recordDoctor.setDoctorName(doctor.getName());
                recordDoctor.setDoctorSalaryNum(doctor.getSalaryNum());
                recordDoctor.setDoctorGroupId(group.getId());
                recordDoctor.setDoctorGroupName(group.getName());
                recordDoctor.setDoctorLevel(doctor.getLevel());
                recordDoctor.setDoctorScore(this.calDoctorScore(doctor.getLevel(), maxSurgeryLevel, levels));

                this.recordDoctorService.save(recordDoctor);
            }
        }

        return Constant.CRUD_SUCCESS;
    }


    /**
     * 修改手术记录（记录-手术项、记录-术者/助手）
     * @param record
     * @param surgeries
     * @param surgeons
     * @param helpers
     * @return
     */
    public Integer update(Record record,
                        ArrayList<HashMap<String, Object>> surgeries,
                        ArrayList<HashMap<String, Object>> surgeons,
                        ArrayList<HashMap<String, Object>> helpers) {

        //修改记录本身
        this.update(record);

        Integer recordId = record.getId();
        Date date = record.getDate();

        //获取手术级别、医师级别系数列表
        Map<String, Double> levels = this.propertyService.readDoubles(Constant.LEVEL_PROPERTIES_FILE_PATH);


        //本场手术的最大级别系数
        Double maxSurgeryLevel = 0.0;


        //删除手术记录-所做手术 原来的信息
        RecordSurgery recordSurgery = new RecordSurgery();
        recordSurgery.setRecordId(recordId);
        this.recordSurgeryService.deleteByWhere(recordSurgery);

        //删除手术记录-术者/助手 原来的信息
        RecordDoctor recordDoctor = new RecordDoctor();
        recordDoctor.setRecordId(recordId);
        this.recordDoctorService.deleteByWhere(recordDoctor);


        //构造手术记录-所做手术对象
        if(!Validator.checkNull(surgeries)) {

            for(HashMap<String, Object> surgery : surgeries) {

                recordSurgery = new RecordSurgery();
                Integer surgeryId = Integer.parseInt((String)surgery.get("key"));
                recordSurgery.setRecordId(recordId);
                recordSurgery.setSurgeryId(surgeryId);
                this.recordSurgeryService.save(recordSurgery);

                //每轮比较找出最高级别手术
                Double level = levels.get(this.surgeryService.queryById(surgeryId).getLevel());
                maxSurgeryLevel = level > maxSurgeryLevel ? level : maxSurgeryLevel;
            }
        }


        //构造手术记录-术者对象
        if(!Validator.checkNull(surgeons)) {

            for(HashMap<String, Object> surgeon : surgeons) {

                Integer surgeonId = Integer.parseInt((String)surgeon.get("key"));

                recordDoctor = new RecordDoctor();
                recordDoctor.setRecordId(recordId);
                recordDoctor.setDoctorId(surgeonId);
                recordDoctor.setDate(date);

                //获取术者当前的信息
                Doctor doctor = this.doctorService.queryById(surgeonId);
                DoctorGroup group = this.doctorGroupService.queryById(doctor.getGroupId());
                recordDoctor.setDoctorType("术者");
                recordDoctor.setDoctorName(doctor.getName());
                recordDoctor.setDoctorSalaryNum(doctor.getSalaryNum());
                recordDoctor.setDoctorGroupId(group.getId());
                recordDoctor.setDoctorGroupName(group.getName());
                recordDoctor.setDoctorLevel(doctor.getLevel());
                recordDoctor.setDoctorScore(this.calDoctorScore(doctor.getLevel(), maxSurgeryLevel, levels));

                this.recordDoctorService.save(recordDoctor);
            }
        }

        //构造手术记录-助手对象
        if(!Validator.checkNull(helpers)) {

            for(HashMap<String, Object> helper : helpers) {

                Integer helperId = Integer.parseInt((String)helper.get("key"));

                recordDoctor = new RecordDoctor();
                recordDoctor.setRecordId(recordId);
                recordDoctor.setDoctorId(helperId);
                recordDoctor.setDate(date);

                //获取术者当前的信息
                Doctor doctor = this.doctorService.queryById(helperId);
                DoctorGroup group = this.doctorGroupService.queryById(doctor.getGroupId());
                recordDoctor.setDoctorType("助手");
                recordDoctor.setDoctorName(doctor.getName());
                recordDoctor.setDoctorSalaryNum(doctor.getSalaryNum());
                recordDoctor.setDoctorGroupId(group.getId());
                recordDoctor.setDoctorGroupName(group.getName());
                recordDoctor.setDoctorLevel(doctor.getLevel());
                recordDoctor.setDoctorScore(this.calDoctorScore(doctor.getLevel(), maxSurgeryLevel, levels));

                this.recordDoctorService.save(recordDoctor);
            }
        }

        return Constant.CRUD_SUCCESS;
    }

    /**
     * 计算医师的工作量积分
     * @param doctorLevel
     * @param maxSurgeryLevel
     * @param levels
     * @return
     */
    private Double calDoctorScore(String doctorLevel, Double maxSurgeryLevel, Map<String, Double> levels) {

        //根据level和本场最高级别手术计算乘积得到工作量积分score
        Double score = 0.0;
        if(doctorLevel.indexOf(Constant.DOCTOR_VICEHEAD) != -1) {

            score = levels.get(Constant.DOCTOR_VICEHEAD) * maxSurgeryLevel;

        } else if(doctorLevel.indexOf(Constant.DOCTOR_HEAD) != -1) {

            score = levels.get(Constant.DOCTOR_HEAD) * maxSurgeryLevel;

        } else if(doctorLevel.indexOf(Constant.DOCTOR_TREAT) != -1) {

            score = levels.get(Constant.DOCTOR_TREAT) * maxSurgeryLevel;

        } else if(doctorLevel.indexOf(Constant.DOCTOR_RESIDENT) != -1) {

            score = levels.get(Constant.DOCTOR_RESIDENT) * maxSurgeryLevel;
        } else if(doctorLevel.indexOf(Constant.DOCTOR_TRAIN) != -1) {

            score = levels.get(Constant.DOCTOR_TRAIN) * maxSurgeryLevel;
        } else if(doctorLevel.indexOf(Constant.DOCTOR_MASTER) != -1) {

            score = levels.get(Constant.DOCTOR_MASTER) * maxSurgeryLevel;
        }

        return score;
    }


    /**
     * 管理员查询手术记录（含工作量）
     * @param pageNow
     * @param pageSize
     * @param filters
     * @return
     */
    public List<RecordExtend> queryDetailListForAdmin(Integer pageNow,
                                                       Integer pageSize,
                                                       Map<String, Object> filters) {

        PageHelper.startPage(pageNow, pageSize);
        return this.recordMapper.selectDetailByFiltersForAdmin(filters);
    }

    public List<RecordExtend> queryDetailListForAdmin(Map<String, Object> filters) {

        return this.recordMapper.selectDetailByFiltersForAdmin(filters);
    }

    public Double queryTotalScore(Map<String, Object> filters) {

        return this.recordMapper.selectTotalScore(filters);
    }


    /**
     * 其它角色查询手术记录（不含工作量）
     * @param pageNow
     * @param pageSize
     * @param filters
     * @return
     */
    public List<RecordExtend> queryDetailListForOthers(Integer pageNow,
                                                 Integer pageSize,
                                                 Map<String, Object> filters) {

        PageHelper.startPage(pageNow, pageSize);
        return this.recordMapper.selectDetailByFiltersForOthers(filters);
    }

    public List<RecordExtend> queryDetailListForOthers(Map<String, Object> filters) {

        return this.recordMapper.selectDetailByFiltersForOthers(filters);
    }

    /**
     * 导出手术记录表格
     * @param recordList
     * @param identity
     * @return
     */
    public Integer exportRecord(List<RecordExtend> recordList, Identity identity) {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet inputSheet = workbook.createSheet("手术记录");
        inputSheet.setDefaultColumnWidth(13);
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
                    18 //last column (0-based)
            ));
        }

        // 第二行表头
        {
            XSSFRow secondRow = inputSheet.createRow((short) 1);
            XSSFRow thirdRow = inputSheet.createRow((short) 2);

            XSSFFont boldFont = workbook.createFont();
            boldFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD); // 加粗

            XSSFCellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);
            boldStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
            boldStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);

            XSSFCell cell = secondRow.createCell((short) 0);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("病历号");
            inputSheet.addMergedRegion(new CellRangeAddress( 1, 2, 0, 0));

            cell = secondRow.createCell((short) 1);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("类型");
            inputSheet.addMergedRegion(new CellRangeAddress( 1, 2, 1, 1));

            cell = secondRow.createCell((short) 2);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("姓名");
            inputSheet.addMergedRegion(new CellRangeAddress( 1, 2, 2, 2));

            cell = secondRow.createCell((short) 3);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("性别");
            inputSheet.addMergedRegion(new CellRangeAddress( 1, 2, 3, 3));

            cell = secondRow.createCell((short) 4);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("年龄");
            inputSheet.addMergedRegion(new CellRangeAddress( 1, 2, 4, 4));

            cell = secondRow.createCell((short) 5);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("眼别");
            inputSheet.addMergedRegion(new CellRangeAddress( 1, 2, 5, 5));

            cell = secondRow.createCell((short) 6);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("地点");
            inputSheet.addMergedRegion(new CellRangeAddress( 1, 2, 6, 6));

            cell = secondRow.createCell((short) 7);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("日期");
            inputSheet.addMergedRegion(new CellRangeAddress( 1, 2, 7, 7));

            cell = secondRow.createCell((short) 8);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("手术信息");
            inputSheet.addMergedRegion(new CellRangeAddress( 1, 1, 8, 10));
            cell = thirdRow.createCell((short) 8);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("名称");
            cell = thirdRow.createCell((short) 9);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("级别");
            cell = thirdRow.createCell((short) 10);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("价格");

            cell = secondRow.createCell((short) 11);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("术者信息");
            inputSheet.addMergedRegion(new CellRangeAddress( 1, 1, 11, 14));
            cell = thirdRow.createCell((short) 11);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("名称");
            cell = thirdRow.createCell((short) 12);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("级别");
            cell = thirdRow.createCell((short) 13);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("所在组");
            cell = thirdRow.createCell((short) 14);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("工作量");

            cell = secondRow.createCell((short) 15);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("助手信息");
            inputSheet.addMergedRegion(new CellRangeAddress( 1, 1, 15, 18));
            cell = thirdRow.createCell((short) 15);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("名称");
            cell = thirdRow.createCell((short) 16);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("级别");
            cell = thirdRow.createCell((short) 17);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("所在组");
            cell = thirdRow.createCell((short) 18);
            cell.setCellStyle(boldStyle);
            cell.setCellValue("工作量");
        }

        {
            int rowIndex = 3;
            XSSFCellStyle centerStyle = workbook.createCellStyle();
            centerStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
            centerStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            for (RecordExtend record : recordList) {

                String[] surgeries = record.getSurgeries() == null ? " / / ".split(",") : record.getSurgeries().split(",");
                String[] surgeons = record.getSurgeons() == null ? " / / / ".split(",") : record.getSurgeons().split(",");
                String[] helpers = record.getHelpers() == null ? " / / / ".split(",") : record.getHelpers().split(",");

                //计算最大的行数
                int rowLength = surgeries.length;
                if(surgeons.length > rowLength) {
                    rowLength = surgeons.length;
                }
                if(helpers.length > rowLength) {
                    rowLength = helpers.length;
                }
                System.out.println(rowIndex + "," + rowLength);

                //构造行
                XSSFRow[] rows = new XSSFRow[rowLength];
                for(int i = 0; i < rows.length; i++) {
                    rows[i] = inputSheet.createRow((short) rowIndex + i);
                }

                XSSFCell cell = rows[0].createCell((short) 0);
                cell.setCellStyle(centerStyle);
                cell.setCellValue(record.getHistoryNum());
                if(rowLength > 1) inputSheet.addMergedRegion(new CellRangeAddress( rowIndex, rowIndex + rowLength - 1, 0, 0));

                cell = rows[0].createCell((short) 1);
                cell.setCellStyle(centerStyle);
                cell.setCellValue(record.getType());
                if(rowLength > 1) inputSheet.addMergedRegion(new CellRangeAddress( rowIndex, rowIndex + rowLength - 1, 1, 1));

                cell = rows[0].createCell((short) 2);
                cell.setCellStyle(centerStyle);
                cell.setCellValue(record.getName());
                if(rowLength > 1) inputSheet.addMergedRegion(new CellRangeAddress( rowIndex, rowIndex + rowLength - 1, 2, 2));

                cell = rows[0].createCell((short) 3);
                cell.setCellStyle(centerStyle);
                cell.setCellValue(record.getSex());
                if(rowLength > 1) inputSheet.addMergedRegion(new CellRangeAddress( rowIndex, rowIndex + rowLength - 1, 3, 3));

                cell = rows[0].createCell((short) 4);
                cell.setCellStyle(centerStyle);
                cell.setCellValue(record.getAge());
                if(rowLength > 1) inputSheet.addMergedRegion(new CellRangeAddress( rowIndex, rowIndex + rowLength - 1, 4, 4));

                cell = rows[0].createCell((short) 5);
                cell.setCellStyle(centerStyle);
                cell.setCellValue(record.getEye());
                if(rowLength > 1) inputSheet.addMergedRegion(new CellRangeAddress( rowIndex, rowIndex + rowLength - 1, 5, 5));

                cell = rows[0].createCell((short) 6);
                cell.setCellStyle(centerStyle);
                cell.setCellValue(record.getPlace());
                if(rowLength > 1) inputSheet.addMergedRegion(new CellRangeAddress( rowIndex, rowIndex + rowLength - 1, 6, 6));

                cell = rows[0].createCell((short) 7);
                cell.setCellStyle(centerStyle);
                cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(record.getDate()));
                if(rowLength > 1) inputSheet.addMergedRegion(new CellRangeAddress( rowIndex, rowIndex + rowLength - 1, 7, 7));


                //手术信息3列
                for(int ri = rowIndex; ri < rowIndex + surgeries.length; ri++) {

                    //拆分名称/级别/价格
                    String[] surgery = surgeries[ri - rowIndex].split("/");

                    //创建列并填写
                    for(int ci = 8; ci <= 10; ci++) {

                        cell = rows[ri - rowIndex].createCell((short) ci);
                        cell.setCellStyle(centerStyle);
                        cell.setCellValue(surgery[ci - 8]);
                    }
                }

                //术者信息3列
                for(int ri = rowIndex; ri < rowIndex + surgeons.length; ri++) {

                    //拆分名称/级别/价格
                    String[] surgeon = surgeons[ri - rowIndex].split("/");

                    //创建列并填写
                    for(int ci = 11; ci <= 14; ci++) {

                        cell = rows[ri - rowIndex].createCell((short) ci);
                        cell.setCellStyle(centerStyle);
                        cell.setCellValue(surgeon[ci - 11]);
                    }
                }

                //助手信息3列
                for(int ri = rowIndex; ri < rowIndex + helpers.length; ri++) {

                    //拆分名称/级别/价格
                    String[] helper = helpers[ri - rowIndex].split("/");

                    //创建列并填写
                    for(int ci = 15; ci <= 18; ci++) {

                        cell = rows[ri - rowIndex].createCell((short) ci);
                        cell.setCellStyle(centerStyle);
                        cell.setCellValue(helper[ci - 15]);
                    }
                }

                rowIndex += rowLength;
            }
        }

        String userId = identity.getId();
        String fileName = Constant.FILE_PATH + "record/手术记录_" + userId + ".xlsx";

        try {
            FileOutputStream out = new FileOutputStream(new File(fileName));
            // OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.close();

            return Constant.CRUD_SUCCESS;
        } catch (IOException e) {

            e.printStackTrace();
            return Constant.CRUD_FAILURE;
        }
    }


//    /**
//     * 导出特定医师的详情表格
//     * @param detailList
//     * @param params
//     * @param identity
//     * @return
//     */
//    public Integer exportDoctorDetail(List<RecordExtend> detailList, Map<String, Object> params, Identity identity) {
//
//        XSSFWorkbook workbook = new XSSFWorkbook();
//        XSSFSheet inputSheet = workbook.createSheet("医师手术记录");
//        inputSheet.setDefaultColumnWidth(13);
//        inputSheet.setDefaultRowHeight((short) (1.6 * 256));
//
//        // 第一行，6个单元格合并，检查亚类
//        {
//            XSSFRow firstRow = inputSheet.createRow((short) 0);
//            XSSFCell firstRowCell = firstRow.createCell((short) 0);
//            firstRowCell.setCellValue("医师手术记录");
//
//            XSSFFont firstFont = workbook.createFont();
//            firstFont.setColor(XSSFFont.COLOR_RED); // 红色
//            firstFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD); // 加粗
//            firstFont.setFontHeightInPoints((short) 14);
//
//            XSSFCellStyle firstStyle = workbook.createCellStyle();
//            firstStyle.setFont(firstFont);
//            firstStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
//
//            firstRowCell.setCellStyle(firstStyle);
//
//            inputSheet.addMergedRegion(new CellRangeAddress(
//                    0, //first firstRow (0-based)
//                    0, //last firstRow (0-based)
//                    0, //first column (0-based)
//                    8 //last column (0-based)
//            ));
//        }
//
//        // 第二行表头： 累计积分
//        {
//            XSSFRow firstRow = inputSheet.createRow((short) 1);
//            XSSFFont boldFont = workbook.createFont();
//            boldFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD); // 加粗
//            XSSFCellStyle boldStyle = workbook.createCellStyle();
//            boldStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
//            boldStyle.setFont(boldFont);
//
//            XSSFCell cell = firstRow.createCell((short) 7);
//            cell.setCellStyle(boldStyle);
//            boldFont.setColor(XSSFFont.COLOR_RED); // 红色
//            Double totalScore = this.queryTotalScore(params);
//            cell.setCellValue("累计工作量：" + (totalScore == null ? 0 : totalScore)  + " 分");
//
//            inputSheet.addMergedRegion(new CellRangeAddress(
//                    1, //first firstRow (0-based)
//                    1, //last firstRow (0-based)
//                    7, //first column (0-based)
//                    8 //last column (0-based)
//            ));
//        }
//
//        // 第三行：表头
//        {
//            XSSFRow secondRow = inputSheet.createRow((short) 2);
//
//            XSSFFont boldFont = workbook.createFont();
//            boldFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD); // 加粗
//            XSSFCellStyle boldStyle = workbook.createCellStyle();
//            boldStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
//            boldStyle.setFont(boldFont);
//
//
//            XSSFCell cell = secondRow.createCell((short) 0);
//            cell.setCellStyle(boldStyle);
//            cell.setCellValue("病历号");
//
//            cell = secondRow.createCell((short) 1);
//            cell.setCellStyle(boldStyle);
//            cell.setCellValue("类型");
//
//            cell = secondRow.createCell((short) 2);
//            cell.setCellStyle(boldStyle);
//            cell.setCellValue("姓名");
//
//            cell = secondRow.createCell((short) 3);
//            cell.setCellStyle(boldStyle);
//            cell.setCellValue("性别");
//
//            cell = secondRow.createCell((short) 4);
//            cell.setCellStyle(boldStyle);
//            cell.setCellValue("年龄");
//
//            cell = secondRow.createCell((short) 5);
//            cell.setCellStyle(boldStyle);
//            cell.setCellValue("眼别");
//
//            cell = secondRow.createCell((short) 6);
//            cell.setCellStyle(boldStyle);
//            cell.setCellValue("地点");
//
//            cell = secondRow.createCell((short) 7);
//            cell.setCellStyle(boldStyle);
//            cell.setCellValue("日期");
//
//            cell = secondRow.createCell((short) 8);
//            cell.setCellStyle(boldStyle);
//            cell.setCellValue("本场工作量");
//        }
//
//        //第四行：数据
//        {
//            XSSFCellStyle centerStyle = workbook.createCellStyle();
//            centerStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
//
//            int rowIndex = 3;
//            for (RecordExtend detail : detailList) {
//                XSSFRow row = inputSheet.createRow((short) rowIndex);
//
//                XSSFCell cell = row.createCell((short) 0);
//                cell.setCellStyle(centerStyle);
//                cell.setCellValue(detail.getHistoryNum());
//
//                cell = row.createCell((short) 1);
//                cell.setCellStyle(centerStyle);
//                cell.setCellValue(detail.getType());
//
//                cell = row.createCell((short) 2);
//                cell.setCellStyle(centerStyle);
//                cell.setCellValue(detail.getName());
//
//                cell = row.createCell((short) 3);
//                cell.setCellStyle(centerStyle);
//                cell.setCellValue(detail.getSex());
//
//                cell = row.createCell((short) 4);
//                cell.setCellStyle(centerStyle);
//                cell.setCellValue(detail.getAge());
//
//                cell = row.createCell((short) 5);
//                cell.setCellStyle(centerStyle);
//                cell.setCellValue(detail.getEye());
//
//                cell = row.createCell((short) 6);
//                cell.setCellStyle(centerStyle);
//                cell.setCellValue(detail.getPlace());
//
//                cell = row.createCell((short) 7);
//                cell.setCellStyle(centerStyle);
//                cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(detail.getDate()));
//
//                cell = row.createCell((short) 8);
//                cell.setCellStyle(centerStyle);
//                cell.setCellValue(detail.getDoctorScore() + " 分");
//
//                rowIndex++;
//            }
//        }
//
//        String userId = identity.getId();
//        String fileName = Constant.FILE_PATH + "doctor_detail/医师手术记录_" + userId + ".xlsx";
//
//        try {
//            FileOutputStream out = new FileOutputStream(new File(fileName));
//            workbook.write(out);
//            out.close();
//
//            return Constant.CRUD_SUCCESS;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return Constant.CRUD_FAILURE;
//        }
//    }

}
