package bgu.spl.a2.sim.JsonFiles;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JsonObjs {


    @SerializedName("threads")
    @Expose
    private Integer  threads;

    @SerializedName("Computers")
    @Expose
    private List<JsonComp> computers;

    @SerializedName("Phase 1")
    @Expose
    private List<JsonActs> phase1;

    @SerializedName("Phase 2")
    @Expose
    private List<JsonActs> phase2;

    @SerializedName("Phase 3")
    @Expose
    private List<JsonActs> phase3;


    //setters and getters
    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public List<JsonComp> getComputers() {
        return computers;
    }

    public void setComputers(List<JsonComp> computers) {
        this.computers = computers;
    }

    public List<JsonActs> getPhase1() {
        return phase1;
    }

    public void setPhase1(List<JsonActs> phase1) {
        this.phase1 = phase1;
    }

    public List<JsonActs> getPhase2() {
        return phase2;
    }

    public void setPhase2(List<JsonActs> phase2) {
        this.phase2 = phase2;
    }

    public List<JsonActs> getPhase3() {
        return phase3;
    }

    public void setPhase3(List<JsonActs> phase3) {
        this.phase3 = phase3;
    }
}
