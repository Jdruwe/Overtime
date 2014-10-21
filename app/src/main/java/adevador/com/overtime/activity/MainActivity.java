package adevador.com.overtime.activity;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import adevador.com.overtime.R;
import adevador.com.overtime.fragment.CalendarFragment;
import adevador.com.overtime.listener.CalendarListener;


public class MainActivity extends ActionBarActivity implements CalendarListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            displayFragment(CalendarFragment.newInstance());
        }
    }

    public void displayFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
