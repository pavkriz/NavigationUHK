package uhk.kikm.navigationuhk.utils.scanners;

import java.util.List;

import uhk.kikm.navigationuhk.dataLayer.BleScan;
import uhk.kikm.navigationuhk.dataLayer.CellScan;
import uhk.kikm.navigationuhk.dataLayer.WifiScan;

/**
 * Interface pro prijem udalosti v prubehu skenovani.
 */
public interface ScanResultListener {

    /**
     * Zavolano pri dokonceni skenovani. Je volano z jineho vlakna proto v MUSI byt v implementaci kod spusten na spravnem (UI) vlakne
     * @param wifiScans Seznam naskenovanych wifin
     * @param bleScans Seznam naskenovanych BLE beaconu
     * @param cellScans Seznam ziskanych bts informaci
     */
    void onScanFinished(List<WifiScan> wifiScans, List<BleScan> bleScans, List<CellScan> cellScans);
}
