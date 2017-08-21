package com.tongren.bean;

/**
 * 常量
 * Created by zlren on 2017/6/6.
 */
public class Constant {

    public static String DEFAULT_PASSWORD = "123456";
    public static String PASSWORD = "password";
    public static String USERNAME = "username";
    public static String INPUT_CODE = "inputCode";

    public static String ID = "id";
    public static String ROLE = "role";

    public static String REDIS_PRE_CODE = "REDIS_PRE_CODE_";

    public static Integer CRUD_SUCCESS = 1;
    public static Integer CRUD_FAILURE = 0;
    public static String SUCCESS = "SUCCESS";
    public static String FAILURE = "FAILURE";
    public static String FAILURE_LOGIN = "FAILURE_LOGIN";
    public static String FAILURE_AUTH = "FAILURE_AUTH";

    public static String IDENTITY = "IDENTITY";
    public static String TOKEN = "TOKEN";


    public static String ADMIN = "系统管理员";
    public static String INPUTER = "数据录入员";
    public static String DOCTOR = "医师";


    public static String FINANCER = "财务部员工";
    public static String ARCHIVER = "档案部员工";
    public static String ARCHIVE_MANAGER = "档案部主管";
    public static String ADVISER = "顾问部员工";
    public static String ADVISE_MANAGER = "顾问部主管";

    public static String USER_1 = "一级会员";
    public static String USER_2 = "二级会员";
    public static String USER_3 = "三级会员";

    public static String HUAYAN = "化验";
    public static String YIJI = "医技";

    public static String TYPE = "type";
    public static String NAME = "name";

    public static String PAGE_NOW = "pageNow";
    public static String PAGE_SIZE = "pageSize";

    public static String MEMBER = "member";
    public static String EMPLOYEE = "employee";

    public static String FIRST_ID = "firstId";
    public static String SECOND_ID = "secondId";
    public static String STAFF_ID = "staffId";
    public static String STAFF_MGR_ID = "staffMgrId";
    public static String SYSTEM_CATEGORY = "systemCategory";
    public static String REFERENCE_VALUE = "referenceValue";
    public static String HOSPITAL = "hospital";


    public static String USER_ID = "userId";
    public static String UPLOADER_ID = "uploaderId";

    public static String IS_PASS = "isPass";
    public static String IS_INPUT = "isInput";

    public static String MANAGER = "manager";

    public static String STATUS = "status";
    public static String REASON = "reason";

    public static String PATH = "path";
    public static String TIME = "time";

    public static String NOTE = "note";
    public static String SHANG_CHUAN_ZHONG = "上传中";
    public static String DAI_SHEN_HE = "待审核";
    public static String WEI_TONG_GUO = "未通过";
    public static String YI_TONG_GUO = "已通过";
    public static String LU_RU_ZHONG = "录入中";



    //doctor表
    public static String SALARY_NUM = "salaryNum";
    public static String LEVEL = "level";

    public static String SURGERY_FIRST = "一级";
    public static String SURGERY_SECOND = "二级";
    public static String SURGERY_THIRD = "三级";
    public static String SURGERY_FORTH = "四级";
    public static String SURGERY_FIFTH = "五级";

    public static String DOCTOR_HEAD = "主任医师";
    public static String DOCTOR_VICEHEAD = "副主任医师";
    public static String DOCTOR_TREAT = "主治医师";
    public static String DOCTOR_RESIDENT = "住院医师";


    //配置文件
    public static String LEVEL_PROPERTIES_FILE_PATH = "level.properties";

    //系统环境配置
    public static Integer SMS_CODE_EXPIRE = 60;
    public static Integer SMS_CODE_LEN = 4;

    //Token
    public static String TOKEN_ISSUER = "yhch";
    public static Long TOKEN_DURATION = 6000000L;
    public static String TOKEN_API_KEY_SECRET = "yaochenkun_510108199311080018";

    //文件服务器
    public static String FILE_PATH = "C:\\eyes_ims\\resources\\";
    //public static String FILE_PATH = "/Users/ken/Documents/";
}
