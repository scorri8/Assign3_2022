package com.example.sdaassign32022;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


/*
 * A simple {@link Fragment} subclass.
 * @author Stephen Corri 2022
 */
public class ProductList extends Fragment {

    private static final String TAG = "RecyclerViewActivity";
    private ArrayList<FlavorAdapter> mFlavor = new ArrayList<>();

    public ProductList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_product_list, container, false);
        // Create an ArrayList of AndroidFlavor objects
        mFlavor.add(new FlavorAdapter("Hat", "1.0", R.drawable.hat));
        mFlavor.add(new FlavorAdapter("Hoodie", "2.0", R.drawable.hoodie));
        mFlavor.add(new FlavorAdapter("Jumper", "3.0", R.drawable.jumper));
        mFlavor.add(new FlavorAdapter("Long Sleeve Shirt", "4.0", R.drawable.long_sleeve_shirt));
        mFlavor.add(new FlavorAdapter("Polo Shirt", "5.0", R.drawable.polo_shirt));
        mFlavor.add(new FlavorAdapter("V Neck", "6.0", R.drawable.v_neck));


        //start it with the view
        Log.d(TAG, "Starting recycler view");
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView_view);
        FlavorViewAdapter recyclerViewAdapter = new FlavorViewAdapter(getContext(), mFlavor);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
    }
}
