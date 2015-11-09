package uhk.kikm.navigationuhk.utils.webview;

import android.util.Log;
import android.webkit.JavascriptInterface;

import uhk.kikm.navigationuhk.activities.LoginActivity;

/**
 * Trida reprezentujici JavascriptInterface pro LoginActivity
 * Dominik Matoulek 2015
 */
public class LoginWebViewInterface {

    private String couhBaseId;
    private String sessionId;
    private String expireTime;
    private String cookieName;

    private LoginActivity activity;

    public LoginWebViewInterface(LoginActivity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @JavascriptInterface
    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    @JavascriptInterface
    public void setExpires(String expires) {
        this.expireTime = expires;
    }

    @JavascriptInterface
    public void setCouchId(String couchId) {
        this.couhBaseId = couchId;
    }

    @JavascriptInterface
    public void done() {
        activity.run(cookieName, sessionId, expireTime, couhBaseId);
        Log.d(getClass().getName(), "Done");
    }
}
