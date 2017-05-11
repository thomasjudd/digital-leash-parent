package com.example.tom.digitalleashparent;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class StatusFragment extends Fragment {
    private boolean childInBounds;
    private ImageView imageView;
    private TextView statusText;
    private Button returnFromStatusButton;
    public StatusFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        childInBounds = getArguments().getBoolean("childInBounds");
        imageView = (ImageView) view.findViewById(R.id.statusImage);
        statusText = (TextView) view.findViewById(R.id.statusText);
        returnFromStatusButton = (Button) view.findViewById(R.id.returnFromStatus);
        returnFromStatusButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.mainContainer, new ParentFragment()).commit();
            }
        });
        if(childInBounds) {

            imageView.setImageResource(R.drawable.status_success);
            imageView.setBackgroundColor(Color.parseColor("#0099C9"));
            view.setBackgroundColor(Color.parseColor("#0099C9"));
            statusText.setText("Child is within the radius");
        } else {
            imageView.setImageResource(R.drawable.status_fail);
            imageView.setBackgroundColor(Color.parseColor("#D0263A"));
            view.setBackgroundColor(Color.parseColor("#D0263A"));
            statusText.setText("Child isn't within the radius");
        }
        return view;
    }
}
