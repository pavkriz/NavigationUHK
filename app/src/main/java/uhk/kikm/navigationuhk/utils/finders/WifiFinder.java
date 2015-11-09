package uhk.kikm.navigationuhk.utils.finders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import uhk.kikm.navigationuhk.dataLayer.Fingerprint;
import uhk.kikm.navigationuhk.dataLayer.WifiScan;

/**
 * Trida reprezentujici vyhledavani polohy
 *
 * Dominik Matoulek 2015
 */
public class WifiFinder {
    private List<Fingerprint> fingerprints;
    private HashMap<String, Fingerprint> navigationData;
    private HashMap<WifiScan, Fingerprint> positionsOfScans;
    private HashMap<Float, Fingerprint> computedDistance;

    private final double SIGNAL_NO_RECIEVED = -100; // Minimalni sila signalu, ktery dokaze WiFi prijimat - Ekvivalent "nuly"

    /**
     * Vytvari novou instaci WifiFinderu
     * @param fingerprints Fingreprinty, ktere maji byt pouzity k urceni polohy
     */
    public WifiFinder(List<Fingerprint> fingerprints) {

        navigationData = new HashMap<>();
        positionsOfScans = new HashMap<>();

        this.fingerprints = fingerprints;
        for (Fingerprint p : fingerprints) // pro vsechny polohy co obsahuji MAC
        {
            navigationData.put(String.valueOf(p.getX()) + " " + String.valueOf(p.getY()), p); // pridej do seznamu vsechny scany s hasem polohy
            for (WifiScan s : p.getWifiScans()) {
                // Kazdy sken patri k urcite poloze... Kazdy sken je take jedinecny, hodnoty muzou byt stejne, ale je jedinencny - je to rychlejsi, nez proheledavani cyklem
                positionsOfScans.put(s, p);
            }
        }

        computedDistance = new HashMap<>();
    }

    /**
     * Vypocita moznou pozici zarizeni. Je pozivan algorimus kNN a k = 3
     * @param scansToIdentify Seznam aktualnich scanu z WifiManageru
     * @return "umely fingerprint" obsahujici pouze pozici a patro
     */
    public Fingerprint computePossibleFingerprint(List<WifiScan> scansToIdentify) {

        float distance = 0;

        Fingerprint nearestFingerprint = new Fingerprint();

        for (Fingerprint p : fingerprints)
        {

            if (p.getWifiScans().size() < scansToIdentify.size()) {
                for (WifiScan s : scansToIdentify) {
                    int index = containsMAC(s.getMAC(), p);

                    if (index >= 0) {
                        distance += Math.pow(p.getScan(index).getStrenght() + s.getStrenght(), 2);
                    }
                    else if (index == -1)
                    {
                        distance += Math.pow(SIGNAL_NO_RECIEVED + s.getStrenght(), 2);
                    }
                }
            } else {
                for (WifiScan s : p.getWifiScans()) {
                    int index = containsMAC(s.getMAC(), scansToIdentify);

                    if (index >= 0) {
                        distance += Math.pow(scansToIdentify.get(index).getStrenght() + s.getStrenght(), 2);
                    } else if (index == -1) {
                        distance += Math.pow(SIGNAL_NO_RECIEVED + s.getStrenght(), 2);
                    }
                }
            }


            distance = (float) Math.sqrt(distance);

            computedDistance.put(distance, p); // hashmapa rikajici, ze ta a ta pozice ma takovou a makovou vzdalenost

            distance = 0;
        }

        ArrayList<Float> sortedDistances = new ArrayList<>(computedDistance.keySet()); // Vezmeme pouze vzdalenosti

        Collections.sort(sortedDistances); // setridime

        if (sortedDistances.size() > 2) { // Pokud je vic jak dva fingerprinty, vypocitame fingerprint = teziste trouhelniku tvoreneho tremi nejblizsimi fingerprinty
            Fingerprint firstFingerprint = computedDistance.get(sortedDistances.get(0));
            Fingerprint secondFingerprint = computedDistance.get(sortedDistances.get(1));
            Fingerprint thirdFingerprint = computedDistance.get(sortedDistances.get(2));

            int computedX = (firstFingerprint.getX() + secondFingerprint.getX() + thirdFingerprint.getX()) / 3;
            int computedY = (firstFingerprint.getY() + secondFingerprint.getY() + thirdFingerprint.getY()) / 3;

            nearestFingerprint.setX(computedX);
            nearestFingerprint.setY(computedY);

            // zjisteni spravneho patra, pokud je vsude rozdilne, dame 2. patro
            if (firstFingerprint.getLevel().equals(secondFingerprint.getLevel())
                    || secondFingerprint.getLevel().equals(thirdFingerprint.getLevel()))
                nearestFingerprint.setLevel(secondFingerprint.getLevel());
             else if (firstFingerprint.getLevel().equals(thirdFingerprint.getLevel()))
                nearestFingerprint.setLevel(firstFingerprint.getLevel());
            else
                nearestFingerprint.setLevel("J2NP");
        }
        else // pokud je jich =< 2, tak k = 1
        {
            nearestFingerprint = computedDistance.get(sortedDistances.get(0));
        }

        return nearestFingerprint;
    }

    /**
     * Zjistuje pokud dana MAC adresa je ve Fingerprintu zanznamenana. Pokud je, vrati jeji index.
     * @param s MAC ve stringu
     * @param p Fingerprint
     * @return Index zaznamenane MAC, pokud neni nalezena, vraci -1
     */
    private int containsMAC(String s, Fingerprint p)
    {
        for(int i = 0; i < p.getWifiScans().size(); i++)
        {
            if (s.equals(p.getScan(i).getMAC()))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     *Zjistuje pokud dana MAC adresa je v seznamu ScaResultu zanznamenana. Pokud je, vrati jeji index.
     * @param s MAC ve stringu
     * @param scans seznam ScanResultu
     * @return Index zaznamenane MAC, pokud neni nalezena, vraci -1
     */
    private int containsMAC(String s, List<WifiScan> scans)
    {
        for(int i = 0; i < scans.size(); i++)
        {
            if (s.equals(scans.get(i).getMAC()))
            {
                return i;
            }
        }
        return -1;
    }

    public List<Fingerprint> getFingerprints() {
        return fingerprints;
    }

    public void setFingerprints(List<Fingerprint> fingerprints) {
        this.fingerprints = fingerprints;
    }


}