package adevador.com.overtime.activity;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import adevador.com.overtime.R;
import adevador.com.overtime.listener.WorkdayListener;
import adevador.com.overtime.util.GoogleCalendarHelper;
import adevador.com.overtime.util.WorkdayHelper;
import adevador.com.overtime.dialog.DeleteDialog;
import adevador.com.overtime.fragment.CalendarFragment;
import adevador.com.overtime.generator.IconGenerator;
import adevador.com.overtime.listener.CalendarListener;
import adevador.com.overtime.model.Workday;
import adevador.com.overtime.util.IabHelper;
import adevador.com.overtime.util.IabResult;
import adevador.com.overtime.util.Inventory;
import adevador.com.overtime.util.Purchase;

public class MainActivity extends ActionBarActivity implements CalendarListener, WorkdayListener {

    private final String SKU_DONATION = "overtime_donation";

    private static final int DONATION_REQUEST_CODE = 1000;

    IabHelper mHelper;
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener;
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpInAppBilling();

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            displayFragment(CalendarFragment.newInstance());
        }
    }

    private void setUpInAppBilling() {

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgymUVX4BnPQsa8Im/E5P/PekzlXuKRg/Imsv7Swg4/P7WmrwB0J7ahM+Qh8kAdrGiPIhj35I4hX/5u55jjBjCJ9pbUY/K2rLEq7pro3o+mrfO27Cr1vRoXrPpojJqosY6jdKvDZmxpTl/mU0XYGAzwYCCjsWXyYK4t8iwi/WfPSy3B69lwEA8/v0EGBWikB+ayGbh55kxeLg02jgDqyUBC/qqATnRY+N7qky/V1roI03E6CyEIKoK/uXXNI7jbNRMjcMIErMVeitOLmeLTQywV+6eMijhx5CM/M2baG5I9PPEjfMYQu1/gaSci34fYGJOFk26E5XqOtJjDcFyja2IQIDAQAB";

        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Toast.makeText(getApplicationContext(), "Problem setting up In-app Billing", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void displayFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_statistics).setIcon(IconGenerator.getIcon(Iconify.IconValue.fa_bar_chart_o, R.color.dark_gray, 24, this));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_statistics:
                Intent statisticsIntent = new Intent(MainActivity.this, StatisticsActivity.class);
                startActivity(statisticsIntent);
                return true;
            case R.id.action_donate:
                openDonateDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openDonateDialog() {

        mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {

            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                //This does not get used!!! ==> http://stackoverflow.com/questions/14800286/oniabpurchasefinished-never-called
            }
        };

        mHelper.launchPurchaseFlow(this, SKU_DONATION, DONATION_REQUEST_CODE,
                mPurchaseFinishedListener);
    }

    private void consumeInAppPurchase() {

        mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
            public void onConsumeFinished(Purchase purchase, IabResult result) {
                if (result.isSuccess()) {
                    // provision the in-app purchase to the user
                    // (for example, credit 50 gold coins to player's character)
                } else {
                    // handle error
                }
            }
        };

        mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

                if (result.isFailure()) {
                    // handle error here
                    Toast.makeText(getApplicationContext(),
                            "Something went wrong while searching the in-app inventory.",
                            Toast.LENGTH_LONG).show();
                } else {
                    // does the user have the premium upgrade?
                    boolean hasDonated = inventory.hasPurchase(SKU_DONATION);
                    if (hasDonated) {
                        Toast.makeText(getApplicationContext(), "Thank you for the donation!",
                                Toast.LENGTH_LONG).show();
                        mHelper.consumeAsync(inventory.getPurchase(SKU_DONATION), mConsumeFinishedListener);
                    }
                }
            }
        };

        if (mHelper != null) {
            mHelper.flagEndAsync();
            mHelper.queryInventoryAsync(mGotInventoryListener);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Fix no activity available
        if (data == null)
            return;
        switch (requestCode) {
            case DONATION_REQUEST_CODE:
                consumeInAppPurchase();
                break;
        }
    }

    @Override
    public void dateSelected(Date date) {
        ArrayList<Date> dateList = new ArrayList<Date>();
        dateList.add(date);
        openTimeDialog(dateList);
    }

    @Override
    public void datesSelected(Set<Date> dates) {
        ArrayList<Date> dateList = new ArrayList<Date>();
        dateList.addAll(dates);
        openTimeDialog(dateList);
    }

    @Override
    public void workdayClicked(Workday workday) {
        openDeleteDialog(workday);
    }

    private void openTimeDialog(ArrayList<Date> dates) {
        Intent intent = new Intent(this, WorkdayActivity.class);
        intent.putExtra("dates", dates);
        startActivity(intent);
    }

    private void openDeleteDialog(Workday workday) {
        DialogFragment deleteDialog = new DeleteDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("workday", workday);
        deleteDialog.setArguments(bundle);
        deleteDialog.show(getSupportFragmentManager(), "delete");
    }

    @Override
    public void deleteWorkday(Workday workday) {
        if (workday.getGoogleCalendarEventId() != 0) {
            GoogleCalendarHelper.deleteCalendarEvent(getContentResolver(), workday.getGoogleCalendarEventId());
        }
        WorkdayHelper.delete(workday);
        refreshCalendar(workday.getDate());
    }

    private void refreshCalendar(Date date) {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof CalendarFragment) {
                if (date != null) {
                    ((CalendarFragment) fragment).resetWorkdayBackground(date);
                }
                ((CalendarFragment) fragment).displayData();
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }
}
