package uhk.kikm.navigationuhk.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import uhk.kikm.navigationuhk.R;
import uhk.kikm.navigationuhk.dataLayer.CouchDBManager;
import uhk.kikm.navigationuhk.dataLayer.Fingerprint;

/**
 * Aktivita zobrazujici detailni informace o fingerprintu
 *
 * Dominik Matoulek 2015
 */

public class PositionInfoActivity extends ActionBarActivity {

    String id;
    Fingerprint fingerprint;
    CouchDBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_info);

        dbManager = new CouchDBManager(this);
        id = getIntent().getStringExtra("id");
        fingerprint = dbManager.getFingerprintById(id);

        TextView textView = (TextView) findViewById(R.id.textView);
        //textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText(fingerprint.toString());


        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                removePosition();
            }
        });
    }

    private void removePosition()
    {
        AlertDialog.Builder removeDialog = new AlertDialog.Builder(this);

        removeDialog.setTitle(R.string.remove_dialog_title);
        removeDialog.setMessage(R.string.remove_dialog_message);

        removeDialog.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbManager.removeFingerprint(fingerprint.getId());
                redirectBack();
            }
        });

        removeDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        removeDialog.create().show();
    }

    public void redirectBack()
    {
        Intent intent = new Intent(this, ListPositionsActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_position_info, menu);

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

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        dbManager.closeConnection();
    }
}
