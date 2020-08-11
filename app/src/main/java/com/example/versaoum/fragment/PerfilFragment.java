package com.example.versaoum.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.versaoum.R;
import com.example.versaoum.activity.FeedActivity;
import com.example.versaoum.activity.LoginActivity;
import com.example.versaoum.helper.ConfiguracaoFirebase;
import com.example.versaoum.helper.UsuarioFirebase;
import com.example.versaoum.model.Usuario;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.example.versaoum.helper.UsuarioFirebase.getIdentificadorUsuario;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {

    public PerfilFragment() {
        // Required empty public constructor
    }

    private static final int SELECAO_GALERIA = 200;
    private CircleImageView roundedImageViewPerfil;
    private StorageReference storageReference;
    private String identificadorUsuario, descricao;
    private EditText textNomePerfil, textDescricaoPerfil;
    private ImageView imageAtualizarNome, imageAtualizarDescricao;
    private Usuario usuarioLogado;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private ImageView mudarFotoPerfil;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        //carregamentos
        mudarFotoPerfil = view.findViewById(R.id.mudarFotoPerfil);
        roundedImageViewPerfil = view.findViewById(R.id.imageNavigationPerfil);
        textNomePerfil = view.findViewById(R.id.textNomePerfil);
        textDescricaoPerfil = view.findViewById(R.id.textDescricaoPerfil);
        imageAtualizarNome = view.findViewById(R.id.imageAtualizarNome);
        imageAtualizarDescricao = view.findViewById(R.id.imageAtualizarDescricao);
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //configuraçoes iniciais
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = getIdentificadorUsuario();

        //recuperando dados usuario
        final FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();
        Uri url = usuario.getPhotoUrl();
        if (url != null) {
            Glide.with(getActivity())
                    .load(url)
                    .into(roundedImageViewPerfil);
        } else {
            roundedImageViewPerfil.setImageResource(R.drawable.padrao);
        }
        textNomePerfil.setText(usuario.getDisplayName());
        textDescricaoPerfil.setText("Carregando...");



        //recuperar descricao
        DatabaseReference databaseRef = ConfiguracaoFirebase.getFireBase();
        String identificador = getIdentificadorUsuario();
        DatabaseReference referenciaFinal = databaseRef.child("usuarios").child(identificador);
        referenciaFinal.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange (@NonNull DataSnapshot dataSnapshot){

            Usuario usuarioSnapshot = dataSnapshot.getValue(Usuario.class);
            descricao = usuarioSnapshot.getDescricao();
            textDescricaoPerfil.setText(descricao);
            imageAtualizarDescricao.setVisibility(View.GONE);

            textDescricaoPerfil.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged (CharSequence s,int start, int count, int after){
                }
                @Override
                public void onTextChanged (CharSequence s,int start, int before, int count){

                    imageAtualizarDescricao.setVisibility(View.VISIBLE);

                }
                @Override
                public void afterTextChanged (Editable s){
                }
            });
        }

        @Override
        public void onCancelled (@NonNull DatabaseError databaseError){

        }
    });

    imageAtualizarDescricao.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String descricaoEdit = textDescricaoPerfil.getText().toString();
            usuarioLogado.setDescricao(descricaoEdit);
            usuarioLogado.atualizar();
            imageAtualizarDescricao.setVisibility(View.GONE);
        }
    });



        //click mudar foto de perfil
        mudarFotoPerfil.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick (View v){
        //validação permissoes
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> listaPermissoes = new ArrayList<>();
            for (String permissao : permissoesNecessarias) {
                Boolean temPermissao = ActivityCompat.checkSelfPermission(getContext(), permissao) == PackageManager.PERMISSION_GRANTED;
                if (!temPermissao) listaPermissoes.add(permissao);
            }
            String[] novasPermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novasPermissoes);
            if (!listaPermissoes.isEmpty()) {
                requestPermissions(permissoesNecessarias, 1);
            } else {
                //iniciando pegar foto
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        }
    }
    });

    //salvar nome
        textNomePerfil.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged (CharSequence s,int start, int count, int after){
        }
        @Override
        public void onTextChanged (CharSequence s,int start, int before, int count){
            imageAtualizarNome.setVisibility(View.VISIBLE);
            imageAtualizarNome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String nome = textNomePerfil.getText().toString();
                    boolean retorno = UsuarioFirebase.atualizarNomeUsuario(nome);
                    if (retorno) {

                        usuarioLogado.setDescricao(descricao);
                        usuarioLogado.setNome(nome);
                        usuarioLogado.atualizar();

                    }
                    imageAtualizarNome.setVisibility(View.GONE);

                }
            });
        }
        @Override
        public void afterTextChanged (Editable s){
        }
    });

        return view;

    }


    /*public void getNumber(FirebaseCallback callback) {
        //descricao
        DatabaseReference databaseRef = ConfiguracaoFirebase.getFireBase();
        String identificador = getIdentificadorUsuario();
        DatabaseReference referenciaFinal = databaseRef.child("usuarios").child(identificador);
        referenciaFinal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot){
                Usuario usuarioSnapshot = dataSnapshot.getValue(Usuario.class);
                descricao = usuarioSnapshot.getDescricao();
                callback.onDataGot(descricao);
            }

            @Override
            public void onCancelled (@NonNull DatabaseError databaseError){

            }
        });

    }*/


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;
            try {

                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localImagemSelecionada);
                        break;
                }

                if(imagem != null){

                    roundedImageViewPerfil.setImageBitmap(imagem);

                    //recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //salvar imagem no firebase
                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("perfil")
                            //.child(identificadorUsuario)
                            .child(identificadorUsuario + ".jpeg");

                    imagemRef.putBytes(dadosImagem).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return imagemRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                atualizarFotoUsuario(downloadUri);
                            } else {
                                Toast.makeText(getActivity(), "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }


    public void atualizarFotoUsuario(Uri url){

        boolean retorno = UsuarioFirebase.atualizarFotoUsuario(url);
        if(retorno){
            usuarioLogado.setFoto(url.toString());
            usuarioLogado.atualizar();
            Toast.makeText(getActivity(), "Sua foto foi alterada!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int permissaoResultado: grantResults){
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
                break;
            }
        }
    }


    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Permissões negadas");
        builder.setCancelable(false);
        builder.setMessage("Para alterar a imagem é necessário aceitar as permissões");
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
