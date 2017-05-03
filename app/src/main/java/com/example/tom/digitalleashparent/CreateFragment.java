package com.example.tom.digitalleashparent;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateFragment extends Fragment {
    private Button returnToParent;

    public CreateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
 //       returnToParent = (Button) getView().findViewById(R.id.returnToParent);
 //       returnToParent.setOnClickListener(new View.OnClickListener(){

            /*@Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.mainContainer, new ParentFragment());
            }
        });*/
        return inflater.inflate(R.layout.fragment_create, container, false);
    }
}
