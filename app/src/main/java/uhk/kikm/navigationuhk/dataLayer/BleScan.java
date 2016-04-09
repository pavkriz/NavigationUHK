package uhk.kikm.navigationuhk.dataLayer;

/**
 * Datova trida obsahujici informace o zaznamenanem BLE zarizeni
 * Dominik Matoulek 2015
 */
public class BleScan {

    int rssi;
    String uuid = "";
    int major, minor;
    String address = "";
    long time;

    public BleScan() {
    }

    public BleScan(int rssi, String address) {
        this.rssi = rssi;
        this.address = address;
    }

    @Override
    public String toString() {
        return "BleScan{" +
                "rssi=" + rssi +
                ", uuid='" + uuid + '\'' +
                ", major=" + major +
                ", minor=" + minor +
                ", address='" + address + '\'' +
                ", time=" + time +
                '}';
    }

    public String toJson() {
        return "{" +
            "\"rssi\":" + rssi +
            ", \"uuid\":\"" + uuid +  "\"" +
            ", \"major\":" + major +
            ", \"minor\":" + minor +
            ", \"address\":\"" + address +  "\"" +
            ", \"time\":" + time +
            "}";
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
