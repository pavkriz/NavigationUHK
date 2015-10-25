package uhk.kikm.navigationuhk.utils.scanners;

/**
 * Interface pro prijem udalosti v prubehu skenovani.
 */
public interface ScanResultListener {

    /**
     * Zavolano pri naskenovani dalsi polozky
     * @param cumulativeCount Aktualni celkovy pocet naskenovanych polozek
     */
    void onScanResult(int cumulativeCount);
}
