package edu.cuhk.csci3310.cufood;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.cuhk.csci3310.cufood.model.MenuCategory;
import edu.cuhk.csci3310.cufood.model.Rating;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class CategoryDialogFragment extends DialogFragment {

    public static final String TAG = "MenuCategoryDialog";
    private Uri selectedImageUri = null;
    private static final String ARG_COMMAND = "command";
    private static final String ARG_ID = "id";
    public static final String ARG_CATEGORY_NAME = "name";
    public static final String ARG_CATEGORY_IMAGE = "image";

    private String command;
    private String categoryId;
    private String categoryName = null;
    private String categoryImage = null;

    public CategoryDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     //     * @param param1 Parameter 1.
     //     * @param param2 Parameter 2.
     * @param  command Parameter 1.
     *  @param  categoryId Parameter 2.
     * @param  categoryName Parameter 2.
     * @param  categoryImage Parameter 3.
     * @return A new instance of fragment CategoryDialogFragment.
     */
    public static CategoryDialogFragment newInstance(String command, String categoryId, String categoryName, String categoryImage) {
        CategoryDialogFragment fragment = new CategoryDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COMMAND, command);
        args.putString(ARG_ID, categoryId);
        args.putString(ARG_CATEGORY_NAME, categoryName);
        args.putString(ARG_CATEGORY_IMAGE, categoryImage);
        fragment.setArguments(args);
        return  fragment;
    }


    // constant to compare
    // the activity result code
    int SELECT_PICTURE = 200;

    @BindView(R.id.dialog_category_title)
    TextView mDialogTitle;

    @BindView(R.id.category_form_name)
    TextView mCategoryName;

//    @BindView(R.id.select_image)
//    Button mSelectImageButton;

    @BindView(R.id.IVPreviewImage)
    ImageView mPreviewImage;

    interface MenuCategoryListener {
        void onCategory(MenuCategory menuCategory);
        void onCategoryEdited(MenuCategory menuCategory, String categoryId);
    }

    private MenuCategoryListener mMenuCategoryListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            command = getArguments().getString(ARG_COMMAND);
            categoryId = getArguments().getString(ARG_ID);
            categoryName = getArguments().getString(ARG_CATEGORY_NAME);
            categoryImage = getArguments().getString(ARG_CATEGORY_IMAGE);
            if (categoryImage != null) {
                selectedImageUri = Uri.parse(categoryImage);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_category, container, false);
        ButterKnife.bind(this, v);
        if (command.equals("edit")) {
            mDialogTitle.setText(R.string.edit_category);
            mCategoryName.setText(categoryName);
            // Load image
            if( categoryImage.equals("")) {
                mPreviewImage.setImageResource(R.drawable.food);
            }
            else {
                Glide.with(mPreviewImage.getContext())
                        .load(categoryImage)
                        .into(mPreviewImage);
            }
            Log.d("categoryName", categoryName);
            Log.d("categoryImage", categoryImage);
        }
        else {
            mDialogTitle.setText(R.string.add_new_category);
        }
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof CategoryDialogFragment.MenuCategoryListener) {
            mMenuCategoryListener = (MenuCategoryListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    @OnClick(R.id.select_image)
    public void selectImage(View view) {
        imageChooser();
    }

    @OnClick(R.id.category_form_submit)
    public void onSubmitClicked(View view) {
        if ( selectedImageUri == null ) {
            selectedImageUri = Uri.parse("");
        }
        MenuCategory category = new MenuCategory(
                mCategoryName.getText().toString(),
                selectedImageUri.toString(),
                false
                );

        if(command.equals("add")) {
            if (mMenuCategoryListener != null) {
                mMenuCategoryListener.onCategory(category);
            }
        }
        else {
            if (mMenuCategoryListener != null) {
                Log.d("categoryId", categoryId);
                mMenuCategoryListener.onCategoryEdited(category, categoryId);
            }
        }

        dismiss();
    }

    @OnClick(R.id.category_form_cancel)
    public void onCancelClicked(View view) {
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
                    mPreviewImage.setImageURI(selectedImageUri);
                }
            }
        }
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        if (command.equals("edit")) {
//            mCategoryName.setText(categoryName);
//            mPreviewImage.setImageURI(Uri.parse(categoryImage));
//        }
//    }


//    @Override
//    public void onAct(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
////        if (command.equals("edit")) {
////            mCategoryName.setText(categoryName);
////            mPreviewImage.setImageURI(Uri.parse(categoryImage));
////        }
//    }
}
