package com.example.versaoum.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.versaoum.R;
import com.example.versaoum.helper.ConfiguracaoFirebase;
import com.example.versaoum.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class PerfilActivity extends AppCompatActivity {

    private DatabaseReference database;
    private DatabaseReference usuarioRef;
    private Usuario usuarioDestinatario;
    private CircleImageView imagePerfilDestinatario;
    private TextView nomePerfilDestinatario, descricaoPerfilDestinatario;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        //config iniciais
        database = ConfiguracaoFirebase.getFireBase();
        imagePerfilDestinatario = findViewById(R.id.imagePerfilDestinatario);
        nomePerfilDestinatario = findViewById(R.id.nomePerfilDestinatario);
        descricaoPerfilDestinatario = findViewById(R.id.descricaoPerfilDestinatario);
        dialog = new SpotsDialog.Builder().setTheme(R.style.Custom).setContext(this).setCancelable(false).build();
        dialog.show();

        //intent recuperar usuario
        Bundle extras = getIntent().getExtras();
        if(extras!=null){

            String idUsuarioDestinatario = extras.getString("usuarioDestinatarioId");

            usuarioRef = database.child("usuarios").child(idUsuarioDestinatario);

            usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    usuarioDestinatario = dataSnapshot.getValue(Usuario.class);
                    Log.i("opa", usuarioDestinatario.getNome());
                    //adicionar dados

                    String urlFotosDestinatario = usuarioDestinatario.getFoto();
                    Picasso.get().load(urlFotosDestinatario).into(imagePerfilDestinatario, new Callback() {
                        @Override
                        public void onSuccess() {
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                    nomePerfilDestinatario.setText(usuarioDestinatario.getNome());
                    descricaoPerfilDestinatario.setText(usuarioDestinatario.getDescricao());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


    }
}
