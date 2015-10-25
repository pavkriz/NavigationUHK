package uhk.kikm.navigationuhk.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import uhk.kikm.navigationuhk.dataLayer.Fingerprint;


/**
 * Trida ziskavajici informace o zarizeni
 * Dominik Matoulek 2015
 */
public class DeviceInformation {
    Context context;

    /**
     * Inicializuje DeviceInformation
     * @param context context
     */
    public DeviceInformation(Context context) {
        this.context = context;
    }

    /**
     * Naplni fingerprint daty o zarizeni, ktere fingerprint delalo
     * @param p fingerprint vhodny k naplneni daty
     * @return fingerprint s naplenynmi daty
     */
    public Fingerprint fillPosition(Fingerprint p)
    {
        p.setSupportsBLE(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE));
        p.setDeviceID(((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
        p.setBoard(Build.BOARD);
        p.setBootloader(Build.BOOTLOADER);
        p.setBrand(Build.BRAND);
        p.setDevice(Build.DEVICE);
        p.setDisplay(Build.DISPLAY);
        p.setFingerprint(Build.FINGERPRINT);
        p.setHardware(Build.HARDWARE);
        p.setHost(Build.HOST);
        p.setOsId(Build.ID);
        p.setManufacturer(Build.MANUFACTURER);
        p.setModel(Build.MODEL);
        p.setProduct(Build.PRODUCT);
        p.setSerial(Build.SERIAL);
        p.setTags(Build.TAGS);
        p.setType(Build.TYPE);
        p.setUser(Build.USER);
        return p;
    }
}
