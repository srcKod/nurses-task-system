package com.example.enursejobs.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enursejobs.databinding.ItemCaringtypeBinding;
import com.example.enursejobs.databinding.ItemNurseBinding;
import com.example.enursejobs.db.model.Caringtype;
import com.example.enursejobs.db.model.Nurse;

import java.util.List;

public class CaringtypeListAdapter extends RecyclerView.Adapter<CaringtypeListAdapter.CaringtypeViewHolder>
{
    List<? extends Caringtype> mCaringtypesList;
    DiffUtil.DiffResult result;

    @Nullable
    private final CaringtypeClickCallBack mCaringtypeClickCallBack;

    public CaringtypeListAdapter(@Nullable CaringtypeClickCallBack mCaringtypeClickCallBack) {
        this.mCaringtypeClickCallBack = mCaringtypeClickCallBack;
        setHasStableIds(true);
    }

    public void setCaringtypeList(final List<? extends Caringtype> caringtypeList) {
        if (mCaringtypesList == null) {
            mCaringtypesList = caringtypeList;
            notifyItemRangeInserted(0, mCaringtypesList.size());
        }
        else {
            result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mCaringtypesList.size();
                }

                @Override
                public int getNewListSize() {
                    return caringtypeList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mCaringtypesList.get(oldItemPosition).getId() ==
                            caringtypeList.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Caringtype newCaringtype = caringtypeList.get(newItemPosition);
                    Caringtype oldCaringtype = mCaringtypesList.get(oldItemPosition);
                    return newCaringtype.getId() == oldCaringtype.getId()
                            && TextUtils.equals(newCaringtype.getName(), oldCaringtype.getName())
                            && TextUtils.equals(newCaringtype.getDescription(), oldCaringtype.getDescription());
                }
            });
            mCaringtypesList = caringtypeList;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public CaringtypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCaringtypeBinding binding = ItemCaringtypeBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        binding.setCaringtypeCallBack(mCaringtypeClickCallBack);

        return new CaringtypeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CaringtypeViewHolder holder, int position) {
        holder.binding.setCaringtype(mCaringtypesList.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public long getItemId(int position) {
        return mCaringtypesList.get(position).getId();
    }
    @Override
    public int getItemCount() {
        return mCaringtypesList == null ? 0 : mCaringtypesList.size();
    }

    public static class CaringtypeViewHolder extends RecyclerView.ViewHolder{

        ItemCaringtypeBinding binding;
        public CaringtypeViewHolder(@NonNull ItemCaringtypeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
