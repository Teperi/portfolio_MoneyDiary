package com.blogspot.teperi31.moneydiary;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class InputDiaryCreate extends AppCompatActivity {
	Calendar myCalendar;
	TextView datepicker;
	DatePickerDialog.OnDateSetListener date;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inputcreatediary);
		
		
		myCalendar = Calendar.getInstance(Locale.KOREA);
		
		// 툴바 입력
		Toolbar myToolbar = findViewById(R.id.my_toolbarTop_basic);
		setActionBar(myToolbar);
		
		datepicker = findViewById(R.id.inputCreateDiaryDateEdit);
		
		updateLabel();
		
		
		date = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
				myCalendar.set(year, month, dayOfMonth);
				updateLabel();
			}
		};
		
		datepicker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new DatePickerDialog(InputDiaryCreate.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		
		
		
		final Intent intent = getIntent();
		final String action = intent.getAction();
		final String type = intent.getType();
		
		
		EditText sharecontent = findViewById(R.id.inputCreateDiaryContent);
		final ImageView shareimage = findViewById(R.id.inputCreateDiaryImage);
		
		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if ("text/plain".equals(type)) {
				String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
				if (sharedText != null) {
					sharecontent.setText(sharedText);
				}
				
			} else if (type.startsWith("image/")) {
				
				Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
				if (imageUri != null) {
					shareimage.setImageURI(imageUri);
					System.out.println(imageUri.toString());
				}
			}
		}
		
		findViewById(R.id.inputCreateDiaryCamera).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dispatchTakePictureIntent();
			}
		});
		
		
		
		findViewById(R.id.inputCreateDiaryComplete).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText inputTitleText = findViewById(R.id.inputCreateDiaryTitle);
				EditText inputContentText = findViewById(R.id.inputCreateDiaryContent);
				
				DataDiary obj = null;
				
				if (inputTitleText.getText().toString().length() <=0) {
					Toast.makeText(InputDiaryCreate.this, "제목을 입력하세요", Toast.LENGTH_SHORT).show();
				} else if (inputContentText.getText().toString().length() <= 0) {
					Toast.makeText(InputDiaryCreate.this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
				} else if (Intent.ACTION_SEND.equals(action) && type.startsWith("image/")) {
					Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
					obj = new DataDiary(myCalendar,
							inputTitleText.getText().toString(),
							inputContentText.getText().toString(),
							imageUri
							);
					ApplicationClass.dList.add(obj);
					
					Intent i = new Intent(InputDiaryCreate.this, RecyclerviewDiary.class);
					// 스택 관리
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					
					startActivity(i);
				} else {
					obj = new DataDiary(myCalendar,
							inputTitleText.getText().toString(),
							inputContentText.getText().toString(),
							null
					);
					Intent i = new Intent(InputDiaryCreate.this, RecyclerviewDiary.class);
					ApplicationClass.dList.add(obj);
					// 스택 관리
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					
					startActivity(i);
				}
			
			}
		});
		
		// 취소 버튼 클릭시 팝업 띄움
		// 뒤로가기 버튼에도 같은 기능 구현
		findViewById(R.id.inputCreateDiaryCancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder cancelaction = new AlertDialog.Builder(InputDiaryCreate.this);
				
				cancelaction.setPositiveButton("계속 입력", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				
				cancelaction.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				
				AlertDialog cancelpopup = cancelaction.create();
				cancelpopup.setTitle("경고");
				cancelpopup.setMessage("다이어리 기록을 그만 하시겠습니까?");
				cancelpopup.show();
				
			}
		});
	}
	// 뒤로가기 버튼 눌렀을 시
	@Override
	public void onBackPressed() {
		
		AlertDialog.Builder cancelaction = new AlertDialog.Builder(InputDiaryCreate.this);
		
		cancelaction.setPositiveButton("계속 입력", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		cancelaction.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				InputDiaryCreate.super.onBackPressed();
			}
		});
		
		AlertDialog cancelpopup = cancelaction.create();
		cancelpopup.setTitle("TEST");
		cancelpopup.setMessage("지출 기록을 그만 하시겠습니까?");
		cancelpopup.show();
	}
	
	@Override
	protected void onPause() {
		finish();
		super.onPause();
	}
	
	//달력 string 으로 텍스트뷰 내보내기
	private void updateLabel(){
		String myFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
		
		datepicker.setText(sdf.format(myCalendar.getTime()));
	}
	
	
	// 카메라 앱 사용을 위한 코드
	
	// 사진 찍기
	String mCurrentPhotoPath;
	
	private File createImageFile() throws IOException {
		// 시간 측정
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		// 파일 이름 만들기
		String imageFileName = "JPEG_" + timeStamp + "_";
		// 경로 찾기
		File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		// 이미지 파일 만들기
		File image = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
		);
		
		// 사진 저장하기
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}
	
	// 사진 가져오기
	static final int REQUEST_TAKE_PHOTO = 1;
	
	// 사진 가져오기 인텐트 생성
	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File
				Toast.makeText(this, "오류로 인해 사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				Uri photoURI = FileProvider.getUriForFile(this,
						"com.example.android.fileprovider",
						photoFile);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			}
		}
	}
}