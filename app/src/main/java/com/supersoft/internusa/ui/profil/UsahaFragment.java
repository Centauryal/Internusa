package com.supersoft.internusa.ui.profil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.imagePicker.Image;
import com.supersoft.internusa.helper.pageIndicator.CirclePageIndicator;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.CircleImageView;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.model.ProfilDB;
import com.supersoft.internusa.model.mUsaha;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itclub21 on 4/13/2017.
 */

public class UsahaFragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.lvwData)
    ListView lvwData;

    @BindView(R.id.btnCreate)
    FloatingActionButton btnCreate;

    @BindView(R.id.lnInfo)
    LinearLayout lnInfo;

    @BindView(R.id.lblInfo)
    TextView lblInfo;

    @BindView(R.id.pgBar)
    ProgressBar pgBar;

    private AlertDialog progressDialog;
    ArrayList<mUsaha> profesiList = new ArrayList<>();
    UsahaAdapter mAdapter;
    private static int REQUEST_CODE_ADD_USAHA = 201;

    public static UsahaFragment newInstance()
    {
        UsahaFragment fm = new UsahaFragment();
        return fm;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.usaha_fragment, null);
        ButterKnife.bind(this, view);
        btnCreate.setOnClickListener(this);
        progressDialog = new SpotsDialog(getContext(), R.style.Custom);
        mAdapter = new UsahaAdapter(getContext(), profesiList);
        lvwData.setAdapter(mAdapter);

        loadUsaha();
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.e("CODE ", "RE : " + requestCode + " RES " + resultCode + "= " + ADD_PROFESI_REQUEST_CODE);
        if(resultCode == getActivity().RESULT_OK)
        {
            if(requestCode == REQUEST_CODE_ADD_USAHA)
            {
                loadUsaha();
            }
        }
    }
    private void loadUsaha()
    {
        pgBar.setVisibility(View.VISIBLE);

        ProfilDB pf = new DBHelper(getContext()).getProfilDb();
        RetrofitBuilder builder = new RetrofitBuilder("getUsaa");
        RetroBuilderInterface service = builder.getRetrofit().create(RetroBuilderInterface.class);
        final Call<List<mUsaha>> repos = service.listUsaha(String.format(Constant.CONTROLLER_4S, Constant.CONTROLLER_DEV, "getUsahaku","read",pf.hp));
        repos.enqueue(new Callback<List<mUsaha>>() {
            @Override
            public void onResponse(Call<List<mUsaha>> call, Response<List<mUsaha>> response) {
                pgBar.setVisibility(View.GONE);
                //Log.e("res", new Gson().toJson(response.body()));
                if(!response.isSuccessful())
                {
                    lnInfo.setVisibility(View.VISIBLE);
                    lblInfo.setText("Oops, tidak mendapat response dari server");
                    return;
                }

                profesiList = new ArrayList<mUsaha>(response.body());
                if(profesiList.size() > 0)
                {
                    if(getContext() != null) {
                        mAdapter = new UsahaAdapter(getContext(), profesiList);
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
            public void onFailure(Call<List<mUsaha>> call, Throwable t) {
                pgBar.setVisibility(View.GONE);
                lnInfo.setVisibility(View.VISIBLE);
                lblInfo.setText("Oops, tidak mendapat response dari server");
            }
        });
    }


    private void deleteUsaha(final String id)
    {
        progressDialog.show();

        RetrofitBuilder builder = new RetrofitBuilder("createProfesi");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<List<mUsaha>> call = githubUserAPI.listUsaha(String.format(Constant.CONTROLLER_4S, Constant.CONTROLLER_DEV, "getUsahaku","delete",id));
        call.enqueue(new Callback<List<mUsaha>>() {
            @Override
            public void onResponse(Call<List<mUsaha>> call, Response<List<mUsaha>> response) {
                if(progressDialog.isShowing())progressDialog.dismiss();
                if(!response.isSuccessful())
                {
                    return;
                }
                ArrayList<mUsaha> profes = new ArrayList<mUsaha>(response.body());
                if(profes.size() > 0)
                {
                    loadUsaha();
                }
                else
                    Constant.showInfoMessageDialog(getContext(), "Profesi tidak bisa di delete", "FAILED");

            }

            @Override
            public void onFailure(Call<List<mUsaha>> call, Throwable t) {
                if(progressDialog.isShowing())progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnCreate:
                Intent inten = new Intent(getContext(), AddUsahaActivity.class);
                startActivityForResult(inten, REQUEST_CODE_ADD_USAHA);
                break;
        }
    }


    public static class HolderView
    {

        @BindView(R.id.imgBtnMore)
        ImageButton imgBtnMore;


        @BindView(R.id.imgLogo)
        CircleImageView imgLogo;

        @BindView(R.id.txtFullname)
        TextView txtFullname;

        @BindView(R.id.txtAddress)
        TextView txtAddress;

        @BindView(R.id.txtBidang)
        TextView txtBidang;

        @BindView(R.id.txtPosisi)
        TextView txtPosisi;

        @BindView(R.id.rl_gallery)
        RelativeLayout rl_gallery;

        @BindView(R.id.viewpager_default)
        ViewPager viewpager_default;

        @BindView(R.id.indicator_default)
        CirclePageIndicator indicator_default;


        @BindView(R.id.txtDescription)
        TextView txtDescription;

        public HolderView(View vw)
        {
            ButterKnife.bind(this, vw);
        }
    }

    private class UsahaAdapter extends BaseAdapter
    {
        Context _context;
        ArrayList<mUsaha> listData;
        BottomSheetDialog mBottomSheetDialog;
        public UsahaAdapter(Context ctx, ArrayList<mUsaha> pro)
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
        public mUsaha getItem(int i) {
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
                view = inflater.inflate(R.layout.usaha_fragment_card_item, null);
                holder = new HolderView(view);
                view.setTag(holder);
            } else holder = (HolderView) view.getTag();

            holder.txtFullname.setText(getItem(i).getPnfNama());
            holder.txtAddress.setText(getItem(i).getAlamatDetail());
            holder.txtPosisi.setText(getItem(i).getPosisi());
            holder.txtBidang.setText(getItem(i).getBidang());
            holder.txtDescription.setText(getItem(i).getDeskripsi());
            Constant.loadDefaultAvatar(_context, getItem(i).getLogo(), holder.imgLogo);
            if(getItem(i).getGallery().size() <= 0)
            {
                holder.rl_gallery.setVisibility(View.GONE);
            }
            else{
                holder.rl_gallery.setVisibility(View.VISIBLE);
                ViewPagerAdapter uAdapter = new ViewPagerAdapter(_context, new ArrayList<>(getItem(i).getGallery()));
                holder.viewpager_default.setAdapter(uAdapter);
                holder.indicator_default.setViewPager(holder.viewpager_default);
                //uAdapter.registerDataSetObserver(holder.indicator_default);
            }


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
                            deleteUsaha(getItem(i).getPnfId());
                        }
                    });

                    sheetView.findViewById(R.id.fragment_history_bottom_sheet_edit).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.e("EDIT ", "ID " + getItem(i).getPnfId());
                            mBottomSheetDialog.dismiss();
                            Intent intenProfesi = new Intent(getActivity(), AddUsahaActivity.class);
                            intenProfesi.putExtra("CRUD", "update");
                            intenProfesi.putExtra("ID", getItem(i).getPnfId());
                            startActivityForResult(intenProfesi, REQUEST_CODE_ADD_USAHA);
                        }
                    });
                }
            });

            return view;
        }
    }


    public class ViewPagerAdapter extends PagerAdapter
    {
        private Context mContext;
        private ArrayList<Image> mResources;

        public ViewPagerAdapter(Context mContext, ArrayList<Image> mResources) {
            this.mContext = mContext;
            this.mResources = mResources;
        }

        @Override
        public int getCount() {
            return mResources.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.image_item_slide, container, false);
            ImageView imageView = itemView.findViewById(R.id.imgView);
            //Log.e("imag", "ID : " + mResources.get(position).id + " PATH " + mResources.get(position).path);
            Glide
                    .with(mContext)
                    .load(mResources.get(position).path)
                    .placeholder(Constant.getRandomDrawbleColor())
                    .centerCrop()
                    .dontAnimate()
                    .into(imageView);
            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }
}

