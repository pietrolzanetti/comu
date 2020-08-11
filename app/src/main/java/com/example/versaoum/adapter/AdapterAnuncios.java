package com.example.versaoum.adapter;

import android.content.Context;
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
import com.example.versaoum.model.Anuncio;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;


public class AdapterAnuncios extends RecyclerView.Adapter<AdapterAnuncios.MyViewHolder> {

    private List<Anuncio> anuncios;
    private Context context;

    public AdapterAnuncios(List<Anuncio> anuncios, Context context) {
        this.anuncios = anuncios;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_anuncio, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.progressBar.setVisibility(View.VISIBLE);

        Anuncio anuncio = anuncios.get(position);
        holder.titulo.setText(anuncio.getTitulo());
        holder.data.setText(anuncio.getData());
        holder.recompensa.setText(anuncio.getRecompensa());
        holder.endereco.setText(anuncio.getEnderecoVisivel());

        //pegar primeira imagem da lista
        List<String> urlFotos = anuncio.getFotos();
        String urlCapa = urlFotos.get(0);
        //Glide.with(context).load(urlCapa).into(holder.foto);
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

    }

    @Override
    public int getItemCount() {
        return anuncios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titulo;
        TextView recompensa;
        TextView data;
        TextView endereco;
        ImageView foto;
        ProgressBar progressBar;

        public MyViewHolder(View itemView){
            super(itemView);

            titulo = itemView.findViewById(R.id.textTitulo);
            recompensa = itemView.findViewById(R.id.textRecompensa);
            data = itemView.findViewById(R.id.textData);
            endereco = itemView.findViewById(R.id.textEndereco);
            foto = itemView.findViewById(R.id.imageAnuncio);
            progressBar = itemView.findViewById(R.id.progressBar3);

        }


    }

}
