package com.example.versaoum.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.versaoum.R;
import com.example.versaoum.activity.EditarAnuncios;
import com.example.versaoum.adapter.AdapterAnuncios;
import com.example.versaoum.helper.ConfiguracaoFirebase;
import com.example.versaoum.helper.RecyclerItemClickListener;
import com.example.versaoum.helper.UsuarioFirebase;
import com.example.versaoum.model.Anuncio;
import com.example.versaoum.model.Conversa;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnunciosFragment extends Fragment {

    public AnunciosFragment() {
        // Required empty public constructor
    }

    private Spinner spinnerOrdenar;
    private RecyclerView recyclerAnuncios;
    private List<Anuncio> anuncios = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference anuncioUsuarioRef;
    private ValueEventListener valueEventListenerAnuncios;
    private ImageView buttonAdd;
    private AlertDialog dialog;
    private String idUsuario;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_anuncios, container, false);

        buttonAdd = view.findViewById(R.id.imageAdd);


        recyclerAnuncios = view.findViewById(R.id.recyclerAnuncios);

        idUsuario = UsuarioFirebase.getIdentificadorUsuario();
        anuncioUsuarioRef = ConfiguracaoFirebase.getFireBase().child("anuncios").child(idUsuario);

        /*FloatingActionButton fab = view.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            startActivity(new Intent(getContext().getApplicationContext(), CadastrarAnunciosActivity.class));

            }
        });*/

        //configurar recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerAnuncios.setLayoutManager(layoutManager);
        recyclerAnuncios.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(anuncios, getActivity());
        recyclerAnuncios.setAdapter(adapterAnuncios);


        recyclerAnuncios.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerAnuncios, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Anuncio anuncioSelecionado = anuncios.get(position);
                int estado = 0;
                Intent i = new Intent(getContext().getApplicationContext(), EditarAnuncios.class);
                i.putExtra("anunciozz",anuncioSelecionado);
                i.putExtra("estado", estado);
                startActivity(i);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));


        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarAnuncios();
    }

    @Override
    public void onStop() {
        super.onStop();
        anuncioUsuarioRef.removeEventListener(valueEventListenerAnuncios);
    }

    private void recuperarAnuncios(){

        dialog = new SpotsDialog.Builder().setContext(getActivity()).setMessage("Recuperando anúncios").setCancelable(false).build();
        dialog.show();

        valueEventListenerAnuncios = anuncioUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                anuncios.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Anuncio anuncio = ds.getValue(Anuncio.class);
                    anuncios.add(anuncio);
                }

                Collections.reverse(anuncios);
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

   /* private void carregarDadosSpinner(){
        String[] ordenar = getResources().getStringArray(R.array.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.custom_spinner,
                ordenar
        );
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        spinnerOrdenar.setAdapter(adapter);
    }*/

}
