package com.eviware.soapui.impl.actions;

import com.eviware.soapui.config.RestParametersConfig;
import com.eviware.soapui.impl.rest.RestMethod;
import com.eviware.soapui.impl.rest.RestRequest;
import com.eviware.soapui.impl.rest.RestRequestInterface;
import com.eviware.soapui.impl.rest.RestResource;
import com.eviware.soapui.impl.rest.RestService;
import com.eviware.soapui.impl.rest.RestServiceFactory;
import com.eviware.soapui.impl.rest.RestURIParser;
import com.eviware.soapui.impl.rest.support.RestParamProperty;
import com.eviware.soapui.impl.rest.support.RestParamsPropertyHolder;
import com.eviware.soapui.impl.rest.support.RestURIParserImpl;
import com.eviware.soapui.impl.rest.support.RestUtils;
import com.eviware.soapui.impl.rest.support.XmlBeansRestParamsTestPropertyHolder;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.support.StringUtils;
import com.eviware.soapui.support.UISupport;

import java.net.MalformedURLException;

/**
 * Created with IntelliJ IDEA.
 * User: manne
 * Date: 11/19/13
 * Time: 11:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class RestServiceBuilder
{

	public void createRestService( WsdlProject project, String URI ) throws MalformedURLException
	{
		if( StringUtils.isNullOrEmpty( URI ) )
		{
			return;
		}

		RestURIParser restURIParser = new RestURIParserImpl( URI );
		RestParamsPropertyHolder params = new XmlBeansRestParamsTestPropertyHolder( null,
				RestParametersConfig.Factory.newInstance() );

		String resourcePath = restURIParser.getResourcePath();
		String resourceName = restURIParser.getResourceName();
		String host = restURIParser.getEndpoint();

		RestService restService = ( RestService )project.addNewInterface( host, RestServiceFactory.REST_TYPE );
		restService.addEndpoint( restURIParser.getEndpoint() );
		RestResource restResource = restService.addNewResource( resourceName, resourcePath );

		RestMethod restMethod = addNewMethod( restResource, resourceName );

		extractAndFillParameters( URI, params );
		copyParameters( params, restResource.getParams() );

		RestRequest restRequest = addNewRequest( restMethod );
		UISupport.select( restRequest );
		UISupport.showDesktopPanel( restRequest );

	}

	protected void extractAndFillParameters( String URI, RestParamsPropertyHolder params )
	{
		// This does lot of magic including extracting and filling up parameters on the params
		RestUtils.extractParams( URI, params, false, false );
	}

	//TODO: In advanced version we have to apply filtering like which type of parameter goes to which location
	protected void copyParameters( RestParamsPropertyHolder srcParams, RestParamsPropertyHolder destinationParams )
	{
		for( int i = 0; i < srcParams.size(); i++ )
		{
			RestParamProperty prop = srcParams.getPropertyAt( i );

			destinationParams.addParameter( prop );

		}
	}

	protected RestMethod addNewMethod( RestResource restResource, String methodName )
	{
		RestMethod restMethod = restResource.addNewMethod( methodName );
		restMethod.setMethod( RestRequestInterface.RequestMethod.GET );
		return restMethod;
	}

	protected RestRequest addNewRequest( RestMethod restMethod )
	{
		return restMethod.addNewRequest( "Request " + ( restMethod.getRequestCount() + 1 ) );
	}
}
