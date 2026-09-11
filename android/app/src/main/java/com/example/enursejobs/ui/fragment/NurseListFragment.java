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
import com.example.enursejobs.databinding.FragmentNurseListBinding;
import com.example.enursejobs.ui.adapter.NurseClickCallBack;
import com.example.enursejobs.ui.adapter.NurseListAdapter;
import com.example.enursejobs.viewmodel.NurseListViewModel;

public class NurseListFragment extends BaseFragment{
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final String TAG = "NurseListFragment";
    private NurseListAdapter mNurseListAdapter;
    private NurseListViewModel nurseListViewModel;
    private FragmentNurseListBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentNurseListBinding.inflate(inflater, container, false);
        mNurseListAdapter = new NurseListAdapter(mNurseClickCallBack);
        mBinding.nurseList.setAdapter(mNurseListAdapter);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nurseListViewModel = new ViewModelProvider(this).get(NurseListViewModel.class);

        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        nurseListViewModel.setIndex(index);

        mBinding.nurseSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                nurseListViewModel.setQuery(newText);
                return false;
            }
        });


        nurseListViewModel.getNurses().observe(getViewLifecycleOwner(),nurses -> {
            if (nurses != null) {
                mBinding.setNursesisLoading(false);
                mNurseListAdapter.setNurseList(nurses);
            } else {
                mBinding.setNursesisLoading(true);
            }
            // espresso does not know how to wait for data binding's loop so we execute changes
            // sync.
            mBinding.executePendingBindings();
        });
    }

    private final NurseClickCallBack mNurseClickCallBack = nurse -> {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
//            NurseFragment nurseFragment = NurseFragment.forNurse(nurse.getId());
            NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment_content_main);
            assert navHostFragment != null;
            NavController navController = navHostFragment.getNavController();

            com.example.enursejobs.ui.fragment.NurseListFragmentDirections.ActionNavNursesToNavNurse action =
                    NurseListFragmentDirections.actionNavNursesToNavNurse(nurse.getId());
            navController.navigate(action);
        }
    };
}
