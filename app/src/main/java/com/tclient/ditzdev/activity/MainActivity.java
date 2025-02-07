package com.tclient.ditzdev.activity;

import android.graphics.Color;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.tclient.ditzdev.components.SettingsSwitch;
import com.tclient.ditzdev.databinding.ActivityMainBinding;
import com.tclient.ditzdev.R;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.drawerLayout.setScrimColor(Color.TRANSPARENT);
        binding.drawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                        View mainContent = findViewById(R.id.coordinator);
                        float moveFactor = drawerView.getWidth() * slideOffset;
                        mainContent.setTranslationX(moveFactor);
                    }

                    @Override
                    public void onDrawerOpened(@NonNull View drawerView) {}

                    @Override
                    public void onDrawerClosed(@NonNull View drawerView) {}

                    @Override
                    public void onDrawerStateChanged(int newState) {}
                });
        setupToolbar();
    }

    private void setupToolbar() {
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(
                        this,
                        binding.drawerLayout,
                        binding.toolbar,
                        R.string.app_name,
                        R.string.app_name);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        binding.toolbar.setNavigationOnClickListener(
                v -> {
                    if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        binding.drawerLayout.openDrawer(GravityCompat.START);
                    } else {
                        binding.drawerLayout.closeDrawer(GravityCompat.START);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}