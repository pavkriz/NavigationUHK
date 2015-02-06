package uhk.kikm.navigationuhk;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import uhk.kikm.navigationuhk.model.CouchDBManager;
import uhk.kikm.navigationuhk.model.Position;
import uhk.kikm.navigationuhk.utils.BluetoothLEScanner;
import uhk.kikm.navigationuhk.utils.WebViewInterface;
import uhk.kikm.navigationuhk.utils.WifiScanner;

import com.couchbase.lite.*;
import com.couchbase.lite.android.AndroidContext;



public class MainActivity extends ActionBarActivity {

    WebViewInterface webInterface;
    WifiScanner wScanner;
    BluetoothLEScanner bleScanner;
    boolean scanningBle;
    CouchDBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webInterface = new WebViewInterface(this);

        WebView view = (WebView) findViewById(R.id.WebView);
        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setBuiltInZoomControls(true);
        view.getSettings().setSupportZoom(true);
        view.setWebViewClient(new WebViewClient());
        view.loadData(readTextFromResource(R.drawable.uhk_j_2_level), null, "UTF-8");
        view.addJavascriptInterface(webInterface, "android");

        final Button newPointButton = (Button) findViewById(R.id.write_point);
        final Button newBlePointButton = (Button) findViewById(R.id.write_point_ble);

        wScanner = new WifiScanner(this);
        wScanner.findAll();

        newPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writePoint();
            }
        });
        newBlePointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeBlePoint();
            }
        });

        scanningBle = false;

        bleScanner = new BluetoothLEScanner(this);

        dbManager = new CouchDBManager(this);
        System.out.println("Open db connection in MainActivity");

    }

    public void writeBlePoint()
    {
        if (!scanningBle)
        {
            bleScanner.findAll();
            scanningBle = true;
        }
        else
        {
            bleScanner.stopScan();
            Toast.makeText(this, bleScanner.getBleDeviceList().toString(), Toast.LENGTH_LONG).show();
            bleScanner.clear();
            scanningBle = false;
        }
    }

    public void writePoint()
    {
        if(webInterface.isChanged())
        {
            Toast.makeText(this, webInterface.getX() + " " + webInterface.getY(), Toast.LENGTH_LONG).show();
            webInterface.setChanged(false);
            dbManager.savePosition(wScanner.getPosition(webInterface.getX(), webInterface.getY()));
        }
        else
        {
            Toast.makeText(this, "Musi byt jine souradnice", Toast.LENGTH_SHORT).show();
        }

        // debug....

        /*List<Position> pos = dbManager.getAllPositions();
        System.out.println(pos.toString());
        dbManager.deleteAll();*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        else if (id == R.id.action_about)
        {
            Intent intent = new Intent(this, ListPositionsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Metoda na nacteni textu z nejakeho souboru.
     * @param resourceID ID zdroje
     * @return String text
     */
    private String readTextFromResource(int resourceID)
    {
        InputStream raw = getResources().openRawResource(resourceID);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int i;
        try
        {
            i = raw.read();
            while (i != -1)
            {
                stream.write(i);
                i = raw.read();
            }
            raw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return stream.toString();

    }

    @Override
    protected void onStop() {
        super.onStop();

        dbManager.closeConnection();
        System.out.println("Close db connection in MainActivity");
    }
}
