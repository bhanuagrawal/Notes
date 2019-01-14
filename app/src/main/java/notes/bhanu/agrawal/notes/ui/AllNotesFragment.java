package notes.bhanu.agrawal.notes.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
 * Use the {@link AllNotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllNotesFragment extends Fragment implements ItemAdater.ItemAdaterListner {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentInteraction mListener;



    @BindView(R.id.all_notes)
    RecyclerView recyclerView;


    @BindView(R.id.add_note)
    Button addNote;


    ItemAdater<Note> itemAdater;
    private NotesViewModel notesViewModel;
    private Observer<List<Note>> notesObserver;

    public AllNotesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllNotesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllNotesFragment newInstance(String param1, String param2) {
        AllNotesFragment fragment = new AllNotesFragment();
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

        notesObserver = new Observer<List<Note>>() {
            @Override
            public void onChanged(@Nullable List<Note> notes) {
                itemAdater.onDataChange((ArrayList<Note>) notes);
            }
        };

        itemAdater = new ItemAdater<>(getContext(), this, ItemAdater.NOTES);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_notes, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.navigateTo(R.id.createNoteFragment, null);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(itemAdater);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        notesViewModel.getNotes().observe(this, notesObserver);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        notesViewModel.getNotes().removeObserver(notesObserver);
    }

    @Override
    public void onItemDelete(final Note note) {
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

    @Override
    public void onItemselected(Note note) {
        Bundle bundle = new Bundle();
        bundle.putInt(SingleNoteFragment.NOTE_ID, note.getNoteId());
        mListener.navigateTo(R.id.singleNoteFragment, bundle);
    }
}
