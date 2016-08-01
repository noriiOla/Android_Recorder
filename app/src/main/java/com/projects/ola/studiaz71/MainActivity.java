package com.projects.ola.studiaz71;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.go_to_notes_list)
    public void idzDoListActivity(){
        startActivity(new Intent(this, ListActivity.class));
    }

    @OnClick(R.id.go_to_rec_new_note)
    public void idzDoNagrywania(){
        startActivity(new Intent(this, RecordActivity.class));
    }
}
