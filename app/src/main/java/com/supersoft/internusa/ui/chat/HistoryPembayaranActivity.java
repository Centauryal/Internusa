package com.supersoft.internusa.ui.chat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.bluetooth.BluetoothService;
import com.supersoft.internusa.helper.bluetooth.BluetoothState;
import com.supersoft.internusa.helper.bluetooth.DeviceList;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.DrawableClickListener;
import com.supersoft.internusa.helper.util.EditTextRightDrawable;
import com.supersoft.internusa.helper.util.Session;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.ProfilDB;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.nio.charset.Charset;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itclub21 on 4/24/2017.
 */

public class HistoryPembayaranActivity extends AppCompatActivity {

    @BindView(R.id.txtKeyword)
    EditTextRightDrawable txtKeyword;

    @BindView(R.id.lblInfo)
    TextView lblInfo;

    @BindView(R.id.lvwChat)ListView lvwChat;
    @BindView(R.id.pgbarLoading)ProgressBar pgbarLoading;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    ArrayList<HistroyBayarModel> listItems = new ArrayList<>();
    ReprintAdapter mAdapter;
    BluetoothService btService;
    BluetoothDevice con_dev = null;

    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    boolean isBtnPrintClick = false;

    Session _session;

    String powered = "";
    String namaOutlet = "";
    String outletid = "";
    HistroyBayarModel selectedModel = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_pembayaran);
        ButterKnife.bind(this);
        _session = new Session(this);
        btService = new BluetoothService(this, handlerBt);
        txtKeyword.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                if(target == DrawablePosition.RIGHT)
                {
                    searching();
                }
            }
        });


        initTollbar();

        mAdapter = new ReprintAdapter(this, listItems);
        lvwChat.setAdapter(mAdapter);


        String address = _session.getLastMacAddrBlutut();
        if((address.length() > 8) && (btService.getState() == 0) && (btService.isBTopen()))
        {
            con_dev = btService.getDevByMac(address);
            btService.connect(con_dev);
        }

    }

    Handler handlerBt = new Handler(){

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            Toast.makeText(HistoryPembayaranActivity.this, "Connected", Toast.LENGTH_SHORT).show();


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("handleConn", "="+isBtnPrintClick + " == " + selectedModel);
                                    if(isBtnPrintClick && (selectedModel != null))
                                    {
                                        printText(selectedModel);
                                    }
                                }
                            });
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Toast.makeText(HistoryPembayaranActivity.this, "Connecting..", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:

                            Toast.makeText(HistoryPembayaranActivity.this, "Unknown Message..", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:
                    Toast.makeText(HistoryPembayaranActivity.this, "Device connection was lost", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:     //ÎÞ·¨Á¬½ÓÉè±¸
                    Toast.makeText(HistoryPembayaranActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {

                    String address = _session.getLastMacAddrBlutut();////_session.getMacAddrPrinter();
                    Log.e("kok", address);
                    if (address.length() < 8){
                        Intent inte = new Intent(getApplicationContext(), DeviceList.class);
                        startActivityForResult(inte, REQUEST_CONNECT_DEVICE);
                    }else{
                        _session.setLastMacAddrBlutut(address);
                        con_dev = btService.getDevByMac(address);
                        btService.connect(con_dev);
                    }
                } else {
                    finish();
                }
                break;
            case  REQUEST_CONNECT_DEVICE:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    String address = extras.getString(BluetoothState.EXTRA_DEVICE_ADDRESS);
                    _session.setLastMacAddrBlutut(address);
                    con_dev = btService.getDevByMac(address);
                    btService.connect(con_dev);
                }
                break;
        }
    }
    public void onStart()
    {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    public void onResume()
    {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void onPause()
    {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (btService != null) btService.stop();
    }

    private void initTollbar()
    {
        if(toolbar != null)
        {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setNavigationIcon(R.drawable.ic_back_home_white);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }


    @Subscribe
    public void onEvent(BluetoothState state)
    {

    }
    private void searching()
    {
        pgbarLoading.setVisibility(View.VISIBLE);
        lblInfo.setVisibility(View.GONE);
        ModelRequestInfo inf = new ModelRequestInfo();
        ProfilDB profil = new DBHelper(this).getProfilDb();
        Session _session = new Session(this);
        inf.agenid = profil.agenid;
        inf.pin = profil.pin;
        inf.hp = profil.hp;
        inf.deviceid = _session.getDeviceId();
        inf.msisdn = txtKeyword.getText().toString();
        //Log.e("request", new Gson().toJson(inf));
        RetrofitBuilder builder = new RetrofitBuilder(getResources().getString(R.string.CONF_URI),"reprint_pembayaran_tagihan");
        RetroBuilderInterface api = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<JsonObject> call = api.historyPembayara(inf);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                pgbarLoading.setVisibility(View.GONE);
                //Log.e("response", new Gson().toJson(response.body()));
                if(!response.isSuccessful())
                {
                    lblInfo.setVisibility(View.VISIBLE);
                    lblInfo.setText(response.message());
                    return;
                }

                /*{"response":[{    "status":1,
                    "agenid":"MFT00004",
                    "nama":"imam m",
                    "powered":"Microsains",
                    "message":"Trx berhasil",
                    "data":[

                    {"tanggal":"2017-06-17 11:16:54",
                    "nopel":"087888783819",
                    "nama":"XL LOKAL 10",
                    "an":null,
                    "tagihan":null,
                    "jml_periode":null,
                    "bulan":null,
                    "admin":null,
                    "denda":null,
                    "total_bayar":"11000",
                    "reff":"215769418","status":4}],"sql":"SELECT pp.*, p.tujuan, p.tglsukses as tgl, p.vsn as reff, v.harga_jual, v.ket FROM purchase p left join ppob pp on p.id=pp.id left join voucher v on p.vtype=v.vtype WHERE 1  AND p.tujuan = '087888783819' AND p.agenid = 'MFT00004' AND p.status = 4  UNION SELECT pp.*, p.tujuan, p.tglsukses as tgl, p.vsn as reff, v.harga_jual, v.ket FROM log_purchase p left join ppob pp on p.id=pp.id left join voucher v on p.vtype=v.vtype WHERE 1  AND p.tujuan = '087888783819' AND p.agenid = 'MFT00004' AND p.status = 4   order by id desc limit 10"}]}
                */
                JsonArray respon = response.body().get("response").getAsJsonArray();
                JsonObject rest=(JsonObject)respon.get(0);
                int status = rest.get("status").getAsInt();
                JsonArray arData = rest.get("data").getAsJsonArray();

                powered = rest.get("powered").getAsString();
                namaOutlet = rest.get("nama").getAsString();
                outletid = rest.get("agenid").getAsString();
                if (arData.size() > 0){
                    listItems = new ArrayList<HistroyBayarModel>();
                    for(int l=0; l< arData.size(); l++)
                    {

                        JsonObject obj = arData.get(l).getAsJsonObject();
                        HistroyBayarModel model = new HistroyBayarModel();
                        model.setTanggal(obj.get("tanggal").isJsonNull() ? "" : obj.get("tanggal").getAsString());
                        model.setMsisdn(obj.get("nopel").isJsonNull() ? "" : obj.get("nopel").getAsString());
                        model.setNama(obj.get("nama").isJsonNull() ? "" : obj.get("nama").getAsString());
                        model.setAn(obj.get("an").isJsonNull() ? "" : obj.get("an").getAsString());
                        model.setTagihan(obj.get("tagihan").isJsonNull() ? "" : obj.get("tagihan").getAsString());
                        model.setJml_periode(obj.get("jml_periode").isJsonNull() ? "" : obj.get("jml_periode").getAsString());
                        model.setBulan(obj.get("bulan").isJsonNull() ? "" : obj.get("bulan").getAsString());
                        model.setAdmin(obj.get("admin").isJsonNull() ? "" : obj.get("admin").getAsString());
                        model.setDenda(obj.get("denda").isJsonNull() ? "" : obj.get("denda").getAsString());
                        model.setTotal_bayar(obj.get("total_bayar").isJsonNull() ? "" : obj.get("total_bayar").getAsString());
                        model.setReff(obj.get("reff").isJsonNull() ? "" : obj.get("reff").getAsString());
                        model.setStatus(obj.get("status").isJsonNull() ? 0 : obj.get("status").getAsInt());
                        listItems.add(model);

                    }

                    mAdapter = new ReprintAdapter(HistoryPembayaranActivity.this, listItems);
                    lvwChat.setAdapter(mAdapter);
                }
                else
                {
                    lblInfo.setVisibility(View.VISIBLE);
                    lblInfo.setText("Data tidak di temukan.");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pgbarLoading.setVisibility(View.GONE);
                lblInfo.setVisibility(View.VISIBLE);
                lblInfo.setText(t.getMessage());
            }
        });
    }



    private class HistroyBayarModel{
        private String jml_periode;
        private String tagihan;
        private String reff;
        private int status;
        private String admin;
        private String an;
        private String denda;
        private String bulan;
        private String tanggal;
        private String total_bayar;
        private String nama;
        private String msisdn;

        private String poweredby;
        private String outletId;
        private String outletName;

        public void setJml_periode(String s){
            this.jml_periode = s;
        }
        public void setMsisdn(String s){
            this.msisdn = s;
        }
        public void setTagihan(String s){
            this.tagihan = s;
        }
        public void setReff(String s){
            this.reff = s;
        }
        public void setStatus(int s){
            this.status = s;
        }
        public void setAdmin(String s){
            this.admin = s;
        }
        public void setAn(String s){
            this.an = s;
        }
        public void setDenda(String s){
            this.denda = s;
        }
        public void setBulan(String s){
            this.bulan = s;
        }
        public void setTanggal(String s){
            this.tanggal = s;
        }
        public void setTotal_bayar(String s){
            this.total_bayar = s;
        }
        public void setNama(String s){
            this.nama = s;
        }

        public void setOutletId(String s){
            this.outletId = s;
        }
        public void setOutletName(String s){
            this.outletName = s;
        }
        public void setPoweredby(String s){
            this.poweredby = s;
        }


        public String getJml_periode(){
            return this.jml_periode;
        }
        public String getMsisdn(){
            return this.msisdn;
        }
        public String getTagihan(){
            return this.tagihan;
        }
        public String getReff(){
            return this.reff;
        }
        public int getStatus(){
            return this.status;
        }
        public String getAdmin(){
            return this.admin;
        }
        public String getAn(){
            return this.an;
        }
        public String getDenda(){
            return this.denda;
        }
        public String getBulan(){
            return this.bulan ;
        }
        public String getTanggal(){
            return this.tanggal;
        }
        public String getTotal_bayar(){
            return this.total_bayar;
        }
        public String getNama(){
            return this.nama;
        }

        public String getOuletId(){
            return this.outletId;
        }
        public String getOutletName(){
            return this.outletName;
        }
        public String getPoweredBy(){
            return this.poweredby;
        }
    }

    static class ViewHolder
    {
        public ViewHolder(View view)
        {
            ButterKnife.bind(this, view);
        }
    }


    class ReprintAdapter extends ArrayAdapter<HistroyBayarModel> {
        Context _ctx;
        ArrayList<HistroyBayarModel> _models;
        Button btnPrint;

        //LinearLayout txtLayoutBody;
        //TextView txtCode, txtTerimakasih, txtOutlet, txtPowered;
        public ReprintAdapter(Context context, ArrayList<HistroyBayarModel> obj) {
            super(context, R.layout.history_pembayaran_card_item, obj);
            _ctx = context;
            _models = obj;
        }

        public int getCount() {
            return _models.size();
        }


        public HistroyBayarModel getItem(int position) {
            return _models.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final viewHolder holder;
            if(convertView == null)
            {
                convertView = LayoutInflater.from(_ctx).inflate(R.layout.history_pembayaran_card_item, parent, false);
                holder = new viewHolder(convertView);
                convertView.setTag(holder);
            }
            else
            {
                holder = (viewHolder)convertView.getTag();
            }
            final HistroyBayarModel mdl = getItem(position);

            if(mdl.getTanggal().equals(""))
                holder.lnTanggal.setVisibility(View.GONE);
            else
            {
                holder.lnTanggal.setVisibility(View.VISIBLE);
                holder.txtTanggal.setText(mdl.getTanggal());
            }

            if(mdl.getMsisdn().equals(""))
                holder.lnNopel.setVisibility(View.GONE);
            else
            {
                holder.lnNopel.setVisibility(View.VISIBLE);
                holder.txtNopelanggan.setText(mdl.getMsisdn());
            }

            if(mdl.getNama().equals(""))
                holder.lnNama.setVisibility(View.GONE);
            else
            {
                holder.lnNama.setVisibility(View.VISIBLE);
                holder.txtNamaproduk.setText(mdl.getNama());
            }

            if(mdl.getAn().equals(""))
                holder.lnAtasnama.setVisibility(View.GONE);
            else
            {
                holder.lnAtasnama.setVisibility(View.VISIBLE);
                holder.txtAtasnama.setText(mdl.getAn());
            }

            if(mdl.getTagihan().equals(""))
                holder.lnTagihan.setVisibility(View.GONE);
            else
            {
                holder.lnTagihan.setVisibility(View.VISIBLE);
                holder.txtTagihan.setText(mdl.getTagihan());
            }

            if(mdl.getJml_periode().equals(""))
                holder.lnJmlPeriod.setVisibility(View.GONE);
            else
            {
                holder.lnJmlPeriod.setVisibility(View.VISIBLE);
                holder.txtJmlPeriod.setText(mdl.getJml_periode());
            }

            if(mdl.getBulan().equals(""))
                holder.lnBulan.setVisibility(View.GONE);
            else
            {
                holder.lnBulan.setVisibility(View.VISIBLE);
                holder.txtBulan.setText(mdl.getBulan());
            }

            if(mdl.getAdmin().equals(""))
                holder.lnAdmin.setVisibility(View.GONE);
            else
            {
                holder.lnAdmin.setVisibility(View.VISIBLE);
                holder.txtAdmin.setText(mdl.getAdmin());
            }

            if(mdl.getDenda().equals(""))
                holder.lnDenda.setVisibility(View.GONE);
            else
            {
                holder.lnDenda.setVisibility(View.VISIBLE);
                holder.txtDenda.setText(mdl.getDenda());
            }

            if(mdl.getTotal_bayar().equals(""))
                holder.lnTotalbayar.setVisibility(View.GONE);
            else
            {
                holder.lnTotalbayar.setVisibility(View.VISIBLE);
                holder.txtTotalbayar.setText(mdl.getTotal_bayar());
            }

            if(mdl.getReff().equals(""))
                holder.lnReff.setVisibility(View.GONE);
            else
            {
                holder.lnReff.setVisibility(View.VISIBLE);
                holder.txtReff.setText(mdl.getReff());
            }

            holder.lnStatus.setVisibility(View.VISIBLE);
            holder.txtStatus.setText(getStatus(mdl.getStatus()));

            holder.btnShareto.setVisibility(View.GONE);
            holder.btnPrint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.vlog.setText("Starting..");
                    if(!btService.isAvailable())
                    {
                        holder.vlog.setText("Not Support");
                        Toast.makeText(HistoryPembayaranActivity.this, "Device tidak support bluetooth", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(!btService.isBTopen())
                    {
                        holder.vlog.setText("Bt disable");
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                        return;
                    }

                    if(_session.getLastMacAddrBlutut().length() < 8)
                    {
                        holder.vlog.setText("BT Address tdk ada");
                        Intent inte = new Intent(getApplicationContext(), DeviceList.class);
                        startActivityForResult(inte, BluetoothState.REQUEST_CONNECT_DEVICE);
                        return;
                    }
                    Log.e("bt", "state "+btService.getState());
                    holder.vlog.setText("Starting..("+btService.getState()+")");
                    if(btService.getState() != BluetoothService.STATE_CONNECTED)
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                isBtnPrintClick = true;
                                selectedModel = mdl;

                                String address = _session.getLastMacAddrBlutut();
                                con_dev = btService.getDevByMac(address);
                                btService.connect(con_dev);
                            }
                        });

                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.vlog.setText("Printing..");
                            printText(mdl);
                            holder.vlog.setText("Finish");
                        }
                    });

                }
            });
            return convertView;
        }

        private String getStatus(int st)
        {
            switch (st)
            {
                case 0: return "Antrian";
                case 1: return "On Order";
                case 2: return "Gagal";
                case 3: return "Refund";
                case 4: return "Sukses";
                default: return "";
            }
        }

        class viewHolder
        {
            @BindView(R.id.vlog)public TextView vlog;
            @BindView(R.id.txtTanggal)public TextView txtTanggal;
            @BindView(R.id.lnTanggal)public LinearLayout lnTanggal;

            @BindView(R.id.txtNopelanggan)public TextView txtNopelanggan;
            @BindView(R.id.lnNopel)public LinearLayout lnNopel;

            @BindView(R.id.txtNamaproduk)public TextView txtNamaproduk;
            @BindView(R.id.lnNama)public LinearLayout lnNama;

            @BindView(R.id.txtAtasnama)public TextView txtAtasnama;
            @BindView(R.id.lnAtasnama)public LinearLayout lnAtasnama;

            @BindView(R.id.txtTagihan)public TextView txtTagihan;
            @BindView(R.id.lnTagihan)public LinearLayout lnTagihan;

            @BindView(R.id.txtJmlPeriod)public TextView txtJmlPeriod;
            @BindView(R.id.lnJmlPeriod)public LinearLayout lnJmlPeriod;

            @BindView(R.id.txtBulan)public TextView txtBulan;
            @BindView(R.id.lnBulan)public LinearLayout lnBulan;

            @BindView(R.id.txtAdmin)public TextView txtAdmin;
            @BindView(R.id.lnAdmin)public LinearLayout lnAdmin;

            @BindView(R.id.txtDenda)public TextView txtDenda;
            @BindView(R.id.lnDenda)public LinearLayout lnDenda;

            @BindView(R.id.txtTotalbayar)public TextView txtTotalbayar;
            @BindView(R.id.lnTotalbayar)public LinearLayout lnTotalbayar;

            @BindView(R.id.txtReff)public TextView txtReff;
            @BindView(R.id.lnreff)public LinearLayout lnReff;

            @BindView(R.id.txtStatus)public TextView txtStatus;
            @BindView(R.id.lnStatus)public LinearLayout lnStatus;

            @BindView(R.id.btnShareto)public ImageView btnShareto;
            @BindView(R.id.btnPrint)public ImageView btnPrint;


            public viewHolder(View vw)
            {
                ButterKnife.bind(this, vw);
            }
        }
    }

    private void printText(HistroyBayarModel mdl){

        Charset charset = Charset.forName("UTF-8");
        byte[] cmd = new byte[3];
        cmd[0] = 0x1b;
        cmd[1] = 0x21;
        cmd[1] &= 0x4D0;
        cmd[2] |= 0x00;
        btService.write(cmd);
        //_bt.sendMessage("#Outlet : "+_session.getAgenid()+"\n", "GBK");
        btService.sendMessage("STRUK PEMBELIAN/PEMBAYARAN \n", "GBK");

        //cmd[2] &= 0xEF;
        //btService.write(cmd);
        //btService.sendMessage(msg,"GBK");

        cmd[2] &= 0xEF;
        btService.write(cmd);
        btService.sendMessage(String.format("%-8s: %s", "Tgl", mdl.getTanggal()),"GBK");
        btService.sendMessage(String.format("%-8s: %s", "Produk", mdl.getNama()),"GBK");
        btService.sendMessage(String.format("%-8s: %s", "No Pel", mdl.getMsisdn()),"GBK");
        if(!mdl.getAn().equals("NULL")){
            btService.sendMessage(String.format("%-8s: %s", "Nama", mdl.getAn()),"GBK");
        }
        if(!mdl.getBulan().equals("NULL")){
            btService.sendMessage(String.format("%-8s: %s", "Tag Bln", mdl.getBulan()),"GBK");
        }
        if(!mdl.getTagihan().equals("NULL")){
            btService.sendMessage(String.format("%-8s: %s", "Tagihan", mdl.getTagihan()),"GBK");
        }
        if(!mdl.getDenda().equals("NULL")){
            btService.sendMessage(String.format("%-8s: %s", "Denda", mdl.getDenda()),"GBK");
        }
        if(!mdl.getAdmin().equals("NULL")){
            btService.sendMessage(String.format("%-8s: %s", "Admin", mdl.getAdmin()),"GBK");
        }
        if(!mdl.getTotal_bayar().equals("NULL")){
            btService.sendMessage(String.format("%-8s: %s", "Jumlah", mdl.getTotal_bayar()),"GBK");
        }



        if(!mdl.getReff().equals("NULL")){
            btService.sendMessage(String.format("%-8s: %s", "Reff", mdl.getReff()),"GBK");
        }

        cmd[2] &=  0xEF;
        btService.write(cmd);
        btService.sendMessage("\nTERIMAKASIH", "GBK");
        btService.sendMessage("ID# : " + outletid + " ["+ namaOutlet+"]", "GBK");
        btService.sendMessage("Powered By " + powered, "GBK");
        isBtnPrintClick = false;
    }
}
