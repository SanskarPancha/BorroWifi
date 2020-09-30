package com.hitket.borrowifi;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class popup_layout extends DialogFragment implements View.OnClickListener{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.popup_info, null);
        Bundle mArgs = getArguments();
        String s = mArgs.getString("key");
        TextView tv =v.findViewById(R.id.tv_info);
        Button bu = v.findViewById(R.id.bu_info_ok);
        bu.setOnClickListener(this);
        tv.setText(s);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v)
                .setTitle(R.string.info);
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        this.dismiss();
    }
}
