package com.phillipt.testapp2;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.phillipt.testapp2.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import java.util.List;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @android.webkit.JavascriptInterface
    public void javascriptInvoke() {
        System.out.println("Invoked from JS");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        MainActivity mainActivity = this;
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 02-0039 - Deprecated Method to Collect List of Apps - getRunningTask
                ActivityManager activityManager = (ActivityManager) mainActivity.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(100);
                System.out.println(tasks);

                // 02-0158 - Application Loads Code Dynamically
                DexClassLoader dx = new DexClassLoader("./network/dynamic.dex", null, null, null);
                PathClassLoader pcl = new PathClassLoader("./vendored/dyn2.dex", null);

                // 02-0108 - Google Play Warning: Embedded AWS Credentials"
                System.out.println("Our AWS credential is " + "AKIAIOSFODNN7EXAMPLE");

                // 02-0123 - Google Play Security Warning: Hardcoded Google Oauth Refresh Token
                String oauthRefreshToken = "1/DGXUA3FmzHZnjEJDvX8ynqJCALJpo6ZT3IVNK6aBYHU";
                String refreshToken2 = "1/EeE4LhI8EVRj3F98wmlwFDFnIycu16EY2Gv8dDujT24";
                System.out.printf("OAuth refresh token: %s token2 %s%n", oauthRefreshToken, refreshToken2);

                // 00-0080 - Hardcoded Kony Password
                System.out.println("Kony@1234DXir4mzKhY7SuAVoTrjti7Sm is very sensitive");

                // 02-0085 - Sensitive Data Exposed via Ordered Broadcast
                String signaturePermission = "com.app.custom_permission.broadcastpermission";
                Intent intent = new Intent("Action name");
                intent.putExtra("PARAM", "Sensitive Info from Sender");
                sendOrderedBroadcast(intent, signaturePermission, null, null, 0, null, null);

                // 02-0075 - Sensitive Data Exposed via Sticky Broadcast
                sendStickyBroadcast(intent);

                // 02-0153 - Google Play Blocker: JavaScript Interface Injection Vulnerability
                WebView wv = new WebView(mainActivity);
                wv.getSettings().setLightTouchEnabled(true);
                wv.getSettings().setJavaScriptEnabled(true);
                wv.addJavascriptInterface(mainActivity, "javascriptInvoke");
                wv.loadUrl("file:///android_asset/www/index.html");

                // 02-0084 - Enable Android Verify Apps
                SafetyNet.getClient(mainActivity)
                        .isVerifyAppsEnabled()
                        .addOnCompleteListener(new OnCompleteListener<SafetyNetApi.VerifyAppsUserResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<SafetyNetApi.VerifyAppsUserResponse> task) {
                                if (task.isSuccessful()) {
                                    SafetyNetApi.VerifyAppsUserResponse result = task.getResult();
                                    assert result != null;
                                    if (result.isVerifyAppsEnabled()) {
                                        Log.d("MY_APP_TAG", "The Verify Apps feature is enabled.");
                                    } else {
                                        Log.d("MY_APP_TAG", "The Verify Apps feature is disabled.");
                                    }
                                } else {
                                    Log.e("MY_APP_TAG", "A general error occurred.");
                                }
                            }
                        });

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}