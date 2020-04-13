package com.debby.appiumdroid;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Create by wakfu on 2020/4/13
 */
public class GetContactPersonInfoService extends AccessibilityService {
    private List<AccessibilityNodeInfo> parents;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        parents = new ArrayList<>();
        Log.e("GetContactPerson", "onServiceConnected: ");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        CharSequence s = event.getClassName();
        if (s == null) return;
        String className = event.getClassName().toString();
        Log.e("GetContactPerson", "onAccessibilityEvent: " + className);
        if (!className.startsWith("com.alibaba.android.user")) {
            return;
        }
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                switch (className) {
                    case "com.alibaba.android.user.external.list.ExternalListActivity":
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                scanListRecord("com.alibaba.android.rimet:id/list_view");
                            }
                        }, 1000);
                        break;
                    case "com.alibaba.android.user.profile.namecard.UserBusinessProfileActivityV2":
//                        new Timer().schedule(new TimerTask() {
//                            @Override
//                            public void run() {
                        getTextInfo("com.alibaba.android.rimet:id/user_header_full_name");
//                            }
//                        }, 1000);
                        break;
                }
                break;
            default:

                break;
        }

    }

    @Override
    public void onInterrupt() {

    }

    /**
     * 通过ID获取控件，并进行模拟点击
     *
     * @param clickId
     */
    private void inputClick(String clickId) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(clickId);
            for (AccessibilityNodeInfo item : list) {
                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    private AccessibilityNodeInfo info;
    private int record = 1;

    private void scanListRecord(String listId) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(listId);
            for (AccessibilityNodeInfo item : list) {
                info = item;
                gotoDetailPage();
            }
        }
    }


    private void gotoDetailPage() {
        int count = info.getChildCount();
        Log.e("GetContactPerson", "count: " + count);
        if (count > record) {
            AccessibilityNodeInfo childInfo = info.getChild(record);
            childInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    /**
     * 通过ID获取控件，并获取文字
     */
    private void getTextInfo(String clickId) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(clickId);
            for (AccessibilityNodeInfo item : list) {
                Log.e("GetContactPerson", "getTextInfo: " + item.getClassName() + item.getText());
                record++;
            }
        }
    }


}
