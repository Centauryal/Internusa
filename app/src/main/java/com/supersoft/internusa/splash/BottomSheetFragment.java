package com.supersoft.internusa.splash;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.MsgEvent;
import com.supersoft.internusa.model.Row;
import com.supersoft.internusa.R;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Centaury on 21/04/2018.
 */
public class BottomSheetFragment extends BottomSheetDialogFragment {
    @BindView(R.id.spnQuestion)
    Spinner spnQuestion;
    @BindView(R.id.txtJawaban)
    EditText txtJawaban;


    @BindView(R.id.btnLanjutkan)Button btnLanjutkan;

    @BindView(R.id.lblInfo)TextView lblInfo;
    static Row mRow;
    static ModelRequestInfo mRequest;
    boolean isMerger;
    String question = "";
    public static BottomSheetFragment instance(Row rw, ModelRequestInfo rv)
    {
        BottomSheetFragment fm = new BottomSheetFragment();
        mRow = rw;
        mRequest = rv;
        return fm;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }


            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                //bottomSheet.setPeekHeight(0);
            }


        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet, null);
        ButterKnife.bind(this, contentView);

        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        //CoordinatorLayout.Behavior behavior = params.getBehavior();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        if( behavior != null && behavior instanceof BottomSheetBehavior ) {

            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_EXPANDED);
            //((BottomSheetBehavior) behavior).setPeekHeight(0);
        }


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.pPengaman, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if(mRow.getNama() == null)
        {
            isMerger = false;
            String str = String.format("Buatlah pertanyaan pengamanan Anda, untuk mencegah hal-hal yang tidak di inginkan.");
            lblInfo.setText(str);
        }
        else
        {
            isMerger = true;
            String str = String.format("Anda akan di merger dengan profil [%s], maka pastikan pertanyaan dibawah benar jika memang benar Anda.", mRow.getNama());
            lblInfo.setText(str);
        }

        spnQuestion.setAdapter(adapter);
        spnQuestion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                question = (String)parent.getSelectedItem();
            }
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    @Optional
    @OnClick(R.id.btnLanjutkan)
    public void onSubmit(View view)
    {

        mRequest.pertanyaan = question;
        mRequest.jawaban = txtJawaban.getText().toString();
        if(isMerger) {
            if (mRow.getJawaban().equals(txtJawaban.getText().toString())) {
                mRequest.member_id = mRow.getMemId();
                mRequest.type = "MERGER";
                EventBus.getDefault().post(new MsgEvent(Constant.EVENT_DETAIL_PROFIL, "benar", mRequest, mRow));
                dismiss();
            }
        }
        else
        {
            mRequest.type = "NEW";
            EventBus.getDefault().post(new MsgEvent(Constant.EVENT_DETAIL_PROFIL, "salah", mRequest, mRow));
            dismiss();
        }

    }
}
