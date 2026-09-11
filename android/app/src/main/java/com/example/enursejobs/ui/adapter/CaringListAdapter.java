package com.example.enursejobs.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enursejobs.databinding.ItemCaringBinding;
import com.example.enursejobs.db.model.Caring;

import java.util.List;
import java.util.Objects;

public class CaringListAdapter extends RecyclerView.Adapter<CaringListAdapter.CaringViewHolder>
{
    List<? extends Caring> mCaringsList;
    DiffUtil.DiffResult result;

    @Nullable
    private final CaringClickCallBack mCaringClickCallBack;

    public CaringListAdapter(@Nullable CaringClickCallBack mCaringClickCallBack) {
        this.mCaringClickCallBack = mCaringClickCallBack;
        setHasStableIds(true);
    }

    public void setCaringList(final List<? extends Caring> caringsList){
        if (mCaringsList == null) {
            mCaringsList = caringsList;
            notifyItemRangeInserted(0, mCaringsList.size());
        }
        else {
            result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mCaringsList.size();
                }

                @Override
                public int getNewListSize() {
                    return caringsList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mCaringsList.get(oldItemPosition).getId() ==
                            caringsList.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Caring newmCaring = caringsList.get(newItemPosition);
                    Caring oldCaring = mCaringsList.get(oldItemPosition);
                    return newmCaring.getId() == oldCaring.getId()
                            && newmCaring.getNurseId() == oldCaring.getNurseId()
                            && TextUtils.equals(newmCaring.getNurseName(), oldCaring.getNurseName())
                            && newmCaring.getCaringtypeId() == oldCaring.getCaringtypeId()
                            && TextUtils.equals(newmCaring.getCaringtypeName(), oldCaring.getCaringtypeName())
                            && newmCaring.getPatientId() == oldCaring.getPatientId()
                            && TextUtils.equals(newmCaring.getPatientName(), oldCaring.getPatientName())
                            && TextUtils.equals(newmCaring.getDescription(), oldCaring.getDescription())
                            && Objects.equals(newmCaring.getTime(), oldCaring.getTime())
                            && TextUtils.equals(newmCaring.getStatus(), oldCaring.getStatus());
                }
            });
            mCaringsList = caringsList;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public CaringViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCaringBinding binding = ItemCaringBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        binding.setCaringCallBack(mCaringClickCallBack);

        return new CaringListAdapter.CaringViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CaringViewHolder holder, int position) {
        holder.binding.setCaring(mCaringsList.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mCaringsList == null ? 0 : mCaringsList.size();
    }

    @Override
    public long getItemId(int position) {
        return mCaringsList.get(position).getId();
    }

    public static class CaringViewHolder extends RecyclerView.ViewHolder{

        final ItemCaringBinding binding;
        public CaringViewHolder(@NonNull ItemCaringBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
