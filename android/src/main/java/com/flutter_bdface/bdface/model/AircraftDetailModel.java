package com.flutter_bdface.bdface.model;

/**
 * 无人机详情
 * Created by 13096 on 2018/10/17.
 */

public class AircraftDetailModel {


    /**
     * planeId : 574
     * boxSn :
     * planeType : FIXED_WING
     * planeBrand : 迈凯飞
     * planeVersion : UV20
     * planeWeight : 18
     * ceilingFlightHeight : 2000
     * ceilingFlightTime : 55
     * planeImageUrl : https://airspace-test.oss-cn-beijing.aliyuncs.com/200914566815813632/7b8295eb44ff39ee3a926933389138f8.png
     * star : 0
     */

    private long planeId;
    /**
     * 云匣子串号
     */
    private String boxSn;
    /**
     * 无人机类型
     */
    private String planeType;
    /**
     * 无人机品牌
     */
    private String planeBrand;
    /**
     * 无人机型号
     */
    private String planeVersion;
    /**
     * 无人机空重
     */
    private float planeWeight;
    /**
     * 最大飞行高度
     */
    private int ceilingFlightHeight;
    /**
     * 最大续航时间
     */
    private int ceilingFlightTime;
    /**
     * 飞机图片地址
     */
    private String planeImageUrl;
    /**
     * 云匣子*数
     */
    private Integer star;

    public long getPlaneId() {
        return planeId;
    }

    public void setPlaneId(long planeId) {
        this.planeId = planeId;
    }

    public String getBoxSn() {
        return boxSn;
    }

    public void setBoxSn(String boxSn) {
        this.boxSn = boxSn;
    }

    public String getPlaneType() {
        return planeType;
    }

    public void setPlaneType(String planeType) {
        this.planeType = planeType;
    }

    public String getPlaneBrand() {
        return planeBrand;
    }

    public void setPlaneBrand(String planeBrand) {
        this.planeBrand = planeBrand;
    }

    public String getPlaneVersion() {
        return planeVersion;
    }

    public void setPlaneVersion(String planeVersion) {
        this.planeVersion = planeVersion;
    }

    public float getPlaneWeight() {
        return planeWeight;
    }

    public void setPlaneWeight(float planeWeight) {
        this.planeWeight = planeWeight;
    }

    public int getCeilingFlightHeight() {
        return ceilingFlightHeight;
    }

    public void setCeilingFlightHeight(int ceilingFlightHeight) {
        this.ceilingFlightHeight = ceilingFlightHeight;
    }

    public int getCeilingFlightTime() {
        return ceilingFlightTime;
    }

    public void setCeilingFlightTime(int ceilingFlightTime) {
        this.ceilingFlightTime = ceilingFlightTime;
    }

    public String getPlaneImageUrl() {
        return planeImageUrl;
    }

    public void setPlaneImageUrl(String planeImageUrl) {
        this.planeImageUrl = planeImageUrl;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }
}
