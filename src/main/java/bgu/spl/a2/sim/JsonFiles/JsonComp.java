package bgu.spl.a2.sim.JsonFiles;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JsonComp {

    @SerializedName("Type")
    @Expose
    private String compType;

    @SerializedName("Sig Success")
    @Expose
    private long successSig;

    @SerializedName("Sig Fail")
    @Expose
    private long failSig;


    //setters and getters
    public String getCompType() {
        return compType;
    }

    public void setCompType(String compType) {
        this.compType = compType;
    }

    public long getSuccessSig() {
        return successSig;
    }

    public void setSuccessSig(long successSig) {
        this.successSig = successSig;
    }

    public long getFailSig() {
        return failSig;
    }

    public void setFailSig(long failSig) {
        this.failSig = failSig;
    }
}
