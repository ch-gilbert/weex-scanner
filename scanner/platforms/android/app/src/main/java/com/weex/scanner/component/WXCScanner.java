package com.weex.scanner.component;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.annotation.Component;
import com.taobao.weex.dom.WXDomObject;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXVContainer;
import com.taobao.weex.utils.WXLogUtils;
import com.weex.scanner.R;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WXCScanner extends WXComponent<DecoratedBarcodeView> {

    private BeepManager beepManager;
    private String lastText = "";

    private final static String EVENT_DECODED = "decoded";

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            // Do something with the scanned QR code.
            if(result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }
            lastText = result.getText();
            Log.d("decode result ", lastText);
            Map<String, Object> params = new HashMap<>(1);
            params.put("value", lastText);
            fireEvent(EVENT_DECODED, params);

            beepManager.playBeepSoundAndVibrate();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    public WXCScanner(WXSDKInstance instance, WXDomObject dom, WXVContainer parent) {
        super(instance, dom, parent);
    }

    @Override
    protected DecoratedBarcodeView initComponentHostView(@NonNull Context context) {
        DecoratedBarcodeView barcodeView = (DecoratedBarcodeView) LayoutInflater.from(context).inflate(R.layout.barcode_view, null);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));

        if (context instanceof Activity) {
            Activity activity = (Activity)context;

            beepManager = new BeepManager(activity);

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA}, 0);
            }
        }

        return barcodeView;
    }

    @Override
    public void onActivityPause() {
        getHostView().pause();
    }

    @Override
    public void onActivityResume() {
        getHostView().resume();
    }

    @Override
    protected void onHostViewInitialized(DecoratedBarcodeView hostView) {
        super.onHostViewInitialized(hostView);

        WXLogUtils.d("start decoding");

        hostView.resume();
        hostView.decodeContinuous(callback);
    }

}