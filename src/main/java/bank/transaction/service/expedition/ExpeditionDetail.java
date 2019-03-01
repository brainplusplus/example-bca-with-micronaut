package bank.transaction.service.expedition;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.inject.Singleton;

@Singleton
public class ExpeditionDetail {

    public ExpeditionDetail(){

    }

    public ExpeditionDetail(String location, String remark, String status, String time, Expedition expedition){
        this.expedition = expedition;
        this.location = location;
        this.remark = remark;
        this.status = status;
        this.time = time;
    }

    @JsonProperty("location")
    private String location;

    @JsonProperty("remark")
    private String remark;

    @JsonProperty("status")
    private String status;

    @JsonProperty("time")
    private String time;

    private Expedition expedition;

    public void setLocation(String location) { this.location = location; }
    public String getLocation() { return location; }

    public void setRemark(String remark) { this.remark = remark; }
    public String getRemark() { return remark; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    public void setTime(String time) { this.time = time; }
    public String getTime() { return time; }

    public void setExpedition(Expedition expedition) { this.expedition = expedition; }
    public Expedition getExpedition() { return expedition; }

    @Override
    public String toString() {
        return "ExpeditionDetail{" +
                "location='" + location + '\'' +
                ", remark='" + remark + '\'' +
                ", status='" + status +'\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
