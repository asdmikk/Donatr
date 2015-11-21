package org.donatr.donatr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.donatr.donatr.model.Donation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int ONE_SECOND = 1000;
    private static final int LESS_SECONDS = 300;
    private static final float LEAST_ALPHA = 0.3f;


    TextView nfcTextview;
    Button confirmButton;
    Button cancelButton;
    Button moreButton;
    ImageButton logo;
    List<Button> donationButtons;

    HorizontalScrollView scrollView;

    View selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        donationButtons = new ArrayList<>();
        setContentView(R.layout.activity_main);


        nfcTextview = (TextView) findViewById(R.id.nfc_textview);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);
//        moreButton = (Button) findViewById(R.id.more_button);
        logo = (ImageButton) findViewById(R.id.imageButton);
        scrollView = (HorizontalScrollView  ) findViewById(R.id.scrollView);

        resetDonationButtonsOpacity();
        scrollView.setVisibility(View.INVISIBLE);
//        setButtonsVisibility(View.INVISIBLE);

        cancelButton.setVisibility(View.INVISIBLE);
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
        String charity = nfcTextview.getText().toString();
        Donation donation = new Donation(charity, amount);
        showPostDialog(donation);
    }

    public void selectAmount(View v) {
        boolean shouldFade = selected == null;
        setSelected(v);
        if(shouldFade) {
            viewFadeIn(cancelButton, 500);
            viewFadeIn(confirmButton, 500);
        }
    }

    public void setSelected(View v) {
        selected = v;
        resetDonationButtonsOpacity();
        v.setAlpha(1f);
    }

    public void cancelSelected(View v) {
        selected = null;
        resetDonationButtonsOpacity();
        viewFadeOut(cancelButton, LESS_SECONDS);
        viewFadeOut(confirmButton, LESS_SECONDS);
    }

    public void viewFadeIn(View v, int time) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(time);
        v.startAnimation(anim);
        v.setVisibility(View.VISIBLE);
    }

    public void viewFadeOut(View v, int time) {
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(time);
        v.startAnimation(anim);
        v.setVisibility(View.INVISIBLE);
    }

    public void mockNfc(View v) {
        Toast.makeText(MainActivity.this, "Scanning...", Toast.LENGTH_SHORT).show();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        nfcTextview.setText(getString(R.string.mock_charity));
                        viewFadeIn(scrollView, ONE_SECOND);
                        viewFadeOut(logo, ONE_SECOND);
                        logo.setImageDrawable(getResources().getDrawable(R.drawable.tp_ig_pv));
                        viewFadeIn(logo, ONE_SECOND);
                    }
                },
                1000);
    }

    public void onPositiveClick(String title) {
        Toast.makeText(this, "donating " + title, Toast.LENGTH_SHORT).show();
    }

    private void showPostDialog(Donation donation) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        Fragment newFragment = PostDonationDialogFragment.newInstance(donation);
        getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .add(android.R.id.content, newFragment, "dialog").commit();
//        newFragment.show(ft, "dialog");
    }

    public void reset() {
        viewFadeOut(scrollView, LESS_SECONDS);
        viewFadeOut(cancelButton, LESS_SECONDS);
        viewFadeOut(confirmButton, LESS_SECONDS);
        resetDonationButtonsOpacity();
        resetLogo();
        selected = null;

        nfcTextview.setText(getString(R.string.nfc_waiting_text));
    }

    private void resetLogo() {
        viewFadeOut(logo, LESS_SECONDS);
        logo.setImageDrawable(getResources().getDrawable(R.drawable.nfs));
        viewFadeIn(logo, ONE_SECOND);
    }

    private void resetDonationButtonsOpacity() {
        LinearLayout container = (LinearLayout) scrollView.getChildAt(0);
        for(int i = 0; i < container.getChildCount(); i++) {
            View button = container.getChildAt(i);
            button.setAlpha(LEAST_ALPHA);
        }
    }

    private void setButtonsVisibility(int mode) {
        for(int i = 0; i < scrollView.getChildCount(); i++) {
            View button = scrollView.getChildAt(i);
            button.setVisibility(mode);
        }
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

    public static class PostDonationDialogFragment extends Fragment {

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

            final View rootView = inflater.inflate(R.layout.post_donate_dialog, container, false);

            TextView charityTextview = (TextView) rootView.findViewById(R.id.charity_textview);
            ImageButton closeButton = (ImageButton) rootView.findViewById(R.id.close_button);

            String charity = donation.getCharity();
            charityTextview.setText(charity + " tänab");
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFragmentManager().beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .remove(getFragmentManager().findFragmentByTag("dialog"))
                            .commit();
                    ((MainActivity)getActivity()).reset();
                }
            });

//            closeButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    getDialog().dismiss();
//                }
//            });

            return rootView;
        }

//        @Override
//        public void onDismiss(DialogInterface dialog) {
//            super.onDismiss(dialog);
//            ((MainActivity)getActivity()).reset();
//        }
    }
}
