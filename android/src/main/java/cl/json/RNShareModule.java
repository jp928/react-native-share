package cl.json;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cl.json.social.EmailShare;
import cl.json.social.FacebookShare;
import cl.json.social.GenericShare;
import cl.json.social.GooglePlusShare;
import cl.json.social.ShareIntent;
import cl.json.social.TwitterShare;
import cl.json.social.WhatsAppShare;

public class RNShareModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private HashMap<String, ShareIntent> sharesExtra = new HashMap<String, ShareIntent>();
    public RNShareModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        sharesExtra.put("generic", new GenericShare(this.reactContext));
        sharesExtra.put("facebook", new FacebookShare(this.reactContext));
        sharesExtra.put("twitter", new TwitterShare(this.reactContext));
        sharesExtra.put("whatsapp",new WhatsAppShare(this.reactContext));
        sharesExtra.put("googleplus",new GooglePlusShare(this.reactContext));
        sharesExtra.put("email",new EmailShare(this.reactContext));
        //  add more customs single intent shares here
    }

    private List<ResolveInfo> getShareApps() {
        List<ResolveInfo> mApps;
        Intent intent = new Intent(Intent.ACTION_SEND, null);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        PackageManager pManager = this.reactContext.getPackageManager();
        mApps = pManager.queryIntentActivities(intent,
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        return mApps;
    }

    @Override
    public String getName() {
    return "RNShare";
    }

    @ReactMethod
    public void open(ReadableMap options, @Nullable Callback failureCallback, @Nullable Callback successCallback) {
        try {
            GenericShare share = new GenericShare(this.reactContext);
            share.open(options);
            successCallback.invoke("OK");
        } catch (ActivityNotFoundException ex) {
            failureCallback.invoke("not_available");
        }
    }

    @ReactMethod
    public void shareSingle(ReadableMap options, @Nullable Callback failureCallback, @Nullable Callback successCallback) {
        System.out.println("SHARE SINGLE METHOD");
        if (ShareIntent.hasValidKey("social", options) ) {
            try{
                this.sharesExtra.get(options.getString("social")).open(options);
                successCallback.invoke("OK");
            } catch (ActivityNotFoundException ex) {
                failureCallback.invoke(ex.getMessage());
            }
        } else {
            failureCallback.invoke("no exists social key");
        }
    }

    @ReactMethod
    public void showShareView(ReadableMap param, final Callback callback) {
        Activity activity = this.reactContext.getCurrentActivity();
        FragmentManager manager = (((FragmentActivity) activity).getSupportFragmentManager());

        List<ResolveInfo> activities = this.getShareApps();

        Typeface fontFace;
        try {
            fontFace = Typeface.createFromAsset(getReactApplicationContext().getAssets(), "fonts/Avenir-Medium.ttf");
        } catch (Exception e) {
            fontFace = Typeface.DEFAULT;
        }
        
        ShareActionListDialogFragment shareList = ShareActionListDialogFragment.newInstance(activities, fontFace);

        shareList.setActionSheetListener(new ShareActionListDialogFragment.ShareActionSheetListener() {
            @Override
            public void onDismiss(ShareActionListDialogFragment actionSheet, boolean isByBtn) {

            }

            @Override
            public void onButtonClicked(ArrayList<ResolveInfo> appList, int index) {

                ResolveInfo appInfo = appList.get(index);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setComponent(new ComponentName(appInfo.activityInfo.packageName, appInfo.activityInfo.name));
                shareIntent.setType("text/plain");

                shareIntent.putExtra(Intent.EXTRA_TEXT, "test");
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getReactApplicationContext().startActivity(shareIntent);
            }
        });


        shareList.show(manager);
    }
}
