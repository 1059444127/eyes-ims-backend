package com.tongren.pojo;

import javax.persistence.*;
import java.util.Date;

@Table(name = "record_doctor")
public class RecordDoctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "record_id")
    private Integer recordId;

    @Column(name = "doctor_id")
    private Integer doctorId;

    /**
     * 术者/助手
     */
    @Column(name = "doctor_type")
    private String doctorType;

    @Column(name = "doctor_salary_num")
    private String doctorSalaryNum;

    @Column(name = "doctor_name")
    private String doctorName;

    @Column(name = "doctor_level")
    private String doctorLevel;

    @Column(name = "doctor_score")
    private Double doctorScore;

    private Date date;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return record_id
     */
    public Integer getRecordId() {
        return recordId;
    }

    /**
     * @param recordId
     */
    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    /**
     * @return doctor_id
     */
    public Integer getDoctorId() {
        return doctorId;
    }

    /**
     * @param doctorId
     */
    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    /**
     * 获取术者/助手
     *
     * @return doctor_type - 术者/助手
     */
    public String getDoctorType() {
        return doctorType;
    }

    /**
     * 设置术者/助手
     *
     * @param doctorType 术者/助手
     */
    public void setDoctorType(String doctorType) {
        this.doctorType = doctorType;
    }

    /**
     * @return doctor_salary_num
     */
    public String getDoctorSalaryNum() {
        return doctorSalaryNum;
    }

    /**
     * @param doctorSalaryNum
     */
    public void setDoctorSalaryNum(String doctorSalaryNum) {
        this.doctorSalaryNum = doctorSalaryNum;
    }

    /**
     * @return doctor_name
     */
    public String getDoctorName() {
        return doctorName;
    }

    /**
     * @param doctorName
     */
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    /**
     * @return doctor_level
     */
    public String getDoctorLevel() {
        return doctorLevel;
    }

    /**
     * @param doctorLevel
     */
    public void setDoctorLevel(String doctorLevel) {
        this.doctorLevel = doctorLevel;
    }

    /**
     * @return doctor_score
     */
    public Double getDoctorScore() {
        return doctorScore;
    }

    /**
     * @param doctorScore
     */
    public void setDoctorScore(Double doctorScore) {
        this.doctorScore = doctorScore;
    }

    /**
     * @return date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date
     */
    public void setDate(Date date) {
        this.date = date;
    }
}