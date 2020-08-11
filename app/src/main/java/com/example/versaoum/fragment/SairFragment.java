package com.example.versaoum.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.versaoum.R;
import com.example.versaoum.activity.FeedActivity;
import com.example.versaoum.activity.LoginActivity;
import com.example.versaoum.activity.MainActivity;
import com.example.versaoum.helper.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 */
public class SairFragment extends Fragment {

    private FirebaseAuth autenticacao;


    public SairFragment() {
        //requerido construtor vazio
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sair, container, false);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        deslogarUsuario();
        getActivity().finish();

        return view;
    }

    public void deslogarUsuario(){

        try {
            autenticacao.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

