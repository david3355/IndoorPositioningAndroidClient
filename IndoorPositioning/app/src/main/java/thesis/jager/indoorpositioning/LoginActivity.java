package thesis.jager.indoorpositioning;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import jager.indoornav.wscconvention.model.Information;
import jager.indoornav.wscconvention.model.InformationCodes;
import thesis.jager.indoorpositioning.client.Client;
import thesis.jager.indoorpositioning.client.config.ConfigManager;
import thesis.jager.indoorpositioning.client.eventhandling.ClientEventCallback;
import thesis.jager.indoorpositioning.client.eventhandling.HTTPResponseEventCallback;
import thesis.jager.indoorpositioning.client.eventhandling.WebsocketServerEventCallback;
import thesis.jager.indoorpositioning.util.ActivityUtil;

public class LoginActivity extends AppCompatActivity implements ClientEventCallback, WebsocketServerEventCallback, HTTPResponseEventCallback
{
       private Button btn_login;
       private EditText edit_username, edit_passwd;
       private ProgressBar progress_login;
       private CheckBox check_savecreds;
       private LinearLayout panel_progress;
       private Client client;
       private ConfigManager configManager;

       public final static String KEY_USERNAME = "username";

       @Override
       protected void onCreate(Bundle savedInstanceState)
       {
              super.onCreate(savedInstanceState);
              setContentView(R.layout.activity_login);
              btn_login = (Button) findViewById(R.id.btn_login);
              edit_username = (EditText) findViewById(R.id.edit_username);
              edit_passwd = (EditText) findViewById(R.id.edit_passwd);
              progress_login = (ProgressBar) findViewById(R.id.progress_login);
              check_savecreds = (CheckBox) findViewById(R.id.check_savecreds);
              panel_progress= (LinearLayout) findViewById(R.id.panel_progress);

              configManager = ConfigManager.getInstance();

              String savedUsername = configManager.getSavedUsername();
              String savedPassword = configManager.getSavedPassword();
              boolean saveCheck = configManager.getSaveChecked();
              if(!savedUsername.equals("")) edit_username.setText(savedUsername);
              if(!savedPassword.equals("")) edit_passwd.setText(savedPassword);
              check_savecreds.setChecked(saveCheck);

              btn_login.setOnClickListener(btnLoginOnClick);
              edit_username.addTextChangedListener(usernameTextWatcher);
              edit_passwd.addTextChangedListener(passwordTextWatcher);
              check_savecreds.setOnCheckedChangeListener(checkSavecredsListener);
       }

       private View.OnClickListener btnLoginOnClick = new View.OnClickListener()
       {
              @Override
              public void onClick(View view)
              {
                     loginProcess();
              }
       };

       private CompoundButton.OnCheckedChangeListener checkSavecredsListener = new CompoundButton.OnCheckedChangeListener()
       {
              @Override
              public void onCheckedChanged(CompoundButton compoundButton, boolean enabled)
              {
                     if(enabled)
                     {
                            String username = edit_username.getText().toString();
                            String pwd = edit_passwd.getText().toString();
                            configManager.saveUsername(username);
                            configManager.savePassword(pwd);
                     }
                     else
                     {
                            configManager.saveUsername("");
                            configManager.savePassword("");
                     }
                     configManager.saveSaveCheck(enabled);
              }
       };

       private TextWatcher usernameTextWatcher = new TextWatcher()
{
       @Override
       public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
       {

       }

       @Override
       public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
       {

       }

       @Override
       public void afterTextChanged(Editable editable)
       {
              configManager.saveUsername(editable.toString());
       }
};

       private TextWatcher passwordTextWatcher = new TextWatcher()
       {
              @Override
              public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
              {

              }

              @Override
              public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
              {

              }

              @Override
              public void afterTextChanged(Editable editable)
              {
                     configManager.savePassword(editable.toString());
              }
       };

       @Override
       protected void onResume()
       {
              client = ActivityUtil.createClientIfNotExists(client, this, this);
              if(client!=null)
              {
                     client.subscribeWebsocketEventHandler(this);
              }
              super.onResume();
       }

       @Override
       protected void onPause()
       {
              if(client!=null)
              {
                     client.unsubscribeWebsocketEventHandler();
              }
              super.onPause();
       }

       private void loginProcess()
       {
              if (client == null)
              {
                     ActivityUtil.popup("Cannot access client: client is null!", this);
                     return;
              }
              String username = edit_username.getText().toString();
              String pwd = edit_passwd.getText().toString();
              btn_login.setEnabled(false);
              panel_progress.setVisibility(View.VISIBLE);
              client.login(username, pwd, this);
       }

       private void activityReturn(String username)
       {
              Intent result = new Intent();
              result.putExtra(KEY_USERNAME, username);
              this.setResult(RESULT_OK, result);
              finish();
       }

       @Override
       public void onError(String message)
       {
              ActivityUtil.popup(message, this);
       }

       @Override
       public void serverMessage(String message)
       {
              // A LoginActivytynek ezt nem kell lekezelnie, csak a Map-nek
       }

       @Override
       public void onEvent(String message)
       {
              ActivityUtil.popup(message, this);
       }

       @Override
       public void onHttpResponse(final Information result)
       {
             runOnUiThread(new Runnable()
             {
                    @Override
                    public void run()
                    {
                           panel_progress.setVisibility(View.GONE);
                           String username = edit_username.getText().toString();
                           if (result.getCode() == InformationCodes.LOGIN_OK.getInfocode())
                           {
                                  client.setUserName(username);
                                  if(!check_savecreds.isEnabled()) ActivityUtil.clearFields(edit_username, edit_passwd);
                                  activityReturn(username);
                           } else
                           {
                                  btn_login.setEnabled(true);
                                  ActivityUtil.popup(result.getInfo(), LoginActivity.this);
                           }
                    }
             });
       }
}
