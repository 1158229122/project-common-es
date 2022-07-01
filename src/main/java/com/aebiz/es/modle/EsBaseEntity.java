package com.aebiz.es.modle;



/**
 * @author jim
 * @date 2022/6/28 16:51
 */
public abstract class EsBaseEntity {

    private String uuid;
    private String oper;
    private String opeTime;
    private int delFlag = 1;
    private String createOper;
    private String createOpeTime;
    private int version;
    private String operationNumber;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    public String getOpeTime() {
        return opeTime;
    }

    public void setOpeTime(String opeTime) {
        this.opeTime = opeTime;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }

    public String getCreateOper() {
        return createOper;
    }

    public void setCreateOper(String createOper) {
        this.createOper = createOper;
    }

    public String getCreateOpeTime() {
        return createOpeTime;
    }

    public void setCreateOpeTime(String createOpeTime) {
        this.createOpeTime = createOpeTime;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getOperationNumber() {
        return operationNumber;
    }

    public void setOperationNumber(String operationNumber) {
        this.operationNumber = operationNumber;
    }
}
