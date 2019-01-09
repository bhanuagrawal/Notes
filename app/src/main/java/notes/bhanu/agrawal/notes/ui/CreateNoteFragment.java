package notes.bhanu.agrawal.notes.ui;


import android.Manifest;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import notes.bhanu.agrawal.notes.NotesViewModel;
import notes.bhanu.agrawal.notes.R;
import notes.bhanu.agrawal.notes.Util;
import notes.bhanu.agrawal.notes.data.entities.Note;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateNoteFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int RESULT_LOAD_IMAGE = 12;
    private static final String IMAGE_URL = "image_url";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.title)
    EditText title;


    @BindView(R.id.text)
    EditText text;


    @BindView(R.id.upload_image)
    Button imageUploadButton;


    @BindView(R.id.submit)
    Button addButton;


    private NotesViewModel notesViewModel;
    private FragmentInteraction mListener;


    private ProgressDialog dialog;

    private String uploadImageUrl = "";

    public CreateNoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateNoteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateNoteFragment newInstance(String param1, String param2) {
        CreateNoteFragment fragment = new CreateNoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        notesViewModel = ViewModelProviders.of((FragmentActivity) getActivity()).get(NotesViewModel.class);
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading");
        if(savedInstanceState!= null){
            uploadImageUrl = savedInstanceState.getString(IMAGE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_note2, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        imageUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE);
                }
                else {
                    selectImage();
                }
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(title.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Title cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }



                Observable<Void> o = Observable.create(new ObservableOnSubscribe<Void>() {
                    @Override
                    public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                        try {
                            notesViewModel.addNote(new Note(title.getText().toString(), text.getText().toString(), new Date(), uploadImageUrl));
                            emitter.onComplete();
                        }
                        catch (Exception e){
                            emitter.onError(e);
                        }
                    }


                }).subscribeOn(Schedulers.newThread()).
                        observeOn(AndroidSchedulers.mainThread());


                o.subscribe(new Observer<Void>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        dialog.show();
                    }

                    @Override
                    public void onNext(Void aVoid) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        if(dialog.isShowing()){
                            dialog.dismiss();

                        }
                    }

                    @Override
                    public void onComplete() {
                        mListener.navigateTo(R.id.allNotesFragment, null);
                        if(dialog.isShowing()){
                            dialog.dismiss();

                        }
                    }
                });

            }
        });
    }

    private void selectImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInteraction) {
            mListener = (FragmentInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null !=data){
            saveImage(data.getData());
            
        }
    }

    private void saveImage(final Uri uri) {

        Observable<String> o = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                try {
                    String url = Util.saveToInternalStorage(getContext(), uri);
                    emitter.onNext(url);
                    emitter.onComplete();

                }
                catch (Exception e) {
                    emitter.onError(e);
                }
            }


        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        o.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                dialog.show();
            }

            @Override
            public void onNext(String s) {
                uploadImageUrl = s;
            }

            @Override
            public void onError(Throwable e) {
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onComplete() {
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
                Toast.makeText(getContext(), "Image added to note", Toast.LENGTH_LONG).show();
                imageUploadButton.setVisibility(View.GONE);
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(getActivity(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(uploadImageUrl != null){
            outState.putString(IMAGE_URL, uploadImageUrl);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
