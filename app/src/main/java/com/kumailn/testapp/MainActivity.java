package com.kumailn.testapp;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.TextView;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    final String versionName = BuildConfig.VERSION_NAME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button myButton = (Button)findViewById(R.id.button);
        Button b2 = (Button)findViewById(R.id.button2);
        TextView mt = (TextView)findViewById(R.id.textView);

        //Non default toolbar creation
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //THIS LINE IS IMPORTANT - TOOK 2 HOURS TO FIND
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }

        else{
            mt.setVisibility(View.VISIBLE);
            mt.setText("Great, you're set. You can now close the app.");
            AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
            fadeIn.setInterpolator(new AccelerateInterpolator()); //and this
            fadeIn.setStartOffset(150);
            fadeIn.setDuration(400);
            AnimationSet animation = new AnimationSet(true); //change to false
            animation.addAnimation(fadeIn);
            mt.setAnimation(animation);

            Intent i = new Intent(getApplicationContext(), MyService.class);
            Log.e("MyService: ", "Started ");
            startService(i);
        }


        myButton.setText("Service One");
        myButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MyService.class);
                startService(i);
            }
        });

        myButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                /*Intent i = new Intent(getApplicationContext(), MyService.class);
                stopService(i);*/



                startService(new Intent(MainActivity.this, ChatHeadService.class));
                finish();
                return true;
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(MainActivity.this, secondService.class);
                startService(i2);
            }
        });
    }
    public void aboutDialog(){

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(MainActivity.this);
        }

        String nodata="<br/>&#8226; Version " + versionName + "<br/>&#8226; Made by Andrew, Asad, Kumail, Nina, HackTheNorth 2017<br/>&#8226; github.com/aanguyen<br/>&#8226; github.com/asadmansr<br/>&#8226; github.com/kumailn<br/>";
        final SpannableString ss = new SpannableString(Html.fromHtml(nodata));
        Linkify.addLinks(ss, Linkify.ALL);

        //added a TextView
        final TextView tx1=new TextView(MainActivity.this);
        tx1.setText(ss);
        tx1.setAutoLinkMask(RESULT_OK);
        tx1.setMovementMethod(LinkMovementMethod.getInstance());
        tx1.setTextSize(16);
        tx1.setTextColor(Color.WHITE);
        tx1.setPadding(48, 0, 0, 0);

        builder.setTitle("About the app")
                //.setMessage("Made by Kumail Naqvi, 2017, Version 1.5, Contact me at kumailmn@gmail.com, github.com/kumailn, powered by mXparser")
                //.setMessage(ss)
                .setView(tx1)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }

                })
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Configures menu (toolbar) button options
        if(item.getItemId() == R.id.action_about){
            aboutDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflates menu
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
