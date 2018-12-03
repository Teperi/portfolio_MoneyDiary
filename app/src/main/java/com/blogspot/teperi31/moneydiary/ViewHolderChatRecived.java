package com.blogspot.teperi31.moneydiary;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolderChatRecived extends RecyclerView.ViewHolder {
	TextView messageTextView;
	ImageView messageImageView;
	TextView messageTimeView;
	TextView messengerTextView;
	CircleImageView messengerImageView;
	
	public ViewHolderChatRecived(View v) {
		super(v);
		messageTextView = itemView.findViewById(R.id.messenger_chatcontent_recived_row_contentText);
		messageImageView = itemView.findViewById(R.id.messenger_chatcontent_recived_row_contentImage);
		messageTimeView = itemView.findViewById(R.id.messenger_chatcontent_recived_row_time);
		messengerTextView = itemView.findViewById(R.id.messenger_chatcontent_recived_row_userNickText);
		messengerImageView = itemView.findViewById(R.id.messenger_chatcontent_recived_row_profileImage);
	}
	
	public void bindToChat(DataChatContent usermessage) {
		//텍스트가 있는 경우 텍스트 연결
		if (usermessage.getText() != null) {
			messageTextView.setText(usermessage.getText());
			messageTextView.setVisibility(TextView.VISIBLE);
			messageImageView.setVisibility(ImageView.GONE);
		}
		// 사진이 있는 경우 사진 연결
		else if (usermessage.getImageUrl() != null) {
			String imageUrl = usermessage.getImageUrl();
			if (imageUrl.startsWith("gs://")) {
				StorageReference storageReference = FirebaseStorage.getInstance()
						.getReferenceFromUrl(imageUrl);
				storageReference.getDownloadUrl().addOnCompleteListener(
						new OnCompleteListener<Uri>() {
							@Override
							public void onComplete(@NonNull Task<Uri> task) {
								if (task.isSuccessful()) {
									String downloadUrl = task.getResult().toString();
									Glide.with(messageImageView.getContext())
											.load(downloadUrl)
											.into(messageImageView);
								} else {
									Log.w("test", "Getting download url was not successful.",
											task.getException());
								}
							}
						});
			} else {
				Glide.with(messageImageView.getContext())
						.load(usermessage.getImageUrl())
						.into(messageImageView);
			}
			messageImageView.setVisibility(ImageView.VISIBLE);
			messageTextView.setVisibility(TextView.GONE);
		}
		messengerTextView.setText(usermessage.getName());
		if (usermessage.getPhotoUrl() == null) {
			messengerImageView.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(),
					R.drawable.cat3));
		} else {
			Glide.with(itemView.getContext())
					.load(usermessage.getPhotoUrl())
					.into(messengerImageView);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		messageTimeView.setText(sdf.format(new Date(usermessage.getDateTime())));
		
	}
}