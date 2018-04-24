package com.supersoft.internusa.ui.payment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import com.supersoft.internusa.R;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.ExpandableRecyclerView;
import com.supersoft.internusa.helper.util.Session;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.PojoGetBalance;
import com.supersoft.internusa.model.ProfilDB;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itclub21 on 3/5/2017.
 */

public class MainPayment extends Fragment implements SwipyRefreshLayout.OnRefreshListener, View.OnClickListener, ExpandableRecyclerView.OnChildItemClickedListener {
    SwipyRefreshLayout swipyrefreshlayout;
    View view;
    Session _session;
    ExpandableRecyclerView recycleView;
    PojoLoadProvider pojos = null;
    RelativeLayout mainFrame;
    TextView txtSaldoAkhir;
    Button btnAddSaldo;
    DBHelper _db;

    // untuk info tidak mendapatkan response
    RelativeLayout relativeError;
    ImageView imgError;
    TextView txtTitleError, txtDescError;

    public static MainPayment newInstance(){
        MainPayment fragment = new MainPayment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.payment_main_activity, null);
        swipyrefreshlayout = view.findViewById(R.id.swipyrefreshlayout);
        recycleView = view.findViewById(R.id.lvwData);
        relativeError = view.findViewById(R.id.relativeError);
        relativeError.setVisibility(View.GONE);
        recycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        swipyrefreshlayout.setOnRefreshListener(this);
        txtSaldoAkhir = view.findViewById(R.id.txtSaldoAkhir);
        btnAddSaldo = view.findViewById(R.id.btnAddSaldo);
        btnAddSaldo.setOnClickListener(this);

        txtTitleError = view.findViewById(R.id.txtTitleError);
        txtDescError = view.findViewById(R.id.txtDescError);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _session = new Session(getActivity());
        _db = new DBHelper(getActivity());
        swipyrefreshlayout.setColorSchemeColors(
                R.color.black3d,
                R.color.colorAccent,
                R.color.orange,
                R.color.darkOrange
        );

        if (savedInstanceState != null) {
            pojos = (PojoLoadProvider) savedInstanceState.getSerializable(Constant.savedInstancePayment);
            if(pojos != null){
                recyclerAdapter adapter = new recyclerAdapter(pojos.getRow());
                adapter.setOnChildItemClickedListener(MainPayment.this);
                recycleView.setAdapter(adapter);
            }
            else
            {
                relativeError.setVisibility(View.VISIBLE);
                swipyrefreshlayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipyrefreshlayout.setDirection(SwipyRefreshLayoutDirection.TOP);
                        swipyrefreshlayout.setRefreshing(true);
                        runUI();
                    }
                });
            }
        }else {
            swipyrefreshlayout.post(new Runnable() {
                @Override
                public void run() {
                    swipyrefreshlayout.setDirection(SwipyRefreshLayoutDirection.TOP);
                    swipyrefreshlayout.setRefreshing(true);
                    runUI();
                }
            });

        }

    }

    private void runUI(){
        if(getActivity() != null){

            swipyrefreshlayout.setRefreshing(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try{
                        getData();
                        getBalance(_db.getProfilDb());
                    }catch (NullPointerException e){}

                }
            }, 1000);

        }
    }

    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
        outState.putSerializable(Constant.savedInstancePayment, pojos);

    }

    private void getData() throws NullPointerException{
        ProfilDB pf = _db.getProfilDb();
        ModelRequestInfo inf = new ModelRequestInfo();
        inf.hp = pf.hp;
        inf.agenid = pf.agenid;
        inf.pin = pf.pin;
        inf.deviceid = _session.getDeviceId();
        RetrofitBuilder builder = new RetrofitBuilder(getContext().getResources().getString(R.string.CONF_URI), "loadProvider");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<PojoLoadProvider> call = githubUserAPI.loadProvider(inf);
        call.enqueue(new Callback<PojoLoadProvider>() {

            @Override
            public void onResponse(Call<PojoLoadProvider> call, Response<PojoLoadProvider> response) {
                swipyrefreshlayout.setRefreshing(false);
                //Log.e("response", new Gson().toJson(response.body()));
                if(!response.isSuccessful()){
                    try {
                        Constant.showInfoMessageDialog(getActivity(),"ERROR: \n" + response.errorBody().string(),"FAIL");
                    }catch (IOException e){}
                    return;
                }
                relativeError.setVisibility(View.GONE);
                pojos = response.body();
                if(pojos.getStatus() == 8)
                {
                    relativeError.setVisibility(View.VISIBLE);
                    txtTitleError.setText("ERROR");
                    txtDescError.setText("Device anda belum terregistrasi, silahkan logout dan aktifasi.");
                }
                else
                {
                    recyclerAdapter adapter = new recyclerAdapter(pojos.getRow());
                    adapter.setOnChildItemClickedListener(MainPayment.this);
                    recycleView.setAdapter(adapter);
                }

            }

            @Override
            public void onFailure(Call<PojoLoadProvider> call, Throwable t) {
                swipyrefreshlayout.setRefreshing(false);
                relativeError.setVisibility(View.VISIBLE);
            }
        });

    }


    private void getBalance(ProfilDB pf) throws NullPointerException{

        ModelRequestInfo inf = new ModelRequestInfo();
        inf.hp = pf.hp;
        inf.agenid = pf.agenid;
        inf.pin = pf.pin;
        inf.deviceid = _session.getDeviceId();
        //Log.e("PARA", new Gson().toJson(inf));
        RetrofitBuilder builder = new RetrofitBuilder(getActivity().getResources().getString(R.string.CONF_URI), "status_agen");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<PojoGetBalance> call = githubUserAPI.getBalance(inf);
        call.enqueue(new Callback<PojoGetBalance>() {
            @Override
            public void onResponse(Call<PojoGetBalance> call, Response<PojoGetBalance> response) {
                //Log.e("RESPO", response.body());
                if(!response.isSuccessful())
                {
                    return;
                }

                PojoGetBalance obj = response.body();
                PojoGetBalance.Row row = obj.getResponse().get(0).getRow();

                String saldo =row.getSaldoDeposit();
                String bonus =row.getBonusSaatIni();

                String isi =  String.format("Saldo : %s Bonus Akhir : %s", saldo, bonus);
                txtSaldoAkhir.setText(isi);
            }

            @Override
            public void onFailure(Call<PojoGetBalance> call, Throwable t) {Log.e("ERR", t.getMessage());}
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnAddSaldo)
        {
            Intent inten = new Intent(getActivity(), TambahSaldoActivity.class);
            startActivity(inten);
        }
    }

    @Override
    public void onChildItemClicked(int group, int position) {
        PojoLoadProvider.Child child = pojos.getRow().get(group).getChild().get(position);
        Intent intent = new Intent(getActivity(), TransactionActivity.class);
        intent.putExtra("myObjectChild", new Gson().toJson(child));
        intent.putExtra("myObjectRow", new Gson().toJson(pojos.getRow().get(group)));
        startActivity(intent);
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if(direction == SwipyRefreshLayoutDirection.TOP){
            relativeError.setVisibility(View.GONE);
            runUI();
        }
    }


    public class recyclerAdapter extends ExpandableRecyclerView.Adapter<recyclerAdapter.ListHeaderViewHolder,ExpandableRecyclerView.SimpleGroupViewHolder,PojoLoadProvider.Child, PojoLoadProvider.Row>
            implements ExpandableRecyclerView.OnChildItemClickedListener
    {

        List<PojoLoadProvider.Row> group;

        public recyclerAdapter(List<PojoLoadProvider.Row> rw){
            group = rw;
            //Log.e("grp", " " + group.size());
        }

        @Override
        public int getGroupItemCount() {
            return group.size();
        }

        @Override
        public int getChildItemCount(int group) {
            return getGroupItem(group).getChild().size();
        }

        @Override
        public PojoLoadProvider.Row getGroupItem(int position) {
            return group.get(position);
        }

        @Override
        public PojoLoadProvider.Child getChildItem(int group, int position) {
            return getGroupItem(group).getChild().get(position);
        }

        @Override
        protected ExpandableRecyclerView.SimpleGroupViewHolder onCreateGroupViewHolder(ViewGroup parent) {
            return new ExpandableRecyclerView.SimpleGroupViewHolder(parent.getContext());
        }

        @Override
        public void onBindGroupViewHolder(ExpandableRecyclerView.SimpleGroupViewHolder holder, int group) {
            super.onBindGroupViewHolder(holder, group);
            holder.setText(getGroupItem(group).getNama());
        }

        @Override
        public void onBindChildViewHolder(ListHeaderViewHolder holder, int group, final int position) {
            super.onBindChildViewHolder(holder, group, position);
            holder.header_title.setText(getChildItem(group, position).getName());
        }

        @Override
        protected ListHeaderViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.list_item_child_vertical, parent, false);
            return new ListHeaderViewHolder(view);
        }

        @Override
        public int getChildItemViewType(int group, int position) {
            return 1;
        }

        @Override
        public void onChildItemClicked(int group, int position) {

        }


        public   class ListHeaderViewHolder extends RecyclerView.ViewHolder {
            public TextView header_title;
            public ImageView btn_expand_toggle;

            public ListHeaderViewHolder(View itemView) {
                super(itemView);
                header_title = itemView.findViewById(R.id.list_item_parent_vertical_parent_textView);
                //btn_expand_toggle = (ImageView) itemView.findViewById(R.id.list_item_parent_horizontal_arrow_imageView);
            }
        }
    }
}
