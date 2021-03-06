/**
 *
 */
package gov.noaa.pmel.dashboard.datatype;

import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 * SOCAT standard types required in various classes.
 *
 * @author Karl Smith
 */
public class SocatTypes {

    public static final String CO2_CATEGORY = "CO2";
    public static final String PRESSURE_CATEGORY = "Pressure";
    public static final String SALINITY_CATEGORY = "Salinity";
    public static final String TEMPERATURE_CATEGORY = "Temperature";

    public static final List<String> SALINITY_UNITS = Collections.singletonList("PSU");
    public static final List<String> TEMPERATURE_UNITS = Collections.singletonList("deg C");
    public static final List<String> PRESSURE_UNITS = Arrays.asList("hPa", "kPa", "mmHg");
    public static final List<String> XCO2_UNITS = Collections.singletonList("umol/mol");
    public static final List<String> PCO2_UNITS = Collections.singletonList("uatm");
    public static final List<String> FCO2_UNITS = Collections.singletonList("uatm");
    public static final List<String> DISTANCE_UNITS = Collections.singletonList("km");
    public static final List<String> SPEED_UNITS = Arrays.asList("knots", "km/h", "m/s", "mph");

    // Additional data provided by the user
    public static final DoubleDashDataType SALINITY = new DoubleDashDataType("sal",
            600.0, "salinity", "salinity", false,
            SALINITY_UNITS, "sea_surface_salinity", SALINITY_CATEGORY, null,
            "-0.1", "0.0", "42.0", "50.0", DashboardServerUtils.USER_FILE_DATA_ROLES);

    public static final DoubleDashDataType TEQU = new DoubleDashDataType("Temperature_equi",
            610.0, "T_equ", "equilibrator chamber temperature", false,
            TEMPERATURE_UNITS, null, TEMPERATURE_CATEGORY, "degrees C",
            "-10.0", "-5.0", "40.0", "50.0", DashboardServerUtils.USER_FILE_DATA_ROLES);

    public static final DoubleDashDataType SST = new DoubleDashDataType("temp",
            611.0, "SST", "sea surface temperature", false,
            TEMPERATURE_UNITS, "sea_surface_temperature", TEMPERATURE_CATEGORY, "degrees C",
            "-10.0", "-5.0", "40.0", "50.0", DashboardServerUtils.USER_FILE_DATA_ROLES);

    public static final DoubleDashDataType PEQU = new DoubleDashDataType("Pressure_equi",
            620.0, "P_equ", "equilibrator chamber pressure", false,
            PRESSURE_UNITS, null, PRESSURE_CATEGORY, null,
            "750.0", "900.0", "1100.0", "1250.0", DashboardServerUtils.USER_FILE_DATA_ROLES);

    public static final DoubleDashDataType PATM = new DoubleDashDataType("Pressure_atm",
            621.0, "P_atm", "sea-level air pressure", false,
            PRESSURE_UNITS, "air_pressure_at_sea_level", PRESSURE_CATEGORY, null,
            "750.0", "900.0", "1100.0", "1250.0", DashboardServerUtils.USER_FILE_DATA_ROLES);

    public static final DoubleDashDataType XCO2_WATER_TEQU_DRY = new DoubleDashDataType("xCO2_water_equi_temp_dry_ppm",
            630.0, "xCO2_water_Tequ_dry", "water xCO2 dry using equi temp", false,
            XCO2_UNITS, "mole_fraction_of_carbon_dioxide_in_sea_water", CO2_CATEGORY, null,
            "0.0", "80.0", "1200.0", "50000.0", DashboardServerUtils.USER_FILE_DATA_ROLES);

    public static final DoubleDashDataType XCO2_WATER_SST_DRY = new DoubleDashDataType("xCO2_water_sst_dry_ppm",
            631.0, "xCO2_water_SST_dry", "water xCO2 dry using sst", false,
            XCO2_UNITS, "mole_fraction_of_carbon_dioxide_in_sea_water", CO2_CATEGORY, null,
            "0.0", "80.0", "1200.0", "50000.0", DashboardServerUtils.USER_FILE_DATA_ROLES);

    public static final DoubleDashDataType PCO2_WATER_TEQU_WET = new DoubleDashDataType("pCO2_water_equi_temp",
            632.0, "pCO2_water_Tequ_wet", "water pCO2 wet using equi temp", false,
            PCO2_UNITS, "surface_partial_pressure_of_carbon_dioxide_in_sea_water", CO2_CATEGORY, null,
            "0.0", "80.0", "1200.0", "50000.0", DashboardServerUtils.USER_FILE_DATA_ROLES);

    public static final DoubleDashDataType PCO2_WATER_SST_WET = new DoubleDashDataType(
            "pCO2_water_sst_100humidity_uatm",
            633.0, "pCO2_water_SST_wet", "water pCO2 wet using sst", false,
            PCO2_UNITS, "surface_partial_pressure_of_carbon_dioxide_in_sea_water", CO2_CATEGORY, null,
            "0.0", "80.0", "1200.0", "50000.0", DashboardServerUtils.USER_FILE_DATA_ROLES);

    public static final DoubleDashDataType FCO2_WATER_TEQU_WET = new DoubleDashDataType("fCO2_water_equi_uatm",
            634.0, "fCO2_water_Tequ_wet", "water fCO2 wet using equi temp", false,
            FCO2_UNITS, "surface_partial_pressure_of_carbon_dioxide_in_sea_water", CO2_CATEGORY, null,
            "0.0", "80.0", "1200.0", "50000.0", DashboardServerUtils.USER_FILE_DATA_ROLES);

    public static final DoubleDashDataType FCO2_WATER_SST_WET = new DoubleDashDataType(
            "fCO2_water_sst_100humidity_uatm",
            635.0, "fCO2_water_SST_wet", "water fCO2 wet using sst", false,
            FCO2_UNITS, "surface_partial_pressure_of_carbon_dioxide_in_sea_water", CO2_CATEGORY, null,
            "0.0", "80.0", "1200.0", "50000.0", DashboardServerUtils.USER_FILE_DATA_ROLES);

    public static final DoubleDashDataType XCO2_ATM_DRY_ACTUAL = new DoubleDashDataType("xCO2_atm_dry_actual",
            640.0, "xCO2_atm_dry_actual", "actual air xCO2 dry", false,
            XCO2_UNITS, "mole_fraction_of_carbon_dioxide_in_air", CO2_CATEGORY, null,
            "0.0", "80.0", "1200.0", "50000.0", DashboardServerUtils.USER_FILE_DATA_ROLES);

    public static final DoubleDashDataType PCO2_ATM_DRY_ACTUAL = new DoubleDashDataType("pCO2_atm_wet_actual",
            641.0, "pCO2_atm_wet_actual", "actual air pCO2 wet", false,
            PCO2_UNITS, "surface_partial_pressure_of_carbon_dioxide_in_air", CO2_CATEGORY, null,
            "0.0", "80.0", "1200.0", "50000.0", DashboardServerUtils.USER_FILE_DATA_ROLES);

    public static final DoubleDashDataType FCO2_ATM_DRY_ACTUAL = new DoubleDashDataType("fCO2_atm_wet_actual",
            642.0, "fCO2_atm_wet_actual", "actual air fCO2 wet", false,
            FCO2_UNITS, "surface_partial_pressure_of_carbon_dioxide_in_air", CO2_CATEGORY, null,
            "0.0", "80.0", "1200.0", "50000.0", DashboardServerUtils.USER_FILE_DATA_ROLES);

    public static final DoubleDashDataType XCO2_ATM_DRY_INTERP = new DoubleDashDataType("xCO2_atm_dry_interp",
            643.0, "xCO2_atm_dry_interp", "interpolated air xCO2 dry", false,
            XCO2_UNITS, "mole_fraction_of_carbon_dioxide_in_air", CO2_CATEGORY, null,
            "0.0", "80.0", "1200.0", "50000.0", DashboardServerUtils.USER_FILE_DATA_ROLES);

    public static final DoubleDashDataType PCO2_ATM_DRY_INTERP = new DoubleDashDataType("pCO2_atm_wet_interp",
            644.0, "pCO2_atm_wet_interp", "interpolated air pCO2 wet", false,
            PCO2_UNITS, "surface_partial_pressure_of_carbon_dioxide_in_air", CO2_CATEGORY, null,
            "0.0", "80.0", "1200.0", "50000.0", DashboardServerUtils.USER_FILE_DATA_ROLES);

    public static final DoubleDashDataType FCO2_ATM_DRY_INTERP = new DoubleDashDataType("fCO2_atm_wet_interp",
            645.0, "fCO2_atm_wet_interp", "interpolated air fCO2 wet", false,
            FCO2_UNITS, "surface_partial_pressure_of_carbon_dioxide_in_air", CO2_CATEGORY, null,
            "0.0", "80.0", "1200.0", "50000.0", DashboardServerUtils.USER_FILE_DATA_ROLES);

    public static final StringDashDataType WOCE_CO2_WATER = new StringDashDataType("WOCE_CO2_water",
            650.0, "WOCE CO2_water", "WOCE flag for aqueous CO2", false,
            DashboardUtils.NO_UNITS, null, DashboardServerUtils.QUALITY_CATEGORY, null,
            "1", "2", "4", "9", DashboardServerUtils.USER_FILE_DATA_ROLES);

    public static final StringDashDataType COMMENT_WOCE_CO2_WATER = new StringDashDataType("comment_WOCE_CO2_water",
            651.0, "comment WOCE CO2_water", "comment about WOCE_CO2_water flag", false,
            DashboardUtils.NO_UNITS, null, null, null,
            null, null, null, null, DashboardServerUtils.USER_ONLY_ROLES);

    public static final StringDashDataType WOCE_CO2_ATM = new StringDashDataType("WOCE_CO2_atm",
            652.0, "WOCE_CO2_atm", "WOCE flag for atmospheric CO2", false,
            DashboardUtils.NO_UNITS, null, DashboardServerUtils.QUALITY_CATEGORY, null,
            "1", "2", "4", "9", DashboardServerUtils.USER_FILE_DATA_ROLES);

    public static final StringDashDataType COMMENT_WOCE_CO2_ATM = new StringDashDataType("comment_WOCE_CO2_atm",
            653.0, "comment WOCE CO2_atm", "comment about WOCE_CO2_atm flag", false,
            DashboardUtils.NO_UNITS, null, null, null,
            null, null, null, null, DashboardServerUtils.USER_ONLY_ROLES);

    // Computed or looked-up values
    public static final DoubleDashDataType WOA_SALINITY = new DoubleDashDataType("woa_sss",
            700.0, "WOA SSS", "salinity from World Ocean Atlas", false,
            SALINITY_UNITS, "sea_surface_salinity", SALINITY_CATEGORY, null,
            null, null, null, null, DashboardServerUtils.FILE_DATA_ONLY_ROLES);

    public static final DoubleDashDataType NCEP_SLP = new DoubleDashDataType("pressure_ncep_slp",
            701.0, "NCEP SLP", "sea level air pressure from NCEP/NCAR reanalysis", false,
            PRESSURE_UNITS, "air_pressure_at_sea_level", PRESSURE_CATEGORY, null,
            null, null, null, null, DashboardServerUtils.FILE_DATA_ONLY_ROLES);

    public static final DoubleDashDataType DELTA_TEMP = new DoubleDashDataType("delta_temp",
            703.0, "delta_temp", "Equilibrator Temp - SST", false,
            TEMPERATURE_UNITS, null, TEMPERATURE_CATEGORY, "degrees C",
            null, null, null, null, DashboardServerUtils.FILE_DATA_ONLY_ROLES);

    // The limits on CALC_SPEED (in knots) are used to check time/location values
    public static final DoubleDashDataType CALC_SPEED = new DoubleDashDataType("calc_speed",
            704.0, "calc ship speed", "calculated ship speed", false,
            SPEED_UNITS, null, DashboardServerUtils.PLATFORM_CATEGORY, null,
            "0.0", null, "80.0", "400.0", DashboardServerUtils.FILE_DATA_ONLY_ROLES);


    public static final DoubleDashDataType ETOPO2_DEPTH = new DoubleDashDataType("etopo2",
            705.0, "ETOPO2 depth", "bathymetry from ETOPO2", false,
            DashboardUtils.DEPTH_UNITS, "sea_floor_depth", DashboardServerUtils.BATHYMETRY_CATEGORY, null,
            null, null, null, null, DashboardServerUtils.FILE_DATA_ONLY_ROLES);

    public static final DoubleDashDataType GVCO2 = new DoubleDashDataType("gvCO2",
            706.0, "GlobalView CO2", "GlobalView xCO2", false,
            XCO2_UNITS, "mole_fraction_of_carbon_dioxide_in_air", CO2_CATEGORY, null,
            null, null, null, null, DashboardServerUtils.FILE_DATA_ONLY_ROLES);

    public static final DoubleDashDataType DIST_TO_LAND = new DoubleDashDataType("dist_to_land",
            707.0, "dist to land", "distance to major land mass", false,
            DISTANCE_UNITS, null, DashboardServerUtils.LOCATION_CATEGORY, null,
            null, null, null, null, DashboardServerUtils.FILE_DATA_ONLY_ROLES);

    public static final DoubleDashDataType FCO2_REC = new DoubleDashDataType("fCO2_recommended",
            710.0, "fCO2_rec", "fCO2 recommended", false,
            FCO2_UNITS, "surface_partial_pressure_of_carbon_dioxide_in_sea_water", CO2_CATEGORY, null,
            null, null, null, null, DashboardServerUtils.FILE_DATA_ONLY_ROLES);

    public static final IntDashDataType FCO2_SOURCE = new IntDashDataType("fCO2_source",
            711.0, "fCO2 src", "Algorithm number for recommended fCO2", false,
            DashboardUtils.NO_UNITS, null, DashboardServerUtils.IDENTIFIER_CATEGORY, null,
            null, null, null, null, DashboardServerUtils.FILE_DATA_ONLY_ROLES);

    /**
     * Case-insensitive map of user-provided column type names used in older versions to those in the latest version.
     * NOTE: TIME is mapped to TIME_OF_DAY because this is for user-provided data column types.
     */
    public static final TreeMap<String,String> OLD_NEW_COL_TYPE_NAMES_MAP =
            new TreeMap<String,String>(String.CASE_INSENSITIVE_ORDER);

    /**
     * Case-insensitive map of user-provided column unit names used in older versions to those in the latest version.
     */
    public static final TreeMap<String,String> OLD_NEW_COL_UNIT_NAMES_MAP =
            new TreeMap<String,String>(String.CASE_INSENSITIVE_ORDER);

    static {
        OLD_NEW_COL_TYPE_NAMES_MAP.put("ATMOSPHERIC_TEMPERATURE", "Temperature_atm");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("CRUISE_NAME", "dataset_name");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("EQUILIBRATOR_PRESSURE", "Pressure_equi");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("EQUILIBRATOR_TEMPERATURE", "Temperature_equi");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("FCO2_WATER_SST_WET", "fCO2_water_sst_100humidity_uatm");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("FCO2_WATER_TEQU_WET", "fCO2_water_equi_uatm");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("INVESTIGATOR_NAMES", "investigators");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("PCO2_WATER_SST_WET", "pCO2_water_sst_100humidity_uatm");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("PCO2_WATER_TEQU_WET", "pCO2_water_equi_temp");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("SALINITY", "sal");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("SEA_LEVEL_PRESSURE", "Pressure_atm");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("SEA_SURFACE_TEMPERATURE", "temp");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("SECOND_OF_DAY", "sec_of_day");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("sec", "second");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("SHIP_DIRECTION", "ship_dir");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("SHIP_NAME", "platform_name");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("TIME", "time_of_day");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("TIMESTAMP", "date_time");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("vessel_name", "platform_name");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("WIND_DIRECTION_RELATIVE", "wind_dir_rel");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("WIND_DIRECTION_TRUE", "wind_dir_true");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("WIND_SPEED_RELATIVE", "wind_speed_rel");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("XCO2_WATER_TEQU_DRY", "xCO2_water_equi_temp_dry_ppm");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("XCO2_WATER_TEQU_WET", "xCO2_water_equi_temp_wet_ppm");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("XCO2_WATER_SST_DRY", "xCO2_water_sst_dry_ppm");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("XCO2_WATER_SST_WET", "xCO2_water_sst_wet_ppm");
        OLD_NEW_COL_TYPE_NAMES_MAP.put("XH2O_EQU", "xH2O_equi");

        OLD_NEW_COL_UNIT_NAMES_MAP.put("degrees_east", "deg E");
        OLD_NEW_COL_UNIT_NAMES_MAP.put("deg.E", "deg E");
        OLD_NEW_COL_UNIT_NAMES_MAP.put("degrees_north", "deg N");
        OLD_NEW_COL_UNIT_NAMES_MAP.put("deg.N", "deg N");
        OLD_NEW_COL_UNIT_NAMES_MAP.put("degrees_west", "deg W");
        OLD_NEW_COL_UNIT_NAMES_MAP.put("deg.W", "deg W");
        OLD_NEW_COL_UNIT_NAMES_MAP.put("degrees_south", "deg S");
        OLD_NEW_COL_UNIT_NAMES_MAP.put("deg.S", "deg S");
        OLD_NEW_COL_UNIT_NAMES_MAP.put("degrees C", "deg C");
        OLD_NEW_COL_UNIT_NAMES_MAP.put("deg.C", "deg C");
        OLD_NEW_COL_UNIT_NAMES_MAP.put("degrees", "deg clk N");
        OLD_NEW_COL_UNIT_NAMES_MAP.put("deg.clk.N", "deg clk N");
    }

}
