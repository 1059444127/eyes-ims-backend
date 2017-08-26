package com.tongren.pojo;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

public class RecordExtend {
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

	private String place;

	private String surgeries;

	private String surgeons;

	private String helpers;

	private Date date;

	@Column(name = "inputer_id")
	private Integer inputerId;

	private String inputerName;
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


	public String getSurgeries() {
		return surgeries;
	}

	public void setSurgeries(String surgeries) {
		this.surgeries = surgeries;
	}

	public String getSurgeons() {
		return surgeons;
	}

	public void setSurgeons(String surgeons) {
		this.surgeons = surgeons;
	}

	public String getHelpers() {
		return helpers;
	}

	public void setHelpers(String helpers) {
		this.helpers = helpers;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public Integer getInputerId() {
		return inputerId;
	}

	public void setInputerId(Integer inputerId) {
		this.inputerId = inputerId;
	}

	public String getInputerName() {
		return inputerName;
	}

	public void setInputerName(String inputerName) {
		this.inputerName = inputerName;
	}
}