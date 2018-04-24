package com.supersoft.internusa.ui.profil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.model.ProfilDB;
import com.supersoft.internusa.model.mProfesi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itclub21 on 4/16/2017.
 */

public class ProfesiFragment extends Fragment {

    @BindView(R.id.lvwData)
    ListView lvwData;

    @BindView(R.id.btnCreate)
    FloatingActionButton btnCreate;

    @BindView(R.id.pgBar)
    ProgressBar pgBar;

    @BindView(R.id.lnInfo)
    LinearLayout lnInfo;

    @BindView(R.id.lblInfo)
    TextView lblInfo;

    ArrayList<mProfesi> profesiList = new ArrayList<>();
    ProfesiAdapter mAdapter;
    static int ADD_PROFESI_REQUEST_CODE = 111;
    private AlertDialog progressDialog;

    public static ProfesiFragment newInstance()
    {
        ProfesiFragment fm = new ProfesiFragment();
        return fm;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profesi_fragment, null);
        ButterKnife.bind(this, view);
        progressDialog = new SpotsDialog(getContext(), R.style.Custom);
        mAdapter = new ProfesiAdapter(getContext(), profesiList);
        lvwData.setAdapter(mAdapter);


        loadProfesi();
        return view;
    }


    @Optional
    @OnClick(R.id.btnCreate)
    public void inSubmit(View view)
    {
        Intent intenProfesi = new Intent(getActivity(), AddProfesiActivity.class);
        intenProfesi.putExtra("CRUD", "create");
        startActivityForResult(intenProfesi, ADD_PROFESI_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.e("CODE ", "RE : " + requestCode + " RES " + resultCode + "= " + ADD_PROFESI_REQUEST_CODE);
        if(resultCode == getActivity().RESULT_OK)
        {
            if(requestCode == ADD_PROFESI_REQUEST_CODE)
            {
                loadProfesi();
            }
        }
    }


    private void deleteProfesi(final String id)
    {
        progressDialog.show();

        RetrofitBuilder builder = new RetrofitBuilder("createProfesi");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<List<mProfesi>> call = githubUserAPI.listProfesi(String.format(Constant.CONTROLLER_4S, Constant.CONTROLLER_DEV, "getProfesi","delete",id));
        call.enqueue(new Callback<List<mProfesi>>() {
            @Override
            public void onResponse(Call<List<mProfesi>> call, Response<List<mProfesi>> response) {
                if(progressDialog.isShowing())progressDialog.dismiss();
                if(!response.isSuccessful())
                {
                    return;
                }
                ArrayList<mProfesi> profes = new ArrayList<mProfesi>(response.body());
                if(profes.size() > 0)
                {
                    loadProfesi();
                }
                else
                    Constant.showInfoMessageDialog(getContext(), "Profesi tidak bisa di delete", "FAILED");

            }

            @Override
            public void onFailure(Call<List<mProfesi>> call, Throwable t) {
                if(progressDialog.isShowing())progressDialog.dismiss();
            }
        });
    }

    private void loadProfesi()
    {
        pgBar.setVisibility(View.VISIBLE);
        DBHelper _db = new DBHelper(getContext());
        ProfilDB pf = _db.getProfilDb();
        RetrofitBuilder builder = new RetrofitBuilder("getProfesi");
        RetroBuilderInterface service = builder.getRetrofit().create(RetroBuilderInterface.class);
        final Call<List<mProfesi>> repos = service.listProfesi(String.format(Constant.CONTROLLER_4S, Constant.CONTROLLER_DEV, "getProfesi","read",pf.hp));
        repos.enqueue(new Callback<List<mProfesi>>() {
            @Override
            public void onResponse(Call<List<mProfesi>> call, Response<List<mProfesi>> response) {
                pgBar.setVisibility(View.GONE);
                Log.e("res", new Gson().toJson(response.body()));
                if(!response.isSuccessful())
                {
                    lnInfo.setVisibility(View.VISIBLE);
                    lblInfo.setText("Oops, tidak mendapat response dari server");
                    return;
                }

                profesiList = new ArrayList<mProfesi>(response.body());
                if(profesiList.size() > 0)
                {
                    if(getContext() != null) {
                        mAdapter = new ProfesiAdapter(getContext(), profesiList);
                        mAdapter.notifyDataSetChanged();
                        lvwData.setAdapter(mAdapter);
                    }
                }
                else
                {
                    lnInfo.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<List<mProfesi>> call, Throwable t) {
                pgBar.setVisibility(View.GONE);
                lnInfo.setVisibility(View.VISIBLE);
                lblInfo.setText("Oops, tidak mendapat response dari server");
            }
        });
    }

    public static class HolderView
    {

        @BindView(R.id.imgBtnMore)
        ImageButton imgBtnMore;

        @BindView(R.id.txtProfesi)
        TextView txtProfesi;

        @BindView(R.id.txtPosisi)
        TextView txtPosisi;

        @BindView(R.id.txtLulusan)
        TextView txtLulusan;

        @BindView(R.id.txtJurusan)
         TextView txtJurusan;

        @BindView(R.id.txtInstansi)
        TextView txtInstansi;

        @BindView(R.id.txtDescription)
        TextView txtDescription;

        public HolderView(View vw)
        {
            ButterKnife.bind(this, vw);
        }
    }

    private class ProfesiAdapter extends BaseAdapter
    {
        Context _context;
        ArrayList<mProfesi> listData;
        BottomSheetDialog mBottomSheetDialog;
        public ProfesiAdapter(Context ctx, ArrayList<mProfesi> pro)
        {
            _context = ctx;
            listData = pro;
            mBottomSheetDialog = new BottomSheetDialog(ctx);
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public mProfesi getItem(int i) {
            return listData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            HolderView holder;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.profesi_fragment_card_item, null);
                holder = new HolderView(view);
                view.setTag(holder);
            } else holder = (HolderView) view.getTag();

            holder.txtProfesi.setText(getItem(i).getBidang());
            holder.txtPosisi.setText(getItem(i).getPosisi());
            holder.txtInstansi.setText(getItem(i).getInstansi());
            holder.txtLulusan.setText(getItem(i).getSekoNama());
            holder.txtJurusan.setText(getItem(i).getJurNama());
            holder.txtDescription.setText(getItem(i).getDeskripsi());
            holder.imgBtnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View sheetView = getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_edit_delete, null);
                    mBottomSheetDialog.setContentView(sheetView);
                    mBottomSheetDialog.show();

                    sheetView.findViewById(R.id.fragment_history_bottom_sheet_delete).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Log.e("DELETE ", "ID " + getItem(i).getId());
                            mBottomSheetDialog.dismiss();
                            deleteProfesi(getItem(i).getId());
                        }
                    });

                    sheetView.findViewById(R.id.fragment_history_bottom_sheet_edit).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Log.e("EDIT ", "ID " + getItem(i).getId());
                            mBottomSheetDialog.dismiss();
                            Intent intenProfesi = new Intent(getActivity(), AddProfesiActivity.class);
                            intenProfesi.putExtra("CRUD", "update");
                            intenProfesi.putExtra("ID", getItem(i).getId());
                            startActivityForResult(intenProfesi, ADD_PROFESI_REQUEST_CODE);
                        }
                    });
                }
            });

            return view;
        }
    }

}
