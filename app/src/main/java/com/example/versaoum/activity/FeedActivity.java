package com.example.versaoum.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.versaoum.R;
import com.example.versaoum.helper.UsuarioFirebase;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;


public class FeedActivity extends AppCompatActivity implements DrawerLayout.DrawerListener{

    private CircleImageView imageNavigationPerfil;
    private TextView textNavigationNome;
    private HeaderViewListAdapter headerNavigationView;
    private TextView chat;
    int contadorNotificacao = 100;
    private ImageView buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        buttonAdd = findViewById(R.id.imageAdd);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CadastrarAnunciosActivity.class));
            }
        });



        //carregar conteudo
        final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        final NavigationView navigationView = findViewById(R.id.navigationView);
        //navigationView.setItemIconTintList(null);



        //navigationView.removeHeaderView(navigationView.getHeaderView(0)); // Remove old header
        //navigationView.inflateHeaderView(R.layout.layout_navigation_header); // Add and refresh new header
        //navigationView.getMenu().clear(); // Remove every element from the old menu
        //navigationView.inflateMenu(R.menu.navigation_menu); // Add and refresh new menu




        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);

        chat = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.menuChat));
        chat.setGravity(Gravity.CENTER_VERTICAL);
        chat.setTypeface(null, Typeface.BOLD);
        chat.setTextColor(getResources().getColor(R.color.colorAccent));
        chat.setText("99+");


        setubBadge();

        //atualizar barra lateral nome e foto
        imageNavigationPerfil = navigationView.getHeaderView(0).findViewById(R.id.imageNavigationPerfil);
        textNavigationNome = navigationView.getHeaderView(0).findViewById(R.id.textNavigationNome);
        //foto perfil
        FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();
        Uri url = usuario.getPhotoUrl();
        if(url != null){
            Glide.with(FeedActivity.this)
                    .load(url)
                    //.placeholder(R.drawable.padrao)
                    //.error(R.drawable.padrao)
                    .into(imageNavigationPerfil);
        }else{
            imageNavigationPerfil.setImageResource(R.drawable.padrao);
        }
        textNavigationNome.setText(usuario.getDisplayName());

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(FeedActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                imageNavigationPerfil = navigationView.getHeaderView(0).findViewById(R.id.imageNavigationPerfil);
                textNavigationNome = navigationView.getHeaderView(0).findViewById(R.id.textNavigationNome);
                //foto perfil
                FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();
                Uri url = usuario.getPhotoUrl();
                if(url != null){
                    Glide.with(FeedActivity.this)
                            .load(url)
                            .into(imageNavigationPerfil);
                }else{
                    imageNavigationPerfil.setImageResource(R.drawable.padrao);
                }
                textNavigationNome.setText(usuario.getDisplayName());
            }
        };
        drawerLayout.addDrawerListener(mDrawerToggle);

    }


    private void setubBadge(){
        if (chat != null) {
            if (contadorNotificacao == 0) {
                if (chat.getVisibility() != View.GONE) {
                    chat.setVisibility(View.GONE);
                }
            } else {
                chat.setText(String.valueOf(Math.min(contadorNotificacao, 99)));
                if (chat.getVisibility() != View.VISIBLE) {
                    chat.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }


}
