package name.weiskirchner.picture_viewer2;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements PlaceholderFragment.OnImageButtonForeverNewListener,PlaceholderFragment.OnImageButtonInvisibleListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Field mScroller;
    private FixedSpeedScroller scroller;

    private int mInterval_task1 = 15000; // show different picture every 15 seconds
    private long isTouched = 0;
    private Handler mHandler_task1;
    private int autoScrollDelay = 6; // n * mInterval_task1

    private int mInterval_task2 = 120000; // check for new pictures every 120 seconds
    private Handler mHandler_task2;

    private long mInterval_task3 = 100000; // dim UI
    private Handler mHandler_task3;
    private boolean calculateNextRun = true;

    private int startDimHour = 22;
    private int endDimHour = 5;

    private String WhatsAppDirPath = "/sdcard/WhatsApp/Media/WhatsApp Images";
    private File sdCardDir;

    private static int ScrollspeedSlow = 5000;
    private static int ScrollspeedFast = 100;

    private static float ScreenDimmed = 0.1f;

    private DatabaseController databaseController;
    private WADatabaseController waDatabaseController;
    private PVimageController imageController;

    private static int MY_PERMISSIONS_REQUEST_READ_CONTACTS =  100;

    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //WindowManager.LayoutParams winParams = getWindow().getAttributes();
        //winParams.rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_ROTATE;
        //getWindow().setAttributes(winParams);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        sdCardDir = Environment.getExternalStorageDirectory();
        RootController mRootController =  new RootController();
        mRootController.execute();
        databaseController = new DatabaseController(getApplicationContext());
        //databaseController.deleteDatabase();
        waDatabaseController = new WADatabaseController(sdCardDir.getAbsolutePath() + "/PictureViewer/WADatabases/msgstore.db");
        //databaseController.onUpgrade(databaseController.getWritableDatabase(), 0, 0);
        imageController = new PVimageController(databaseController, waDatabaseController, getApplicationContext());
        imageController.initializeImageList(WhatsAppDirPath);
        //imageController.updateWhatsAppData();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.setPvimagecontroller(imageController);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        try {
            //Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            scroller = new FixedSpeedScroller(mViewPager.getContext());
            scroller.setmDuration(ScrollspeedSlow);
            mScroller.set(mViewPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //mSectionsPagerAdapter.notifyDataSetChanged();

        //WindowManager.LayoutParams layout = getWindow().getAttributes();
        //Log.d("mainActivity.onCreate", "ScreenBrightness: " + layout.screenBrightness);
        //Log.d("mainActivity.onCreate", "DimAmount: " + layout.dimAmount);

        File f = new File(sdCardDir, "PictureViewer");
        if (!f.exists()) {
            f.mkdirs();
            File f1 = new File(sdCardDir, "PictureViewer/WAProfilePictures");
            f1.mkdirs();
            File f2 = new File(sdCardDir, "PictureViewer/WADatabases");
            f2.mkdirs();
            File f3 = new File(sdCardDir, "PictureViewer/WAProfilePictures_small");
            f3.mkdirs();
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("onCreate", "Contacts Permission: " + checkSelfPermission(Manifest.permission.READ_CONTACTS));
        }


        final Activity thisActivty = this;

        //Log.d("MainActivity", "Root: " + ExecuteAsRootBase.canRunRootCommands());


        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread thread, Throwable ex) {
                Intent launchIntent = new Intent(thisActivty, MainActivity.class);
                PendingIntent pending = PendingIntent.getActivity(thisActivty.getApplicationContext(), 0,
                        launchIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, pending);
                defaultHandler.uncaughtException(thread, ex);
            }
        });



        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.imageView_avatar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Empfangen: " + imageController.getPVimage(mViewPager.getCurrentItem()).getReceivedate(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isTouched = 0;
                Log.d("onTouchListener", "touched");
                if (scroller.getmDuration() != ScrollspeedFast) {
                    scroller.setmDuration(ScrollspeedFast);
                    Log.d("onTouchListener", "ScrollspeedFast set");
                }
                if (screenGetCurrentDim() == ScreenDimmed) {
                    screenDim(-1f);
                    mHandler_task3.removeCallbacks(mStatusChecker_task3);
                    mHandler_task3.postDelayed(mStatusChecker_task3, 30000); //don't dim for the next 600 seconds
                    Log.d("onTouchListener", "Screen undimmed");
                }
                return false;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler_task1 = new Handler();
        startRepeatingTask1();

        mHandler_task2 = new Handler();
        mHandler_task2.postDelayed(mStatusChecker_task2, mInterval_task2);
        //startRepeatingTask2();

        mHandler_task3 = new Handler();
        startRepeatingTask3();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRepeatingTask1();
        stopRepeatingTask2();
        stopRepeatingTask3();
        Log.i("onPause", "onPause called");
    }

    @Override
    protected void onDestroy() {
        Log.i("onDestroy", "onDestroy called");
        databaseController.close();
        super.onDestroy();
    }

    public void onImageButtonForeverNewChange(int pVimageID, int dBimageID, boolean forevernew) {
        imageController.updateForevernew(pVimageID, dBimageID, forevernew);
    }

    public void onImageButtonInvisibleChange(int pVimageID, int dBimageID, boolean invisible) {
        imageController.updateInvisible(pVimageID, dBimageID, invisible);
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // finish activity
        System.exit(0);
    }

    void screenDim(float brightness) {
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = brightness;
        getWindow().setAttributes(layout);
    }

    float screenGetCurrentDim() {
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        return layout.screenBrightness;
    }



    Runnable mStatusChecker_task1 = new Runnable() {
        @Override
        public void run() {
            int currentPos;
            Toast toast;

            try {
                Log.d("mStatusChecker_task1", "isTouched:" + isTouched);
                if(isTouched>=(autoScrollDelay-1)) {
                    //change picture
                    //mViewPager.beginFakeDrag();
                    //mViewPager.arrowScroll(View.FOCUS_RIGHT);
                    //mViewPager.arrowScroll(View.FOCUS_LEFT);

                    if (scroller.getmDuration() != ScrollspeedSlow) {
                        scroller.setmDuration(ScrollspeedSlow);
                        Log.d("mStatusChecker_task1", "ScrollspeedSlow set");
                    }
                    currentPos = mViewPager.getCurrentItem();
                    if (currentPos >= mSectionsPagerAdapter.getCount() - 1) {
                        mViewPager.setCurrentItem(0, false);
                    } else {
                        mViewPager.setCurrentItem(currentPos + 1, true);
                    }
                }
                isTouched++;
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler_task1.postDelayed(mStatusChecker_task1, mInterval_task1);
            }

        }
    };

    void startRepeatingTask1() {
        mStatusChecker_task1.run();
    }

    void stopRepeatingTask1() {
        mHandler_task1.removeCallbacks(mStatusChecker_task1);
    }

    Runnable mStatusChecker_task2 = new Runnable() {
        @Override
        public void run() {
            try {
                new UpdateImageData().execute();
                Log.d("mStatusChecker_task2", "Run");
                /*if(imageController.updateImageDatabase()) {
                    //imageController.updateImageList();
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    Log.d("UpdateImageData", "New images have been found");
                }else{
                    Log.d("UpdateImageData", "NO new images have been found");
                }*/


            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler_task2.postDelayed(mStatusChecker_task2, mInterval_task2);
            }
        }
    };

    void startRepeatingTask2() {
        mStatusChecker_task2.run();
    }

    void stopRepeatingTask2() {
        mHandler_task2.removeCallbacks(mStatusChecker_task2);
    }

    Runnable mStatusChecker_task3 = new Runnable() {
        @Override
        public void run() {
            try {
                //dim screen between startDimHour and endDimHour
                    // Calculate next runtime
                    //check Scenarios:
                    // first: after startDimHour and before endDimHour
                    //// dim and check next time short after endDimHour
                    // second: after endDimHour and before startDimHour
                    //// remove dim and check next time short after startDimHour
                    // else: at endDimHour or at startDimHour
                    //// unusual scenario, check again in 10 seconds
                Calendar currentCalendar = Calendar.getInstance();
                int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);
                Calendar nextRunCalendar = Calendar.getInstance();
                if (currentHour >= startDimHour | currentHour < endDimHour) {
                    //first scenario
                    screenDim(ScreenDimmed); //use android OS value
                    if(currentHour >= startDimHour) {
                        //next run is on the next day
                        nextRunCalendar.add(Calendar.DAY_OF_MONTH, 1); //
                        Log.d("mStatusChecker_task3","Scenario1 - 1 day has been added");
                        nextRunCalendar.set(Calendar.HOUR_OF_DAY, endDimHour);
                        nextRunCalendar.set(Calendar.MINUTE, 0);
                        nextRunCalendar.set(Calendar.SECOND, 10);
                    }else{
                        //next run is on the same day
                        nextRunCalendar.set(Calendar.HOUR_OF_DAY, endDimHour);
                        nextRunCalendar.set(Calendar.MINUTE, 0);
                        nextRunCalendar.set(Calendar.SECOND, 10);
                    }
                    Log.d("mStatusChecker_task3","Scenario1 - nextRun: " + (nextRunCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis()));
                    mInterval_task3 = nextRunCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis();

                }else if(currentHour < startDimHour | currentHour >= endDimHour) {
                    //second scenario
                    screenDim(-1f); //use android OS value
                    nextRunCalendar.set(Calendar.HOUR_OF_DAY, startDimHour);
                    nextRunCalendar.set(Calendar.MINUTE, 0);
                    nextRunCalendar.set(Calendar.SECOND, 10);
                    Log.d("mStatusChecker_task3","Scenario2 - currentHour: " + currentHour);
                    Log.d("mStatusChecker_task3","Scenario2 - currentMillis: " + currentCalendar.getTimeInMillis());
                    Log.d("mStatusChecker_task3","Scenario2 - nextRunMillis: " + nextRunCalendar.getTimeInMillis());
                    Log.d("mStatusChecker_task3","Scenario2 - nextRun: " + (nextRunCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis()));
                    mInterval_task3 = nextRunCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis();

                    imageController.updateImageList();
                    mSectionsPagerAdapter.notifyDataSetChanged();

                }else {
                    //else scenario
                    mInterval_task3 = 10000; //check again in 10 seconds
                }


            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler_task3.postDelayed(mStatusChecker_task3, mInterval_task3);
            }
        }
    };



    void startRepeatingTask3() {
        mStatusChecker_task3.run();
    }

    void stopRepeatingTask3() {
        mHandler_task3.removeCallbacks(mStatusChecker_task3);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private PVimageController pvimagecontroller;

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
                return PlaceholderFragment.newInstance(position + 1, pvimagecontroller.getPVimage(position+1));
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return pvimagecontroller.getSize();
            //return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Position " +  position;
        }

        public void setPvimagecontroller(PVimageController controller) {pvimagecontroller = controller; }
    }

    public class FixedSpeedScroller extends Scroller {



        private int mDuration = 5000;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }


        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        public void setmDuration(int mDuration) {
            Log.d("setmDuration", "Old: " + this.mDuration + " " + "New: " +mDuration);
            this.mDuration = mDuration;
        }

        public int getmDuration() {
            return mDuration;
        }


    }
    private class UpdateImageData extends AsyncTask<Void, Void, Boolean> {

        protected Boolean doInBackground(Void... unused) {
            try {
                if(imageController.updateImageDatabase()) {
                    return true;
                }else{
                    return false;
                }

            }catch (Exception e) {
                Log.d("UpdateImageData", "Exception: " + e.getMessage());
            }
            return false;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Boolean result) {
            if(result) {
                mSectionsPagerAdapter.notifyDataSetChanged();
                Log.w("UpdateImageData", "New images have been found");
            }else{
                Log.w("UpdateImageData", "NO new images have been found");
            }
        }
    }
}
