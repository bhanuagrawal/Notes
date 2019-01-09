package notes.bhanu.agrawal.notes.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import notes.bhanu.agrawal.notes.R;
import notes.bhanu.agrawal.notes.data.entities.Note;

public class ItemAdater<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    public static final int NOTES = 0;

    private final int itemVIewType;
    private ArrayList<T> mData;
    private ItemAdaterListner itemAdaterListner;
    private Context context;

    public ItemAdater(Context context, ItemAdaterListner itemAdaterListner, int viewType) {
        this.mData = new ArrayList<>();
        this.itemAdaterListner = itemAdaterListner;
        this.context = context;
        this.itemVIewType = viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (itemVIewType){
            case NOTES:
                NoteViewHolder noteViewHolder = new NoteViewHolder(LayoutInflater.from(context).inflate(R.layout.note, parent, false));
                return noteViewHolder;
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()){
            case NOTES:
                ((NoteViewHolder)holder).onBind((Note) mData.get(position));
                break;
            default:
                ((NoteViewHolder)holder).onBind((Note) mData.get(position));
        }



    }


    @Override
    public int getItemViewType(int position) {
        return itemVIewType;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void onDataChange(ArrayList<T> data) {
        this.mData.clear();
        this.mData.addAll(data);
        notifyDataSetChanged();
    }


    class NoteViewHolder extends RecyclerView.ViewHolder {

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




        public NoteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }


        public void onBind(final Note note) {
            title.setText(note.getTitle());
            text.setText(note.getText());
            time.setText(note.getTimeCreated().toString());
            if(note.getImageURL()!=null &&
                    !note.getImageURL().isEmpty()){

                image.setVisibility(View.VISIBLE);

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.ic_action_name);

                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(note.getImageURL()).into(image);
            }
            else{
                image.setVisibility(View.GONE);
            }

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemAdaterListner.onItemDelete(note);
                }
            });

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemAdaterListner.onItemselected(note);
                }
            });

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemAdaterListner.onItemselected(note);
                }
            });

            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemAdaterListner.onItemselected(note);
                }
            });

        }


    }


    public interface ItemAdaterListner{
        void onItemDelete(Note note);
        void onItemselected(Note note);
    }
}
