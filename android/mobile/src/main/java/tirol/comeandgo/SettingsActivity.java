package tirol.comeandgo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import tirol.comeandgo.business.settings.Settings;

public class SettingsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Settings mSettings;

    EditText mEditTextUserName;
    EditText mEditTextPassword;
    EditText mEditTextHost;
    EditText mEditTextPort;
    private String mUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final View parentLayout = findViewById(R.id.relativeLayoutRoot);
        initNaviagionDrawer(toolbar);

        mSettings = new Settings(this);
        mUrl = String.format("%s:%d", mSettings.getHost(), mSettings.getPort());
        loadSettings();

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSettings.setHost(mEditTextHost.getText().toString());
                mSettings.setPort(Integer.valueOf(mEditTextPort.getText().toString()));
                mSettings.setUserName(mEditTextUserName.getText().toString());
                mSettings.setPassword(mEditTextPassword.getText().toString());

                Snackbar.make(parentLayout, "Saved settings", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    private void initNaviagionDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    private void loadSettings() {
        mEditTextUserName = (EditText) findViewById(R.id.editTextUserName);
        mEditTextUserName.setText(mSettings.getUserName());

        mEditTextPassword = (EditText) findViewById(R.id.editTextPassword);
        mEditTextPassword.setText(mSettings.getPassword());

        mEditTextHost = (EditText) findViewById(R.id.editTextHost);
        mEditTextHost.setText(mSettings.getHost());

        mEditTextPort = (EditText) findViewById(R.id.editTextPort);
        int port = mSettings.getPort();
        mEditTextPort.setText(String.valueOf(port));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent myIntent = new Intent(this, MainActivity.class);
            this.startActivity(myIntent);
        } else if (id == R.id.nav_open_in_browser) {
            String browserUrl = String.format("http://%s/", mUrl);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(browserUrl));
            startActivity(browserIntent);

        } else if (id == R.id.nav_settings) {
            //Intent myIntent = new Intent(this, SettingsActivity.class);
            //this.startActivity(myIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
