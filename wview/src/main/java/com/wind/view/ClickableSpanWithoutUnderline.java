package com.wind.view;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public abstract class ClickableSpanWithoutUnderline extends ClickableSpan {// extend ClickableSpan



    @Override
    public void updateDrawState(TextPaint ds) {// override updateDrawState
        ds.setUnderlineText(false); // set to false to remove underline
    }


}