package com.example.versaoum.model;

import com.example.versaoum.helper.ConfiguracaoFirebase;
import com.example.versaoum.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Anuncio implements Serializable {

    private String idAnuncio;
    private String idCriador;
    private String nomeCriador;
    private String titulo;
    private String descricao;
    private String data;
    private String endereco;
    private String enderecoVisivel;
    private String recompensa;
    private List<String> fotos;
    private List<String> recusados;

    public Anuncio() {
        String idUsuario = UsuarioFirebase.getIdentificadorUsuario();
        String nomeCriador = UsuarioFirebase.getUsuarioAtual().getDisplayName();
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFireBase()
                .child("anuncios");
        setIdAnuncio(anuncioRef.push().getKey());
        setIdCriador(idUsuario);
        setNomeCriador(nomeCriador);
    }

    public void salvar(){

        String idUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFireBase()
                .child("anuncios");

        anuncioRef.child(idUsuario)
                .child(getIdAnuncio())
                .setValue(this);
    }

    public void remover(){
        String idUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFireBase()
                .child("anuncios")
                .child(idUsuario)
                .child(getIdAnuncio());
        anuncioRef.removeValue();
    }


    public String getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(String idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public String getIdCriador() {
        return idCriador;
    }

    public void setIdCriador(String idCriador) {
        this.idCriador = idCriador;
    }

    public String getNomeCriador() {
        return nomeCriador;
    }

    public void setNomeCriador(String nomeCriador) {
        this.nomeCriador = nomeCriador;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getEnderecoVisivel() {
        return enderecoVisivel;
    }

    public void setEnderecoVisivel(String enderecoVisivel) {
        this.enderecoVisivel = enderecoVisivel;
    }

    public String getRecompensa() {
        return recompensa;
    }

    public void setRecompensa(String recompensa) {
        this.recompensa = recompensa;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }

    public List<String> getRecusados() {
        return recusados;
    }

    public void setRecusados(List<String> recusados) {
        this.recusados = recusados;
    }
}
