package com.tongren.pojo;

import javax.persistence.*;

@Table(name = "record_surgery")
public class RecordSurgery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "record_id")
    private Integer recordId;

    @Column(name = "surgery_id")
    private Integer surgeryId;

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
     * @return surgery_id
     */
    public Integer getSurgeryId() {
        return surgeryId;
    }

    /**
     * @param surgeryId
     */
    public void setSurgeryId(Integer surgeryId) {
        this.surgeryId = surgeryId;
    }
}