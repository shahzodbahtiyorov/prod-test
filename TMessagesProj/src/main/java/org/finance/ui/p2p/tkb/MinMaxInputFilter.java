package org.finance.ui.p2p.tkb;

import android.text.InputFilter;
import android.text.Spanned;

import android.text.InputFilter;
import android.text.Spanned;

public class MinMaxInputFilter implements InputFilter {
    private int min, max;

    public MinMaxInputFilter(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            String newInput = dest.toString().substring(0, dstart) + source.toString() + dest.toString().substring(dend);
            int input = Integer.parseInt(newInput);
            if (isInRange(min, max, input)) {
                return null; // Agar kirish to'g'ri bo'lsa hech qanday o'zgarishsiz qaytariladi
            }
        } catch (NumberFormatException e) {
            // Xato qiymat bo'lsa, uni qabul qilmaymiz
        }
        return ""; // Noto'g'ri kiritish bo'lsa, uni rad qilamiz
    }

    private boolean isInRange(int min, int max, int input) {
        return input >= min && input <= max;
    }
}
