package uhk.kikm.navigationuhk.utils.scanners;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.service.scanner.NonBeaconLeScanCallback;
import org.altbeacon.beacon.Beacon;

import uhk.kikm.navigationuhk.utils.C;

/**
 * Created by Kriz on 16. 2. 2016.
 */
public class ImmediateBeaconConsumer extends DefaultBeaconConsumer {
    protected BeaconParser beaconParser = new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24");
    protected boolean parseAdvertisementData;

    public ImmediateBeaconConsumer(Scanner scanner, Context context, boolean parseAdvertisementData) {
        super(scanner, context);

        this.parseAdvertisementData = parseAdvertisementData;

        // Remove all beacon parsers intentionally in order to force the alt-beacon library to report
        // signals immediately via NonBeaconLeScanCallback. Otherwise the library filters signals received
        // during the internal scanning period resulting into one signal (beacon) per second.
        beaconManager.getBeaconParsers().clear();
    }

    @Override
    public void onBeaconServiceConnect() {
        // We have configured alt-beacon library not to parse beacon data, thus reporting
        // all devices as non-beacons (while they might be and probably are beacons actually)
        beaconManager.setNonBeaconLeScanCallback(new NonBeaconLeScanCallback() {
            @Override
            public void onNonBeaconLeScan(final BluetoothDevice bluetoothDevice, final int rssi, byte[] scanData) {
                // parse beacon data manually here or create beacon a simplyfied Beacon instance
                // without additional data
                Beacon beacon;
                if (parseAdvertisementData) {
                    beacon = beaconParser.fromScanData(scanData, rssi, bluetoothDevice);
                } else {
                    beacon = createSimplyfiedBeacon(bluetoothDevice, rssi);
                }
                if (beacon != null) {
                    scanner.handleBleResult(beacon);
                }
            }
        });
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            Log.e(C.LOG_BLESCAN, "Error while conducting a BLE Scan", e);
        }
    }

    /**
     * Create simplified beacon without UUID, Major, Minor numbers,
     * only RSSI and MAC address are populated, the rest is set to ""
     *
     * This method reduces computational complexity
     * in contrast to BeaconParser
     *
     * @param bluetoothDevice
     * @param rssi
     * @return
     */
    private Beacon createSimplyfiedBeacon(BluetoothDevice bluetoothDevice, int rssi) {
        return new Beacon.Builder()
                .setRssi(rssi)
                .setBluetoothAddress(bluetoothDevice.getAddress())
                .setId1("")
                .setId2("")
                .setId3("")
                .build();
    }

}
