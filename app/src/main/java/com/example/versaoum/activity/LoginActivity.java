package com.example.versaoum.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.versaoum.R;
import com.example.versaoum.helper.ConfiguracaoFirebase;
import com.example.versaoum.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Button botaoLogin;
    private ProgressBar progressBar;
    private FirebaseAuth autenticacao;

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inicializarComponentes();
        progressBar.setVisibility(View.GONE);

        botaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                if(!textoEmail.isEmpty()){
                    if(!textoSenha.isEmpty()){

                        usuario = new Usuario();
                        usuario.setEmail(textoEmail);
                        usuario.setSenha(textoSenha);
                        validarLogin(usuario);

                    }else{
                        Toast.makeText(LoginActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Preencha o e-mail!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void validarLogin(Usuario usuario){

        progressBar.setVisibility(View.VISIBLE);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    abrirFeed();
                    Toast.makeText(LoginActivity.this, "Login feito", Toast.LENGTH_SHORT).show();
                }else{
                    String erroExcecao = "";
                    try {
                        throw task.getException();
                    }catch(FirebaseAuthInvalidUserException e){
                        erroExcecao = "Usuário não está cadastrado!";
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        erroExcecao = "E-mail e senha não correspondem a um usuário cadastrado!";
                    }catch(Exception e){
                        erroExcecao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, erroExcecao, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void inicializarComponentes(){
        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);
        botaoLogin = findViewById(R.id.buttonLogin);
        progressBar = findViewById(R.id.progressBarLogin);
    }

    public void abrirFeed(){
        startActivity(new Intent(this, FeedActivity.class));
        finish();
    }
}
