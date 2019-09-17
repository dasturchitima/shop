package com.example.socketioapp.about;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;
import com.example.socketioapp.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setTitle("About Me");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.profile_image_round)
                .addItem(new Element().setTitle("Get in touch"))
                .addItem(new Element().setTitle("LinkedIn")
                        .setIconDrawable(R.drawable.ic_linkedin_logo)
                        .setIconTint(R.color.colorTextRegular)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Uri webpage = Uri.parse("https://linkedin.com/in/mayurrokade/");
                                startActivity(new Intent(Intent.ACTION_VIEW, webpage));
                            }
                        }))
                .addItem(new Element().setTitle("Medium")
                        .setIconDrawable(R.drawable.ic_medium_logo)
                        .setIconTint(R.color.colorTextRegular)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Uri webpage = Uri.parse("https://medium.com/@mayuroks");
                                startActivity(new Intent(Intent.ACTION_VIEW, webpage));
                            }
                        }))
                .addGitHub("mayuroks")
                .addFacebook("mayurzenith")
                .addInstagram("mayurzenith")
                .addTwitter("mayuroks")
                .addEmail("mayurzenith@gmail.com")
                .addItem(getCopyRightsElement())
                .create();

        setContentView(aboutPage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copy_right),
                Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.about_icon_copy_right);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutActivity.this, copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }

}
