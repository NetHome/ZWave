package nu.nethome.zwave.messages.commandclasses;

public enum SensorType {
    TEMPERATURE (0x01, "Temperature"),
    GENERAL_PURPOSE (0x02, ""),
    LUMINANCE (0x03, "Luminance"),
    POWER (0x04, "Power"),
    RELATIVE_HUMIDITY (0x05, "Relative humidity"),
    VELOCITY (0x06, "Velocity"),
    DIRECTION (0x07,"Direction"),
    ATMOSPHERIC_PRESSURE (0x08,"Atmospheric pressure"),
    BAROMETRIC_PRESSURE (0x09,"Barometric pressure"),
    SOLAR_RADIATION (0x0A,"Solar radiation"),
    DEW_POINT (0x0B,"Dew point"),
    RAIN_RATE (0x0C,"Rain rate"),
    TIDE_LEVEL (0x0D,"Tide level"),
    WEIGHT (0x0E,"Weight"),
    VOLTAGE (0x0F,"Voltage"),
    CURRENT (0x10,"Current"),
    CO2_LEVEL (0x11,"CO2-level"),
    AIR_FLOW (0x12,"Air flow"),
    TANK_CAPACITY (0x13,"Tank capacity"),
    DISTANCE (0x14,"Distance");

    final public int value;
    final public String name;

    SensorType(int value, String name) {
        this.value = value;
        this.name = name;
    }
}
