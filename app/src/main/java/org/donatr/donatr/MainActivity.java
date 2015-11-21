package org.donatr.donatr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;

public class MainActivity extends AppCompatActivity {

    Button amountButton1;
    Button amountButton2;
    Button amountButton3;
    TextView nfcTextview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        amountButton1 = (Button) findViewById(R.id.amount_button1);
        amountButton2 = (Button) findViewById(R.id.amount_button2);
        amountButton3 = (Button) findViewById(R.id.amount_button3);
        nfcTextview = (TextView) findViewById(R.id.nfc_textview);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void donate(View v) {
        String title = "Do you want to donate " + ((Button)v).getText().toString();
        DialogFragment dialog = ConfirmDonationDialogFragment.newInstance(title);
        dialog.show(getFragmentManager(), "askDialog");
    }

    public void mockNfc(View v) {
        Toast.makeText(MainActivity.this, "Scanning...", Toast.LENGTH_SHORT).show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        nfcTextview.setText(getString(R.string.mock_charity));
                    }
                },
                1000);
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

    public static class ConfirmDonationDialogFragment extends DialogFragment {

        public static ConfirmDonationDialogFragment newInstance(String title) {
            ConfirmDonationDialogFragment fragment = new ConfirmDonationDialogFragment();
            Bundle args = new Bundle();
            args.putString("title", title);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String title = getArguments().getString("title");

            return new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(ConfirmDonationDialogFragment.this.getActivity(), "tootab", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create();
        }
    }
}
