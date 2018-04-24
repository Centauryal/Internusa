package com.supersoft.internusa.ui.payment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.ProfilDB;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itclub21 on 4/27/2017.
 */

public class InfoJaringanFragment extends Fragment {

    @BindView(R.id.html)
    WebView html;

    @BindView(R.id.pgBar)
    ProgressBar pgBar;

    public static InfoJaringanFragment newInstance()
    {
        InfoJaringanFragment fm = new InfoJaringanFragment();
        return fm;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_jaringan_fragment, container, false);

        ButterKnife.bind(this, view);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pgBar.setVisibility(View.VISIBLE);
                infoJaringan();
            }
        }, 1000);
    }

    private void infoJaringan()
    {
        ProfilDB profil = new DBHelper(getActivity()).getProfilDb();
        ModelRequestInfo inf = new ModelRequestInfo();
        inf.agenid = profil.agenid;
        inf.deviceid = Constant.getUUID(getActivity());
        inf.pin = profil.pin;
        inf.hp = profil.hp;

        RetrofitBuilder builder = new RetrofitBuilder(getActivity().getResources().getString(R.string.CONF_URI),"jaringan_agen");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<JsonObject> api = githubUserAPI.onSubmitDefault(inf);
        api.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                pgBar.setVisibility(View.GONE);
                //Log.e("res", new Gson().toJson(response.body()));
                String htmlText;
                if(response.isSuccessful())
                {
                    JsonArray jArr = response.body().getAsJsonArray("response");
                    htmlText = "";
                    htmlText = htmlText + "<TABLE cellpadding=3 cellspacing=2 width=100% bgcolor='#cecece'>";
                    for (int i=0; i<jArr.size(); i++){
                        JsonObject giobj = jArr.get(i).getAsJsonObject();
                        for (Map.Entry<String, JsonElement> entry : giobj.entrySet()) {
                            String key=entry.getKey();
                            String valu = entry.getValue().getAsString();
                            if (!key.equals("status") && !key.equals("message")){
                                htmlText = htmlText + "<TR>";
                                htmlText = htmlText + "<TD bgcolor='#cecece' width='50%'>"+key+"</TD><TD bgcolor='#FFFFFF'>"+valu+"</TD>";
                                htmlText = htmlText + "</TR>";
                            }else if (key.equals("message")){
                                htmlText = htmlText + "<TR>";
                                htmlText = htmlText + "<TD bgcolor='#cecece' colspan=2 style='font-size:15px; font-style:bold;padding:5px'>"+valu+"</TD>";
                                htmlText = htmlText + "</TR>";
                            }
                        }
                    }
                    htmlText = htmlText+ "</TABLE>";
                    html.loadData(htmlText, "text/html", null);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pgBar.setVisibility(View.GONE);
            }
        });
    }

}
