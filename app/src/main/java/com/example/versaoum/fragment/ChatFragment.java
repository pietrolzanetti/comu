package com.example.versaoum.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;

import com.example.versaoum.R;
import com.example.versaoum.activity.ChatActivity;
import com.example.versaoum.adapter.AdapterConversas;
import com.example.versaoum.helper.ConfiguracaoFirebase;
import com.example.versaoum.helper.RecyclerItemClickListener;
import com.example.versaoum.helper.UsuarioFirebase;
import com.example.versaoum.model.Anuncio;
import com.example.versaoum.model.Conversa;
import com.google.android.gms.common.api.internal.ListenerHolder;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private RecyclerView recyclerViewConversas;
    private List<Conversa> listaConversa = new ArrayList<>();
    private AdapterConversas adapter;
    private DatabaseReference database;
    private DatabaseReference conversasRef;
    private ChildEventListener childEventListenerConversas;
    private ValueEventListener valueEventListenerConversas;
    private AlertDialog dialog;

    public ChatFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerViewConversas = view.findViewById(R.id.recyclerListaConversas);
        dialog = new SpotsDialog.Builder().setTheme(R.style.Custom).setContext(getActivity()).setCancelable(false).build();
        dialog.show();

        //configura adapter
        adapter = new AdapterConversas(listaConversa, getActivity());

        //configura recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewConversas.setLayoutManager(layoutManager);
        recyclerViewConversas.setHasFixedSize(true);
        recyclerViewConversas.setAdapter(adapter);


        //
        recyclerViewConversas.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerViewConversas, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Conversa conversaSelecionada = listaConversa.get(position);
                Intent i = new Intent(getContext().getApplicationContext(), ChatActivity.class);

                Anuncio anuncio = conversaSelecionada.getAnuncio();
                i.putExtra("anuncios", anuncio);

                String destinatario = conversaSelecionada.getIdDestinatario();
                i.putExtra("destinatario", destinatario);

                int chat = 1;
                i.putExtra("chat", chat);

                startActivity(i);

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));


        String idUsuario = UsuarioFirebase.getIdentificadorUsuario();
        database = ConfiguracaoFirebase.getFireBase();
        conversasRef = database.child("conversas").child(idUsuario);

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarConversas();

    }

    @Override
    public void onStop() {
        super.onStop();
        conversasRef.removeEventListener(valueEventListenerConversas);
    }


    public void recuperarConversas(){

        //listaConversa.clear();

        valueEventListenerConversas = conversasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listaConversa.clear();

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    for(DataSnapshot dns: ds.getChildren()){
                        //recuperar conversas
                        Conversa conversa = dns.getValue(Conversa.class);
                        listaConversa.add(conversa);
                    }

                }

                Collections.sort(listaConversa, new Comparator<Conversa>() {
                    public int compare(Conversa o1, Conversa o2) {
                        return o1.getTempo().compareTo(o2.getTempo());
                    }
                });
                Collections.reverse(listaConversa);
                adapter.notifyDataSetChanged();
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*childEventListenerConversas = conversasRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                for(DataSnapshot ds: dataSnapshot.getChildren()){

                        //recuperar conversas
                        Conversa conversa = ds.getValue(Conversa.class);
                        listaConversa.add(conversa);
                }

                Collections.sort(listaConversa, new Comparator<Conversa>() {
                    public int compare(Conversa o1, Conversa o2) {
                        return o1.getTempo().compareTo(o2.getTempo());
                    }
                });
                Collections.reverse(listaConversa);
                adapter.notifyDataSetChanged();
                dialog.dismiss();


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
        });*/

    }

}
