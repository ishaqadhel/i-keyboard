package com.example.ikeyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class MainKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView kv;
    private Keyboard qwertyKeyboard;
    private Keyboard extraKeyboard;
    private Keyboard symbolKeyboard;
    private Keyboard numpadKeyboard;

    private boolean isCaps = false;
    private boolean isSymbol = false;
    private boolean isSymbolExtra = false;
    private boolean isNumpad = false;

    @Override
    public View onCreateInputView() {
        kv = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard, null);
        qwertyKeyboard = new Keyboard(this, R.xml.qwerty);
        symbolKeyboard = new Keyboard(this, R.xml.symbol);
        extraKeyboard = new Keyboard(this, R.xml.symbolextra);
        numpadKeyboard = new Keyboard(this, R.xml.numpad);
        kv.setKeyboard(qwertyKeyboard);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }

    @Override
    public void onPress(int i) {
        InputConnection ic = getCurrentInputConnection();
        if (i == Keyboard.KEYCODE_DONE) {
            final int options = this.getCurrentInputEditorInfo().imeOptions;
            final int actionId = options & EditorInfo.IME_MASK_ACTION;
            switch (actionId) {
                case EditorInfo.IME_ACTION_NEXT:
                    sendDefaultEditorAction(true);
                    break;
                default:
                    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
            }
        }
    }

    @Override
    public void onRelease(int i) {
        InputConnection ic = getCurrentInputConnection();
        if (i == Keyboard.KEYCODE_DONE) {
            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
        }
    }

    @Override
    public void onKey(int i, int[] ints) {
        InputConnection ic = getCurrentInputConnection();
        playClick(i);
        switch (i)
        {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                isCaps = !isCaps;
                qwertyKeyboard.setShifted(isCaps);
                kv.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                break;
            case Keyboard.KEYCODE_MODE_CHANGE:
                if (!isSymbol) {
                    kv.setKeyboard(symbolKeyboard);
                } else {
                    kv.setKeyboard(qwertyKeyboard);
                    isSymbolExtra = false;
                }
                isSymbol = !isSymbol;
                break;
            case -7:
                if (!isSymbolExtra) {
                    kv.setKeyboard(extraKeyboard);
                } else {
                    kv.setKeyboard(symbolKeyboard);
                }
                isSymbolExtra = !isSymbolExtra;
                break;
            case -8:
                isNumpad = !isNumpad;
                kv.setKeyboard(numpadKeyboard);
                isSymbolExtra = false;
            default:
                char code = (char)i;
                if (Character.isLetter(code) && isCaps)
                    code = Character.toUpperCase(code);
                ic.commitText(String.valueOf(code), 1);
        }
    }

    private void playClick(int i) {
        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        switch(i)
        {
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default: am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}