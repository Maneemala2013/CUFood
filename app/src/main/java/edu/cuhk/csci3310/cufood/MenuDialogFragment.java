package edu.cuhk.csci3310.cufood;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.cuhk.csci3310.cufood.model.Menu;
import edu.cuhk.csci3310.cufood.model.MenuCategory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuDialogFragment extends DialogFragment {

    public static final String TAG = "MenuDialogFragment";
    private Uri selectedImageUri = null;
    private static final String ARG_COMMAND = "command";
    public static final String ARG_MENU_ID = "id";
    public static final String ARG_MENU_NAME = "name";
    public static final String ARG_MENU_PRICE = "price";
    public static final String ARG_MENU_DESC = "description";
    public static final String ARG_MENU_AVAILABLE = "available";
    public static final String ARG_MENU_IMAGE = "image";
    public static final String ARG_MENU_CATEGORY = "category";

    private String command;
    private String menuId = null;
    private String menuName = null;
    private double menuPrice = 0;
    private String menuDesc = null;
    private boolean menuAvailable = true;
    private String menuImage = null;
    private String menuCategoryId = null;

    public MenuDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MenuDialogFragment.
     */
    // String menuId, String menuName, double menuPrice, boolean menuAvailable, String menuImage
    public static MenuDialogFragment newInstance(String command, Menu menu, String menuId, String categoryId) {
        MenuDialogFragment fragment = new MenuDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COMMAND, command);
        if (menu != null) {
            args.putString(ARG_MENU_ID, menuId);
            args.putString(ARG_MENU_NAME, menu.getMenuName());
            args.putDouble(ARG_MENU_PRICE, menu.getPrice());
            args.putString(ARG_MENU_DESC, menu.getDescription());
            args.putBoolean(ARG_MENU_AVAILABLE, menu.getAvailableStatus());
            args.putString(ARG_MENU_IMAGE, menu.getPhoto());
        }
//        else {
//            args.putString(ARG_MENU_ID, null);
//            args.putString(ARG_MENU_NAME, null);
//            args.putDouble(ARG_MENU_PRICE, 0);
//            args.putBoolean(ARG_MENU_AVAILABLE, true);
//            args.putString(ARG_MENU_IMAGE, null);
//        }
        args.putString(ARG_MENU_CATEGORY, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    interface MenuListener {
        void onMenuAdded(Menu menu);
        void onMenuEdited(Menu menu, String menuId);
        void onMenuDelete(String menuId);
    }

    private MenuDialogFragment.MenuListener mMenuListener;

    int SELECT_PICTURE = 200;

    @BindView(R.id.dialog_menu_title)
    TextView mTitle;

    @BindView(R.id.menu_form_name)
    EditText mMenuName;

    @BindView(R.id.menu_form_price)
    EditText mMenuPrice;

    @BindView(R.id.menu_form_description)
    EditText mMenuDescription;

    @BindView(R.id.menu_available_status)
    Switch mMenuAvailable;

    @BindView(R.id.menu_select_image)
    Button mSelectImage;

    @BindView(R.id.menu_IVPreviewImage)
    ImageView mMenuPreview;

    @BindView(R.id.menu_form_delete)
    Button mDeleteButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            command = getArguments().getString(ARG_COMMAND);
            menuId = getArguments().getString(ARG_MENU_ID);
            menuName = getArguments().getString(ARG_MENU_NAME);
            menuPrice = getArguments().getDouble(ARG_MENU_PRICE);
            menuDesc = getArguments().getString(ARG_MENU_DESC);
            menuAvailable = getArguments().getBoolean(ARG_MENU_AVAILABLE);
            menuImage = getArguments().getString(ARG_MENU_IMAGE);
            menuCategoryId = getArguments().getString(ARG_MENU_CATEGORY);
            if (menuImage != null) {
                selectedImageUri = Uri.parse(menuImage);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.dialog_menu, container, false);
        ButterKnife.bind(this, v);
        if (command.equals("edit")) {
            mTitle.setText("Edit a menu");
            mMenuName.setText(menuName);
            mMenuPrice.setText(String.valueOf(menuPrice));
            mMenuDescription.setText(menuDesc);
            mMenuAvailable.setChecked(menuAvailable);
            // Load image
            if( !menuImage.equals("")) {
                Glide.with(mMenuPreview.getContext())
                        .load(menuImage)
                        .into(mMenuPreview);
            }
            mDeleteButton.setVisibility(View.VISIBLE);
        }
//        else {
//            mDialogTitle.setText(R.string.add_new_category);
//        }
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MenuDialogFragment.MenuListener) {
            mMenuListener = (MenuDialogFragment.MenuListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    @OnClick(R.id.menu_select_image)
    public void selectImage(View view) {
        imageChooser();
    }

    @OnClick(R.id.menu_form_submit)
    public void onSubmitClicked(View view) {
        if ( selectedImageUri == null ) {
            selectedImageUri = Uri.parse("");
        }
        Menu menu = new Menu(
                mMenuName.getText().toString(),
                Double.parseDouble(mMenuPrice.getText().toString()),
                selectedImageUri.toString(),
                mMenuDescription.getText().toString(),
                mMenuAvailable.isChecked(),
                menuCategoryId

        );

        if(command.equals("add")) {
            if (mMenuListener != null) {
                mMenuListener.onMenuAdded(menu);
            }
        }
        else {
            if (mMenuListener != null) {
                Log.d("categoryId", menuId);
                mMenuListener.onMenuEdited(menu, menuId);
            }
        }
        Log.d(TAG, "onSubmitClicked");
        dismiss();
    }

    @OnClick(R.id.menu_form_cancel)
    public void onCancelClicked(View view) {
        dismiss();
    }

    @OnClick(R.id.menu_form_delete)
    public void onDeleteClicked(View vies) {
        if (mMenuListener != null) {
            Log.d("categoryId", menuId);
            mMenuListener.onMenuDelete(menuId);
        }
        dismiss();
    }

    // this function is triggered when
    // the Select Image Button is clicked
    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                selectedImageUri = data.getData();
                Log.d("selectedImageURi", String.valueOf(selectedImageUri));
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    mMenuPreview.setImageURI(selectedImageUri);
                }
            }
        }
    }
}