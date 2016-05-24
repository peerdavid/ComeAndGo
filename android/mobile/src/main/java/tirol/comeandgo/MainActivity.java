package tirol.comeandgo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;

import tirol.comeandgo.business.api.Client;
import tirol.comeandgo.business.api.ClientResult;
import tirol.comeandgo.business.api.ClientResultListener;
import tirol.comeandgo.business.api.TimeTrackState;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ClientResultListener {


    private Button mBtnCome;
    private Button mBtnGo;
    private Button mBtnBreak;
    private Client mClient;
    private FloatingActionButton mBtnRefresh;

    private String mTimeTrackState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mClient = new Client("192.168.10.116:9000", "v1");
        mClient.setOnResultListener(this);
        mBtnCome = (Button) findViewById(R.id.btnCome);
        mBtnCome.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!mTimeTrackState.equals(TimeTrackState.INACTIVE)){
                            return;
                        }

                        mClient.come();
                    }
                }
        );

        mBtnGo = (Button) findViewById(R.id.btnGo);
        mBtnGo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!mTimeTrackState.equals(TimeTrackState.ACTIVE)){
                            return;
                        }

                        mClient.go();
                    }
                }
        );

        mBtnBreak = (Button) findViewById(R.id.btnBreak);
        mBtnBreak.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mTimeTrackState.equals(TimeTrackState.ACTIVE)){
                            mClient.startBreak();
                        } else if (mTimeTrackState.equals(TimeTrackState.BREAK)){
                            mClient.endBreak();
                        }
                    }
                }
        );

        mBtnRefresh = (FloatingActionButton) findViewById(R.id.btnRefresh);
        mBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClient.readState();
            }
        });

        mClient.readState();
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
            mClient.readState();
        } else if (id == R.id.nav_open_in_browser) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.10.116:9000/"));
            startActivity(browserIntent);

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onResult(final ClientResult result) {
        Log.d("tirol.comeandgo.app", result.getUseCase() + "=" + result.getStatusCode() + " | " + result.getMessage());

        if(result.getUseCase().equals(Client.READ_STATE)){
            mTimeTrackState = result.getMessage();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    resetTimeTrackState();
                }
            });
        }
    }

    private void resetTimeTrackState() {
        Log.d("tirol.comeandgo.app", "STATE=" + mTimeTrackState);
        switch(mTimeTrackState){
            case TimeTrackState.ACTIVE:
                mBtnCome.setEnabled(false);
                mBtnGo.setEnabled(true);
                mBtnBreak.setEnabled(true);
                mBtnBreak.setText("Start break");
                break;

            case TimeTrackState.INACTIVE:
                mBtnCome.setEnabled(true);
                mBtnGo.setEnabled(false);
                mBtnBreak.setEnabled(false);
                mBtnBreak.setText("Start break");
                break;

            case TimeTrackState.BREAK:
                mBtnCome.setEnabled(false);
                mBtnGo.setEnabled(true);
                mBtnBreak.setEnabled(true);
                mBtnBreak.setText("End break");
                break;

            default:
            Log.e("tirol.comeandgo.app", "STATE=" + mTimeTrackState);
        }
    }
}
