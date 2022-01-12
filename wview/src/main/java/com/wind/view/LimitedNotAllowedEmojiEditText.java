package com.wind.view;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.wind.toastlib.ToastUtil;
import com.wind.view.util.TextUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 限制长度
 * 不允许输入表情
 */
public class LimitedNotAllowedEmojiEditText extends EditText {
    public interface OnFilterChangedListener {
        /**
         * @param editText
         * @param hasInputHint 已经输入多少提示
         * @param result       结果
         */
        void onFitlerChanged(EditText editText, String hasInputHint, String result);
    }

    private String inputAfterText;      //输入表情前EditText中的文本
    private boolean resetText;          //是否重置了EditText的内容
    private Context mContext;
    private OnFilterChangedListener listener = null;
    private int maxLength = 0;
    private int selectionend = 0;       // 光标位置

    public LimitedNotAllowedEmojiEditText(Context context) {
        super(context);
        this.mContext = context;
        initEditText();
    }

    public LimitedNotAllowedEmojiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initEditText();
    }

    public LimitedNotAllowedEmojiEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initEditText();
    }

    public void setOnFilterChangedListener(int max, OnFilterChangedListener listener) {
        setMaxLength(max);
        this.listener = listener;
    }

    public void setMaxLength(int max) {
        this.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
        this.maxLength = max;
    }

    // 初始化edittext 控件
    private void initEditText() {
        addTextChangedListener(textWatcher);
    }
    public void onDestroy(){
        removeTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int before, int count) {
            if (!resetText) {
                // 这里用s.toString()而不直接用s是因为如果用s，
                // 那么，inputAfterText和s在内存中指向的是同一个地址，s改变了，
                // inputAfterText也就改变了，那么表情过滤就失败了
                inputAfterText = s.toString();
                selectionend = LimitedNotAllowedEmojiEditText.this.getSelectionEnd();
            }

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!resetText) {
                if (count >= 1) {//表情符号的字符长度最小为2
                    CharSequence input = s.subSequence(start, start + count);
                    if (!isQualifiedName(input.toString())) {
                        resetText = true;
                        //是表情符号就将文本还原为输入表情符号之前的内容
                        ToastUtil.showToast((Activity) mContext,"不支持输入Emoji表情符号及其他非法字符");
                        setText(inputAfterText);
                        CharSequence text = getText();
                        if (text instanceof Spannable) {
                            Spannable spanText = (Spannable) text;
                            Selection.setSelection(spanText, text.length());
                        }
                        return;
                    }
                }
                if (maxLength > 0) {
                    int ccc = TextUtil.getWordCount(s.toString());
                    if (ccc > maxLength) {
                        resetText = true;
                        if (TextUtil.getWordCount(inputAfterText)> maxLength) {
                            selectionend = 0;
                            setText("");
                        } else {
                            setText(inputAfterText);
                        }
                    } else {
                        // 正常处理
                        selectionend = -1;
                        doResult(s);
                    }
                } else {
                    // 正常处理
                    selectionend = -1;
                    doResult(s);
                }
            } else {
                resetText = false;
                if (selectionend != -1) {
                    LimitedNotAllowedEmojiEditText.this.setSelection(selectionend);
                    selectionend = -1;
                }
                // 之前的情况
                doResult(s);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };
    private void doResult(CharSequence s) {
        if (null != listener) {
            String hint = null;
            if (maxLength > 0) {
                int ccc = TextUtil.getWordCount(s.toString());
                hint = TextUtil.formatLeftHint(getContext(), ccc, maxLength);
            }
            listener.onFitlerChanged(this, hint, s.toString());
        }
    }

    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    private boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0)
                || (codePoint == 0x9)
                || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    //private String NAME_PATTERN = "^[\\\\s\\u4e00-\\u9fa5a-zA-Z0-9\\s\\p{P}+~$`^=|<>～｀＄＾＋＝｜＜＞￥×£€]+$";
    private String NAME_PATTERN = "^[\\\\s\\u4e00-\\u9fa5a-zA-Z0-9\\p{P}+~$`^=|<>～｀＄＾＋＝｜＜＞￥×£€]+$";
    private String NAME_PATTERN_WITH_BLANK = "^[\\\\s\\u4e00-\\u9fa5a-zA-Z0-9\\s\\p{P}+~$`^=|<>～｀＄＾＋＝｜＜＞￥×£€]+$";

    private boolean mEnabledBlack;
    /**
     * 允许空白字符
     */
    public void setEnableBlank(boolean enableBlank){
        this.mEnabledBlack=enableBlank;
    }
    /**
     * 判断是否正确输入
     *
     * @param name
     * @return
     */
    private boolean isQualifiedName(String name) {
        Pattern p=null;
        if (mEnabledBlack){
            p = Pattern.compile(NAME_PATTERN_WITH_BLANK);
        }else {
            p = Pattern.compile(NAME_PATTERN);
        }
        Matcher m = p.matcher(name);

        return m.matches();
    }


}