package com.example.enursejobs.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.enursejobs.R;
import com.example.enursejobs.databinding.ActivityLoginBinding;
import com.example.enursejobs.db.EntityLocal.AuthEntity;
import com.example.enursejobs.ui.fragment.HomeFragment;
import com.example.enursejobs.viewmodel.LoginViewModel;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityLoginBinding binding;
    private static final String KEY_PROFILE_EMAIL = "profileEmail";
    private LoginViewModel loginViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        binding.setLifecycleOwner(this);

        binding.login.setOnClickListener(this);

        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    @Override
    public void onClick(@NonNull View v) {
        if(v.getId() == binding.login.getId()){
            if((Objects.requireNonNull(binding.Email.getEditText()).getText().toString().isEmpty()) ||
                    (Objects.requireNonNull(binding.Password.getEditText())).getText().toString().isEmpty()){
                Toast.makeText(this, R.string.Login_EmptyFields_msg,Toast.LENGTH_SHORT).show();
            }
             String email = binding.Email.getEditText().getText().toString();
            loginViewModel.login(new AuthEntity(binding.Email.getEditText().getText().toString(),
                    Objects.requireNonNull(binding.Password.getEditText()).getText().toString()))
                    .observe(this, token ->{
                        if(token != null){
                            Toast.makeText(this, "Welcome "+ email, Toast.LENGTH_SHORT).show();
                            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(myIntent);
                            finish();
                        }else{
                            Toast.makeText(this, R.string.Login_Failed_msg,
                                    Toast.LENGTH_SHORT).show();
                        }
                 });

        }
    }
}
