package uhk.kikm.navigationuhk.utils;

import uhk.kikm.navigationuhk.utils.LocalizationService.LocalizationServicePoint;

/**
 * Trida obsahujici Configuracni a Constantni hodnoty
 * <p/>
 * Dominik Matoulek 2015
 */
public class C {
    public static final String LOG_BLESCAN = "BLE Scan";
    public static final String LOG_WIFISCAN = "Wifi Scan";

    public static LocalizationServicePoint pointA = new LocalizationServicePoint(34, 31, new Float(50.2045875), new Float(15.8290822));
    public static LocalizationServicePoint pointB = new LocalizationServicePoint(642, 39, new Float(50.2045247), new Float(15.8297047));
    public static LocalizationServicePoint pointC = new LocalizationServicePoint(26, 794, new Float(50.2040850), new Float(15.8289544));

    public static String LOGIN_URL = "http://beacon.uhk.cz/auth";
    public static String DB_URL = "http://beacon.uhk.cz/beacongw";

    /**
     * Jak dlouho ma probihat skenovani pri sberu dat
     */
    public static int SCAN_TIME = 10000;
}
