package com.infozimo.android;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import com.infozimo.beans.User;
import com.infozimo.ui.util.DownloadImageTask;
import com.infozimo.util.Constants;
import com.infozimo.util.JSONParser;
import com.infozimo.util.ServiceCaller;
import com.infozimo.webservice.WebServiceURL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private static final int SELECT_PICTURE = 1;
	private LinearLayout layoutRegister, layoutLogin;
	private Button loginButton;
	private TextView tvRegister, tvPwdReset;

	private ImageView ivProfilePic;
	private EditText etUserId, etPwd;
	private EditText etEmailId, etName, etDob, etMobile, etNewPwd, etConfirmPwd;
	private RadioButton radioFemale, radioMale;

	private SharedPreferences sharedPref;
	private SharedPreferences.Editor spEditor;

	private byte[] imageInByte;

	private String gender = "";

	private Properties props;
	private boolean isEditMode;
	private boolean newUser;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_login);

		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		layoutRegister = (LinearLayout) findViewById(R.id.layoutRegister);
		layoutLogin = (LinearLayout) findViewById(R.id.layoutLogin);

		ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
		etUserId = (EditText) findViewById(R.id.etUserId);
		etPwd = (EditText) findViewById(R.id.etPwd);

		etEmailId = (EditText) findViewById(R.id.etEmailId);
		etName = (EditText) findViewById(R.id.etUserName);
		etDob = (EditText) findViewById(R.id.etDob);
		etMobile = (EditText) findViewById(R.id.etMobile);
		etNewPwd = (EditText) findViewById(R.id.etNewPwd);
		etConfirmPwd = (EditText) findViewById(R.id.etConfirmPwd);
		radioFemale = (RadioButton) findViewById(R.id.radioFemale);
		radioMale = (RadioButton) findViewById(R.id.radioMale);

		tvRegister = (TextView) findViewById(R.id.registerButton);
		tvPwdReset = (TextView) findViewById(R.id.pwdResetButton);
		loginButton = (Button) findViewById(R.id.loginButton);

		if("edit".equals((String) getIntent().getStringExtra("mode"))) {
			isEditMode = true;
			layoutRegister.setVisibility(View.VISIBLE);
			layoutLogin.setVisibility(View.GONE);
			tvRegister.setVisibility(View.GONE);
			tvPwdReset.setVisibility(View.GONE);
			loginButton.setText("Save");
			this.user = getUserProfile();
			populateUserProfile(user);
		}

		etDob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					Calendar cal = Calendar.getInstance();
					DatePickerDialog picker = new DatePickerDialog(LoginActivity.this, new DatePickerDialog.OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
							etDob.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
							etMobile.findFocus();
						}
					},
							cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

					cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 5);
					picker.getDatePicker().setMaxDate(cal.getTime().getTime());
					picker.show();

				}
			}
		});

		ivProfilePic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
			}
		});

		tvRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				layoutRegister.setVisibility(View.VISIBLE);
				layoutLogin.setVisibility(View.GONE);
				tvRegister.setVisibility(View.GONE);
				tvPwdReset.setVisibility(View.GONE);
				loginButton.setText("Register");
			}
		});

		tvPwdReset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String userId = etUserId.getText().toString();
				if(userId.equals("")) {
					Toast.makeText(MyApplication.getAppContext(), "Enter User ID", Toast.LENGTH_SHORT).show();
				} else {
					ServiceCaller serviceCaller = new ServiceCaller();
					String json = serviceCaller.callResetPasswordService(userId);
					try {
						if(JSONParser.isSucceeded(json)) {
							Toast.makeText(MyApplication.getAppContext(), "Your Password is reset. Please check your Mail.", Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(validateFields()) {
					if(layoutRegister.getVisibility() == View.VISIBLE) {
						if(isEditMode) {
							registerAndLogin();
						} else {
							ServiceCaller serviceCaller = new ServiceCaller();
							try {
								String json = serviceCaller.callGetUserService(etEmailId.getText().toString());
								User user = JSONParser.parseUser(json);
								if(null != user) {
									Toast.makeText(LoginActivity.this, "User ID Already Registered", Toast.LENGTH_SHORT).show();
								} else {
									newUser = true;
									registerAndLogin();
								}
							} catch(Exception e) {
								e.printStackTrace();
							}
						}

					} else {
						login();
					}
				}
			}
		});


	}

	public void login() {
		ServiceCaller serviceCaller = new ServiceCaller();
		String authJson = null;
		authJson = serviceCaller.callAuthenticateService(etUserId.getText().toString(), etPwd.getText().toString());

		JSONParser jsonParser = new JSONParser();
		try {
			if(jsonParser.isAuthenticationSucceded(authJson)) {
				String json = serviceCaller.callGetUserService(etUserId.getText().toString());
				User user = JSONParser.parseUser(json);
				updateSharedPrefs(user);
				writeProperty();
			} else {
				Toast.makeText(this, "Bad User ID/Password or Account Not Activated", Toast.LENGTH_SHORT).show();
			}
		} catch(JSONException ex) {
			ex.printStackTrace();
		}
	}

	public void registerAndLogin() {
		String emailId = etEmailId.getText().toString().trim();
		String name = etName.getText().toString().trim();
		String dob = etDob.getText().toString().trim();
		String mobile = etMobile.getText().toString().trim();
		String newPwd = etNewPwd.getText().toString().trim();

		User user = new User();
		user.setUserId(emailId);
		user.setUserName(name);

		if(radioFemale.isChecked())
			user.setGender('F');
		else
			user.setGender('M');

		user.setDob(dob);
		user.setPwd(newPwd);
		user.setMobile(mobile);

		ServiceCaller serviceCaller = new ServiceCaller();

		/*if(imageInByte == null) {
			String json = serviceCaller.callProfilePicService(sharedPref.getString(Constants.USER_ID, ""));
			String imageByte = null;
			try {
				imageByte = JSONParser.parseProfilePicBytes(json);
				imageInByte = Base64.decode(imageByte, Base64.DEFAULT);
				user.setPicture(imageByte);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}*/

		if(imageInByte != null) {
			user.setPicture(Base64.encodeToString(imageInByte, Base64.DEFAULT));
		}

		updateUserProfile(user);
	}

	public void populateUserProfile(User user) {
		etEmailId.setText(user.getUserId());
		etEmailId.setEnabled(false);
		etName.setText(user.getUserName());
		etDob.setText(user.getDob());
		etMobile.setText(user.getMobile());
		if(user.getGender() == 'M') {
			radioMale.setChecked(true);
		} else {
			radioFemale.setChecked(true);
		}

		new DownloadImageTask(ivProfilePic).execute(WebServiceURL.USER_PIC.toString() + user.getUserId());
	}

	private void updateUserProfile(User user) {
		ServiceCaller serviceCaller = new ServiceCaller();
		try {
			String json = null;

			serviceCaller.callUpdateUserService(user);

			json = serviceCaller.callGetUserService(user.getUserId());
			User updatedUser = JSONParser.parseUser(json);
			if(updatedUser != null) {
				if(isEditMode) {
					Toast.makeText(this, "Update Success", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(this, MainActivity.class);
					intent.putExtra("display", 1);
					startActivity(intent);
					finish();
				} else {
					String mailJson = serviceCaller.callSendMailService(user.getUserId());
					Toast.makeText(this, "Registration Success and Activation Mail Sent", Toast.LENGTH_SHORT).show();
					if(newUser) {
						Intent intent = new Intent(this, MainActivity.class);
						startActivity(intent);
					} else {
						writeProperty();
					}
				}
			} else {
				Toast.makeText(this, "Registration Failed. Please Try After Some Time.", Toast.LENGTH_SHORT).show();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void updateSharedPrefs(User user) {
		spEditor = sharedPref.edit();
		spEditor.putString(Constants.USER_ID, user.getUserId());
		spEditor.putString(Constants.USER_NAME, user.getUserName());
		spEditor.putString(Constants.USER_PIC_URL, user.getPicture());
		spEditor.putString(Constants.GENDER, String.valueOf(user.getGender()));
		spEditor.putString(Constants.DOB, user.getDob());
		spEditor.putString(Constants.MOBILE, user.getMobile());
		spEditor.apply();
	}

	private void writeProperty() {
		try {
			props = new Properties();
			props.setProperty(Constants.USER_ID, etUserId.getText().toString());

			File file = new File(getFilesDir(), "infozimo.properties");
			FileOutputStream fileOut = new FileOutputStream(file);
			fileOut.write(new String(Constants.USER_ID + "=" + etUserId.getText().toString()).getBytes());

			fileOut.close();

			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
			Uri uri = data.getData();

			try {
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
				imageInByte = stream.toByteArray();

				//Toast.makeText(this, String.valueOf(lengthbmp), Toast.LENGTH_LONG).show();
				ivProfilePic.setBackground(null);
				ivProfilePic.setImageBitmap(null);
				ivProfilePic.setImageBitmap(bitmap);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onBackPressed() {
		if(layoutRegister.getVisibility() == View.VISIBLE) {
			if(isEditMode) {
				finish();
			} else {
				layoutLogin.setVisibility(View.VISIBLE);
				layoutRegister.setVisibility(View.GONE);
				tvRegister.setVisibility(View.VISIBLE);
				tvPwdReset.setVisibility(View.VISIBLE);
				loginButton.setText("Login");
			}
		} else {
			super.onBackPressed();
		}
	}

	public static Bitmap getPicture(Uri selectedImage) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = MyApplication.getAppContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String picturePath = cursor.getString(columnIndex);
		cursor.close();
		return BitmapFactory.decodeFile(picturePath);
	}

	private User getUserProfile() {
		ServiceCaller serviceCaller = new ServiceCaller();
		String userJson = null;
		try {
			userJson = serviceCaller.callGetUserService(sharedPref.getString(Constants.USER_ID, ""));
		} catch (JSONException ex) {
			ex.printStackTrace();
		}

		User user = null;
		JSONParser jsonParser = new JSONParser();
		try {
			user = jsonParser.parseUser(userJson);
		} catch(JSONException ex) {
			ex.printStackTrace();
		}

		if(null != user) {
			SharedPreferences.Editor spEditor = sharedPref.edit();
			spEditor.putString(Constants.USER_ID, user.getUserId());
			spEditor.putString(Constants.USER_NAME, user.getUserName());
			spEditor.putString(Constants.GENDER, Character.toString(user.getGender()));
			spEditor.putString(Constants.DOB, user.getDob());
			spEditor.putString(Constants.MOBILE, user.getMobile());
			spEditor.putString(Constants.USER_PIC_URL, user.getPicture());
			spEditor.apply();
		}

		return user;

	}

	public boolean validateFields() {
		if(layoutRegister.getVisibility() == View.VISIBLE) {
			String emailId = etEmailId.getText().toString().trim();
			String name = etName.getText().toString().trim();
			String dob = etDob.getText().toString().trim();
			String mobile = etMobile.getText().toString().trim();
			String newPwd = etNewPwd.getText().toString().trim();
			String confirmPwd = etConfirmPwd.getText().toString().trim();

			String[] fields = {emailId, name, dob, mobile, newPwd, confirmPwd};
			for(String field : fields) {
				if("".equals(field)) {
					Toast.makeText(this, "All Fields are Mandatory", Toast.LENGTH_SHORT).show();
					return false;
				}
			}

			if(!emailId.contains("@") || !emailId.contains(".")) {
				Toast.makeText(this, "Please Enter a Valid Email ID", Toast.LENGTH_SHORT).show();
				return false;
			}

			if(!newPwd.equals(confirmPwd)) {
				Toast.makeText(this, "Passwords Doesn't Match", Toast.LENGTH_SHORT).show();
				return false;
			}

			if(newPwd.length() < 6) {
				Toast.makeText(this, "Password Should Contain Atleast 6 Character", Toast.LENGTH_SHORT).show();
				return false;
			}

		} else {
			if(etUserId.getText().toString().trim().equals("") || etPwd.getText().toString().trim().equals("")) {
				Toast.makeText(this, "All Fields are Mandatory", Toast.LENGTH_SHORT).show();
				return false;
			}
			if(!etUserId.getText().toString().trim().contains("@") || !etUserId.getText().toString().trim().contains(".")) {
				Toast.makeText(this, "Please Enter a Valid Email ID", Toast.LENGTH_SHORT).show();
				return false;
			}
		}

		return true;
	}

}