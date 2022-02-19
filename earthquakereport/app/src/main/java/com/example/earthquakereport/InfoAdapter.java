package com.example.earthquakereport;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class InfoAdapter extends ArrayAdapter<Info> {
    public InfoAdapter(Activity context, ArrayList<Info>A)
    {
        super(context,0,A);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view,parent,false);

        Info I = getItem(position);

        double m = new Double (I.mag);
        DecimalFormat D = new DecimalFormat("0.0");
        String magnitude = D.format(m);
        TextView T = convertView.findViewById(R.id.mag);
        T.setText(magnitude);

        TextView MagView = convertView.findViewById(R.id.mag) ;
        GradientDrawable G = (GradientDrawable) MagView.getBackground();
        int Bg = getMagColor(m);
        G.setColor(Bg);

        String S = I.city;
        String city1,city2;
        int index = S.indexOf(" of");
        city1 = S.substring(0,index+3);
        city2 = S.substring(index+4,S.length());

        if(index == -1)
        {
            city1 = "Near the";
            city2 = S;
        }

        T = convertView.findViewById(R.id.city);
        T.setText(city1);

        T = convertView.findViewById(R.id.city2);
        T.setText(city2);

        T = convertView.findViewById(R.id.date);
        T.setText(I.date);

        return convertView;
    }
    public int getMagColor(double m)
    {
        int M = (int) Math.floor(m);
        int color;
        switch(M){
            case 0:
            case 1: color = R.color.magnitude1;break;
            case 2: color = R.color.magnitude2;break;
            case 3: color = R.color.magnitude3;break;
            case 4: color = R.color.magnitude4;break;
            case 5: color = R.color.magnitude5;break;
            case 6: color = R.color.magnitude6;break;
            case 7: color = R.color.magnitude7;break;
            case 8: color = R.color.magnitude8;break;
            case 9: color = R.color.magnitude9;break;
            default: color = R.color.magnitude10plus;break;
        }
        return ContextCompat.getColor(getContext(),color);
    }
}
