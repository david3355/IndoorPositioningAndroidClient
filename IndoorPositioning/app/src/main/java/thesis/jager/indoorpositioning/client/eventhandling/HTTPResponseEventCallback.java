package thesis.jager.indoorpositioning.client.eventhandling;

import jager.indoornav.wscconvention.model.Information;

/**
 * Created by Jager on 2016.04.27..
 */
public interface HTTPResponseEventCallback
{
       void onHttpResponse(final Information result);
}
