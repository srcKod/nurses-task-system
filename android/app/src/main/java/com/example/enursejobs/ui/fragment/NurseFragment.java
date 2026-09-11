package com.example.enursejobs.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.enursejobs.R;
import com.example.enursejobs.databinding.FragmentNurseBinding;
import com.example.enursejobs.db.EntityLocal.NurseEntity;
import com.example.enursejobs.viewmodel.NurseViewModel;

import java.util.Objects;

public class NurseFragment extends BaseFragment implements View.OnClickListener{

    private FragmentNurseBinding mBinding;
    private NurseViewModel nurseViewModel;
    private static final String KEY_NURSE_ID = "nurseid";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NurseViewModel.Factory factory = new NurseViewModel.Factory(
                requireActivity().getApplication(), requireArguments().getInt(KEY_NURSE_ID));
        nurseViewModel = new ViewModelProvider(this, factory).get(NurseViewModel.class);

        mBinding = FragmentNurseBinding.inflate(inflater, container, false);
        mBinding.setLifecycleOwner(getViewLifecycleOwner());
        mBinding.deleteNurseBtn.setOnClickListener(this);
        mBinding.updateNurseBtn.setOnClickListener(this);
        final TextView textView = mBinding.textNurse;

        View root = mBinding.getRoot();

        nurseViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        nurseViewModel.getNurse().observe(getViewLifecycleOwner(), nurse ->{
            if (nurse != null) {
                mBinding.setNurse(nurse);
            }
            mBinding.executePendingBindings();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onClick(@NonNull View v) {
        if(v.getId() == mBinding.deleteNurseBtn.getId()){

            assert this.getArguments() != null;

            nurseViewModel.removeNurse(this.getArguments().getInt(KEY_NURSE_ID))
            .observe(getViewLifecycleOwner(), result-> {
                if(result){
                        Toast.makeText(requireActivity(), R.string.success_Deleted_msg, Toast.LENGTH_SHORT).show();
                    NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                            .findFragmentById(R.id.nav_host_fragment_content_main);
                    assert navHostFragment != null;
                    NavController navController = navHostFragment.getNavController();

                    navController.navigate(NurseFragmentDirections.actionNavNurseToNavNurses());

                }else{
                        Toast.makeText(requireActivity(), R.string.failed_Deleted_msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if(v.getId() == mBinding.updateNurseBtn.getId()){
            if(this.getArguments()==null){
                return;
            }else if(Objects.requireNonNull(mBinding.nurseName.getEditText()).getText().toString().equals("")){
                mBinding.nurseName.setError(getString(R.string.Empty_Field_error_msg));
                return;
            }else if(Objects.requireNonNull(mBinding.nurseEmail.getEditText()).getText().toString().equals("")){
                mBinding.nurseEmail.setError(getString(R.string.Empty_Field_error_msg));
                return;
            }else if(Objects.requireNonNull(mBinding.nursePhone.getEditText()).getText().toString().equals("")){
                mBinding.nursePhone.setError(getString(R.string.Empty_Field_error_msg));
                return;
            }

            NurseEntity nurseEntity = new NurseEntity();
            assert this.getArguments() != null;
            nurseEntity.setId(this.getArguments().getInt(KEY_NURSE_ID));
            nurseEntity.setName(Objects.requireNonNull(mBinding.nurseName.getEditText()).getText().toString());
            nurseEntity.setEmail(Objects.requireNonNull(mBinding.nurseEmail.getEditText()).getText().toString());
            nurseEntity.setPhone(Objects.requireNonNull(mBinding.nursePhone.getEditText()).getText().toString());
            nurseEntity.setPassword(Objects.requireNonNull(mBinding.nursePassword.getEditText()).getText().toString());

           nurseViewModel.editNurse(nurseEntity).observe(getViewLifecycleOwner(), result-> {
               if(result != 0){
                   Toast.makeText(requireActivity(), R.string.success_update_msg, Toast.LENGTH_SHORT).show();
               }else{
                   Toast.makeText(requireActivity(), R.string.Failed_update_msg, Toast.LENGTH_SHORT).show();
               }
           });
        }
    }
}
