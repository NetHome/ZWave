package nu.nethome.zwave.messages.commandclasses;

import nu.nethome.zwave.messages.framework.DecoderException;

import static nu.nethome.zwave.messages.commandclasses.SensorType.*;

public enum SensorUnit {
    C(TEMPERATURE, 0, "°C"),
    F(TEMPERATURE, 1, "°F"),
    PERCENTAGE(GENERAL_PURPOSE, 0, "%"),
    DIMENSIONLESS(GENERAL_PURPOSE, 1, ""),
    LIGHT_PERCENTAGE(LUMINANCE, 0, "%"),
    LUX(LUMINANCE, 0, "Lux"),
    W(POWER, 0, "W"),
    BTU_H(POWER, 1, "Btu/h"),
    PERCENTAGE_HUMIDITY(RELATIVE_HUMIDITY, 0, "%"),
    M_S(VELOCITY, 0, "m/s"),
    MPH(VELOCITY, 1, "mph"),
    DIRECTION(SensorType.DIRECTION, 0, ""),
    KPA(ATMOSPHERIC_PRESSURE, 0, "kPa"),
    IOM(ATMOSPHERIC_PRESSURE, 1, "ioM"),
    BKPA(BAROMETRIC_PRESSURE, 0, "kPa"),
    BIOM(BAROMETRIC_PRESSURE, 1, "ioM"),
    W_M2(SOLAR_RADIATION, 0, "W/m2"),
    DPC(DEW_POINT, 0, "°C"),
    DPF(DEW_POINT, 1, "°F"),
    MM_H(RAIN_RATE, 0, "mm/h"),
    I_H(RAIN_RATE, 1, "in/h"),
    M_T(TIDE_LEVEL, 0, "m"),
    F_T(TIDE_LEVEL, 1, "feet"),
    KG(WEIGHT, 0, "kg"),
    POUNDS(WEIGHT, 1, "pounds"),
    V(VOLTAGE, 0, "V"),
    MV(VOLTAGE, 1, "mV"),
    A(CURRENT, 0, "A"),
    MA(CURRENT, 1, "mA"),
    PPM(CO2_LEVEL, 0, "ppm"),
    M3_H(AIR_FLOW, 0, "m³/h"),
    CFM(AIR_FLOW, 1, "cfm"),
    L(TANK_CAPACITY, 0, "l"),
    CBM(TANK_CAPACITY, 1, "cbm"),
    GALLONS(TANK_CAPACITY, 2, "US gallons"),
    M(DISTANCE, 0, "m"),
    CM(DISTANCE, 1, "cm"),
    FEET(DISTANCE, 2, "feet");

    final public SensorType type;
    final private int scale;
    final public String unit;

    SensorUnit(SensorType type, int scale, String unit) {
        this.type = type;
        this.scale = scale;
        this.unit = unit;
    }

    public static SensorUnit fromMeterScale(int meterType, int scale) throws DecoderException {
        for (SensorUnit u : SensorUnit.values()) {
            if ((u.type.value == meterType) && (u.scale == scale)) {
                return u;
            }
        }
        throw new DecoderException("Unknown Meter Unit");
    }
}
