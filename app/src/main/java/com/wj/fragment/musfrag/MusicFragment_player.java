package com.wj.fragment.musfrag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.myforever.R;

/**
 * Created by 汪京 on 2016/8/26.
 */
public class MusicFragment_player extends Fragment{

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_music_player, null);
        return view;


    }
}
