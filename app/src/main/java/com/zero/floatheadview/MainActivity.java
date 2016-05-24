package com.zero.floatheadview;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zero.floatheadview.adapter.CommonString;
import com.zero.floatheadview.adapter.ContactAdapter;
import com.zero.floatheadview.adapter.expandRecyclerviewadapter.StickyRecyclerHeadersDecoration;
import com.zero.floatheadview.model.ContactModel;
import com.zero.floatheadview.pinyin.CharacterParser;
import com.zero.floatheadview.pinyin.PinyinComparator;
import com.zero.floatheadview.widget.DividerDecoration;
import com.zero.floatheadview.widget.MyLinearLayoutManager;
import com.zero.floatheadview.widget.MyLinearSmoothScroller;
import com.zero.floatheadview.widget.SideBar;
import com.zero.floatheadview.widget.TouchableRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * RecyclerView实现联系人列表
 */
public class MainActivity extends AppCompatActivity {

    private SideBar mSideBar;
    private TextView mUserDialog;
    private TouchableRecyclerView mRecyclerView;
    private ViewGroup blockCategoryContainer;
    private ScrollView scrollView;

    ContactModel mModel;
    private List<ContactModel.MembersEntity> mMembers = new ArrayList<>();
    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;
    private ContactAdapter mAdapter;
    private List<ContactModel.MembersEntity> mAllLists = new ArrayList<>();
    private int mPermission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermission();
        initView();

    }

    private void getPermission() {
        mPermission = CommonString.PermissionCode.TEACHER;
    }


    private void initView() {
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        mSideBar = (SideBar) findViewById(R.id.contact_sidebar);
        mUserDialog = (TextView) findViewById(R.id.contact_dialog);
        mRecyclerView = (TouchableRecyclerView) findViewById(R.id.contact_member);
        blockCategoryContainer = (ViewGroup)findViewById(R.id.shophome_goods_category_container);
        scrollView = (ScrollView)findViewById(R.id.shophome_goods_category_scrollview);
        mSideBar.setTextView(mUserDialog);


//        fillData();
        getNetData(0);


    }


    public void getNetData(final int type) {

        //id 已经被处理过
        String tempData = "{\"groupName\":\"中国\",\"admins\":[{\"id\":\"111221\",\"username\":\"程景瑞\",\"profession\":\"teacher\"},{\"id\":\"bfcd1feb5db2\",\"username\":\"钱黛\",\"profession\":\"teacher\"},{\"id\":\"bfcd1feb5db2\",\"username\":\"许勤颖\",\"profession\":\"teacher\"},{\"id\":\"bfcd1feb5db2\",\"username\":\"孙顺元\",\"profession\":\"teacher\"},{\"id\":\"fcd1feb5db2\",\"username\":\"朱佳\",\"profession\":\"teacher\"},{\"id\":\"bfcd1feb5db2\",\"username\":\"李茂\",\"profession\":\"teacher\"},{\"id\":\"d1feb5db2\",\"username\":\"周莺\",\"profession\":\"teacher\"},{\"id\":\"cd1feb5db2\",\"username\":\"任倩栋\",\"profession\":\"teacher\"},{\"id\":\"d1feb5db2\",\"username\":\"严庆佳\",\"profession\":\"teacher\"}],\"members\":[{\"id\":\"d1feb5db2\",\"username\":\"彭怡1\",\"profession\":\"student\"},{\"id\":\"d1feb5db2\",\"username\":\"方谦\",\"profession\":\"student\"},{\"id\":\"dd2feb5db2\",\"username\":\"谢鸣瑾\",\"profession\":\"student\"},{\"id\":\"dd2478fb5db2\",\"username\":\"孔秋\",\"profession\":\"student\"},{\"id\":\"dd24cd1feb5db2\",\"username\":\"曹莺安\",\"profession\":\"student\"},{\"id\":\"dd2478eb5db2\",\"username\":\"酆有松\",\"profession\":\"student\"},{\"id\":\"dd2478b5db2\",\"username\":\"姜莺岩\",\"profession\":\"student\"},{\"id\":\"dd2eb5db2\",\"username\":\"谢之轮\",\"profession\":\"student\"},{\"id\":\"dd2eb5db2\",\"username\":\"钱固茂\",\"profession\":\"student\"},{\"id\":\"dd2d1feb5db2\",\"username\":\"潘浩\",\"profession\":\"student\"},{\"id\":\"dd24ab5db2\",\"username\":\"花裕彪\",\"profession\":\"student\"},{\"id\":\"dd24ab5db2\",\"username\":\"史厚婉\",\"profession\":\"student\"},{\"id\":\"dd24a00d1feb5db2\",\"username\":\"陶信勤\",\"profession\":\"student\"},{\"id\":\"dd24a5db2\",\"username\":\"水天固\",\"profession\":\"student\"},{\"id\":\"dd24a5db2\",\"username\":\"柳莎婷\",\"profession\":\"student\"},{\"id\":\"dd2d1feb5db2\",\"username\":\"冯茜\",\"profession\":\"student\"},{\"id\":\"dd24a0eb5db2\",\"username\":\"吕言栋\",\"profession\":\"student\"}],\"creater\":{\"id\":\"1\",\"username\":\"褚奇清\",\"profession\":\"teacher\"}}";

        try {
            Gson gson = new GsonBuilder().create();
            mModel = gson.fromJson(tempData, ContactModel.class);
            setUI();
        } catch (Exception e) {

        }


    }
    private MyLinearLayoutManager layoutManager;
    private void setUI() {

        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                if (mAdapter != null) {
                    mAdapter.closeOpenedSwipeItemLayoutWithAnim();
                }
                int position = mAdapter.getPositionForSectionByName(s);
                if (position != -1) {
                    mRecyclerView.scrollToPosition(position);
                }

            }
        });
        seperateLists(mModel);
        initCategoryView();
        if (mAdapter == null) {
            mAdapter = new ContactAdapter(this, mAllLists, mPermission, mModel.getCreater().getId());
            int orientation = LinearLayoutManager.VERTICAL;
            layoutManager = new MyLinearLayoutManager(this, orientation, false);
            layoutManager.setSmoothScrollerListener(smoothScrollerListener);
            mRecyclerView.setLayoutManager(layoutManager);
//            layoutManager.setReverseLayout(true);

            mRecyclerView.setAdapter(mAdapter);
            final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
            headersDecor.setOnHeadViewChangeListener(new StickyRecyclerHeadersDecoration.OnHeadViewChangeListener() {
                @Override
                public void onHeadViewChangeListener(View headView) {
                    Log.e("HongLi","更新HeadView：" + ((TextView)((LinearLayout)headView).getChildAt(0)).getText().toString());
                    if(!canChangeCategory){
                        return;
                    }
                    changeCategory(((TextView)((LinearLayout)headView).getChildAt(0)).getText().toString());
                }
            });
            mRecyclerView.addItemDecoration(headersDecor);
            mRecyclerView.addItemDecoration(new DividerDecoration(this));

            //   setTouchHelper();
            mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    headersDecor.invalidateHeaders();
                }
            });
        } else {
            mAdapter.notifyDataSetChanged();
        }

    }
    private ArrayList<String> categoryNameList = new ArrayList<>();
    private void initCategoryView(){
        for(ContactModel.MembersEntity member : mAllLists){
            if(!categoryNameList.contains(member.getSortLetters())){
                categoryNameList.add(member.getSortLetters());
            }
        }
        View itemView;
        TextView textView;
        for(String name : categoryNameList){
            itemView = LayoutInflater.from(this).inflate(R.layout.view_shophome_goods_category_item,null);
            textView = (TextView)itemView.findViewById(R.id.shophome_goods_category_name);
            textView.setText(name);
            itemView.setTag(name);
            itemView.setOnClickListener(onClickListener);
            blockCategoryContainer.addView(itemView);
        }
    }
    private boolean canChangeCategory = true;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view instanceof  LinearLayout){
                int position = mAdapter.getPositionForSectionByName(view.getTag().toString());
                PointF pointf = layoutManager.computeScrollVectorForPosition(position);
//                layoutManager.smoothScrollToPosition(mRecyclerView,null,pointf.y < 0 ? position > 0 ? position + 1 : position : position);
                layoutManager.smoothScrollToPosition(mRecyclerView,null,position);
                canChangeCategory = false;
                changeCategory(view.getTag().toString());
                //延迟一秒，用于防止滚动造成的HeadView改变,如果可以监控View的滑动开始于结束可以不用此方式
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        canChangeCategory  = true;
                    }
                },1000);
            }
        }
    };

    private void changeCategory(String newCategoryName){
        int childSize = blockCategoryContainer.getChildCount();
        for(int i = 0;i < childSize;i++){
            if(newCategoryName.equals(blockCategoryContainer.getChildAt(i).getTag().toString())){
                blockCategoryContainer.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.white));
//                if(canChangeCategory){
//                    scrollView.smoothScrollTo(0,(int)blockCategoryContainer.getChildAt(i).getY());
//                }
                scrollView.smoothScrollTo(0,(int)blockCategoryContainer.getChildAt(i).getY());
//                if(scrollView.getScrollY() <= blockCategoryContainer.getChildAt(i).getY() && scrollView.getScrollY() + scrollView.getMeasuredHeight() >= blockCategoryContainer.getChildAt(i).getY()){
//
//                }else{
//                    scrollView.smoothScrollTo(0,(int)blockCategoryContainer.getChildAt(i).getY());
//                }
            }else{
                blockCategoryContainer.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.black));
            }
        }
    }

    private MyLinearSmoothScroller.SmoothScrollerListener smoothScrollerListener = new MyLinearSmoothScroller.SmoothScrollerListener() {
        @Override
        public void onStart() {
//            canChangeCategory = false;
        }

        @Override
        public void onStop() {
//            canChangeCategory = true;
        }
    };

    private void seperateLists(ContactModel mModel) {
        //群主
        int headId = 0;
        ContactModel.CreaterEntity creatorEntity = mModel.getCreater();
        ContactModel.MembersEntity tempMember = new ContactModel.MembersEntity();
        tempMember.setUsername(creatorEntity.getUsername());
        tempMember.setId(creatorEntity.getId());
        tempMember.setProfession(creatorEntity.getProfession());
        tempMember.setSortLetters("群主");
        tempMember.setHeadId(headId);
        headId++;
        mAllLists.add(tempMember);


        //管理员

        if (mModel.getAdmins() != null && mModel.getAdmins().size() > 0) {
            for (ContactModel.AdminsEntity e : mModel.getAdmins()) {
                ContactModel.MembersEntity eMember = new ContactModel.MembersEntity();
                eMember.setSortLetters("管理员");
                eMember.setProfession(e.getProfession());
                eMember.setUsername(e.getUsername());
                eMember.setId(e.getId());
                eMember.setHeadId(headId);
                mAllLists.add(eMember);
            }
        }
        //members;
        if (mModel.getMembers() != null && mModel.getMembers().size() > 0) {
            for (int i = 0; i < mModel.getMembers().size(); i++) {
                ContactModel.MembersEntity entity = new ContactModel.MembersEntity();
                entity.setId(mModel.getMembers().get(i).getId());
                entity.setUsername(mModel.getMembers().get(i).getUsername());
                entity.setProfession(mModel.getMembers().get(i).getProfession());
                String pinyin = characterParser.getSelling(mModel.getMembers().get(i).getUsername());
                String sortString = pinyin.substring(0, 1).toUpperCase();

                if (sortString.matches("[A-Z]")) {
                    entity.setSortLetters(sortString.toUpperCase());
                } else {
                    entity.setSortLetters("#");
                }
                entity.setHeadId(sortString.charAt(0));
                mMembers.add(entity);
            }
            Collections.sort(mMembers, pinyinComparator);
            mAllLists.addAll(mMembers);
        }


    }


    public void deleteUser(final int position) {
        mAdapter.remove(mAdapter.getItem(position));
        showToast("删除成功");

    }

    public void showToast(String value) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();

    }


}
