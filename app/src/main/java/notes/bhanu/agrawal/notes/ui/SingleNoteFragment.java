package notes.bhanu.agrawal.notes.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import notes.bhanu.agrawal.notes.NotesViewModel;
import notes.bhanu.agrawal.notes.R;
import notes.bhanu.agrawal.notes.data.entities.Note;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleNoteFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String NOTE_ID = "noteId";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentInteraction mListener;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.text)
    TextView text;

    @BindView(R.id.time)
    TextView time;

    @BindView(R.id.image)
    ImageView image;

    @BindView(R.id.delete)
    ImageView delete;
    private NotesViewModel notesViewModel;
    private Observer<List<Note>> notesObserver;
    private int noteId;

    public SingleNoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SingleNoteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SingleNoteFragment newInstance(String param1, String param2) {
        SingleNoteFragment fragment = new SingleNoteFragment();
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
            noteId = getArguments().getInt(NOTE_ID);
        }
        notesViewModel = ViewModelProviders.of((FragmentActivity) getActivity()).get(NotesViewModel.class);
        notesObserver = new Observer<List<Note>>() {
            @Override
            public void onChanged(@Nullable List<Note> notes) {
                for(Note note :notes){
                    if(note.getNoteId() == noteId){
                        bindToView(note);
                        return;
                    }
                }
                closeFragment();

            }
        };

    }

    private void closeFragment() {
        mListener.navigateTo(R.id.action_singleNoteFragment_to_allNotesFragment, null);
    }

    private void bindToView(final Note note) {
        title.setText(note.getTitle());
        text.setText(note.getText());
        time.setText(note.getTimeCreated().toString());
        if(note.getImageURL()!=null &&
                !note.getImageURL().isEmpty()){

            image.setVisibility(View.VISIBLE);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.ic_action_name);

            Glide.with(getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(note.getImageURL()).into(image);
        }
        else{
            image.setVisibility(View.GONE);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable<Void> o = Observable.create(new ObservableOnSubscribe<Void>() {
                    @Override
                    public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                        try {
                            notesViewModel.deleteNote(note);
                            emitter.onComplete();
                        }
                        catch (Exception e){
                            emitter.onError(e);
                        }
                    }


                }).subscribeOn(Schedulers.newThread()).
                        observeOn(AndroidSchedulers.mainThread());


                o.subscribe(new io.reactivex.Observer<Void>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Void aVoid) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_note, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        notesViewModel.getNotes().observe(getActivity(), notesObserver);

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
    public void onDestroyView() {
        super.onDestroyView();
        notesViewModel.getNotes().removeObserver(notesObserver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
