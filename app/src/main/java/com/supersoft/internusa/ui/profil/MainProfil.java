package com.supersoft.internusa.ui.profil;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.supersoft.internusa.R;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.CircleImageView;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.SpacesItemDecoration;
import com.supersoft.internusa.helper.util.Util;
import com.supersoft.internusa.model.ListUsahaProfesi;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.MutualFriend;
import com.supersoft.internusa.model.PojoProfilUsaha;
import com.supersoft.internusa.model.ProfilDB;
import com.supersoft.internusa.model.Row;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itclub21 on 3/10/2017.
 */

public class MainProfil extends Fragment implements View.OnClickListener, SwipyRefreshLayout.OnRefreshListener {
    SwipyRefreshLayout swipyrefreshlayout;

    ListView lvwData;
    View rootView;
    TextView txtFilter;

    String action_selection = "";
    ModelRequestInfo inf;
    listUsahaAdapter adapter;
    DBHelper _db;
    ProfilDB profil;

    ArrayList<Row> mAllResults = new ArrayList<Row>();
    boolean onrefreshDown = false;

    public static MainProfil newInstance(){
        MainProfil fragment = new MainProfil();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.main_profil, null);
        _db = new DBHelper(getActivity());
        profil = _db.getProfilDb();
        lvwData = rootView.findViewById(R.id.lvwData);
        txtFilter = rootView.findViewById(R.id.txtFilter);
        txtFilter.setOnClickListener(this);
        swipyrefreshlayout = rootView.findViewById(R.id.swipyrefreshlayout);
        swipyrefreshlayout.setOnRefreshListener(this);



        //inf.setTag("");

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.txtFilter:
                openDialog();
                break;
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if(direction == SwipyRefreshLayoutDirection.BOTTOM)
        {
            onrefreshDown = true;
            inf.limit += 1;
            swipyrefreshlayout.post(new Runnable() {
                @Override
                public void run() {
                    swipyrefreshlayout.setRefreshing(true);
                    getUsaha(action_selection);
                }
            });
        }
    }

    private void openDialog(){
        final CharSequence[] items = {"Usaha Rekan", "Semua Usaha"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(items[i].equals("Usaha Rekan")){
                    action_selection = "get_usaha_rekan";
                    txtFilter.setText((action_selection == "get_usaha_rekan") ? "Usaha Rekan" : "Semua Usaha");
                    inf.limit = 0;
                    swipyrefreshlayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipyrefreshlayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
                            swipyrefreshlayout.setRefreshing(true);
                            mAllResults = new ArrayList<Row>();
                            adapter = new listUsahaAdapter(getActivity(), mAllResults);
                            lvwData.setAdapter(adapter);
                            getUsaha(action_selection);
                        }
                    });
                }else if(items[i].equals("Semua Usaha")){
                    action_selection = "get_usaha";
                    txtFilter.setText((action_selection == "get_usaha_rekan") ? "Usaha Rekan" : "Semua Usaha");
                    inf.limit = 0;

                    swipyrefreshlayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipyrefreshlayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
                            swipyrefreshlayout.setRefreshing(true);
                            mAllResults = new ArrayList<Row>();
                            adapter = new listUsahaAdapter(getActivity(), mAllResults);
                            lvwData.setAdapter(adapter);
                            getUsaha(action_selection);
                        }
                    });
                }
            }
        });
        builder.show();
    }

    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        swipyrefreshlayout.setColorSchemeColors(
                R.color.black,
                R.color.colorAccent,
                R.color.orange,
                R.color.darkOrange
        );

        if (savedInstance != null) {
            inf = (ModelRequestInfo)savedInstance.getSerializable("REQUEST_MODEL");
            action_selection = savedInstance.getString("ACT_SELECTION");
            try{
                mAllResults = (ArrayList<Row>)savedInstance.getSerializable(Constant.savedInstanceProfil);
                if(mAllResults.size() > 0){
                    adapter = new listUsahaAdapter(getActivity(),mAllResults);
                    lvwData.setAdapter(adapter);
                }
            }catch (NullPointerException e){

            }

        }else {
            inf = new ModelRequestInfo();
            inf.hp = profil.hp;
            inf.deviceid = Constant.getUUID(getActivity());
            inf.mitraid = getActivity().getResources().getString(R.string.CONF_MITRAID);
            inf.limit = 0;
            action_selection = "get_usaha";
            swipyrefreshlayout.post(new Runnable() {
                @Override
                public void run() {
                    swipyrefreshlayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
                    swipyrefreshlayout.setRefreshing(true);
                    getUsaha(action_selection);
                }
            });

        }

        txtFilter.setText((action_selection == "get_usaha_rekan") ? "Usaha Rekan" : "Usaha Semua");
    }


    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
        outState.putSerializable(Constant.savedInstanceProfil, mAllResults);
        outState.putString("ACT_SELECTION",action_selection);
        outState.putSerializable("REQUEST_MODEL", inf);
    }

    private void getUsaha(String action){


        //Log.e("RQ", "ACT : "+action +" JSON "+new Gson().toJson(inf).toString());
        RetrofitBuilder builder = new RetrofitBuilder(action);
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<PojoProfilUsaha> call = githubUserAPI.getProfilUsahaRekan(String.format(Constant.CONTROLLER_2S, Constant.CONTROLLER_DEV, action),inf);
        call.enqueue(new Callback<PojoProfilUsaha>() {
            @Override
            public void onResponse(Call<PojoProfilUsaha> call, Response<PojoProfilUsaha> response) {
                //Log.e("respon", new Gson().toJson(response.body()));
                swipyrefreshlayout.setRefreshing(false);

                if(!response.isSuccessful()){
                    if(onrefreshDown) inf.limit -= 1;
                    return;
                }

                PojoProfilUsaha pojos = response.body();
                ArrayList<Row> datas = new ArrayList<Row>(pojos.getRows());
                //Log.e("respon", "datas " +datas.size() + " is " + onrefreshDown);
                if(datas.size() <= 0 && onrefreshDown)
                {
                    if(onrefreshDown) inf.limit -= 1;
                }


                if (mAllResults == null) {
                    mAllResults = new ArrayList<Row>();
                } else {
                    mAllResults.addAll(datas);
                }

                if(getActivity() != null) {
                    if (inf.limit > 0) {

                        adapter = new listUsahaAdapter(getActivity(), mAllResults);
                        lvwData.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        lvwData.setSelection(adapter.getCount() - datas.size());
                    } else {


                        mAllResults = new ArrayList<Row>();
                        mAllResults.addAll(datas);
                        adapter = new listUsahaAdapter(getActivity(), datas);
                        lvwData.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }
                onrefreshDown = false;

            }

            @Override
            public void onFailure(Call<PojoProfilUsaha> call, Throwable t) {
                if(onrefreshDown) inf.limit -= 1;
                onrefreshDown = false;
                swipyrefreshlayout.setRefreshing(false);
            }
        });
    }

    private class listUsahaAdapter extends ArrayAdapter<Row>
    {
        ArrayList<Row> datum;
        Context _context;
        LinearLayoutManager mLayoutManager_Linear_Butab;

        public listUsahaAdapter(Context context, ArrayList<Row> adt) {
            super(context, 0, adt);
            _context = context;
            datum = adt;

        }
        public Row getItem(int position){
            return datum.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            final Row item = getItem(position);
            holder _holder;
            if(convertView == null){
                mLayoutManager_Linear_Butab = new LinearLayoutManager(_context);
                mLayoutManager_Linear_Butab.setOrientation(LinearLayoutManager.HORIZONTAL);
                _holder = new holder();
                convertView = LayoutInflater.from(_context).inflate(R.layout.profil_usaha_item, parent, false);
                _holder.txtFullname = convertView.findViewById(R.id.txtFullname);
                _holder.txtPhonenumber = convertView.findViewById(R.id.txtPhonenumber);
                _holder.txtAlamat = convertView.findViewById(R.id.txtAddress);
                _holder.imgAvatar = convertView.findViewById(R.id.imgAvatar);
                _holder.contentLinear = convertView.findViewById(R.id.contentLinear);
                _holder.rc_selected_photos = convertView.findViewById(R.id.rc_selected_photos);
                _holder.rc_selected_photos.setLayoutManager(mLayoutManager_Linear_Butab);
                _holder.rc_selected_photos.addItemDecoration(new SpacesItemDecoration(Util.dpToPx(_context, 5), 5), SpacesItemDecoration.TYPE_VERTICAL);
                _holder.rc_selected_photos.setHasFixedSize(true);
                //_holder.txtTelp = (TextView)          convertView.findViewById(R.id.txtTelp);
                //_holder.txtChat = (TextView)          convertView.findViewById(R.id.txtChat);

                convertView.setTag(_holder);
            }else {
                _holder = (holder)convertView.getTag();
            }
            _holder.txtFullname.setText(item.getMemNama());
            _holder.txtPhonenumber.setText(item.getHp());

            _holder.txtAlamat.setText(item.getAlmtDetail());
            _holder.txtAlamat.setVisibility((item.getAlmtDetail().length() > 5) ? View.VISIBLE : View.GONE);
            Constant.loadDefaultAvatar(_context, item.getLogo(), _holder.imgAvatar);

            if(item.getListUsahaProfesi().size() > 0)
            {
                _holder.contentLinear.removeAllViews();
                for(int i=0; i < item.getListUsahaProfesi().size(); i++){
                    View vw = LayoutInflater.from(_context).inflate(R.layout.list_usaha_profesi_item_cardview, null);
                    ListUsahaProfesi usaha = item.getListUsahaProfesi().get(i);
                    TextView txtJudul = vw.findViewById(R.id.txtJudul);
                    TextView txtIsi = vw.findViewById(R.id.txtIsi);
                    txtJudul.setText(usaha.getNama());
                    txtIsi.setText(usaha.getDeskripsi());
                    _holder.contentLinear.addView(vw);
                }

            }

            MutualFriendAdapter adapter = new MutualFriendAdapter(item.getMutualFriend());
            _holder.rc_selected_photos.setAdapter(adapter);

            return convertView;
        }



        private class holder
        {
            private TextView txtFullname;
            private TextView txtPhonenumber;
            private TextView txtAlamat;
            private CircleImageView imgAvatar;
            private LinearLayout contentLinear;
            private RecyclerView rc_selected_photos;
            private TextView txtTelp;
            private TextView txtChat;


        }
    }

    private class MutualFriendAdapter extends  RecyclerView.Adapter<MutualFriendAdapter.SelectedPhotoHolder>
    {
        private List<MutualFriend> aDatum;
        class SelectedPhotoHolder extends RecyclerView.ViewHolder {



            TextView txtNama;


            public SelectedPhotoHolder(View itemView) {
                super(itemView);
                txtNama = itemView.findViewById(R.id.txtFullname);

            }
        }

        public MutualFriendAdapter(List<MutualFriend> lst) {
            this.aDatum = lst;
        }

        @Override
        public SelectedPhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mutual_friend_item, parent, false);
            return new SelectedPhotoHolder(itemView);
        }

        @Override
        public void onBindViewHolder(SelectedPhotoHolder holder, int position) {
            final MutualFriend movie = aDatum.get(position);

            holder.txtNama.setText(movie.getNama());

        }

        @Override
        public int getItemCount() {
            return aDatum.size();
        }
    }
}
