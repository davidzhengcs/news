package com.yizheng.newsgateway;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

//    private OnFragmentInteractionListener mListener;

    public StoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StoryFragment newInstance(Story story, int index, int max) {
        StoryFragment fragment = new StoryFragment();
        Bundle args = new Bundle();
        args.putSerializable("story", story);
        args.putInt("index", index);
        args.putInt("total_count", max);
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment_layout = inflater.inflate(R.layout.fragment_story, container, false);

        if (getArguments() == null)
            return fragment_layout;

        final Story currentStory = (Story) getArguments().getSerializable("story");

        if (currentStory == null) {
            return fragment_layout;
        }

        int index = getArguments().getInt("index");
        int total = getArguments().getInt("total_count");

        TextView headline = fragment_layout.findViewById(R.id.textView4);
        if (currentStory.getTitle() != null && !currentStory.getTitle().equals("null")) {
            headline.setText(currentStory.getTitle());
            headline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(currentStory.getUrl()));
                    startActivity(intent);
                }
            });
        } else {
            headline.setVisibility(View.INVISIBLE);
        }

        TextView date = fragment_layout.findViewById(R.id.textView5);
        try {
            if (currentStory.getPublishedAt() != null && !currentStory.getPublishedAt().equals("null")) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US);
                //format.setTimeZone(TimeZone.getTimeZone("UTC"));
                date.setText(format.parse(currentStory.getPublishedAt()).toString());
            } else {
                date.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e){
            date.setVisibility(View.INVISIBLE);
        }

        TextView author = fragment_layout.findViewById(R.id.textView6);
        if (currentStory.getAuthor() != null && !currentStory.getAuthor().equals("null")) {
            author.setText(currentStory.getAuthor());
        } else {
            author.setVisibility(View.INVISIBLE);
        }

        TextView newsText = fragment_layout.findViewById(R.id.textView7);
        newsText.setText(currentStory.getDescription());
        newsText.setMovementMethod(new ScrollingMovementMethod());

        newsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(currentStory.getUrl()));
                startActivity(intent);
            }
        });

        TextView pageNum = fragment_layout.findViewById(R.id.textView8);
        pageNum.setText(String.format(Locale.US, "%d of %d", index, total));

        ImageView imageView = fragment_layout.findViewById(R.id.imageView);
        if (currentStory.getUrlToImage() != null && !currentStory.getUrlToImage().isEmpty())
            loadImage(currentStory.getUrlToImage(), imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(currentStory.getUrl()));
                startActivity(intent);
            }
        });

        return fragment_layout;

    }

//    public void articleClicked(View v){
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(uri);
//        startActivity(intent);
//    }

    private void loadImage(final String url, ImageView imageView) {

        Picasso picasso = new Picasso.Builder(getContext()).build();
        picasso.setLoggingEnabled(true);
        picasso.load(url).error(R.drawable.brokenimage).placeholder(R.drawable.loading).into(imageView);

    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
