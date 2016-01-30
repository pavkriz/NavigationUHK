package uhk.kikm.navigationuhk.activities;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import uhk.kikm.navigationuhk.R;

/**
 * This appears when application crashes
 * @see https://github.com/ajaysaini-sgvu/crash-report
 */
public class CrashActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crash_activity);

        final TextView textView = (TextView) findViewById(R.id.textView1);
        textView.setText("Sorry, something went wrong. \nPlease send error logs to developer.");

        findViewById(R.id.btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "errorTrace.txt";
                sendErrorMail(CrashActivity.this, filePath);
                finish();
            }
        });

    }

    /**
     * This list a set of application which can send email.
     * Here user have to pick one apps via email will be send to developer email id.
     *
     * @param _context
     * @param filePath
     */
    private void sendErrorMail(Context mContext, String filePath) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        String subject = "[UHK Navigation] Pád aplikace"; // here subject
        String body = "Dobrý den,\npři využívání Vaší aplikace jsem narazil na její náhodný pád.\nV přiloženém souboru posílám detailní informace.\n\nPředem děkuji za vyřešení problému.";
        sendIntent.setType("plain/text");
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"bruhara1@uhk.cz"});
        sendIntent.putExtra(Intent.EXTRA_TEXT, body);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
        sendIntent.setType("message/rfc822");
        mContext.startActivity(Intent.createChooser(sendIntent, "Complete action using"));
    }
}
