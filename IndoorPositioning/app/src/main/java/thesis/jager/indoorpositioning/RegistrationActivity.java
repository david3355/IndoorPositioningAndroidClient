package thesis.jager.indoorpositioning;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import jager.indoornav.wscconvention.model.Information;
import jager.indoornav.wscconvention.model.InformationCodes;
import thesis.jager.indoorpositioning.client.Client;
import thesis.jager.indoorpositioning.client.eventhandling.ClientEventCallback;
import thesis.jager.indoorpositioning.client.eventhandling.HTTPResponseEventCallback;
import thesis.jager.indoorpositioning.client.eventhandling.WebsocketServerEventCallback;
import thesis.jager.indoorpositioning.util.ActivityUtil;

public class RegistrationActivity extends AppCompatActivity implements HTTPResponseEventCallback, ClientEventCallback, WebsocketServerEventCallback
{

       private Button btn_registration;
       private EditText edit_reg_username, edit_reg_passwd, edit_reg_email, edit_reg_fname, edit_reg_lname;
       private ProgressBar progress_reg;
       private LinearLayout panel_progress;

       private Client client;

       @Override
       protected void onCreate(Bundle savedInstanceState)
       {
              super.onCreate(savedInstanceState);
              setContentView(R.layout.activity_registration);

              btn_registration = (Button) findViewById(R.id.btn_registration);
              edit_reg_username = (EditText) findViewById(R.id.edit_reg_username);
              edit_reg_passwd = (EditText) findViewById(R.id.edit_reg_passwd);
              edit_reg_email = (EditText) findViewById(R.id.edit_reg_email);
              edit_reg_fname = (EditText) findViewById(R.id.edit_reg_fname);
              edit_reg_lname = (EditText) findViewById(R.id.edit_reg_lname);
              progress_reg = (ProgressBar) findViewById(R.id.progress_registration);
              panel_progress= (LinearLayout) findViewById(R.id.panel_progress);

              btn_registration.setOnClickListener(btnRegOnClick);
       }

       @Override
       protected void onResume()
       {
              client = ActivityUtil.createClientIfNotExists(client, this, this);
              if (client != null) client.subscribeWebsocketEventHandler(this);
              super.onResume();
       }

       @Override
       protected void onPause()
       {
              if (client != null)
              {
                     client.unsubscribeWebsocketEventHandler();
              }
              super.onPause();
       }

       private View.OnClickListener btnRegOnClick = new View.OnClickListener()
       {
              @Override
              public void onClick(View view)
              {
                     registrationProcess();
              }
       };

       private void registrationProcess()
       {
              String username = edit_reg_username.getText().toString();
              String pwd = edit_reg_passwd.getText().toString();
              String email = edit_reg_email.getText().toString();
              String fname = edit_reg_fname.getText().toString();
              String lname = edit_reg_lname.getText().toString();

              panel_progress.setVisibility(View.VISIBLE);
              btn_registration.setEnabled(false);

              client.registration(username, pwd, email, fname, lname, this);
       }


       @Override
       public void onError(String message)
       {

       }

       @Override
       public void serverMessage(String message)
       {

       }

       @Override
       public void onEvent(String message)
       {

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
                            if (result.getCode() == InformationCodes.REG_OK.getInfocode())
                            {
                                   ActivityUtil.clearFields(edit_reg_username, edit_reg_passwd, edit_reg_email, edit_reg_fname, edit_reg_fname);
                            }
                            ActivityUtil.popup(result.getInfo(),RegistrationActivity.this);
                            btn_registration.setEnabled(true);
                     }
              });
       }
}
