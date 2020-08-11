package com.example.versaoum.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.versaoum.R;
import com.example.versaoum.helper.ConfiguracaoFirebase;
import com.example.versaoum.helper.Permissoes;
import com.example.versaoum.model.Anuncio;
import com.example.versaoum.model.Endereco;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskedittext.MaskEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class CadastrarAnunciosActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textHabilitarLocal, textHabilitarData, textHabilitarRecompensa, textCidade, textBairro, textNumero, textRua, textEstado, textCEP;
    private Switch switchLocal, switchData, switchRecompensa;
    private EditText editCidade, editBairro, editNumero, editRua, editTitulo, editDescricao;
    private MaskEditText editData, editEstado, editCEP;
    private ImageView imageCadastro1, imageCadastro2, imageCadastro3;
    private CurrencyEditText editRecompensa;
    private LinearLayout grupoEstado, grupoBairro, linearLayoutImage;
    private android.app.AlertDialog dialog;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaUrlFotos = new ArrayList<>();
    private Anuncio anuncio;
    private StorageReference storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncios);

        Permissoes.validarPermissoes(permissoes, this, 1);

        //configurações iniciais
        carregarItens();
        storage = ConfiguracaoFirebase.getFirebaseStorage();

        //fechar a activity
        findViewById(R.id.imageVolta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //definir local do dinheiro
        Locale locale = new Locale("pr", "BR");
        editRecompensa.setLocale(locale);
        editRecompensa.setText(null);



        //exibir local
        switchLocal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    switchLocal.setText("Local");
                    editCidade.setVisibility(View.VISIBLE);
                    editBairro.setVisibility(View.VISIBLE);
                    editNumero.setVisibility(View.VISIBLE);
                    editRua.setVisibility(View.VISIBLE);
                    editEstado.setVisibility(View.VISIBLE);
                    editCEP.setVisibility(View.VISIBLE);
                    textCidade.setVisibility(View.VISIBLE);
                    textBairro.setVisibility(View.VISIBLE);
                    textNumero.setVisibility(View.VISIBLE);
                    textRua.setVisibility(View.VISIBLE);
                    textEstado.setVisibility(View.VISIBLE);
                    textCEP.setVisibility(View.VISIBLE);
                    grupoEstado.setVisibility(View.VISIBLE);
                    grupoBairro.setVisibility(View.VISIBLE);


                    textHabilitarLocal.setText("Desabilite se deseja que o anuncio esteja visivel a todos");
                }else{

                    switchLocal.setText("Sem local definido");
                    editCidade.setText(null);
                    editBairro.setText(null);
                    editNumero.setText(null);
                    editRua.setText(null);
                    editEstado.setText(null);
                    editCEP.setText(null);

                    grupoEstado.setVisibility(View.GONE);
                    grupoBairro.setVisibility(View.GONE);
                    textCidade.setVisibility(View.GONE);
                    textBairro.setVisibility(View.GONE);
                    textNumero.setVisibility(View.GONE);
                    textRua.setVisibility(View.GONE);
                    textEstado.setVisibility(View.GONE);
                    textCEP.setVisibility(View.GONE);
                    editCidade.setVisibility(View.GONE);
                    editBairro.setVisibility(View.GONE);
                    editNumero.setVisibility(View.GONE);
                    editRua.setVisibility(View.GONE);
                    editEstado.setVisibility(View.GONE);
                    editCEP.setVisibility(View.GONE);
                    textHabilitarLocal.setText("Habilite se deseja que o anuncio seja visível apenas por pessoas na região");
                }
            }
        });
        //exibir data
        switchData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    switchData.setText("Data/ Prazo");
                    editData.setVisibility(View.VISIBLE);
                    textHabilitarData.setText("Desabilite se não deseja inserir data/ prazo ao pedido");
                }else{
                    switchData.setText("Sem data/ prazo");
                    editData.setText(null);
                    editData.setVisibility(View.GONE);
                    textHabilitarData.setText("Habilite se deseja inserir data/ prazo ao pedido");
                }
            }
        });
        //exibir recompensa
        switchRecompensa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    switchRecompensa.setText("Oferecer uma recompensa");
                    editRecompensa.setVisibility(View.VISIBLE);
                    editRecompensa.setText("R$ 0.00");
                    textHabilitarRecompensa.setText("Desabilite se deseja que o pedido não tenha valor definido");
                }else{
                    editRecompensa.setText("");
                    switchRecompensa.setText("Negociável");
                    editRecompensa.setVisibility(View.GONE);
                    textHabilitarRecompensa.setText("Habilite se deseja inserir uma recompensa");
                }
            }
        });

    }

    public void salvarAnuncio(){

        Boolean localSwitch = switchLocal.isChecked();




        if(localSwitch){
            salvarAnuncioLocalON();
        }else{
            salvarAnuncioFirebase();
        }

    }

    public void salvarAnuncioFirebase(){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando anuncio")
                .setCancelable(false)
                .build();
        dialog.show();

        //salvar primeiro imagem no storage
        for(int i=0;  i < listaFotosRecuperadas.size(); i++){
            String urlImagem = listaFotosRecuperadas.get(i);
            int tamanhoLista=listaFotosRecuperadas.size();
            salvarFotosStorage(urlImagem, tamanhoLista, i);
        }

    }

    private void salvarFotosStorage(String urlString, final int totalFotos, int contador){

        //criar nó no storage
        StorageReference imagemAnuncio = storage.child("imagens")
                .child("anuncios")
                .child(anuncio.getIdAnuncio())
                .child("imagem"+contador);
        //fazer upload arquivo
        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String urlConvertida = uri.toString();

                        listaUrlFotos.add(urlConvertida);

                        if(totalFotos==listaUrlFotos.size()){
                            anuncio.setFotos(listaUrlFotos);
                            anuncio.salvar();
                            dialog.dismiss();
                            finish();
                        }
                    }
                });

                //String urlConvertida = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                /*listaUrlFotos.add(urlConvertida);

                if(totalFotos==listaUrlFotos.size()){
                    anuncio.setFotos(listaUrlFotos);
                    anuncio.salvar();
                    dialog.dismiss();
                    finish();
                }*/

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CadastrarAnunciosActivity.this, "Falha ao fazer upload", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void configurarAnuncio(){

        String titulo = editTitulo.getText().toString();
        String descricao = editDescricao.getText().toString();
        String data = editData.getText().toString();
        String  numero = editNumero.getText().toString();
        String  rua = editRua.getText().toString();
        String  bairro = editBairro.getText().toString();
        String  cidade = editCidade.getText().toString();
        String  estado = editEstado.getText().toString();
        String  cep = editCEP.getText().toString();
        String recompensa = editRecompensa.getText().toString();
        List<String> listaaa = new ArrayList<>();
        listaaa.add("");

        anuncio = new Anuncio();
        anuncio.setEndereco(estado + ", " + cidade + ", " + bairro + ", " + rua + ", " + numero + ", " + cep);
        anuncio.setDescricao(descricao);
        anuncio.setData(data);
        anuncio.setTitulo(titulo);
        anuncio.setRecompensa(recompensa);
        anuncio.setEnderecoVisivel(cidade + ", " + bairro + ", " + rua);
        anuncio.setRecusados(listaaa);

    }

    private void salvarAnuncioLocalON(){
        String enderecoAnuncio = (anuncio.getEndereco());

        if(!enderecoAnuncio.equals("") || enderecoAnuncio != null){

            Address addressAnuncio = recuperarEndereco(enderecoAnuncio);

            if(addressAnuncio != null ){
                Endereco endereco = new Endereco();
                endereco.setCidade(addressAnuncio.getSubAdminArea());
                endereco.setCep(addressAnuncio.getPostalCode());
                endereco.setBairro(addressAnuncio.getSubLocality());
                endereco.setRua(addressAnuncio.getThoroughfare());
                endereco.setNumero(addressAnuncio.getFeatureName());
                endereco.setLatitude(String.valueOf(addressAnuncio.getLatitude()));
                endereco.setLongitude(String.valueOf(addressAnuncio.getLongitude()));
                endereco.setEstado(addressAnuncio.getAdminArea());
                if(endereco.getNumero().isEmpty()){
                    endereco.setNumero(" - ");
                }

                StringBuilder mensagem = new StringBuilder();
                mensagem.append("Estado: " + endereco.getEstado());
                mensagem.append("\nCidade: " + endereco.getCidade());
                mensagem.append("\nRua: " + endereco.getRua());
                mensagem.append("\nBairro: " + endereco.getBairro());
                mensagem.append("\nNumero: " + endereco.getNumero());
                mensagem.append("\nCEP: " + endereco.getCep());

                AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Confirme seu endereço!").setMessage(mensagem).setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        salvarAnuncioFirebase();
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                Toast.makeText(this, "Endereço inválido", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Informe o endereço ou desabilite o campo endereço", Toast.LENGTH_SHORT).show();
        }
    }

    public void validarDadosAnuncio(View view){

        configurarAnuncio();

        Boolean dataSwitch = switchData.isChecked();
        Boolean recompensaSwitch = switchRecompensa.isChecked();
        Boolean localSwitch = switchLocal.isChecked();

        String titulo = editTitulo.getText().toString();
        String descricao = editDescricao.getText().toString();
        String data = editData.getText().toString();
        String  numero = editNumero.getText().toString();
        String  rua = editRua.getText().toString();
        String  bairro = editBairro.getText().toString();
        String  cidade = editCidade.getText().toString();
        String  estado = editEstado.getText().toString();
        String  cep = editCEP.getText().toString();
        String recompensa = String.valueOf(editRecompensa.getRawValue());

        String date = "";
        if(editData.getRawText() != null){
            date = editData.getRawText().toString();
        }

        if(listaFotosRecuperadas.size() != 0){
            if(!titulo.isEmpty()){
                if(!descricao.isEmpty()){
                    if (recompensaSwitch && localSwitch && dataSwitch){
                        if(!data.isEmpty() && date.length()==6){
                            if(!estado.isEmpty()){
                                if(!cep.isEmpty()){
                                    if(!cidade.isEmpty()){
                                        if(!rua.isEmpty()){
                                            if(!bairro.isEmpty()){
                                                if(!numero.isEmpty()){
                                                    if(!recompensa.isEmpty()){
                                                        salvarAnuncio();
                                                    }else{
                                                        Toast.makeText(this, "Preencha a recompensa", Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    Toast.makeText(this, "Preencha o numero", Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                Toast.makeText(this, "Preencha o bairro", Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            Toast.makeText(this, "Preencha a rua", Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        Toast.makeText(this, "Preencha a cidade", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(this, "Preencha o CEP", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(this, "Preencha o estado", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(this, "Preencha a data", Toast.LENGTH_SHORT).show();
                        }
                    }else if(recompensaSwitch && dataSwitch && !localSwitch){
                        if(!data.isEmpty() && date.length()==6){
                            if(!recompensa.isEmpty()){
                                anuncio.setEnderecoVisivel("Sem endereço/ Remoto");
                                anuncio.setEndereco(" ");
                                salvarAnuncio();
                            }else{
                                Toast.makeText(this, "Preencha a recompensa", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(this, "Preencha a data", Toast.LENGTH_SHORT).show();
                        }
                    }else if(recompensaSwitch && !dataSwitch && localSwitch){
                        if(!estado.isEmpty()){
                            if(!cep.isEmpty()){
                                if(!cidade.isEmpty()){
                                    if(!rua.isEmpty()){
                                        if(!bairro.isEmpty()){
                                            if(!numero.isEmpty()){
                                                if(!recompensa.isEmpty()){
                                                    anuncio.setData("Sem prazo/ data");
                                                    salvarAnuncio();
                                                }else{
                                                    Toast.makeText(this, "Preencha a recompensa", Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                Toast.makeText(this, "Preencha o numero", Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            Toast.makeText(this, "Preencha o bairro", Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        Toast.makeText(this, "Preencha a rua", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(this, "Preencha a cidade", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(this, "Preencha o CEP", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(this, "Preencha o estado", Toast.LENGTH_SHORT).show();
                        }
                    }else if(!recompensaSwitch && dataSwitch && localSwitch){
                        if(!data.isEmpty() && date.length()==6){
                            if(!estado.isEmpty()){
                                if(!cep.isEmpty()){
                                    if(!cidade.isEmpty()){
                                        if(!rua.isEmpty()){
                                            if(!bairro.isEmpty()){
                                                if(!numero.isEmpty()){
                                                    anuncio.setRecompensa("Negociável");
                                                    salvarAnuncio();
                                                }else{
                                                    Toast.makeText(this, "Preencha o numero", Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                Toast.makeText(this, "Preencha o bairro", Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            Toast.makeText(this, "Preencha a rua", Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        Toast.makeText(this, "Preencha a cidade", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(this, "Preencha o CEP", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(this, "Preencha o estado", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(this, "Preencha a data", Toast.LENGTH_SHORT).show();
                        }
                    }else if(!recompensaSwitch && !dataSwitch && localSwitch){
                        if(!estado.isEmpty()){
                            if(!cep.isEmpty()){
                                if(!cidade.isEmpty()){
                                    if(!rua.isEmpty()){
                                        if(!bairro.isEmpty()){
                                            if(!numero.isEmpty()){
                                                anuncio.setRecompensa("Negociável");
                                                anuncio.setData("Sem prazo/ data");
                                                salvarAnuncio();
                                            }else{
                                                Toast.makeText(this, "Preencha o numero", Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            Toast.makeText(this, "Preencha o bairro", Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        Toast.makeText(this, "Preencha a rua", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(this, "Preencha a cidade", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(this, "Preencha o CEP", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(this, "Preencha o estado", Toast.LENGTH_SHORT).show();
                        }
                    }else if(!recompensaSwitch && dataSwitch && !localSwitch){
                        if(!data.isEmpty() && date.length()==6){
                            anuncio.setRecompensa("Negociável");
                            anuncio.setEndereco(" ");
                            anuncio.setEnderecoVisivel("Sem endereço/ Remoto");
                            salvarAnuncio();
                        }else{
                            Toast.makeText(this, "Preencha a data", Toast.LENGTH_SHORT).show();
                        }
                    }else if(recompensaSwitch && !dataSwitch && !localSwitch){
                        if(!recompensa.isEmpty()){
                            anuncio.setData("Sem prazo/ data");
                            anuncio.setEndereco(" ");
                            anuncio.setEnderecoVisivel("Sem endereço/ Remoto");
                            salvarAnuncio();
                        }else{
                            Toast.makeText(this, "Preencha a recompensa", Toast.LENGTH_SHORT).show();
                        }
                    }else if(!recompensaSwitch && !dataSwitch && !localSwitch){
                        anuncio.setData("Sem prazo/ data");
                        anuncio.setRecompensa("Negociável");
                        anuncio.setEndereco(" ");
                        anuncio.setEnderecoVisivel("Sem endereço/ Remoto");
                        salvarAnuncio();
                    }

                }else{
                    Toast.makeText(this, "Preencha a descrição", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Preencha o título", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Insira ao menos uma imagem", Toast.LENGTH_SHORT).show();
        }

    }


    private Address recuperarEndereco( String endereco){

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> listaEndereços = geocoder.getFromLocationName(endereco, 1);
            if( listaEndereços!=null && listaEndereços.size() > 0 ){
                Address address = listaEndereços.get(0);

                return address;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.imageCadastrar1:
                escolherImagem(1);
                break;
            case R.id.imageCadastrar2:
                escolherImagem(2);
                break;
            case R.id.imageCadastrar3:
                escolherImagem(3);
                break;
        }

    }

    public void escolherImagem(int requestCode){

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK){

            //Recuperar imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //configura no imageview
            if(requestCode == 1){
                imageCadastro1.setImageURI(imagemSelecionada);
                imageCadastro2.setVisibility(View.VISIBLE);

            }else if(requestCode == 2){
                imageCadastro2.setImageURI(imagemSelecionada);
                imageCadastro3.setVisibility(View.VISIBLE);
            }else if(requestCode == 3){
                imageCadastro3.setImageURI(imagemSelecionada);
            }
            listaFotosRecuperadas.add(caminhoImagem);
        }

    }

    public void carregarItens(){
        editRecompensa = findViewById(R.id.editRecompensa);
        textHabilitarData = findViewById(R.id.textHabilitarData);
        textHabilitarLocal = findViewById(R.id.textHabilitarLocal);
        textHabilitarRecompensa = findViewById(R.id.textHabilitarRecompensa);
        switchRecompensa = findViewById(R.id.switchRecompensa);
        switchData = findViewById(R.id.switchData);
        editData = findViewById(R.id.editData);
        switchLocal = findViewById(R.id.switchLocal);
        textRua = findViewById(R.id.textRua);
        textCEP = findViewById(R.id.textCEP);
        textNumero = findViewById(R.id.textNumero);
        textBairro = findViewById(R.id.textBairro);
        textCidade = findViewById(R.id.textCidade);
        textEstado = findViewById(R.id.textEstado);
        editRua = findViewById(R.id.editRua);
        editCEP = findViewById(R.id.editCEP);
        editNumero = findViewById(R.id.editNumero);
        editBairro = findViewById(R.id.editBairro);
        editCidade = findViewById(R.id.editCidade);
        editEstado = findViewById(R.id.editEstado);
        editTitulo = findViewById(R.id.editTitulo);
        grupoBairro = findViewById(R.id.grupoBairro);
        grupoEstado = findViewById(R.id.grupoEstado);
        editDescricao = findViewById(R.id.editDescricao);
        imageCadastro1 = findViewById(R.id.imageCadastrar1);
        imageCadastro2 = findViewById(R.id.imageCadastrar2);
        imageCadastro3 = findViewById(R.id.imageCadastrar3);
        imageCadastro1.setOnClickListener(this);
        imageCadastro2.setOnClickListener(this);
        imageCadastro3.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissaoResultado: grantResults){
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permissões negadas");
                builder.setMessage("Para fazer um pedido de ajuda é necessário aceitar as permissões");
                builder.setCancelable(false);
                builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

}
