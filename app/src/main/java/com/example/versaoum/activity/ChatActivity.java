package com.example.versaoum.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.versaoum.R;
import com.example.versaoum.adapter.AdapterMensagens;
import com.example.versaoum.helper.ConfiguracaoFirebase;
import com.example.versaoum.helper.UsuarioFirebase;
import com.example.versaoum.model.Anuncio;
import com.example.versaoum.model.Conversa;
import com.example.versaoum.model.Mensagem;
import com.example.versaoum.model.Usuario;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView textNomeChat, textAnuncioChat;
    private CircleImageView imageUsuarioChat, imageAnuncioChat;
    private EditText editMensagem;

    private String nomeDestinatario, nomeRemetente;
    private String fotoDestinatario, fotoRemetente;
    private LinearLayout layoutPerfilDestinatario, linearAceitar;

    private ImageView imageFoto, imageArrow;

    private DatabaseReference database;
    private DatabaseReference mensagensRef;
    private ChildEventListener childEventListenerMensagens;

    private Button buttonAceitar;

    private TextView buttonRecusar;

    private RecyclerView recyclerMensagens;
    private AdapterMensagens adapter;
    private List<Mensagem> mensagens = new ArrayList<>();

    private String idUsuarioRemetente, idUsuarioDestinatario, idAnuncio;

    private Boolean expandir;

    private Anuncio anuncio;

    private List<String> listaRecusados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //configuracoes iniciais
        textNomeChat = findViewById(R.id.textNomeChat);
        imageUsuarioChat = findViewById(R.id.imageUsuarioChat);
        editMensagem = findViewById(R.id.editMensagem);
        recyclerMensagens = findViewById(R.id.recylerMensagens);
        textAnuncioChat = findViewById(R.id.textTituloAnuncioChat);
        imageAnuncioChat = findViewById(R.id.imageAnuncioChat);
        layoutPerfilDestinatario = findViewById(R.id.layoutPerfilDestinatario);
        database = ConfiguracaoFirebase.getFireBase();
        imageFoto = findViewById(R.id.imageFoto);
        imageArrow = findViewById(R.id.imageArrow);
        linearAceitar = findViewById(R.id.linearAceitar);
        buttonAceitar = findViewById(R.id.buttonAceitar);
        buttonRecusar = findViewById(R.id.buttonRecusar);



        //recupera id remetente
        idUsuarioRemetente = UsuarioFirebase.getIdentificadorUsuario();

        //fechar actvity
        findViewById(R.id.imageVoltaChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //abrir perfil
        layoutPerfilDestinatario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), PerfilActivity.class);
                i.putExtra("usuarioDestinatarioId", idUsuarioDestinatario);
                startActivity(i);
            }
        });


        //intent
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            anuncio = (Anuncio) extras.getSerializable("anuncios");

            if(extras.getInt("chat")==0){
                //recuperar dados destinatario
                idUsuarioDestinatario = anuncio.getIdCriador();
            }else if(extras.getInt("chat")==1){
                String destinatario = extras.getString("destinatario");
                idUsuarioDestinatario = destinatario;
            }


            idAnuncio = anuncio.getIdAnuncio();

            //anuncio titulo e foto
            textAnuncioChat.setText(anuncio.getTitulo());
            List<String> urlFotos = anuncio.getFotos();
            Glide.with(ChatActivity.this).load(urlFotos.get(0)).into(imageAnuncioChat);


            //recuperar dados destinatario anuncio chat
            DatabaseReference databaseRef = ConfiguracaoFirebase.getFireBase();
            DatabaseReference referenciaFinal = databaseRef.child("usuarios").child(idUsuarioDestinatario);

            referenciaFinal.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Usuario usuarioSnapshot = dataSnapshot.getValue(Usuario.class);
                    nomeDestinatario = usuarioSnapshot.getNome();
                    textNomeChat.setText(usuarioSnapshot.getNome());
                    String foto = usuarioSnapshot.getFoto();
                    if(foto!=null){
                        Uri url = Uri.parse(usuarioSnapshot.getFoto());
                        Glide.with(ChatActivity.this).load(url).into(imageUsuarioChat);
                        fotoDestinatario = usuarioSnapshot.getFoto();
                    }else{
                        imageUsuarioChat.setImageResource(R.drawable.padrao);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //ir para informacoes anuncio
            textAnuncioChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(anuncio.getIdCriador().equals(idUsuarioRemetente)){
                        Intent i = new Intent(getApplicationContext(), EditarAnuncios.class);
                        i.putExtra("anunciozz", anuncio);
                        startActivity(i);
                    }else if(anuncio.getIdCriador().equals(idUsuarioDestinatario)){
                        Intent i = new Intent(getApplicationContext(), InfoAnuncio.class);
                        i.putExtra("anunciozz", anuncio);
                        i.putExtra("estado", 2);
                        startActivity(i);
                    }
                }
            });
            imageAnuncioChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(anuncio.getIdCriador().equals(idUsuarioRemetente)){
                        Intent i = new Intent(getApplicationContext(), EditarAnuncios.class);
                        i.putExtra("anunciozz", anuncio);
                        startActivity(i);
                    }else if(anuncio.getIdCriador().equals(idUsuarioDestinatario)){
                        Intent i = new Intent(getApplicationContext(), InfoAnuncio.class);
                        i.putExtra("anunciozz", anuncio);
                        i.putExtra("estado", 2);
                        startActivity(i);
                    }
                }
            });


            //recuperar nome e foto remetente

            DatabaseReference databaseRefNomeRemetente = ConfiguracaoFirebase.getFireBase();
            DatabaseReference referenciaFinalNomeRemetente = databaseRef.child("usuarios").child(idUsuarioRemetente);

            referenciaFinalNomeRemetente.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Usuario usuarioSnapshot = dataSnapshot.getValue(Usuario.class);
                    nomeRemetente = usuarioSnapshot.getNome();
                    String foto = usuarioSnapshot.getFoto();
                    if(foto!=null){
                        fotoRemetente = usuarioSnapshot.getFoto();
                    }else{
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            //configuracao adapter
            adapter = new AdapterMensagens(mensagens, getApplicationContext());

            //configuracao reclucler
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerMensagens.setLayoutManager(layoutManager);
            recyclerMensagens.setHasFixedSize(true);
            recyclerMensagens.setAdapter(adapter);

            /*adapter.setOnItemClickListener(new AdapterMensagens.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    mensagens.get(position).changeText
                }
            });*/

            mensagensRef = database.child("mensagens").child(idAnuncio).child(idUsuarioRemetente).child(idUsuarioDestinatario);

            //mostrar aceitar ou recusar
            if(anuncio.getIdCriador().equals(idUsuarioRemetente)){
                //expandir aceitar
                expandir = false;
                imageArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!expandir){
                            linearAceitar.setVisibility(View.GONE);
                            imageArrow.setImageResource(R.drawable.ic_keyboard_arrow_up);
                            expandir = true;
                        }else{
                            linearAceitar.setVisibility(View.VISIBLE);
                            imageArrow.setImageResource(R.drawable.ic_keyboard_arrow_down);
                            expandir = false;
                        }
                    }
                });

                //aceitar e recusar
                buttonRecusar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatabaseReference conversaRefR = ConfiguracaoFirebase.getFireBase()
                                .child("conversas")
                                .child(idUsuarioRemetente)
                                .child(anuncio.getIdAnuncio())
                                .child(idUsuarioDestinatario);
                        conversaRefR.removeValue();
                        DatabaseReference mensagemRefeR = ConfiguracaoFirebase.getFireBase()
                                .child("mensagens")
                                .child(anuncio.getIdAnuncio())
                                .child(idUsuarioRemetente)
                                .child(idUsuarioDestinatario);
                        mensagemRefeR.removeValue();

                        DatabaseReference conversaRefD = ConfiguracaoFirebase.getFireBase()
                                .child("conversas")
                                .child(idUsuarioDestinatario)
                                .child(anuncio.getIdAnuncio())
                                .child(idUsuarioRemetente);
                        //conversaRefD.removeValue();
                        //listaRecusados.addAll(anuncio.getRecusados());
                        HashMap<String, Object> recusadosMap = new HashMap<>();
                        recusadosMap.put(String.valueOf(anuncio.getRecusados().size()), idUsuarioDestinatario);
                        DatabaseReference recusadosRef = ConfiguracaoFirebase.getFireBase()
                                .child("anuncios")
                                .child(anuncio.getIdCriador())
                                .child(anuncio.getIdAnuncio())
                                .child("recusados");
                        recusadosRef.updateChildren(recusadosMap);

                        /*DatabaseReference mensagemRefeD = ConfiguracaoFirebase.getFireBase()
                                .child("mensagens")
                                .child(anuncio.getIdAnuncio())
                                .child(idUsuarioDestinatario)
                                .child(idUsuarioRemetente);
                        mensagemRefeD.removeValue();*/


                        finish();

                    }
                });

                buttonAceitar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }else{
                imageArrow.setVisibility(View.GONE);
                linearAceitar.setVisibility(View.GONE);
            }



        }

        recuperarMensagens();

    }

    public void enviarMensagem(View view){

        String textoMensagem = editMensagem.getText().toString();

        if(!textoMensagem.isEmpty()){

            Mensagem mensagem = new Mensagem();
            mensagem.setIdUsuario(idUsuarioRemetente);
            mensagem.setMensagem(textoMensagem);
            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            mensagem.setData(currentDate);
            mensagem.setHora(currentTime);

            //salvar mensagem remetente
            salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem, idAnuncio);

            //salvar mensagem destinatario
            salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem, idAnuncio);

            //salvar conversa
            salvarConversa(mensagem);

            //limpar texto
            editMensagem.setText("");

        }else{

        }
    }

    private void salvarConversa(Mensagem msg){

        salvarConversaRemetente(msg);
        salvarConversaDestinatario(msg);

    }

    private void salvarConversaRemetente(Mensagem msg){
        Conversa conversaRemetente = new Conversa();
        conversaRemetente.setIdRemetente(idUsuarioRemetente);
        conversaRemetente.setIdDestinatario(idUsuarioDestinatario);
        conversaRemetente.setUltimaMensagem(msg.getMensagem());
        conversaRemetente.setAnuncio(anuncio);

        Date currentTime = Calendar.getInstance().getTime();
        conversaRemetente.setTempo(currentTime);

        conversaRemetente.setNomeDestinatario(nomeDestinatario);
        conversaRemetente.setFotoDestinatario(fotoDestinatario);

        conversaRemetente.salvar();
    }

    private void salvarConversaDestinatario(Mensagem msg){
        Conversa conversaRemetente = new Conversa();
        conversaRemetente.setIdRemetente(idUsuarioDestinatario);
        conversaRemetente.setIdDestinatario(idUsuarioRemetente);
        conversaRemetente.setUltimaMensagem(msg.getMensagem());
        conversaRemetente.setAnuncio(anuncio);

        Date currentTime = Calendar.getInstance().getTime();
        conversaRemetente.setTempo(currentTime);

        conversaRemetente.setNomeDestinatario(nomeRemetente);
        conversaRemetente.setFotoDestinatario(fotoRemetente);

        conversaRemetente.salvar();
    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagem msg, String idAnuncio){

        DatabaseReference database = ConfiguracaoFirebase.getFireBase();
        DatabaseReference mensagemRef = database.child("mensagens");

        mensagemRef.child(idAnuncio).child(idRemetente).child(idDestinatario).push().setValue(msg);

    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagensRef.removeEventListener(childEventListenerMensagens);
    }

    private void recuperarMensagens(){

        childEventListenerMensagens = mensagensRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Mensagem mensagem = dataSnapshot.getValue(Mensagem.class);
                mensagens.add(mensagem);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
