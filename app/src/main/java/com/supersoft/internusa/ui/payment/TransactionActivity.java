package com.supersoft.internusa.ui.payment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.util.Constant;

/**
 * Created by itclub21 on 3/6/2017.

 $resp[] = array("nama"=>"M-Pasca Bayar","code"=>"isiulang","child"=>$isiulang);
 $resp[] = array("nama"=>"M-Prabayar","code"=>"pembayaran","child"=>$pembayaran);
 $resp[] = array("nama"=>"M-Commerce","code"=>"commerce","child"=>$commerce);
 $resp[] = array("nama"=>"M-Gate","code"=>"gate","child"=>$gate);
 $resp[] = array("nama"=>"M-Transaksi","code"=>"transaksi","child"=>$transaksi);
 $arr = array("status"=>1, "message"=>"Transaksi OK", "row"=>$resp);

 */


public class TransactionActivity extends AppCompatActivity {
    PojoLoadProvider.Child getChild;
    PojoLoadProvider.Row getRow;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaksi_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            getChild = new Gson().fromJson(extras.getString("myObjectChild"), PojoLoadProvider.Child.class);
            getRow = new Gson().fromJson(extras.getString("myObjectRow"), PojoLoadProvider.Row.class);
        }
        else {
            Constant.showMessageDialogColoseActivity(TransactionActivity.this,"Ops!!\nError when getting data from extras","Error");
            return;
        }

        Log.e("LOGG", getChild.getOpr() + " -- " + getChild.getCode()+ " -- " + getRow.getNama());

        switch (getRow.getCode())
        {
            case "isiulang":
                Constant.goToNextFragement(TransactionActivity.this, IsiulangFragment.newInstance(getChild, getRow),R.id.container, false, false, false);
                break;
            case "pembayaran":
                Constant.goToNextFragement(TransactionActivity.this, PrabayarFragment.newInstance(getChild, getRow),R.id.container, false, false, false);
                break;
            case "commerce":
                break;
            case "gate":
                if(getChild.getCode().equals("trfmember"))
                {
                    Constant.goToNextFragement(TransactionActivity.this, TransferSaldoMemberFragment.newInstance(),R.id.container, false, false, false);
                }
                else if(getChild.getCode().equals("trfbank"))
                {
                    Constant.goToNextFragement(TransactionActivity.this, TransferSaldoBankFragment.newInstance(),R.id.container, false, false, false);
                }
                break;
            case "transaksi":
                if(getChild.getCode().equals("tiketdeposit"))
                {
                    Constant.showActivity(this, TambahSaldoActivity.class, true);
                }
                else if(getChild.getCode().equals("komplain"))
                {
                    Constant.goToNextFragement(TransactionActivity.this, KomplainFragment.newInstance(),R.id.container, false, false, false);
                }
                else if(getChild.getCode().equals("konfirmasideposit"))
                {
                    Constant.goToNextFragement(TransactionActivity.this, KomplainFragment.newInstance(),R.id.container, false, false, false);
                }
                else if(getChild.getCode().equals("registrasidownline"))
                {
                    Constant.goToNextFragement(TransactionActivity.this, RegisterDownlineFragment.newInstance(),R.id.container, false, false, false);
                }
                else if(getChild.getCode().equals("tarikbonus"))
                {
                    Constant.goToNextFragement(TransactionActivity.this, TarikBonusFragment.newInstance(),R.id.container, false, false, false);
                }
                else if(getChild.getCode().equals("infojaringan"))
                {
                    Constant.goToNextFragement(TransactionActivity.this, InfoJaringanFragment.newInstance(),R.id.container, false, false, false);
                }
                break;
        }

    }
}
