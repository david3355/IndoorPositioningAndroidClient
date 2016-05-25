package thesis.jager.indoorpositioning;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jager.indoornav.wscconvention.model.Information;
import jager.indoornav.wscconvention.model.InformationCodes;
import thesis.jager.indoorpositioning.bluetooth.BluetoothHelper;
import thesis.jager.indoorpositioning.client.Client;
import thesis.jager.indoorpositioning.client.ServerData;
import thesis.jager.indoorpositioning.client.config.ConfigManager;
import thesis.jager.indoorpositioning.client.eventhandling.ClientEventCallback;
import thesis.jager.indoorpositioning.client.eventhandling.HTTPResponseEventCallback;
import thesis.jager.indoorpositioning.client.eventhandling.WebsocketServerEventCallback;
import thesis.jager.indoorpositioning.util.ActivityUtil;
import thesis.jager.indoorpositioning.view.main_activity.ImageAdapter;
import thesis.jager.indoorpositioning.view.main_activity.MenuIcon;
import thesis.jager.indoorpositioning.view.main_activity.MenuIconID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, HTTPResponseEventCallback, ClientEventCallback, WebsocketServerEventCallback
{
       private GridView menu;
       private BluetoothHelper btHelper;
       private LinearLayout panel_user;
       private TextView txt_user_loggedin;
       private Button btn_logout;

       private Client client;

       private final int START_LOGIN = 1;

       @Override
       protected void onCreate(Bundle savedInstanceState)
       {
              super.onCreate(savedInstanceState);
              setContentView(R.layout.activity_main);
              menu = (GridView) findViewById(R.id.grid_menu);
              panel_user = (LinearLayout) findViewById(R.id.panel_user);
              txt_user_loggedin = (TextView) findViewById(R.id.txt_user_loggedin);
              btn_logout = (Button) findViewById(R.id.btn_logout);
              btn_logout.setOnClickListener(this);

              ServerData.HOST = ConfigManager.getInstance(this).getHostAddress();

              btHelper = BluetoothHelper.getInstance(this);
              List<MenuIcon> menuicons = new ArrayList<MenuIcon>();
              createMenuItems(menuicons);
              ImageAdapter adapter = new ImageAdapter(this, menuicons);
              menu.setAdapter(adapter);
              menu.setOnItemClickListener(onMenuClick);
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

       private AdapterView.OnItemClickListener onMenuClick = new AdapterView.OnItemClickListener()
       {
              @Override
              public void onItemClick(AdapterView<?> parent, View view, int position, long id)
              {
                     MenuIcon item = (MenuIcon) parent.getAdapter().getItem(position);
                     switch (item.getId())
                     {
                            case BLUETOOTH:
                                   bluetoothClick();
                                   break;
                            case LOGIN:
                                   startActivityForResult(LoginActivity.class);
                                   break;
                            case REGISTRATION:
                                   startActivity(RegistrationActivity.class);
                                   break;
                            case MAP:
                                   startActivity(MapActivity.class);
                                   break;
                            case POSITIONING:
                                   startActivity(PositioningActivity.class);
                                   break;
                     }
              }
       };

       @Override
       protected void onActivityResult(int requestCode, int resultCode, Intent data)
       {
              if (requestCode == START_LOGIN)
              {
                     if (resultCode == RESULT_OK)
                     {
                            String username = data.getStringExtra(LoginActivity.KEY_USERNAME);
                            userLoggedIn(username);
                     }
              }
       }

       private void userLoggedIn(String username)
       {
              panel_user.setVisibility(View.VISIBLE);
              txt_user_loggedin.setText(username);
       }

       private void userLoggedOut()
       {
              panel_user.setVisibility(View.GONE);
              txt_user_loggedin.setText("");
       }

       private void createMenuItems(List<MenuIcon> items)
       {
              int bticon;
              if (btHelper.deviceSupportsBluetoothLE()) bticon = R.drawable.bluetooth;
              else bticon = R.drawable.nobluetooth;
              MenuIcon login = new MenuIcon(ActivityUtil.getStringRes(this, R.string.login), R.drawable.user, MenuIconID.LOGIN);
              MenuIcon reg = new MenuIcon(ActivityUtil.getStringRes(this, R.string.registration), R.drawable.register, MenuIconID.REGISTRATION);
              MenuIcon map = new MenuIcon(ActivityUtil.getStringRes(this, R.string.map), R.drawable.map, MenuIconID.MAP);
              MenuIcon pos = new MenuIcon(ActivityUtil.getStringRes(this, R.string.positioning), R.drawable.positioning, MenuIconID.POSITIONING);
              MenuIcon bt = new MenuIcon(ActivityUtil.getStringRes(this, R.string.bluetooth), bticon, MenuIconID.BLUETOOTH);
              addMenuItems(items, login, reg, map, pos, bt);
       }

       private void addMenuItems(List<MenuIcon> iconlist, MenuIcon... items)
       {
              for (MenuIcon item : items)
              {
                     iconlist.add(item);
              }
       }

       private void startActivity(Class activityClass)
       {
              Intent i = new Intent(this, activityClass);
              startActivity(i);
       }

       private void startActivityForResult(Class activityClass)
       {
              Intent i = new Intent(this, activityClass);
              startActivityForResult(i, START_LOGIN);
       }

       private void bluetoothClick()
       {
              if (!btHelper.deviceSupportsBluetoothLE())
                     ActivityUtil.popup("Your device does not support Bluetooth Low Energy", this);
              else
              {
                     btHelper.enableBluetooth();
              }
       }

       @Override
       public void onClick(View view)
       {
              switch (view.getId())
              {
                     case R.id.btn_logout:
                            client.logout(this);
                            break;
              }
       }

       @Override
       public void onHttpResponse(final Information result)
       {
              String message = null;
              if (result.getCode() == InformationCodes.LOGOUT_OK.getInfocode())
              {
                     runOnUiThread(new Runnable()
                     {
                            @Override
                            public void run()
                            {
                                   userLoggedOut();
                                   client.setUserName(null);
                                   ActivityUtil.popup(result.getInfo(), MainActivity.this);
                            }
                     });
              }
       }

       @Override
       public void onError(String message)
       {
              ActivityUtil.popup("Error: " + message, this);
       }

       @Override
       public void serverMessage(String message)
       {

       }

       @Override
       public void onEvent(String message)
       {
              ActivityUtil.popup("Event: " + message, this);
       }
}
