package nu.nethome.zwave.messages.commandclasses;

import nu.nethome.zwave.messages.framework.DecoderException;

public enum MeterUnit {
    KWH(1, 0, "KWh", "Power"),
    KVAh(1, 1, "kVAh", "Energy"),
    W(1, 2, "W", "Power"),
    ELECTRICAL_PULSES(1, 3, "Pulses", "Count"),
    V(1, 4, "V", "Voltage"),
    A(1, 5, "A", "Current"),
    POWER_FACTOR(1, 6, "Power Factor", "Power Factor"),
    CUBIC_METERS_GAS(2, 0, "Cubic Meters", "Volume"),
    CUBIC_FEET_GAS(2, 1, "Cubic Feet", "Volume"),
    GAS_PULSES(2, 3, "Pulses", "Count"),
    CUBIC_METERS_WATER(3, 0, "Cubic Meters", "Volume"),
    CUBIC_FEET_WATER(3, 1, "Cubic Feet", "Volume"),
    GALLONS_WATER(3, 2, "US gallons", "Volume"),
    WATER_PULSES(3, 3, "Pulses", "Count");

    // Meter Types
    public static final int ELECTRIC_METER = 0x01;
    public static final int GAS_METER = 0x02;
    public static final int WATER_METER = 0x03;

    final public int meterType;
    final private int scale;
    final public String unit;
    final public String name;

    MeterUnit(int meterType, int scale, String unit, String name) {
        this.meterType = meterType;
        this.scale = scale;
        this.unit = unit;
        this.name = name;
    }

    public static MeterUnit fromMeterScale(int meterType, int scale) throws DecoderException {
        for (MeterUnit u : MeterUnit.values()) {
            if ((u.meterType == meterType) && (u.scale == scale)) {
                return u;
            }
        }
        throw new DecoderException("Unknown Meter Unit");
    }
}
