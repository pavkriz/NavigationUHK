package uhk.kikm.navigationuhk.dataLayer;

/**
 * Modelova trida reprezentujci scan Wifi site
 * Dominik Matoulek 2015
 */
public class WifiScan {
    private String SSID, MAC, technology;
    private int frequency, channel, strength;
    long time;

    //region CONSTRUCTORS / GETTERS / SETTERS
    public WifiScan() {
    }

    public WifiScan(String SSID, String MAC, int strength, int frequency) {
        this.SSID = SSID;
        this.MAC = MAC;
        this.strength = strength;
        this.frequency = frequency;
        setChannelAndTechnology();
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strenght) {
        this.strength = strenght;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
    //endregion

    @Override
    public String toString() {
        return "WifiScan{" +
                "SSID='" + SSID + '\'' +
                ", MAC='" + MAC + '\'' +
                ", technology='802.11" + technology + "'" +
                ", frequency=" + frequency +
                ", channel=" + channel +
                ", strength=" + strength +
                ", time=" + time +
                '}';
    }

    /**
     * Get WiFi channel and technology from its frequency
     *
     * @see https://en.wikipedia.org/wiki/List_of_WLAN_channels#Interference_concerns
     * @see https://en.wikipedia.org/wiki/List_of_WLAN_channels#5.C2.A0GHz_.28802.11a.2Fh.2Fj.2Fn.2Fac.29.5B17.5D
     */
    private void setChannelAndTechnology() {
        switch (this.frequency) {
            case 2412:
                this.channel = 1;
                this.technology = "g/n";
                break;
            case 2417:
                this.channel = 2;
                this.technology = "g/n";
                break;
            case 2422:
                this.channel = 3;
                this.technology = "g/n";
                break;
            case 2427:
                this.channel = 4;
                this.technology = "g/n";
                break;
            case 2432:
                this.channel = 5;
                this.technology = "g/n";
                break;
            case 2437:
                this.channel = 6;
                this.technology = "g/n";
                break;
            case 2442:
                this.channel = 7;
                this.technology = "g/n";
                break;
            case 2447:
                this.channel = 8;
                this.technology = "g/n";
                break;
            case 2452:
                this.channel = 9;
                this.technology = "g/n";
                break;
            case 2457:
                this.channel = 10;
                this.technology = "g/n";
                break;
            case 2462:
                this.channel = 11;
                this.technology = "g/n";
                break;
            case 2467:
                this.channel = 12;
                this.technology = "g/n";
                break;
            case 2472:
                this.channel = 13;
                this.technology = "g/n";
                break;
            case 2484:
                this.channel = 14;
                this.technology = "g/n";
                break;
            case 5035:
                this.channel = 7;
                this.technology = "a/n";
                break;
            case 5040:
                this.channel = 8;
                this.technology = "a/n";
                break;
            case 5045:
                this.channel = 9;
                this.technology = "a/n";
                break;
            case 5055:
                this.channel = 11;
                this.technology = "a/n";
                break;
            case 5060:
                this.channel = 12;
                this.technology = "a/n";
                break;
            case 5080:
                this.channel = 16;
                this.technology = "a/n";
                break;
            case 5170:
                this.channel = 34;
                this.technology = "a/n";
                break;
            case 5180:
                this.channel = 36;
                this.technology = "a/n";
                break;
            case 5190:
                this.channel = 38;
                this.technology = "a/n";
                break;
            case 5200:
                this.channel = 40;
                this.technology = "a/n";
                break;
            case 5210:
                this.channel = 42;
                this.technology = "a/n";
                break;
            case 5220:
                this.channel = 44;
                this.technology = "a/n";
                break;
            case 5230:
                this.channel = 46;
                this.technology = "a/n";
                break;
            case 5240:
                this.channel = 48;
                this.technology = "a/n";
                break;
            case 5250:
                this.channel = 50;
                this.technology = "a/n";
                break;
            case 5260:
                this.channel = 52;
                this.technology = "a/n";
                break;
            case 5270:
                this.channel = 54;
                this.technology = "a/n";
                break;
            case 5280:
                this.channel = 56;
                this.technology = "a/n";
                break;
            case 5290:
                this.channel = 58;
                this.technology = "a/n";
                break;
            case 5300:
                this.channel = 60;
                this.technology = "a/n";
                break;
            case 5310:
                this.channel = 62;
                this.technology = "a/n";
                break;
            case 5320:
                this.channel = 64;
                this.technology = "a/n";
                break;
            case 5500:
                this.channel = 100;
                this.technology = "a/n";
                break;
            case 5510:
                this.channel = 102;
                this.technology = "a/n";
                break;
            case 5520:
                this.channel = 104;
                this.technology = "a/n";
                break;
            case 5530:
                this.channel = 106;
                this.technology = "a/n";
                break;
            case 5540:
                this.channel = 108;
                this.technology = "a/n";
                break;
            case 5550:
                this.channel = 110;
                this.technology = "a/n";
                break;
            case 5560:
                this.channel = 112;
                this.technology = "a/n";
                break;
            case 5570:
                this.channel = 114;
                this.technology = "a/n";
                break;
            case 5580:
                this.channel = 116;
                this.technology = "a/n";
                break;
            case 5590:
                this.channel = 118;
                this.technology = "a/n";
                break;
            case 5600:
                this.channel = 120;
                this.technology = "a/n";
                break;
            case 5610:
                this.channel = 122;
                this.technology = "a/n";
                break;
            case 5620:
                this.channel = 124;
                this.technology = "a/n";
                break;
            case 5630:
                this.channel = 126;
                this.technology = "a/n";
                break;
            case 5640:
                this.channel = 128;
                this.technology = "a/n";
                break;
            case 5660:
                this.channel = 132;
                this.technology = "a/n";
                break;
            case 5670:
                this.channel = 134;
                this.technology = "a/n";
                break;
            case 5680:
                this.channel = 136;
                this.technology = "a/n";
                break;
            case 5690:
                this.channel = 138;
                this.technology = "a/n";
                break;
            case 5700:
                this.channel = 140;
                this.technology = "a/n";
                break;
            case 5710:
                this.channel = 142;
                this.technology = "a/n";
                break;
            case 5720:
                this.channel = 144;
                this.technology = "a/n";
                break;
            case 5745:
                this.channel = 149;
                this.technology = "a/n";
                break;
            case 5755:
                this.channel = 151;
                this.technology = "a/n";
                break;
            case 5765:
                this.channel = 153;
                this.technology = "a/n";
                break;
            case 5775:
                this.channel = 155;
                this.technology = "a/n";
                break;
            case 5785:
                this.channel = 157;
                this.technology = "a/n";
                break;
            case 5795:
                this.channel = 159;
                this.technology = "a/n";
                break;
            case 5805:
                this.channel = 161;
                this.technology = "a/n";
                break;
            case 5825:
                this.channel = 165;
                this.technology = "a/n";
                break;
            case 4915:
                this.channel = 183;
                this.technology = "a/n";
                break;
            case 4920:
                this.channel = 184;
                this.technology = "a/n";
                break;
            case 4925:
                this.channel = 185;
                this.technology = "a/n";
                break;
            case 4935:
                this.channel = 187;
                this.technology = "a/n";
                break;
            case 4940:
                this.channel = 188;
                this.technology = "a/n";
                break;
            case 4945:
                this.channel = 189;
                this.technology = "a/n";
                break;
            case 4960:
                this.channel = 192;
                this.technology = "a/n";
                break;
            case 4980:
                this.channel = 196;
                this.technology = "a/n";
            default:
                this.channel = -1;
                this.technology = "";
                break;
        }
    }
}
