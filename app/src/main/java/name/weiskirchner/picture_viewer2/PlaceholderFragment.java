package name.weiskirchner.picture_viewer2;

/**
 * Created by michael on 23.07.17.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_POSITION = "position";
    private static final String ARG_ID = "id";
    private static final String ARG_PATH = "path";
    private static final String ARG_SENDER = "sender";
    private static final String ARG_SENDERNUMBER = "sendernumber";
    private static final String ARG_RECEIVEDATE = "receivedate";
    private static final String ARG_FOREVERNEW = "forevernew";
    private static final String ARG_INVISIBLE = "invisible";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy");

    OnImageButtonForeverNewListener mCallbackForeverNew;
    OnImageButtonInvisibleListener mCallbackInvisible;

    // Container Activity must implement this interface
    public interface OnImageButtonForeverNewListener {
        public void onImageButtonForeverNewChange(int pVimageID, int dBimageID, boolean forevernew);
    }

    // Container Activity must implement this interface
    public interface OnImageButtonInvisibleListener {
        public void onImageButtonInvisibleChange(int pVimageID, int dBimageID, boolean invisible);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallbackForeverNew = (OnImageButtonForeverNewListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnImageButtonForeverNewListener");
        }

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallbackInvisible = (OnImageButtonInvisibleListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnImageButtonInvisibleListener");
        }
    }

    public PlaceholderFragment() {

    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber, PVimage image) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, sectionNumber);
        args.putInt(ARG_ID, image.getId());
        args.putString(ARG_PATH, image.getFilename());
        args.putString(ARG_SENDER, image.getSender());
        args.putString(ARG_SENDERNUMBER, image.getSendernumber());
        args.putString(ARG_RECEIVEDATE, dateFormat.format(image.getReceivedate()));
        args.putInt(ARG_FOREVERNEW, image.isForevernew());
        args.putInt(ARG_INVISIBLE, image.isInvisible());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ZoomableImageView jpgView = (ZoomableImageView) rootView.findViewById(R.id.imageView);
        ImageView avatarView = (ImageView) rootView.findViewById(R.id.imageView_avatar);
        TextView avatarText = (TextView) rootView.findViewById(R.id.textView_avatar);
        //ImageButton imageButton_tag = (ImageButton) rootView.findViewById(R.id.button_favourite);
        ToggleButton imageButton_tag = (ToggleButton) rootView.findViewById(R.id.button_favourite);
        ToggleButton imageButton_inv = (ToggleButton) rootView.findViewById(R.id.button_invisible);

        String path = getArguments().getString(ARG_PATH);
        String avatarpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PictureViewer/WAProfilePictures/" + getArguments().getString(ARG_SENDERNUMBER) + ".jpg";
        String avatarpath_small = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PictureViewer/WAProfilePictures_small/" + getArguments().getString(ARG_SENDERNUMBER) + "@s.whatsapp.net.j";
        File imagefile = new File(path);
        File avatarfile = new File(avatarpath);
        File avatarfile_small = new File(avatarpath_small);
        Bitmap bitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
        jpgView.setImageBitmap(bitmap);
        TextView textViewInfo = (TextView) rootView.findViewById(R.id.imageInfo);
        textViewInfo.setText(getArguments().getString(ARG_RECEIVEDATE));
        Log.d("onCreateView", "Loading Avatar: " + avatarpath);
        if(avatarfile.exists()) {
            RoundedBitmapDrawable avatar_bitmap = RoundedBitmapDrawableFactory.create(getResources(),avatarfile.getAbsolutePath());
            //Bitmap avatar_bitmap = BitmapFactory.decodeFile(avatarfile.getAbsolutePath());
            avatar_bitmap.setCornerRadius(300f);
            avatarView.setImageDrawable(avatar_bitmap);
            avatarText.setText(getArguments().getString(ARG_SENDER));
            Log.d("onCreateView", "High res Avatar found.");
        }else if(avatarfile_small.exists()) {
            Bitmap avatar_bitmap = BitmapFactory.decodeFile(avatarfile_small.getAbsolutePath());
            avatarView.setImageBitmap(avatar_bitmap);
            avatarText.setText(getArguments().getString(ARG_SENDER));
            Log.d("onCreateView", "Low res Avatar found.");
        }else{
            avatarView.setImageResource(R.drawable.ic_account_circle_black_48px);
            avatarText.setText("Unbekannt");
        }
        Log.d("onCreateView", "ARG_FOREVERNEW: " + getArguments().getInt(ARG_FOREVERNEW));
        if(getArguments().getInt(ARG_FOREVERNEW)!=0) {
            imageButton_tag.setChecked(true);
        }else{
            imageButton_tag.setChecked(false);
        }

        if(getArguments().getInt(ARG_INVISIBLE)!=0) {
            imageButton_inv.setChecked(true);
        }else{
            imageButton_inv.setChecked(false);
        }

        ImageView imageView_avatar = (ImageView) rootView.findViewById(R.id.imageView_avatar);
        imageView_avatar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LayoutInflater  mInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                Snackbar snackbar =  Snackbar.make(v, "" , Snackbar.LENGTH_LONG);
                Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
                // Hide the text
                TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
                textView.setVisibility(View.INVISIBLE);
                // Inflate our custom view
                View snackView = mInflater.inflate(R.layout.my_snakebar, null);
                // Configure the view
                TextView textViewLine1 = (TextView) snackView.findViewById(R.id.mysnackbar_text1);
                textViewLine1.setText("Absender: " +  getArguments().getString(ARG_SENDER));
                TextView textViewLine2 = (TextView) snackView.findViewById(R.id.mysnackbar_text2);
                textViewLine2.setText("Datum: " +  getArguments().getString(ARG_RECEIVEDATE));
                // Add the view to the Snackbar's layout
                layout.addView(snackView, 0);
                // Show the Snackbar
                snackbar.show();
            }
        });


        /* imageButton_tag.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("imageButton_tag.onClick", "Current: " + v.isPressed());
                if(!v.findViewById(R.id.button_favourite).isPressed()) {
                    //activate ForeverNew
                    mCallbackForeverNew.onImageButtonForeverNewChange(getArguments().getInt(ARG_ID), true);
                    v.setPressed(false);
                    Log.d("imageButton_tag.onClick", "New: Not Pressed");
                }else{
                    //deactivate ForeverNew
                    mCallbackForeverNew.onImageButtonForeverNewChange(getArguments().getInt(ARG_ID), false);
                    v.findViewById(R.id.button_favourite).setPressed(true);
                    Log.d("imageButton_tag.onClick", "New: Pressed");
                }
            }

        }); */

        imageButton_tag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("imageButton_tag.onClick", "Current: " + isChecked);
                if(isChecked) {
                    //activate ForeverNew
                    mCallbackForeverNew.onImageButtonForeverNewChange(getArguments().getInt(ARG_POSITION),getArguments().getInt(ARG_ID), true);
                    Log.d("imageButton_tag.onClick", "New: Pressed");
                }else{
                    //deactivate ForeverNew
                    mCallbackForeverNew.onImageButtonForeverNewChange(getArguments().getInt(ARG_POSITION),getArguments().getInt(ARG_ID), false);
                    Log.d("imageButton_tag.onClick", "New: Not Pressed");
                }
            }

        });

        imageButton_inv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("imageButton_inv.onClick", "Current: " + isChecked);
                if(isChecked) {
                    //activate Invisible
                    mCallbackInvisible.onImageButtonInvisibleChange(getArguments().getInt(ARG_POSITION),getArguments().getInt(ARG_ID), true);
                    Log.d("imageButton_inv.onClick", "New: Pressed");
                }else{
                    //deactivate Invisible
                    mCallbackInvisible.onImageButtonInvisibleChange(getArguments().getInt(ARG_POSITION),getArguments().getInt(ARG_ID), false);
                    Log.d("imageButton_inv.onClick", "New: Not Pressed");
                }
            }

        });


        //textView.append("Sender " + getArguments().getString(ARG_SENDER));

        return rootView;
    }
}


