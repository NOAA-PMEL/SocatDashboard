package gov.noaa.pmel.socatmetadata.instrument;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Describes an equilibrator for measuring the amount of a dissolved gas in water.
 */
public class Equilibrator extends Sampler implements Cloneable, Serializable {

    private static final long serialVersionUID = 2784316631869459343L;

    protected String equilibratorType;
    protected String chamberVol;
    protected String chamberWaterVol;
    protected String chamberGasVol;
    protected String waterFlowRate;
    protected String gasFlowRate;
    protected String venting;

    /**
     * Create with all fields empty.
     */
    public Equilibrator() {
        equilibratorType = "";
        chamberVol = "";
        chamberWaterVol = "";
        chamberGasVol = "";
        waterFlowRate = "";
        gasFlowRate = "";
        venting = "";
    }

    @Override
    public HashSet<String> invalidFieldNames() {
        HashSet<String> invalids = super.invalidFieldNames();
        if ( equilibratorType.isEmpty() )
            invalids.add("equilibratorType");
        if ( chamberWaterVol.isEmpty() )
            invalids.add("chamberWaterVol");
        if ( chamberGasVol.isEmpty() )
            invalids.add("chamberGasVol");
        if ( waterFlowRate.isEmpty() )
            invalids.add("waterFlowRate");
        if ( gasFlowRate.isEmpty() )
            invalids.add("gasFlowRate");
        if ( venting.isEmpty() )
            invalids.add("venting");
        return invalids;
    }

    /**
     * @return the equilibrator type; never null but may be empty
     */
    public String getEquilibratorType() {
        return equilibratorType;
    }

    /**
     * @param equilibratorType
     *         assign as the equilibrator type; if null or blank, an empty string is assigned
     */
    public void setEquilibratorType(String equilibratorType) {
        this.equilibratorType = (equilibratorType != null) ? equilibratorType.trim() : "";
    }

    /**
     * @return the total volume of the equilibrator chamber; never null but may be empty
     */
    public String getChamberVol() {
        return chamberVol;
    }

    /**
     * @param chamberVol
     *         assign as the totla volume of the equilibrator chamber; if null or blank, an empty string is assigned
     */
    public void setChamberVol(String chamberVol) {
        this.chamberVol = (chamberVol != null) ? chamberVol.trim() : "";
    }

    /**
     * @return the water volume in the equilibrator chamber; never null but may be empty
     */
    public String getChamberWaterVol() {
        return chamberWaterVol;
    }

    /**
     * @param chamberWaterVol
     *         assign as the water volume in the equilibrator chamber; if null or blank, an empty string is assigned
     */
    public void setChamberWaterVol(String chamberWaterVol) {
        this.chamberWaterVol = (chamberWaterVol != null) ? chamberWaterVol.trim() : "";
    }

    /**
     * @return the gas volume in the equilibrator chamber; never null but may be empty
     */
    public String getChamberGasVol() {
        return chamberGasVol;
    }

    /**
     * @param chamberGasVol
     *         assign as the gas volume in the equilibrator chamber; if null or blank, an empty string is assigned
     */
    public void setChamberGasVol(String chamberGasVol) {
        this.chamberGasVol = (chamberGasVol != null) ? chamberGasVol.trim() : "";
    }

    /**
     * @return the water flow rate through the equilibrator; never null but may be empty
     */
    public String getWaterFlowRate() {
        return waterFlowRate;
    }

    /**
     * @param waterFlowRate
     *         assign as the water flow rate through the equilibrator; if null or blank, an empty string is assigned
     */
    public void setWaterFlowRate(String waterFlowRate) {
        this.waterFlowRate = (waterFlowRate != null) ? waterFlowRate.trim() : "";
    }

    /**
     * @return the gas flow rate through the equilibrator; never null but may be empty
     */
    public String getGasFlowRate() {
        return gasFlowRate;
    }

    /**
     * @param gasFlowRate
     *         assign as the gas flow rate through the equilibrator; if null or blank, an empty string is assigned
     */
    public void setGasFlowRate(String gasFlowRate) {
        this.gasFlowRate = (gasFlowRate != null) ? gasFlowRate.trim() : "";
    }

    /**
     * @return information about venting of the equilibrator chamber; never null but may be empty
     */
    public String getVenting() {
        return venting;
    }

    /**
     * @param venting
     *         assign as information about venting the equilibrator chamber;
     *         if null or blank, an empty string is assigned
     */
    public void setVenting(String venting) {
        this.venting = (venting != null) ? venting.trim() : "";
    }

    @Override
    public Equilibrator clone() {
        Equilibrator dup = (Equilibrator) super.clone();
        dup.equilibratorType = equilibratorType;
        dup.chamberVol = chamberVol;
        dup.chamberWaterVol = chamberWaterVol;
        dup.chamberGasVol = chamberGasVol;
        dup.waterFlowRate = waterFlowRate;
        dup.gasFlowRate = gasFlowRate;
        dup.venting = venting;
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof Equilibrator) )
            return false;
        if ( !super.equals(obj) )
            return false;

        Equilibrator other = (Equilibrator) obj;

        if ( !equilibratorType.equals(other.equilibratorType) )
            return false;
        if ( !chamberVol.equals(other.chamberVol) )
            return false;
        if ( !chamberWaterVol.equals(other.chamberWaterVol) )
            return false;
        if ( !chamberGasVol.equals(other.chamberGasVol) )
            return false;
        if ( !waterFlowRate.equals(other.waterFlowRate) )
            return false;
        if ( !gasFlowRate.equals(other.gasFlowRate) )
            return false;
        if ( !venting.equals(other.venting) )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = result * prime + equilibratorType.hashCode();
        result = result * prime + chamberVol.hashCode();
        result = result * prime + chamberWaterVol.hashCode();
        result = result * prime + chamberGasVol.hashCode();
        result = result * prime + waterFlowRate.hashCode();
        result = result * prime + gasFlowRate.hashCode();
        result = result * prime + venting.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String repr = super.toString().replaceFirst("Sampler", "Equilibrator");
        return repr.substring(0, repr.length() - 1) +
                ", equilibratorType='" + equilibratorType + '\'' +
                ", chamberVol='" + chamberVol + '\'' +
                ", chamberWaterVol='" + chamberWaterVol + '\'' +
                ", chamberGasVol='" + chamberGasVol + '\'' +
                ", waterFlowRate='" + waterFlowRate + '\'' +
                ", gasFlowRate='" + gasFlowRate + '\'' +
                ", venting='" + venting + '\'' +
                '}';
    }

}
