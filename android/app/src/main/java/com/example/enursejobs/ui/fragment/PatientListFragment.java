package com.example.enursejobs.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.enursejobs.R;
import com.example.enursejobs.databinding.FragmentPatientListBinding;
import com.example.enursejobs.ui.adapter.PatientClickCallBack;
import com.example.enursejobs.ui.adapter.PatientListAdapter;
import com.example.enursejobs.viewmodel.PatientListViewModel;

public class PatientListFragment extends BaseFragment{
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final String TAG = "PatientListFragment";
    private PatientListAdapter mPatientListAdapter;
    private PatientListViewModel patientListViewModel;
    private FragmentPatientListBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentPatientListBinding.inflate(inflater, container, false);
        mPatientListAdapter = new PatientListAdapter(mPatientClickCallBack);
        mBinding.patientList.setAdapter(mPatientListAdapter);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        patientListViewModel = new ViewModelProvider(this).get(PatientListViewModel.class);

        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        patientListViewModel.setIndex(index);

        mBinding.patientSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                patientListViewModel.setQuery(newText);
                return false;
            }
        });


        patientListViewModel.getPatients().observe(getViewLifecycleOwner(),patients -> {
            if (patients != null) {
                mBinding.setPatientsisLoading(false);
                mPatientListAdapter.setPatientList(patients);
            } else {
                mBinding.setPatientsisLoading(true);
            }
            // espresso does not know how to wait for data binding's loop so we execute changes
            // sync.
            mBinding.executePendingBindings();
        });
    }

    private final PatientClickCallBack mPatientClickCallBack = patient -> {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment_content_main);
            assert navHostFragment != null;
            NavController navController = navHostFragment.getNavController();

            navController.navigate(PatientListFragmentDirections.actionNavPatientsToNavPatient(patient.getId()));
        }
    };
}
