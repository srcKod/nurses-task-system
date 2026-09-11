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
import com.example.enursejobs.databinding.FragmentPatientBinding;
import com.example.enursejobs.db.EntityLocal.PatientEntity;
import com.example.enursejobs.viewmodel.PatientViewModel;

import java.util.Objects;

public class PatientFragment extends BaseFragment implements View.OnClickListener{

    private FragmentPatientBinding mBinding;
    private PatientViewModel patientViewModel;
    private static final String KEY_PATIENT_ID = "patientid";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PatientViewModel.Factory factory = new PatientViewModel.Factory(
                requireActivity().getApplication(), requireArguments().getInt(KEY_PATIENT_ID));
        patientViewModel = new ViewModelProvider(this, factory).get(PatientViewModel.class);

        mBinding = FragmentPatientBinding.inflate(inflater, container, false);
        mBinding.setLifecycleOwner(getViewLifecycleOwner());
        mBinding.deletePatientBtn.setOnClickListener(this);
        mBinding.updatePatientBtn.setOnClickListener(this);
        final TextView textView = mBinding.textPatient;

        View root = mBinding.getRoot();

        patientViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        patientViewModel.getPatient().observe(getViewLifecycleOwner(), patient ->{
            if (patient != null) {
                mBinding.setPatient(patient);
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
        if(v.getId() == mBinding.deletePatientBtn.getId()){

            assert this.getArguments() != null;

            patientViewModel.removePatient(this.getArguments().getInt(KEY_PATIENT_ID))
                    .observe(getViewLifecycleOwner(), result-> {
                        if(result){
                            Toast.makeText(requireActivity(), R.string.success_Deleted_msg, Toast.LENGTH_SHORT).show();
                            NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                                    .findFragmentById(R.id.nav_host_fragment_content_main);
                            assert navHostFragment != null;
                            NavController navController = navHostFragment.getNavController();

                            navController.navigate(PatientFragmentDirections.actionNavPatientToNavPatients());

                        }else{
                            Toast.makeText(requireActivity(), R.string.failed_Deleted_msg, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else if(v.getId() == mBinding.updatePatientBtn.getId()){
            if(this.getArguments()==null){
                return;
            }else if(Objects.requireNonNull(mBinding.patientName.getEditText()).getText().toString().equals("")){
                mBinding.patientName.setError(getString(R.string.Empty_Field_error_msg));
                return;
            }else if(Objects.requireNonNull(mBinding.patientRoomPhPathTv.getEditText()).getText().toString().equals("")){
                mBinding.patientRoomPhPathTv.setError(getString(R.string.Empty_Field_error_msg));
                return;
            }else if(Objects.requireNonNull(mBinding.patientIsStopped.getEditText()).getText().toString().equals("")){
                mBinding.patientIsStopped.setError(getString(R.string.Empty_Field_error_msg));
                return;
            }

            PatientEntity patientEntity = new PatientEntity();
            assert this.getArguments() != null;
            patientEntity.setId(this.getArguments().getInt(KEY_PATIENT_ID));
            patientEntity.setName(Objects.requireNonNull(mBinding.patientName.getEditText()).getText().toString());
            patientEntity.setRoomPhotoPath(Objects.requireNonNull(mBinding.patientRoomPhPathTv.getEditText()).getText().toString());
            patientEntity.setIsStopped(Integer.parseInt(Objects.requireNonNull(
                    mBinding.patientIsStopped.getEditText()).getText().toString()));

            patientViewModel.editPatient(patientEntity).observe(getViewLifecycleOwner(), result-> {
                if(result != 0){
                    Toast.makeText(requireActivity(), R.string.success_update_msg, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(requireActivity(), R.string.Failed_update_msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
