package uhk.kikm.navigationuhk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import uhk.kikm.navigationuhk.R;
import uhk.kikm.navigationuhk.dataLayer.CouchDBManager;
import uhk.kikm.navigationuhk.dataLayer.Fingerprint;

/**
 * Aktivita urcena na zobrazeni seznamu vsech porizenych fingerprintu
 *
 * Dominik Matoulek 2015
 */


public class ListPositionsActivity extends ActionBarActivity {

    CouchDBManager dbManager;
    List<Fingerprint> fingerprints;
    SortedMap<String, String> positionsMap;
    ArrayList<String> positionsStrings;
    ListView lv;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_positions);

        dbManager = new CouchDBManager(this);
        Log.d(getClass().getName(), "Open db connection");

        positionsMap = new TreeMap(new Comparator<String>() {
            public int compare(String s1, String s2) {
            long t1 = 0, t2 = 0;
                try {
                    t1 = new SimpleDateFormat("dd. MM. yyyy kk:mm:ss").parse(s1.substring(0, s1.indexOf("-") - 1)).getTime();
                    t2 = new SimpleDateFormat("dd. MM. yyyy kk:mm:ss").parse(s2.substring(0, s2.indexOf("-") - 1)).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return t1 == t2 ? 0 : (t1 < t2 ? 1 : -1);
            }
        });

        makeDataForView();

        lv = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, positionsStrings);

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDetailsOfFingerprint(positionsStrings.get(position));

            }
        });
    }

    /**
     * Pripravuje data pro zobrazeni
     */
    private void makeDataForView() {
        positionsMap.clear();
        fingerprints = dbManager.getAllFingerprints();

        for (Fingerprint p : fingerprints) {
            positionsMap.put(DateFormat.format("dd. MM. yyyy kk:mm:ss", p.getCreatedDate()) + " - "
                    + p.getLevel() + " - "
                    + p.getManufacturer() + " "
                    + p.getModel() + " "
                    + p.getId(), p.getId());
        }

        positionsStrings = new ArrayList<>(positionsMap.keySet());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_positions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_upload)
        {
            dbManager.uploadDBToServer(this);


        }
        else if (id == R.id.action_download)
        {
            dbManager.downloadDBFromServer(this);
            makeDataForView();
            adapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        dbManager.closeConnection();
        Log.d(getClass().getName(), "Close db connection");
    }

    /**
     * Zobrazi novou aktivitu obsahujici informace o fingerprintu
     * @param position ID fingerprintu v DB
     */
    private void showDetailsOfFingerprint(final String position)
    {
        Intent intent = new Intent(this, PositionInfoActivity.class);
        intent.putExtra("id", positionsMap.get(position));
        startActivity(intent);

    }

}
