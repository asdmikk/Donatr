package org.donatr.donatr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;

import org.donatr.donatr.model.Donation;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

    private static final int ONE_SECOND = 1000;

    TextView nfcTextview;
    Button amountButton1;
    Button amountButton2;
    Button amountButton3;
    Button confirmButton;

    View selected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        nfcTextview = (TextView) findViewById(R.id.nfc_textview);
        amountButton1 = (Button) findViewById(R.id.amount_button1);
        amountButton2 = (Button) findViewById(R.id.amount_button2);
        amountButton3 = (Button) findViewById(R.id.amount_button3);
        confirmButton = (Button) findViewById(R.id.confirm_button);

        amountButton1.setVisibility(View.INVISIBLE);
        amountButton2.setVisibility(View.INVISIBLE);
        amountButton3.setVisibility(View.INVISIBLE);
        confirmButton.setVisibility(View.INVISIBLE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void donate(View v) {
        String amount = ((Button)selected).getText().toString();
        String charity = ((Button)selected).getText().toString();
        Donation donation = new Donation(charity, amount);
        showPostDialog(donation);
    }

    public void selectAmount(View v) {
        setSelected(v);
        buttonFadeIn(confirmButton, 500);
    }

    public void setSelected(View v) {
        selected = v;

    }

    public void cancelSelected(View v) {
        selected = null;
        confirmButton.setVisibility(View.INVISIBLE);
    }

    public void buttonFadeIn(View v, int time) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(time);
        v.startAnimation(anim);
        v.setVisibility(View.VISIBLE);
    }

    public void mockNfc(View v) {
        Toast.makeText(MainActivity.this, "Scanning...", Toast.LENGTH_SHORT).show();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        nfcTextview.setText(getString(R.string.mock_charity));
                        buttonFadeIn(amountButton1, ONE_SECOND);
                        buttonFadeIn(amountButton2, ONE_SECOND);
                        buttonFadeIn(amountButton3, ONE_SECOND);
                    }
                },
                1000);
    }

    public void onPositiveClick(String title) {
        Toast.makeText(this, "donating " + title, Toast.LENGTH_SHORT).show();
    }

    void showPostDialog(Donation donation) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = PostDonationDialogFragment.newInstance(donation);
        newFragment.show(ft, "dialog");
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

        public static ConfirmDonationDialogFragment newInstance(String amountString) {
            ConfirmDonationDialogFragment fragment = new ConfirmDonationDialogFragment();
            Bundle args = new Bundle();
            args.putString("amount", amountString);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final String amount = getArguments().getString("amount");
            final String title = "Do you want to donate " + amount;

            return new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((MainActivity)getActivity()).onPositiveClick(amount);
                        }
                    })
                    .create();
        }
    }

    public static class PostDonationDialogFragment extends DialogFragment {

        public static PostDonationDialogFragment newInstance(Serializable donation) {
            PostDonationDialogFragment frag = new PostDonationDialogFragment();
            Bundle args = new Bundle();
            args.putSerializable("donation", donation);
            frag.setArguments(args);
            return frag;
        }


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            Donation donation = (Donation) getArguments().getSerializable("donation");

            View rootView = inflater.inflate(R.layout.post_donate_dialog, container, false);

            TextView amountTextview = (TextView) rootView.findViewById(R.id.amount_textview);
            TextView charityTextview = (TextView) rootView.findViewById(R.id.charity_textview);

            charityTextview.setText(donation.getCharity());
            amountTextview.setText(donation.getAmount());

            return rootView;
        }
    }
}
