package thesis.jager.indoorpositioning.client;

import java.io.*;
import java.net.*;

import jager.indoornav.wscconvention.*;
import jager.indoornav.wscconvention.model.*;
import jager.indoornav.wscconvention.rest.RestApiParameters;
import thesis.jager.indoorpositioning.client.eventhandling.HTTPResponseEventCallback;

/**
 * Created by Jager on 2016.04.27..
 */
public class RESTClient
{

       private String connect(URL conn, String method) throws ConnectException
       {
              HttpURLConnection connection = null;
              String response = null;
              try
              {
                     connection = (HttpURLConnection) conn.openConnection();
                     connection.setRequestMethod(method);
                     connection.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
                     connection.setRequestProperty("Accept","*/*");
                     InputStream in = new BufferedInputStream(connection.getInputStream());
                     response = readStream(in);
              } catch (Exception e)
              {
                     throw new ConnectException("Connection failed! Reason: " + e.getMessage());
              } finally
              {
                     connection.disconnect();
              }
              return response;
       }
       private String readStream(InputStream istream) throws IOException
       {
              BufferedReader in = new BufferedReader(new InputStreamReader(istream));
              String inputLine;
              StringBuffer response = new StringBuffer();

              while ((inputLine = in.readLine()) != null)
              {
                     response.append(inputLine);
              }
              in.close();

              // print result
              return response.toString();
       }

       public void login(final String username, final String password, final String sessionid, final HTTPResponseEventCallback httpResponseEventCallback)
       {
              Thread connThread = new Thread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            try
                            {
                                   URL url = new URL(String.format("http://%s:%s/indoorpositioning/login?%s=%s&%s=%s&%s=%s", ServerData.HOST, ServerData.PORT_REST, RestApiParameters.KEY_USERNAME, username, RestApiParameters.KEY_PASSWORD, password, RestApiParameters.KEY_CLIENTID, sessionid));
                                   String response = connect(url, "POST");
                                   Message msg = Communicator.getInstance().getMessage(response);
                                  Information info =  (Information)msg.getTaskObject();
                                   httpResponseEventCallback.onHttpResponse(info);
                            } catch (Exception e)
                            {
                                   httpResponseEventCallback.onHttpResponse(new Information(InformationCodes.CONNECTION_FAILED.getInfocode(), e.getMessage()));
                            }
                     }
              });

              connThread.start();
       }

       public void logout(final String sessionid, final HTTPResponseEventCallback httpResponseEventCallback)
       {
              Thread connThread = new Thread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            try
                            {
                                   URL url = new URL(String.format("http://%s:%s/indoorpositioning/logout?%s=%s", ServerData.HOST, ServerData.PORT_REST, RestApiParameters.KEY_CLIENTID, sessionid));
                                   String response = connect(url, "POST");
                                   Message msg = Communicator.getInstance().getMessage(response);
                                   Information info =  (Information)msg.getTaskObject();
                                   httpResponseEventCallback.onHttpResponse(info);
                            } catch (Exception e)
                            {
                                   httpResponseEventCallback.onHttpResponse(new Information(InformationCodes.CONNECTION_FAILED.getInfocode(), e.getMessage()));
                            }
                     }
              });

              connThread.start();
       }

       public void registration(final String username, final String password, final String email, final String firstname, final String lastname, final String sessionid, final HTTPResponseEventCallback httpResponseEventCallback)
       {
              Thread connThread = new Thread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            try
                            {
                                   URL url = new URL(String.format("http://%s:%s/indoorpositioning/registration?%s=%s&%s=%s&%s=%s&%s=%s&%s=%s", ServerData.HOST, ServerData.PORT_REST, RestApiParameters.KEY_USERNAME, username, RestApiParameters.KEY_PASSWORD, password, RestApiParameters.KEY_EMAIL, email, RestApiParameters.KEY_FNAME, firstname, RestApiParameters.KEY_LNAME, lastname));
                                   String response = connect(url, "POST");
                                   Message msg = Communicator.getInstance().getMessage(response);
                                   Information info =  (Information)msg.getTaskObject();
                                   httpResponseEventCallback.onHttpResponse(info);
                            } catch (Exception e)
                            {
                                   httpResponseEventCallback.onHttpResponse(new Information(InformationCodes.CONNECTION_FAILED.getInfocode(), e.getMessage()));
                            }
                     }
              });

              connThread.start();
       }

}
