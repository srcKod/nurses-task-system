package com.example.enursejobs.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.enursejobs.databinding.FragmentCaringBinding;
import com.example.enursejobs.viewmodel.CaringViewModel;

public class CaringFragment extends BaseFragment implements View.OnClickListener{
    private FragmentCaringBinding mBinding;
    private CaringViewModel caringViewModel;
    private static final String KEY_Caring_ID = "caringid";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CaringViewModel.Factory factory = new CaringViewModel.Factory(
                requireActivity().getApplication(), requireArguments().getInt(KEY_Caring_ID));
        caringViewModel = new ViewModelProvider(this, factory).get(CaringViewModel.class);

        mBinding = FragmentCaringBinding.inflate(inflater, container, false);
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        final TextView textView = mBinding.textCaring;

        View root = mBinding.getRoot();

        caringViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        caringViewModel.getCaring().observe(getViewLifecycleOwner(), caring ->{
            if (caring != null) {
                mBinding.setCaring(caring);
            }
            mBinding.executePendingBindings();
        });

        return root;
    }

    @Override
    public void onClick(View v) {

    }
}
