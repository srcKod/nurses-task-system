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
import com.example.enursejobs.databinding.FragmentCaringtypeListBinding;
import com.example.enursejobs.db.model.Caringtype;
import com.example.enursejobs.ui.adapter.CaringtypeClickCallBack;
import com.example.enursejobs.ui.adapter.CaringtypeListAdapter;
import com.example.enursejobs.viewmodel.CaringtypeListViewModel;
import com.example.enursejobs.viewmodel.NurseListViewModel;

public class CaringtypeListFragment extends BaseFragment{
    public static final String TAG = "CaringtypeListFragment";
    private static final String ARG_SECTION_NUMBER = "section_number";

    private CaringtypeListAdapter mCaringtypeListAdapter;
    private CaringtypeListViewModel caringtypeListViewModel;
    private FragmentCaringtypeListBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentCaringtypeListBinding.inflate(inflater, container, false);
        mCaringtypeListAdapter = new CaringtypeListAdapter(mCaringtypeClickCallBack);
        mBinding.CaringtypeList.setAdapter(mCaringtypeListAdapter);
        return mBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        caringtypeListViewModel = new ViewModelProvider(this).get(CaringtypeListViewModel.class);

        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        caringtypeListViewModel.setIndex(index);

        mBinding.CaringtypeSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                caringtypeListViewModel.setQuery(newText);
                return false;
            }
        });


        caringtypeListViewModel.getCaringtypes().observe(getViewLifecycleOwner(),caringtypes -> {
            if (caringtypes != null) {
                mBinding.setCaringtypesisLoading(false);
                mCaringtypeListAdapter.setCaringtypeList(caringtypes);
            } else {
                mBinding.setCaringtypesisLoading(true);
            }
            // espresso does not know how to wait for data binding's loop so we execute changes
            // sync.
            mBinding.executePendingBindings();
        });
    }

    private final CaringtypeClickCallBack mCaringtypeClickCallBack = caringtype -> {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment_content_main);
            assert navHostFragment != null;
            NavController navController = navHostFragment.getNavController();

            CaringtypeListFragmentDirections.ActionNavCaringtypesToNavCaringtype action =
                    CaringtypeListFragmentDirections.actionNavCaringtypesToNavCaringtype(caringtype.getId());
            navController.navigate(action);
        }
    };
}
