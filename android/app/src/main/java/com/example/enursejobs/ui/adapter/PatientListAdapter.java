package com.example.enursejobs.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enursejobs.databinding.ItemPatientBinding;
import com.example.enursejobs.db.model.Patient;

import java.util.List;

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.PatientViewHolder>
{
    List<? extends Patient> mPatientsList;
    DiffUtil.DiffResult result;

    @Nullable
    private final PatientClickCallBack mPatientClickCallBack;

    public PatientListAdapter(@Nullable PatientClickCallBack mPatientClickCallBack) {
        super();
        this.mPatientClickCallBack = mPatientClickCallBack;
        setHasStableIds(true);
    }

    public void setPatientList(final List<? extends Patient> patientsList) {
        if (mPatientsList == null) {
            mPatientsList = patientsList;
            notifyItemRangeInserted(0, mPatientsList.size());
        }
        else {
            result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mPatientsList.size();
                }

                @Override
                public int getNewListSize() {
                    return patientsList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mPatientsList.get(oldItemPosition).getId() ==
                            patientsList.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Patient newmPatient = patientsList.get(newItemPosition);
                    Patient oldPatient = mPatientsList.get(oldItemPosition);
                    return newmPatient.getId() == oldPatient.getId()
                            && TextUtils.equals(newmPatient.getName(), oldPatient.getName())
                            && TextUtils.equals(newmPatient.getRoomPhotoPath(), oldPatient.getRoomPhotoPath())
                            && newmPatient.getIsStopped() == oldPatient.getIsStopped();
                }
            });
            mPatientsList = patientsList;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPatientBinding binding = ItemPatientBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        binding.setPatientCallBack(mPatientClickCallBack);

        return new PatientListAdapter.PatientViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientListAdapter.PatientViewHolder holder, int position) {
        holder.binding.setPatient(mPatientsList.get(position));
        holder.binding.executePendingBindings();
    }


    @Override
    public int getItemCount() {
        return mPatientsList == null ? 0 : mPatientsList.size();
    }

    @Override
    public long getItemId(int position) {
        return mPatientsList.get(position).getId();
    }

    public static class PatientViewHolder extends RecyclerView.ViewHolder{

        final ItemPatientBinding binding;
        public PatientViewHolder(@NonNull ItemPatientBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
