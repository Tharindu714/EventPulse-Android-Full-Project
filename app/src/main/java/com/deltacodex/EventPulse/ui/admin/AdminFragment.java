package com.deltacodex.EventPulse.ui.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.deltacodex.EventPulse.R;
import com.deltacodex.EventPulse.Utils.StatusBarUtils;

public class AdminFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        if (getActivity() != null) {
            StatusBarUtils.applyGradientStatusBar(getActivity());  // Pass the Activity context
        }
        FrameLayout adminButton = view.findViewById(R.id.AdminLayout); // Replace with your button ID
        // Set the onClick listener for the button
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClassName("com.deltacodex.epadmins","com.deltacodex.epadmins.SplashActivity");
                startActivity(intent);
            }
        });
        return view;
    }
}