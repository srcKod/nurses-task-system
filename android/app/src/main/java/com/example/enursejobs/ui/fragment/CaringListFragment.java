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
import com.example.enursejobs.databinding.FragmentCaringListBinding;
import com.example.enursejobs.ui.adapter.CaringClickCallBack;
import com.example.enursejobs.ui.adapter.CaringListAdapter;
import com.example.enursejobs.viewmodel.CaringListViewModel;

public class CaringListFragment extends BaseFragment{
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final String TAG = "CaringListFragment";
    private CaringListAdapter mCaringListAdapter;
    private CaringListViewModel caringListViewModel;
    private FragmentCaringListBinding mBinding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentCaringListBinding.inflate(inflater, container, false);
        mCaringListAdapter = new CaringListAdapter(mCaringClickCallBack);
        mBinding.caringList.setAdapter(mCaringListAdapter);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        caringListViewModel = new ViewModelProvider(this).get(CaringListViewModel.class);

        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        caringListViewModel.setIndex(index);

        mBinding.caringSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                caringListViewModel.setQuery(newText);
                return false;
            }
        });


        caringListViewModel.getCarings().observe(getViewLifecycleOwner(),carings -> {
            if (carings != null) {
                mBinding.setCaringsisLoading(false);
                mCaringListAdapter.setCaringList(carings);
            } else {
                mBinding.setCaringsisLoading(true);
            }
            // espresso does not know how to wait for data binding's loop so we execute changes
            // sync.
            mBinding.executePendingBindings();
        });
    }

    private final CaringClickCallBack mCaringClickCallBack = caring -> {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
//            NurseFragment nurseFragment = NurseFragment.forNurse(nurse.getId());
            NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment_content_main);
            assert navHostFragment != null;
            NavController navController = navHostFragment.getNavController();

            navController.navigate(CaringListFragmentDirections.actionCaringListFragmentToNavCaring(caring.getId()));
        }
    };
}
