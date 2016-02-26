package org.masonapps.robotcontrol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.masonapps.robotcontrol.views.D_PadView;
import org.masonapps.robotcontrol.views.DepthView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((D_PadView)findViewById(R.id.dpad)).setOnDirectionPressedListener(new D_PadView.OnDirectionPressedListener() {
            @Override
            public void touchDown(D_PadView.DirectionButton directionButton) {
//                Toast.makeText(MainActivity.this, directionButton.name(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void touchMove(D_PadView.DirectionButton directionButton) {
                
            }

            @Override
            public void touchUp() {

            }
        });
        
        final Random random = new Random(123456);
        final DepthView depthView = (DepthView) findViewById(R.id.depthView);
        for (int i = 0; i <= 180; i += 5) {
            depthView.updateDepth(i, random.nextFloat() * 80f + 60f);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
