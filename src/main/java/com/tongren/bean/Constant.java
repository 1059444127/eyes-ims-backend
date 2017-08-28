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

    public static String ROLE = "role";

    public static Integer CRUD_SUCCESS = 1;
    public static Integer CRUD_FAILURE = 0;
    public static String SUCCESS = "SUCCESS";
    public static String FAILURE = "FAILURE";
    public static String IDENTITY = "IDENTITY";
    public static String TOKEN = "TOKEN";


    public static String ADMIN = "系统管理员";
    public static String INPUTER = "数据录入员";
    public static String DOCTOR = "医师";


    public static String NAME = "name";

    public static String PAGE_NOW = "pageNow";
    public static String PAGE_SIZE = "pageSize";


    //doctor表
    public static String SALARY_NUM = "salaryNum";
    public static String LEVEL = "level";



    public static String DOCTOR_HEAD = "主任医师";
    public static String DOCTOR_VICEHEAD = "副主任医师";
    public static String DOCTOR_TREAT = "主治医师";
    public static String DOCTOR_RESIDENT = "住院医师";
    public static String DOCTOR_TRAIN = "规培医师";
    public static String DOCTOR_MASTER = "研究生";

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
