package com.tongren.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 配置文件service
 * Created by zlren on 2017/2/28.
 */
@Service
public class PropertyService {

    @Value("${page.rows}")
    public int pageRows;

    @Value("${sms.code.len}")
    public int smsCodeLen;

    @Value("${sms.code.expire}")
    public int smsCodeExpire;

    @Value("${token.issuer}")
    public String issuer;

    @Value("${token.duration}")
    public long tokenDuration;

    @Value("${token.apiKeySecret}")
    public String apiKeySecret;

    @Value("${default.password}")
    public String defaultPassword;

    @Value("${file.path}")
    public String filePath;
}