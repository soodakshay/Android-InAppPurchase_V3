package com.inapppurchase.data;

import com.inapppurchase.utills.IabResult;
import com.inapppurchase.utills.Purchase;

/**
 * Created by AKSHAY_SOOD on 18/09/16.
 */
public class PurchaseData {
    public static PurchaseData purchaseData;
    public PurchaseResult purchaseResult;

    /**
     * interface that will give purchase result
     */
    interface PurchaseResult {
        public void onPurchaseResult(IabResult iabResult, Purchase purchase);
    }

    public static PurchaseData getInstance() {
        if (purchaseData == null) {
            purchaseData = new PurchaseData();
        }
        return purchaseData;
    }

    /**
     * set listener
     *
     * @param purchaseResult
     */
    public void setPurchaseListener(PurchaseResult purchaseResult) {
        if (this.purchaseResult == null) {
            this.purchaseResult = purchaseResult;
        }
    }

    /**
     * set purchase result
     *
     * @param iabResult
     * @param purchase
     */
    public void setPurchaseResult(IabResult iabResult, Purchase purchase) {
        if (purchaseResult != null) {
            purchaseResult.onPurchaseResult(iabResult, purchase);
        }
    }
}
