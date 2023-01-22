
package com.reactlibrary;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import com.facebook.react.bridge.*;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.encode.EncodeActivity;
import com.google.zxing.client.android.Intents;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RNLmCodeScanModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    public static final int REQUEST_CODE = 0x0ba7c;

    private static final String SCAN = "scan";
    private static final String ENCODE = "encode";
    private static final String CANCELLED = "cancelled";
    private static final String FORMAT = "format";
    private static final String TEXT = "text";
    private static final String DATA = "data";
    private static final String TYPE = "type";
    private static final String PREFER_FRONTCAMERA = "preferFrontCamera";
    private static final String ORIENTATION = "orientation";
    private static final String SHOW_FLIP_CAMERA_BUTTON = "showFlipCameraButton";
    private static final String RESULTDISPLAY_DURATION = "resultDisplayDuration";
    private static final String SHOW_TORCH_BUTTON = "showTorchButton";
    private static final String TORCH_ON = "torchOn";
    private static final String SAVE_HISTORY = "saveHistory";
    private static final String DISABLE_BEEP = "disableSuccessBeep";
    private static final String FORMATS = "formats";
    private static final String PROMPT = "prompt";
    private static final String TEXT_TYPE = "TEXT_TYPE";
    private static final String EMAIL_TYPE = "EMAIL_TYPE";
    private static final String PHONE_TYPE = "PHONE_TYPE";
    private static final String SMS_TYPE = "SMS_TYPE";

    private static final String LOG_TAG = "BarcodeScanner";

    private static final String E_RESULT_CANCELLED = "BARCODE_SCANNER_CANCEL";
    private static final String E_RESULT_ERROR = "BARCODE_SCANNER_ERROR";

    private JSONArray requestArgs;

    private Promise mBarcodeScanPromise;

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {

        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
            if (requestCode == REQUEST_CODE) {
                if (mBarcodeScanPromise != null) {
                    if (resultCode == Activity.RESULT_CANCELED) {
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put(TEXT, "");
                            obj.put(FORMAT, "");
                            obj.put(CANCELLED, true);
                        } catch (JSONException e) {
                            Log.d(LOG_TAG, "This should never happen");
                        }

                        mBarcodeScanPromise.resolve(obj);
                    } else if (resultCode == Activity.RESULT_OK) {
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put(TEXT, intent.getStringExtra("SCAN_RESULT"));
                            obj.put(FORMAT, intent.getStringExtra("SCAN_RESULT_FORMAT"));
                            obj.put(CANCELLED, false);
                        } catch (JSONException e) {
                            Log.d(LOG_TAG, "This should never happen");
                        }

                        mBarcodeScanPromise.resolve(obj);
                    } else {
                        mBarcodeScanPromise.reject(E_RESULT_ERROR, "Unexpected Error");
                    }

                    mBarcodeScanPromise = null;
                }
            }
        }
    };

    public RNLmCodeScanModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @ReactMethod
    public void scan(final ReadableMap args) {
        Activity currentActivity = getCurrentActivity();
        Intent intentScan = new Intent(reactContext, CaptureActivity.class);
        intentScan.setAction(Intents.Scan.ACTION);
        intentScan.addCategory(Intent.CATEGORY_DEFAULT);

        intentScan.putExtra(Intents.Scan.CAMERA_ID, args.hasKey(PREFER_FRONTCAMERA) && args.getBoolean(PREFER_FRONTCAMERA) ? 1 : 0); // If front cam, cam id = 1
        intentScan.putExtra(Intents.Scan.SHOW_FLIP_CAMERA_BUTTON, args.hasKey(SHOW_FLIP_CAMERA_BUTTON) && args.getBoolean(SHOW_FLIP_CAMERA_BUTTON));
        intentScan.putExtra(Intents.Scan.SHOW_TORCH_BUTTON, args.hasKey(SHOW_TORCH_BUTTON) && args.getBoolean(SHOW_TORCH_BUTTON));
        intentScan.putExtra(Intents.Scan.TORCH_ON, args.hasKey(TORCH_ON) && args.getBoolean(TORCH_ON));
        intentScan.putExtra(Intents.Scan.SAVE_HISTORY,args.hasKey(SAVE_HISTORY) && args.getBoolean(SAVE_HISTORY));
        boolean beep = args.hasKey(DISABLE_BEEP) && args.getBoolean(DISABLE_BEEP);
        intentScan.putExtra(Intents.Scan.BEEP_ON_SCAN, !beep);
        if (args.hasKey(RESULTDISPLAY_DURATION)) {
            intentScan.putExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS, "" + args.getInt(RESULTDISPLAY_DURATION));
        }
        if (args.hasKey(FORMATS)) {
            intentScan.putExtra(Intents.Scan.FORMATS, args.getString(FORMATS));
        }
        if (args.hasKey(PROMPT)) {
            intentScan.putExtra(Intents.Scan.PROMPT_MESSAGE, args.getString(PROMPT));
        }
        if (args.hasKey(ORIENTATION)) {
            intentScan.putExtra(Intents.Scan.ORIENTATION_LOCK, args.getString(ORIENTATION));
        }
        // avoid calling other phonegap apps
        intentScan.setPackage(reactContext.getPackageName());
        currentActivity.startActivityForResult(intentScan, REQUEST_CODE);
    }

    @Override
    public String getName() {
        return "RNLmCodeScan";
    }
}