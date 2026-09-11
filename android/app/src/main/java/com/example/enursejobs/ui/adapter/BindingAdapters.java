package com.example.enursejobs.ui.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;

import java.util.Locale;

public class BindingAdapters {
    @BindingAdapter("visibleGone")
    public static void showHide(@NonNull View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    @BindingAdapter("android:text")
    public static void setText(@NonNull TextView view, String text) {
        String tmp = String.format(String.valueOf(text), Locale.ENGLISH, "x%d" );
        view.setText(tmp);
    }
}
