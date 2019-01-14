package notes.bhanu.agrawal.notes.ui;


import android.Manifest;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import androidx.work.Data;
import androidx.work.WorkInfo;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import notes.bhanu.agrawal.notes.Constants;
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
    private Observer<List<WorkInfo>> workObserver;
    private Observer<Util.NoteSaveStatus> noteSaveStatusObserver;



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


        workObserver = (List<WorkInfo> workInfos) -> {

            if (workInfos == null || workInfos.isEmpty()) {
                return;
            }

            // We only care about the one output status.
            // Every continuation has only one worker tagged TAG_OUTPUT
            WorkInfo workInfo = workInfos.get(0);

            boolean finished = workInfo.getState().isFinished();


            if (!finished) {
                dialog.show();
            } else if(notesViewModel.getImageSaveWork() != null){

                dialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.image_added), Toast.LENGTH_SHORT).show();
                Data outputData = workInfo.getOutputData();

                String outputImageUri =
                        outputData.getString(Constants.IMAGE_URL);

                if (!TextUtils.isEmpty(outputImageUri)) {
                    notesViewModel.setSavedImageUrl(outputImageUri);
                }
                imageUploadButton.setVisibility(View.GONE);

            }
        };


        noteSaveStatusObserver = (Util.NoteSaveStatus noteSaveStatus) -> {
            if(noteSaveStatus == Util.NoteSaveStatus.SAVING){
                dialog.show();
            }
            else if(noteSaveStatus == Util.NoteSaveStatus.SAVED){
                dialog.dismiss();
                notesViewModel.setSavedImageUrl(null);
                notesViewModel.setImageSaveWork(null);
                notesViewModel.getNoteSaveStatusMutableLiveData().postValue(Util.NoteSaveStatus.NOT_SAVED);
                mListener.navigateTo(R.id.action_createNoteFragment_to_allNotesFragment, null);
            }
        };
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
                notesViewModel.addNote(new Note(title.getText().toString(), text.getText().toString(), new Date(), notesViewModel.getSavedImageUrl()));

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        notesViewModel.getmImageSaveWorkInfo().observe(getActivity(), workObserver);
        notesViewModel.getNoteSaveStatusMutableLiveData().observe(getActivity(), noteSaveStatusObserver);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null !=data){
            notesViewModel.saveImage(data.getData());
        }
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
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        notesViewModel.getmImageSaveWorkInfo().removeObserver(workObserver);
        notesViewModel.getNoteSaveStatusMutableLiveData().removeObserver(noteSaveStatusObserver);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
