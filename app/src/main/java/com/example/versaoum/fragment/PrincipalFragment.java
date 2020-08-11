package com.example.versaoum.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.example.versaoum.R;
import com.example.versaoum.activity.EditarAnuncios;
import com.example.versaoum.activity.InfoAnuncio;
import com.example.versaoum.adapter.AdapterAnuncios;
import com.example.versaoum.helper.ConfiguracaoFirebase;
import com.example.versaoum.helper.Permissoes;
import com.example.versaoum.helper.RecyclerItemClickListener;
import com.example.versaoum.helper.UsuarioFirebase;
import com.example.versaoum.model.Anuncio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class PrincipalFragment extends Fragment {

    private RecyclerView recyclerPrincipal;
    private AdapterAnuncios adapterAnuncios;
    private List<Anuncio> listaAnuncios = new ArrayList<>();
    private DatabaseReference anunciosPrincipalRef;
    private AlertDialog dialog;
    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private FirebaseAuth autenticacao;


    public PrincipalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_principal, container, false);


        //validar permissao maps
        Permissoes.validarPermissoes(permissoes, getActivity(), 1);

        //configurações iniciais
        recyclerPrincipal = view.findViewById(R.id.recyclerPrincipal);
        anunciosPrincipalRef = ConfiguracaoFirebase.getFireBase().child("anuncios");

        //configurar recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerPrincipal.setLayoutManager(layoutManager);
        recyclerPrincipal.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(listaAnuncios, getActivity());
        recyclerPrincipal.setAdapter(adapterAnuncios);

        recuperarAnunciosPrincipal();

        recyclerPrincipal.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerPrincipal, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Anuncio anuncioSelecionado = listaAnuncios.get(position);
                int estado = 1;
                Intent i = new Intent(getContext().getApplicationContext(), InfoAnuncio.class);
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

    public void recuperarAnunciosPrincipal(){

        dialog = new SpotsDialog.Builder().setContext(getActivity()).setTheme(R.style.Custom).setMessage("Recuperando anúncios").setCancelable(false).build();
        dialog.show();

        anunciosPrincipalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaAnuncios.clear();
                for(DataSnapshot usuarios: dataSnapshot.getChildren()){
                    for(DataSnapshot anuncios: usuarios.getChildren()){
                        Anuncio anuncio = anuncios.getValue(Anuncio.class);
                        String usuario = UsuarioFirebase.getIdentificadorUsuario();
                        //if(anuncio.getEndereco())
                        if(!usuario.equals(anuncio.getIdCriador()) && !anuncio.getRecusados().contains(usuario)){
                            listaAnuncios.add(anuncio);
                        }

                    }
                }
                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int permissaoResultado: grantResults){
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissoes();
            }
        }
    }

    private void alertaValidacaoPermissoes(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Permissoes Negadas");
        builder.setMessage("Para utilizar o aplicativo é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    autenticacao.signOut();
                }catch (Exception e){
                    e.printStackTrace();
                }
                getActivity().finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
