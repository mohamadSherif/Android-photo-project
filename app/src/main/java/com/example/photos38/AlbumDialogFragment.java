package com.example.photos38;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class AlbumDialogFragment extends DialogFragment {
    public static final String MESSAGE_KEY = "message_key";

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        builder.setMessage(bundle.getString(MESSAGE_KEY))
                .setPositiveButton("OK", (dialog, id) -> {
                });
        return builder.create();
    }
}
