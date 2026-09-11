package com.example.enursejobs.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enursejobs.databinding.ItemNurseBinding;
import com.example.enursejobs.db.model.Nurse;

import java.util.List;

public class NurseListAdapter extends RecyclerView.Adapter<NurseListAdapter.NurseViewHolder>
{
    List<? extends Nurse> mNursesList;
    DiffUtil.DiffResult result;

    @Nullable
    private final NurseClickCallBack mNurseClickCallBack;

    public NurseListAdapter(@Nullable NurseClickCallBack mNurseClickCallBack) {
        this.mNurseClickCallBack = mNurseClickCallBack;
        setHasStableIds(true);
    }

    public void setNurseList(final List<? extends Nurse> nursesList) {
        if (mNursesList == null) {
            mNursesList = nursesList;
            notifyItemRangeInserted(0, mNursesList.size());
        }
        else {
            result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mNursesList.size();
                }

                @Override
                public int getNewListSize() {
                    return nursesList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mNursesList.get(oldItemPosition).getId() ==
                            nursesList.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Nurse newmNurse = nursesList.get(newItemPosition);
                    Nurse oldNurse = mNursesList.get(oldItemPosition);
                    return newmNurse.getId() == oldNurse.getId()
                            && TextUtils.equals(newmNurse.getName(), oldNurse.getName())
                            && TextUtils.equals(newmNurse.getEmail(), oldNurse.getEmail())
                            && TextUtils.equals(newmNurse.getPhone(), oldNurse.getPhone())
                            && newmNurse.getGender() == oldNurse.getGender()
                            && newmNurse.getIsResigned() == oldNurse.getIsResigned();
                }
            });
            mNursesList = nursesList;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public NurseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemNurseBinding binding = ItemNurseBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        binding.setNurseCallBack(mNurseClickCallBack);

        return new NurseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NurseViewHolder holder, int position) {
        holder.binding.setNurse(mNursesList.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mNursesList == null ? 0 : mNursesList.size();
    }

    @Override
    public long getItemId(int position) {
        return mNursesList.get(position).getId();
    }

    public static class NurseViewHolder extends RecyclerView.ViewHolder{

        final ItemNurseBinding binding;
        public NurseViewHolder(@NonNull ItemNurseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
