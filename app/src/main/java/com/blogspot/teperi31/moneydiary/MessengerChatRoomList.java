package com.blogspot.teperi31.moneydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MessengerChatRoomList extends AppCompatActivity implements View.OnClickListener {
	// 레이아웃 내 뷰 연결
	private Toolbar mToolbar;
	private FloatingActionButton mFAB;
	
	// 파이어베이스 연결
	private FirebaseUser mUser;
	private DatabaseReference mDatabase;
	
	//리사이클러뷰 연결
	private RecyclerView mRecycler;
	private FirebaseRecyclerAdapter<DataMessengerUserRoom, ViewHolderChatRoomList> mFirebaseAdapter;
	private LinearLayoutManager mLayoutManager;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messenger_chatlist);
		
		// 툴바 연결
		mToolbar = findViewById(R.id.messenger_chatlist_toolbarTop);
		mToolbar.setTitle("채팅방 목록");
		setSupportActionBar(mToolbar);
		
		// 네비게이션 바 연결
		findViewById(R.id.messenger_chatlist_bottomBar_dashboardicon).setOnClickListener(this);
		findViewById(R.id.messenger_chatlist_bottomBar_listicon).setOnClickListener(this);
		findViewById(R.id.messenger_chatlist_bottomBar_myinfoicon).setOnClickListener(this);
		((ImageButton) findViewById(R.id.messenger_chatlist_bottomBar_messengericon)).setImageResource(R.drawable.ic_action_messenger_clicked);
		
		
		
		// 플로팅 버튼 연결
		// 전체 유저 리스트 띄우기
		mFAB = findViewById(R.id.messenger_chatlist_floatingbutton);
		mFAB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MessengerChatRoomList.this, MessengerUserList.class);
				startActivity(intent);
			}
		});
		
		// 파이어베이스 연결
		mUser = FirebaseAuth.getInstance().getCurrentUser();
		mDatabase = FirebaseDatabase.getInstance().getReference();
		
		mRecycler = findViewById(R.id.messenger_chatlist_recyclerview);
		
		// 세로로 쌓기 기능
		mLayoutManager = new LinearLayoutManager(this);
		// Query 에 있는 정렬 기능을 역순으로 해 주는 부분
		mLayoutManager.setReverseLayout(true);
		mLayoutManager.setStackFromEnd(true);
		
		mRecycler.setLayoutManager(mLayoutManager);
		
		
		// 채팅방 목록 가져오기
		Query chatRoomQuery = mDatabase.child("UserRooms").child(mUser.getUid()).orderByChild("lastTime");
		
		FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<DataMessengerUserRoom>()
				.setQuery(chatRoomQuery, DataMessengerUserRoom.class)
				.build();
		
		mFirebaseAdapter = new FirebaseRecyclerAdapter<DataMessengerUserRoom, ViewHolderChatRoomList>(options) {
			@NonNull
			@Override
			public ViewHolderChatRoomList onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
				View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.messenger_chatlist_row, viewGroup, false);
				return new ViewHolderChatRoomList(itemView);
			}
			
			@Override
			protected void onBindViewHolder(@NonNull ViewHolderChatRoomList holder, int position, @NonNull DataMessengerUserRoom model) {
				holder.bindToChatRoomList(model);
				
				DatabaseReference ChatRoomRef = getRef(position);
				final String ChatRoomKey = ChatRoomRef.getKey();
				holder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(v.getContext(), ChatActivity.class);
						intent.putExtra("ChatRoomKey", ChatRoomKey);
						startActivity(intent);
					}
				});
			}
			
			@Override
			public void onDataChanged() {
				// TODO : 로딩화면 넣기
				super.onDataChanged();
			}
		};
		// 어뎁터와 리사이클러뷰 연결
		mRecycler.setAdapter(mFirebaseAdapter);
		
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// 그룹 만들기 버튼 숨기기 & 네비게이션바 및 플로팅 버튼 보이기
		findViewById(R.id.messenger_chatlist_bottomBar).setVisibility(View.VISIBLE);
		findViewById(R.id.messenger_chatlist_floatingbutton).setVisibility(View.VISIBLE);
		findViewById(R.id.messenger_chatlist_createchatGroup).setVisibility(View.GONE);
		// 어뎁터가 있으면 실시간 연결
		if (mFirebaseAdapter != null) {
			//mProgessStart();
			mFirebaseAdapter.startListening();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		// 페이지가 멈출 때 실시간 연결기능 해제
		if (mFirebaseAdapter != null) {
			mFirebaseAdapter.stopListening();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			//네비게이션 버튼 클릭시 이동하는 인텐트
			case R.id.messenger_chatlist_bottomBar_dashboardicon:
				startActivity(new Intent(this, MainTestActivity.class));
				// 애니메이션
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
				break;
			case R.id.messenger_chatlist_bottomBar_listicon:
				startActivity(new Intent(this, RecyclerViewMoneyFlowFB.class));
				// 애니메이션
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
				break;
			case R.id.messenger_chatlist_bottomBar_myinfoicon:
				startActivity(new Intent(this, SignInAccountInfo.class));
				// 애니메이션
				finish();
				overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
				break;
			default:
				break;
		}
	}
}
