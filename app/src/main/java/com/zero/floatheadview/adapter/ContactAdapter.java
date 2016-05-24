/**
 * created by jiang, 12/3/15
 * Copyright (c) 2015, jyuesong@gmail.com All Rights Reserved.
 * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG              #
 * #                                                   #
 */

package com.zero.floatheadview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zero.floatheadview.MainActivity;
import com.zero.floatheadview.R;
import com.zero.floatheadview.adapter.expandRecyclerviewadapter.StickyRecyclerHeadersAdapter;
import com.zero.floatheadview.model.ContactModel;
import com.zero.floatheadview.widget.SwipeItemLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jiang on 12/3/15.
 * 根据当前权限进行判断相关的滑动逻辑
 */
public class ContactAdapter extends BaseAdapter<ContactAdapter.ContactViewHolder>
        implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    /**
     * 当前处于打开状态的item
     */
    private List<SwipeItemLayout> mOpenedSil = new ArrayList<>();

    private List<ContactModel.MembersEntity> mLists;

    private Context mContext;
    private int mPermission;
    private String createrID;
    private boolean isCreator;


    public static final String OWNER = "1";
    public static final String CREATER = "1";
    public static final String STUDENT = "student";

    public ContactAdapter(Context ct, List<ContactModel.MembersEntity> mLists, int permission, String createrID) {
        this.mLists = mLists;
        mContext = ct;
        mPermission = permission;
        this.addAll(mLists);
        this.createrID = createrID;
        if (createrID.equals(CREATER)) {
            isCreator = true;
        } else {
            isCreator = false;
        }
    }

    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactAdapter.ContactViewHolder holder, final int position) {
        SwipeItemLayout swipeRoot = holder.mRoot;
        if (getItem(position).getId().equals(OWNER)) {
            swipeRoot.setSwipeAble(false);
        } else if (isCreator) {
            swipeRoot.setSwipeAble(true);
            swipeRoot.setDelegate(new SwipeItemLayout.SwipeItemLayoutDelegate() {
                @Override
                public void onSwipeItemLayoutOpened(SwipeItemLayout swipeItemLayout) {
                    closeOpenedSwipeItemLayoutWithAnim();
                    mOpenedSil.add(swipeItemLayout);
                }

                @Override
                public void onSwipeItemLayoutClosed(SwipeItemLayout swipeItemLayout) {
                    mOpenedSil.remove(swipeItemLayout);
                }

                @Override
                public void onSwipeItemLayoutStartOpen(SwipeItemLayout swipeItemLayout) {
                    closeOpenedSwipeItemLayoutWithAnim();
                }
            });
            holder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ((MainActivity) mContext).deleteUser(position);
                }
            });
        } else {
            if (mPermission == CommonString.PermissionCode.TEACHER) {
                if (position != 0) {
                    if (getItem(position).getProfession().equals(STUDENT)) {

                        swipeRoot.setSwipeAble(true);
                        swipeRoot.setDelegate(new SwipeItemLayout.SwipeItemLayoutDelegate() {
                            @Override
                            public void onSwipeItemLayoutOpened(SwipeItemLayout swipeItemLayout) {
                                closeOpenedSwipeItemLayoutWithAnim();
                                mOpenedSil.add(swipeItemLayout);
                            }

                            @Override
                            public void onSwipeItemLayoutClosed(SwipeItemLayout swipeItemLayout) {
                                mOpenedSil.remove(swipeItemLayout);
                            }

                            @Override
                            public void onSwipeItemLayoutStartOpen(SwipeItemLayout swipeItemLayout) {
                                closeOpenedSwipeItemLayoutWithAnim();
                            }
                        });
                        holder.mDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                ((MainActivity) mContext).deleteUser(position);
                            }
                        });
                    } else {
                        swipeRoot.setSwipeAble(false);
                    }
                } else {
                    swipeRoot.setSwipeAble(false);
                }
            } else {
                swipeRoot.setSwipeAble(false);
            }
        }
        TextView textView = holder.mName;
        textView.setText(getItem(position).getUsername());

    }

    @Override
    public long getHeaderId(int position) {

//        return getItem(position).getSortLetters().charAt(0);
        return getItem(position).getHeadId();

    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        LinearLayout root = (LinearLayout) holder.itemView;
        TextView textView = (TextView)root.findViewById(R.id.recyclerview_head_txt);
//        String showValue = String.valueOf(getItem(position).getSortLetters().charAt(0));
//        if ("$".equals(showValue)) {
//            textView.setText("群主");
//        } else if ("%".equals(showValue)) {
//            textView.setText("系统管理员");
//
//        } else {
//            textView.setText(showValue);
//        }
        textView.setText(getItem(position).getSortLetters());
    }


    public int getPositionForSectionById(long headId) {
        for (int i = 0; i < getItemCount(); i++) {
            long itemHeadId = mLists.get(i).getHeadId();
            if(headId == itemHeadId){
                return i;
            }
        }
        return -1;

    }

    public int getPositionForSectionByName(String headName) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mLists.get(i).getSortLetters();
            String firstChar = sortStr.toUpperCase();
            if (firstChar.equals(headName)) {
                return i;
            }
        }
        return -1;

    }

    public void closeOpenedSwipeItemLayoutWithAnim() {
        for (SwipeItemLayout sil : mOpenedSil) {
            sil.closeWithAnim();
        }
        mOpenedSil.clear();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        public TextView mName;
        public SwipeItemLayout mRoot;
        public TextView mDelete;

        public ContactViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.item_contact_title);
            mRoot = (SwipeItemLayout) itemView.findViewById(R.id.item_contact_swipe_root);
            mDelete = (TextView) itemView.findViewById(R.id.item_contact_delete);


        }


    }
}
