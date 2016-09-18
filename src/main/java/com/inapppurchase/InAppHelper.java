package com.inapppurchase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.inapppurchase.data.PurchaseData;
import com.inapppurchase.utills.IabBroadcastReceiver;
import com.inapppurchase.utills.IabHelper;
import com.inapppurchase.utills.IabResult;
import com.inapppurchase.utills.Purchase;


/**
 * Created by AKSHAY_SOOD on 18/09/16.
 */
public class InAppHelper implements IabBroadcastReceiver.IabBroadcastListener {

    private IabHelper mIabHelper;
    private Context mContext;
    private String mKey;

    public InAppHelper(Activity activity, Context context, String key) {
        mIabHelper = new IabHelper(activity, key);
        this.mContext = context;
        this.mKey = key;
        mIabHelper.enableDebugLogging(true);
        mIabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.e("***** Error setting IabHelper", "Result ==> " + result);
                } else if (mIabHelper == null) {
                    return;
                } else if (result.isSuccess()) {
                    Log.e("**** IabHelper Success Setup", "Result ==> " + result);
                    IabBroadcastReceiver broadcastReceiver = new IabBroadcastReceiver(InAppHelper.this);
                    IntentFilter intentFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                    mContext.registerReceiver(broadcastReceiver, intentFilter);
                }
            }
        });

    }


    @Override
    public void receivedBroadcast() {

    }

    /**
     * @param activity        - Activity Reference which will handle activityResult
     * @param requestCode     - Request Code for the request
     * @param productID       -  Sku Id (Product Id which we want to purchase)
     * @param consumeRightNow - Consume right now (Enable user to purchase same item multiple times)
     * @throws IabHelper.IabAsyncInProgressException
     */
    public void purchase(final Activity activity
            , int requestCode
            , String productID
            , final boolean consumeRightNow) throws IabHelper.IabAsyncInProgressException {
        if (mIabHelper != null)
            mIabHelper.launchPurchaseFlow(activity, productID, requestCode, new IabHelper.OnIabPurchaseFinishedListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onIabPurchaseFinished(IabResult result, Purchase info) {
                    PurchaseData.getInstance().setPurchaseResult(result, info);
                    if (!result.isSuccess()) {
                        Log.e("***** Purchase Finish Error ", "Result ==> " + result);
                    } else if (mIabHelper != null && activity != null) {
                        if (consumeRightNow) {
                            try {
                                consume(info);
                            } catch (IabHelper.IabAsyncInProgressException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    if (info != null) {
                        if (consumeRightNow) {
                            try {
                                consume(info);
                            } catch (IabHelper.IabAsyncInProgressException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });

    }

    /**
     * consume the purchased product
     *
     * @param purchase
     * @throws IabHelper.IabAsyncInProgressException
     */
    public void consume(Purchase purchase) throws IabHelper.IabAsyncInProgressException {
        if (mIabHelper != null) {
            mIabHelper.consumeAsync(purchase, new IabHelper.OnConsumeFinishedListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onConsumeFinished(Purchase purchase, IabResult result) {
                    if (result.isSuccess()) {
                        Log.e("***** Product is consumed successfully", "Result ==> " + result);
                    } else {
                        Log.e("***** Error in consuming product", "Result ==> " + result);
                    }
                }
            });
        } else {
            throw new RuntimeException("IabHelper is null");
        }

    }

    /**
     * handles the activity result
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        mIabHelper.handleActivityResult(requestCode, resultCode, data);
    }


}
