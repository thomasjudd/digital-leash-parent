package com.example.tom.digitalleashparent;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class StatusFragment extends Fragment {
    private boolean childInBounds;
    private ImageView imageView;
    private Button returnFromStatusButton;
    public StatusFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        childInBounds = getArguments().getBoolean("childInBounds");
        imageView = (ImageView) view.findViewById(R.id.statusImage);
        returnFromStatusButton = (Button) view.findViewById(R.id.returnFromStatus);
        returnFromStatusButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.mainContainer, new ParentFragment());
            }
        });
        if(childInBounds) {
            imageView.setImageResource(R.drawable.status_success);
            //TODO background colors
            //imageView.setBackgroundColor();
        } else {
            imageView.setImageResource(R.drawable.status_fail);
        }
        return view;
    }
}
