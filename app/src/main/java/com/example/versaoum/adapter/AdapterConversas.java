package com.example.versaoum.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.versaoum.R;
import com.example.versaoum.helper.ConfiguracaoFirebase;
import com.example.versaoum.helper.UsuarioFirebase;
import com.example.versaoum.model.Anuncio;
import com.example.versaoum.model.Conversa;
import com.example.versaoum.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class AdapterConversas extends RecyclerView.Adapter<AdapterConversas.MyViewHolder> {

    private List<Conversa> conversas;
    private Context context;


    public AdapterConversas(List<Conversa> lista, Context c) {

        this.conversas = lista;
        this.context = c;

    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_conversas, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.progressBar.setVisibility(View.VISIBLE);
        holder.progressBar2.setVisibility(View.VISIBLE);


        Conversa conversa = conversas.get(position);
        holder.ultimaMensagem.setText(conversa.getUltimaMensagem());

        Anuncio anuncio = conversa.getAnuncio();
        holder.titulo.setText(anuncio.getTitulo());

        holder.nome.setText(conversa.getNomeDestinatario());

        holder.textMeuAnuncio.setVisibility(View.GONE);
        String usuario = UsuarioFirebase.getIdentificadorUsuario();
        if(anuncio.getIdCriador().equals(usuario)){
            holder.textMeuAnuncio.setVisibility(View.VISIBLE);
        }

        //pegar primeira imagem da lista
        List<String> urlFotos = anuncio.getFotos();
        String urlCapa = urlFotos.get(0);
        Picasso.get().load(urlCapa).into(holder.foto, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                holder.progressBar.setVisibility(View.GONE);
            }
        });

        String urlFotosDestinatario = conversa.getFotoDestinatario();
        //Glide.with(context).load(urlCapa).into(holder.foto);
        Picasso.get().load(urlFotosDestinatario).into(holder.fotoDestinatario, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar2.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                holder.progressBar2.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return conversas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView foto, fotoDestinatario;
        TextView titulo, nome, ultimaMensagem, textMeuAnuncio;
        private ProgressBar progressBar, progressBar2;

        public MyViewHolder(View itemView){
            super(itemView);

            foto = itemView.findViewById(R.id.imageConversas);
            titulo = itemView.findViewById(R.id.textTituloConversa);
            nome = itemView.findViewById(R.id.textNomeConversa);
            ultimaMensagem = itemView.findViewById(R.id.textUltimaMensagem);
            fotoDestinatario = itemView.findViewById(R.id.imageDestinatarioConversas);
            progressBar = itemView.findViewById(R.id.progressBar);
            progressBar2 = itemView.findViewById(R.id.progressBar2);
            textMeuAnuncio = itemView.findViewById(R.id.textMeuAnuncio);

        }

    }

}
