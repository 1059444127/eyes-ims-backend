package com.tongren.pojo;

import javax.persistence.*;

@Table(name = "surgery_record")
public class SurgeryRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "history_num")
    private String historyNum;

    private String type;

    private String name;

    private String sex;

    private Integer age;

    private String eye;

    @Column(name = "surgery1_id")
    private Integer surgery1Id;

    @Column(name = "surgery2_id")
    private Integer surgery2Id;

    @Column(name = "surgery3_id")
    private Integer surgery3Id;

    @Column(name = "surgery4_id")
    private Integer surgery4Id;

    @Column(name = "surgery5_id")
    private Integer surgery5Id;

    @Column(name = "surgery6_id")
    private Integer surgery6Id;

    @Column(name = "surgeon1_id")
    private Integer surgeon1Id;

    @Column(name = "surgeon1_score")
    private Integer surgeon1Score;

    @Column(name = "surgeon2_id")
    private Integer surgeon2Id;

    @Column(name = "surgeon2_score")
    private Integer surgeon2Score;

    @Column(name = "helper1_id")
    private Integer helper1Id;

    @Column(name = "helper1_score")
    private Integer helper1Score;

    @Column(name = "helper2_id")
    private Integer helper2Id;

    @Column(name = "helper2_score")
    private Integer helper2Score;

    @Column(name = "helper3_id")
    private Integer helper3Id;

    @Column(name = "helper3_score")
    private Integer helper3Score;

    @Column(name = "helper4_id")
    private Integer helper4Id;

    @Column(name = "helper4_score")
    private Integer helper4Score;

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
     * @return history_num
     */
    public String getHistoryNum() {
        return historyNum;
    }

    /**
     * @param historyNum
     */
    public void setHistoryNum(String historyNum) {
        this.historyNum = historyNum;
    }

    /**
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return sex
     */
    public String getSex() {
        return sex;
    }

    /**
     * @param sex
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * @return age
     */
    public Integer getAge() {
        return age;
    }

    /**
     * @param age
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * @return eye
     */
    public String getEye() {
        return eye;
    }

    /**
     * @param eye
     */
    public void setEye(String eye) {
        this.eye = eye;
    }

    /**
     * @return surgery1_id
     */
    public Integer getSurgery1Id() {
        return surgery1Id;
    }

    /**
     * @param surgery1Id
     */
    public void setSurgery1Id(Integer surgery1Id) {
        this.surgery1Id = surgery1Id;
    }

    /**
     * @return surgery2_id
     */
    public Integer getSurgery2Id() {
        return surgery2Id;
    }

    /**
     * @param surgery2Id
     */
    public void setSurgery2Id(Integer surgery2Id) {
        this.surgery2Id = surgery2Id;
    }

    /**
     * @return surgery3_id
     */
    public Integer getSurgery3Id() {
        return surgery3Id;
    }

    /**
     * @param surgery3Id
     */
    public void setSurgery3Id(Integer surgery3Id) {
        this.surgery3Id = surgery3Id;
    }

    /**
     * @return surgery4_id
     */
    public Integer getSurgery4Id() {
        return surgery4Id;
    }

    /**
     * @param surgery4Id
     */
    public void setSurgery4Id(Integer surgery4Id) {
        this.surgery4Id = surgery4Id;
    }

    /**
     * @return surgery5_id
     */
    public Integer getSurgery5Id() {
        return surgery5Id;
    }

    /**
     * @param surgery5Id
     */
    public void setSurgery5Id(Integer surgery5Id) {
        this.surgery5Id = surgery5Id;
    }

    /**
     * @return surgery6_id
     */
    public Integer getSurgery6Id() {
        return surgery6Id;
    }

    /**
     * @param surgery6Id
     */
    public void setSurgery6Id(Integer surgery6Id) {
        this.surgery6Id = surgery6Id;
    }

    /**
     * @return surgeon1_id
     */
    public Integer getSurgeon1Id() {
        return surgeon1Id;
    }

    /**
     * @param surgeon1Id
     */
    public void setSurgeon1Id(Integer surgeon1Id) {
        this.surgeon1Id = surgeon1Id;
    }

    /**
     * @return surgeon1_score
     */
    public Integer getSurgeon1Score() {
        return surgeon1Score;
    }

    /**
     * @param surgeon1Score
     */
    public void setSurgeon1Score(Integer surgeon1Score) {
        this.surgeon1Score = surgeon1Score;
    }

    /**
     * @return surgeon2_id
     */
    public Integer getSurgeon2Id() {
        return surgeon2Id;
    }

    /**
     * @param surgeon2Id
     */
    public void setSurgeon2Id(Integer surgeon2Id) {
        this.surgeon2Id = surgeon2Id;
    }

    /**
     * @return surgeon2_score
     */
    public Integer getSurgeon2Score() {
        return surgeon2Score;
    }

    /**
     * @param surgeon2Score
     */
    public void setSurgeon2Score(Integer surgeon2Score) {
        this.surgeon2Score = surgeon2Score;
    }

    /**
     * @return helper1_id
     */
    public Integer getHelper1Id() {
        return helper1Id;
    }

    /**
     * @param helper1Id
     */
    public void setHelper1Id(Integer helper1Id) {
        this.helper1Id = helper1Id;
    }

    /**
     * @return helper1_score
     */
    public Integer getHelper1Score() {
        return helper1Score;
    }

    /**
     * @param helper1Score
     */
    public void setHelper1Score(Integer helper1Score) {
        this.helper1Score = helper1Score;
    }

    /**
     * @return helper2_id
     */
    public Integer getHelper2Id() {
        return helper2Id;
    }

    /**
     * @param helper2Id
     */
    public void setHelper2Id(Integer helper2Id) {
        this.helper2Id = helper2Id;
    }

    /**
     * @return helper2_score
     */
    public Integer getHelper2Score() {
        return helper2Score;
    }

    /**
     * @param helper2Score
     */
    public void setHelper2Score(Integer helper2Score) {
        this.helper2Score = helper2Score;
    }

    /**
     * @return helper3_id
     */
    public Integer getHelper3Id() {
        return helper3Id;
    }

    /**
     * @param helper3Id
     */
    public void setHelper3Id(Integer helper3Id) {
        this.helper3Id = helper3Id;
    }

    /**
     * @return helper3_score
     */
    public Integer getHelper3Score() {
        return helper3Score;
    }

    /**
     * @param helper3Score
     */
    public void setHelper3Score(Integer helper3Score) {
        this.helper3Score = helper3Score;
    }

    /**
     * @return helper4_id
     */
    public Integer getHelper4Id() {
        return helper4Id;
    }

    /**
     * @param helper4Id
     */
    public void setHelper4Id(Integer helper4Id) {
        this.helper4Id = helper4Id;
    }

    /**
     * @return helper4_score
     */
    public Integer getHelper4Score() {
        return helper4Score;
    }

    /**
     * @param helper4Score
     */
    public void setHelper4Score(Integer helper4Score) {
        this.helper4Score = helper4Score;
    }
}