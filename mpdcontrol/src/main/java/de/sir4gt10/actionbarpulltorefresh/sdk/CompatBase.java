package de.sir4gt10.actionbarpulltorefresh.sdk;

import android.view.View;

class CompatBase {

    static void setAlpha(View view, float alpha) {
        // NO-OP
    }

    static void postOnAnimation(View view, Runnable runnable) {
        view.postDelayed(runnable, 10l);
    }

}
