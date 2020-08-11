package com.example.versaoum.model;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.versaoum.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Conversa {

    private String idRemetente;
    private String idDestinatario;
    private String ultimaMensagem;
    private Anuncio anuncio;
    private String nomeDestinatario;
    private String fotoDestinatario;
    private Date tempo;
    private String estado;

    public Conversa() {
    }

    public void salvar(){



        DatabaseReference database = ConfiguracaoFirebase.getFireBase();
        DatabaseReference conversaRef = database.child("conversas");

        conversaRef.child(this.getIdRemetente()).child(this.anuncio.getIdAnuncio()).child(this.getIdDestinatario()).setValue(this);

    }


    public String getIdRemetente() {
        return idRemetente;
    }

    public void setIdRemetente(String idRemetente) {
        this.idRemetente = idRemetente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }

    public Anuncio getAnuncio() {
        return anuncio;
    }

    public void setAnuncio(Anuncio anuncio) {
        this.anuncio = anuncio;
    }

    public String getNomeDestinatario() {
        return nomeDestinatario;
    }

    public void setNomeDestinatario(String nomeDestinatario) {
        this.nomeDestinatario = nomeDestinatario;
    }

    public String getFotoDestinatario() {
        return fotoDestinatario;
    }

    public void setFotoDestinatario(String fotoDestinatario) {
        this.fotoDestinatario = fotoDestinatario;
    }

    public Date getTempo() {
        return tempo;
    }

    public void setTempo(Date tempoUltimaMensagem) {
        this.tempo = tempoUltimaMensagem;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
