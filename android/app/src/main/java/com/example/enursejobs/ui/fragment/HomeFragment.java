package com.example.enursejobs.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
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

import com.example.enursejobs.DataRepository;
import com.example.enursejobs.Enurse;
import com.example.enursejobs.R;
import com.example.enursejobs.databinding.FragmentHomeBinding;
import com.example.enursejobs.ui.adapter.CaringClickCallBack;
import com.example.enursejobs.ui.adapter.CaringListAdapter;
import com.example.enursejobs.viewmodel.HomeViewModel;

public class HomeFragment extends BaseFragment {

    public static final String TAG = "HomeFragment";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private FragmentHomeBinding mBinding;
    private HomeViewModel homeViewModel;
    private CaringListAdapter mCaringListAdapter;
    private DataRepository mRepository;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false);
        mCaringListAdapter = new CaringListAdapter(mCaringClickCallBack);
        mBinding.caringList.setAdapter(mCaringListAdapter);
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        mRepository = ((Enurse) requireActivity().getApplication()).getRepository();


        return mBinding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }

        mBinding.caringSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                homeViewModel.setQuery(newText);
                return false;
            }
        });

        mRepository.getProfile().observe(getViewLifecycleOwner(), profile ->{
            Log.d(TAG,"inside getprofile(): "+profile.getEmail()+" "+profile.getName()+" "+profile.getId());
            mBinding.setProfileName("Hello "+ profile.getName());
            mBinding.setProfileEmail(profile.getEmail());
            mBinding.setCaringsisLoading(false);
            HomeViewModel.Factory factory = new HomeViewModel.Factory(
                    requireActivity().getApplication(), profile.getId());
            homeViewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);
            homeViewModel.getCarings().observe(getViewLifecycleOwner(), carings -> {
                if (carings != null) {
                    Log.d(TAG,"Carings fetched");
                    mCaringListAdapter.setCaringList(carings);
                } else {
                    mBinding.setCaringsisLoading(true);
                }
                mBinding.executePendingBindings();
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    private final CaringClickCallBack mCaringClickCallBack = caring -> {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment_content_main);
            assert navHostFragment != null;
            NavController navController = navHostFragment.getNavController();

             HomeFragmentDirections.ActionNavHomeToNavCaring action =
                    HomeFragmentDirections.actionNavHomeToNavCaring(caring.getId());
            navController.navigate(action);
        }
    };
}
