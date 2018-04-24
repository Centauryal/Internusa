package com.supersoft.internusa.splash;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.MsgEvent;
import com.supersoft.internusa.model.PojoDefault;
import com.supersoft.internusa.model.Row;
import com.supersoft.internusa.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Centaury on 21/04/2018.
 */
public class MergeMemberFragment extends Fragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    static ArrayList<Row> pjoDefault;
    static ModelRequestInfo mRequest;

    public static MergeMemberFragment instance(ModelRequestInfo req, PojoDefault pj)
    {
        MergeMemberFragment fm = new MergeMemberFragment();
        pjoDefault = new ArrayList<>(pj.getRow());
        mRequest = req;
        return fm;
    }

    public static MergeMemberFragment instance(ModelRequestInfo req)
    {
        MergeMemberFragment fm = new MergeMemberFragment();
        pjoDefault = new ArrayList<>();
        mRequest = req;
        return fm;
    }

    public static MergeMemberFragment instance()
    {
        MergeMemberFragment fm = new MergeMemberFragment();
        pjoDefault = new ArrayList<>();
        mRequest = new ModelRequestInfo();
        return fm;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.merge_member_fragment, container, false);
        ButterKnife.bind(this, rootView);
        iniToolbar();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                //return position == 0 ? 2 : 1;
                return 1;
            }
        });

        recyclerview.setLayoutManager(layoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getContext(), R.dimen.item_offset);
        recyclerview.addItemDecoration(itemDecoration);
        recyclerview.setAdapter(new MyGridAdapter(getContext(), pjoDefault));

        if(pjoDefault.size() <= 0)
        {
            BottomSheetDialogFragment bottomSheetDialogFragment = BottomSheetFragment.instance(new Row(), mRequest);
            bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
        }
    }

    private void iniToolbar()
    {
        if(toolbar != null)
        {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setNavigationIcon(activity.getApplicationContext().getResources().getDrawable(R.drawable.ic_back_home_white));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new MsgEvent(Constant.EVENT_AKTIFASI_DAFTAR_ONLINE, null));
                }
            });
        }
    }

    @Optional
    @OnClick(R.id.btnSubmit)
    public void onSubmit(View view){
        BottomSheetDialogFragment bottomSheetDialogFragment = BottomSheetFragment.instance(new Row(), mRequest);
        bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.imgLogo)
        public ImageView imglogo;

        @BindView(R.id.txtFullname)
        public TextView txtFullname;

        View vw;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            vw = itemView;
        }

        public View getView()
        {
            return vw;
        }


    }

    public class MyGridAdapter extends RecyclerView.Adapter<MyViewHolder> {

        ArrayList<Row> modeling;
        Context _context;
        public MyGridAdapter(Context ctx, ArrayList<Row> model) {
            _context = ctx;
            this.modeling = model;
        }

        @Override
        public int getItemCount() {
            return modeling.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.merge_member_fragment_card_item, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            // set up any onClickListener you need on the view here
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            Constant.loadDefaulSlideImage(_context,modeling.get(position).getImagePath(), holder.imglogo);
            holder.txtFullname.setText(modeling.get(position).getNama());
            holder.getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    view.setSelected(true);
                    BottomSheetFragment    bottomSheetDialogFragment = BottomSheetFragment.instance(modeling.get(position), mRequest);
                    bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.setSelected(false);
                        }
                    }, 100);
                }
            });
        }
    }
}
