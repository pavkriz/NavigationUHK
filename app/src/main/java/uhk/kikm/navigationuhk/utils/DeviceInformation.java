package uhk.kikm.navigationuhk.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * Created by dominik on 5.3.15.
 */
public class DeviceInformation {

    TelephonyManager telephonyManager;
    Build build;

    public DeviceInformation(Context context) {
        build = new Build();
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }
}