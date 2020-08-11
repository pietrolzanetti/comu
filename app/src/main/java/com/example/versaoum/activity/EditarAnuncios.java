package com.example.versaoum.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.versaoum.PopUpClass;
import com.example.versaoum.R;
import com.example.versaoum.helper.ConfiguracaoFirebase;
import com.example.versaoum.helper.UsuarioFirebase;
import com.example.versaoum.model.Anuncio;
import com.example.versaoum.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class EditarAnuncios extends AppCompatActivity {

    private ImageView imageFoto1;
    private TextView textTitulo, textDescricao, textRecompensa, textData, textEndereco, textCriadorAnuncio, textQuantFotos;
    private Button buttonExcluir, buttonConversar;
    private CircleImageView imageCriadorAnuncio;
    private LinearLayout layoutPerfilDestinatarioAnuncio;
    private RoundedImageView imageFoto2, imageFoto3, imageFoto4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_anuncios);

        String idUsuario = UsuarioFirebase.getIdentificadorUsuario();
        textTitulo=findViewById(R.id.textTituloEdit);
        textDescricao=findViewById(R.id.textDescricaoEdit);
        imageFoto1=findViewById(R.id.imageFotoEdit);
        imageFoto2=findViewById(R.id.imageFotoEdit1);
        imageFoto3=findViewById(R.id.imageFotoEdit2);
        imageFoto4=findViewById(R.id.imageFotoEdit3);
        buttonExcluir=findViewById(R.id.buttonExcluirEdit);
        textRecompensa = findViewById(R.id.textRecompensaAnuncio);
        textData = findViewById(R.id.textaDataAnuncio);
        textEndereco = findViewById(R.id.textEnderecoAnuncio);
        textCriadorAnuncio = findViewById(R.id.textCriadorAnuncio);
        imageCriadorAnuncio = findViewById(R.id.imageCriadorAnuncio);
        layoutPerfilDestinatarioAnuncio = findViewById(R.id.layoutPerfilDestinatarioAnuncio);
        textQuantFotos = findViewById(R.id.textQuantFotos);

        findViewById(R.id.imageVoltaEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Bundle extras = getIntent().getExtras();
        if(extras!=null){

            final Anuncio anuncio= (Anuncio) extras.getSerializable("anunciozz");


            //load fotos anuncio
            final List<String> urlFotos = anuncio.getFotos();
            int tamanho = urlFotos.size();
            textQuantFotos.setText("+ " + tamanho);
            int falta = (3-tamanho);
            for(int i=0; i<tamanho;i++){
                urlFotos.get(i);
                if(i==0){
                    Picasso.get().load(urlFotos.get(i)).into(imageFoto1);
                    Picasso.get().load(urlFotos.get(i)).into(imageFoto2);
                }else if(i==1){
                    Picasso.get().load(urlFotos.get(i)).into(imageFoto3);
                }else if(i==2){
                    Picasso.get().load(urlFotos.get(i)).into(imageFoto4);
                }
            }
            for(int i = 0; i<falta;i++){
                if(i==0){
                    imageFoto4.setVisibility(View.GONE);
                }else if(i==1){
                    imageFoto3.setVisibility(View.GONE);
                }
            }


            // Create a button handler and call the dialog box display method in it
            imageFoto2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopUpClass popUpClass = new PopUpClass();
                    popUpClass.showPopupWindow(v, urlFotos, 0);
                }
            });
            imageFoto3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopUpClass popUpClass = new PopUpClass();
                    popUpClass.showPopupWindow(v, urlFotos, 1);
                }
            });
            imageFoto4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopUpClass popUpClass = new PopUpClass();
                    popUpClass.showPopupWindow(v, urlFotos, 2);
                }
            });

            //pegar dados criador do anuncio
            final DatabaseReference databaseRef = ConfiguracaoFirebase.getFireBase();
            DatabaseReference referenciaFinal = databaseRef.child("usuarios").child(anuncio.getIdCriador());
            referenciaFinal.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Usuario usuarioSnapshot = dataSnapshot.getValue(Usuario.class);
                    textCriadorAnuncio.setText(usuarioSnapshot.getNome());
                    String foto = usuarioSnapshot.getFoto();
                    if(foto!=null){
                        Uri url = Uri.parse(usuarioSnapshot.getFoto());
                        Glide.with(EditarAnuncios.this).load(url).into(imageCriadorAnuncio);
                    }else{
                        imageCriadorAnuncio.setImageResource(R.drawable.padrao);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //abrir perfil
            layoutPerfilDestinatarioAnuncio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(getApplicationContext(), PerfilActivity.class);
                    i.putExtra("usuarioDestinatarioId", anuncio.getIdCriador());
                    startActivity(i);
                }
            });


            //setar titulo descricao...
            textTitulo.setText(anuncio.getTitulo());
            textDescricao.setText(anuncio.getDescricao());
            textData.setText(anuncio.getData());
            textEndereco.setText(anuncio.getEnderecoVisivel());
            textRecompensa.setText(anuncio.getRecompensa());

            //botao excluir
            findViewById(R.id.buttonExcluirEdit).setOnClickListener(new View.OnClickListener(){
                @Override
                 public void onClick(View v){
                    anuncio.remover();
                    finish();
                }
            });

        }
    }
}
