package com.example.enursejobs.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.enursejobs.R;
import com.example.enursejobs.databinding.FragmentCaringtypeBinding;
import com.example.enursejobs.db.EntityLocal.CaringtypeEntity;
import com.example.enursejobs.viewmodel.CaringtypeViewModel;

import java.util.Objects;

public class CaringtypeFragment extends BaseFragment implements View.OnClickListener{

    private FragmentCaringtypeBinding mBinding;
    private CaringtypeViewModel caringtypeViewModel;
    private static final String KEY_CARINGTYPE_ID = "caringtypeid";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        CaringtypeViewModel.Factory factory = new CaringtypeViewModel.Factory(
                requireActivity().getApplication(), requireArguments().getInt(KEY_CARINGTYPE_ID));
        caringtypeViewModel = new ViewModelProvider(this, factory).get(CaringtypeViewModel.class);


        mBinding = FragmentCaringtypeBinding.inflate(inflater, container, false);
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        caringtypeViewModel.getCaringtype().observe(getViewLifecycleOwner(), caringtype ->{
            if (caringtype != null) {
                mBinding.setCaringtype(caringtype);
            }
            mBinding.executePendingBindings();
        });

        View root = mBinding.getRoot();
        mBinding.deleteCaringtypeBtn.setOnClickListener(this);
        mBinding.updateCaringtypeBtn.setOnClickListener(this);
        final TextView textView = mBinding.textCaringtype;
        caringtypeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onClick(@NonNull View v) {
        if(v.getId() == mBinding.deleteCaringtypeBtn.getId()){

            assert this.getArguments() != null;

            caringtypeViewModel.removeCaringtype(this.getArguments().getInt(KEY_CARINGTYPE_ID))
                    .observe(getViewLifecycleOwner(), result-> {
                        if(result != 0){
                          Toast.makeText(requireActivity(), R.string.success_Deleted_msg, Toast.LENGTH_SHORT).show();
                        }else{
                          Toast.makeText(requireContext(), R.string.failed_Deleted_msg, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else if(v.getId() == mBinding.updateCaringtypeBtn.getId()){
            if(this.getArguments()==null){
                return;
            }else if(Objects.requireNonNull(mBinding.caringtypeName.getEditText()).getText().toString().equals("")){
                mBinding.caringtypeName.setError(getString(R.string.Empty_Field_error_msg));
                return;
            }else if(Objects.requireNonNull(mBinding.caringtypeDescription.getEditText()).getText().toString().equals("")){
                mBinding.caringtypeDescription.setError(getString(R.string.Empty_Field_error_msg));
                return;
            }

            CaringtypeEntity caringtypeEntity = new CaringtypeEntity();
            assert this.getArguments() != null;
            caringtypeEntity.setId(this.getArguments().getInt(KEY_CARINGTYPE_ID));
            caringtypeEntity.setName(Objects.requireNonNull(mBinding.caringtypeName.getEditText()).getText().toString());
            caringtypeEntity.setDescription(Objects.requireNonNull(mBinding.caringtypeDescription.getEditText()).getText().toString());

            MediatorLiveData<Integer> mResult = new MediatorLiveData<>();
            mResult.addSource(caringtypeViewModel.editCaringtype(caringtypeEntity), result -> {
                if(result != 0){
                    Toast.makeText(requireActivity(),
                            R.string.success_update_msg, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(requireActivity(),
                            R.string.Failed_update_msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
