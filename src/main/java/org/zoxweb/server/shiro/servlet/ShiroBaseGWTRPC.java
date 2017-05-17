/*
 * Copyright (c) 2012-2017 ZoxWeb.com LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.zoxweb.server.shiro.servlet;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.zoxweb.shared.security.AccessException;
import org.zoxweb.shared.security.SessionInfoDAO;
import org.zoxweb.shared.util.Const;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.RpcTokenException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public abstract class ShiroBaseGWTRPC
	extends RemoteServiceServlet
{
	
	private static final transient Logger log = Logger.getLogger(Const.LOGGER_NAME);

	private final Object localDelegate;

	public ShiroBaseGWTRPC()
    {
		super();
		localDelegate = this;
	}
	
	public ShiroBaseGWTRPC( Object delegate)
    {
		super( delegate);
		localDelegate = delegate;
	}

	protected abstract boolean isSecurityCheckRequired(Method method, Object[] parameters);

	protected abstract boolean isSecureCommnunicationRequired(Method method, Object[] parameters);
	
	public static String toString(Method method,  HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		sb.append("URL:" + req.getRequestURL() + "\n");
		sb.append("URI:" + req.getRequestURI() + "\n");
		sb.append("LocalName:" + req.getLocalName() + "\n");
		sb.append("LocalAddr:" + req.getLocalAddr() + "\n");
		sb.append("LocalPort:" + req.getLocalPort() + "\n");
		sb.append("Method:" + method + "\n");
		return sb.toString();
	}
	
	protected void checkSecurity(Object delgate, Method method, Object[] parameters)
		throws AccessException {
		//log.info( toString(method, getServletRequest()));
		
		//log.info( this.getClass().getName() + "#" + method.getName());
		// check if secure socket is required
		if (isSecureCommnunicationRequired( method, parameters)) {
			HttpServletRequest req = getThreadLocalRequest();
			if (!req.isSecure()) {
				throw new AccessException("Connection not secure", "");
			}
		}
		
		// check sercurity permission
		if (isSecurityCheckRequired(  method, parameters)) {
			Subject subject = SecurityUtils.getSubject();
			if (!subject.isAuthenticated()) {
				// the current user is not authenticated
				String url = (String) subject.getSession().getAttribute( SessionInfoDAO.LOGOUT_URL);
				log.info("Subject NOT AUTHENICATED redirect:" + url);
				throw new AccessException(subject + " not authenticated", url, true);
			}

			// check the resource access
		}
	}

	@Override
	public String processCall(String payload) throws SerializationException {
	    // First, check for possible XSRF situation
	    checkPermutationStrongName();
	   
	    try
        {
            RPCRequest rpcRequest = RPC.decodeRequest(payload, localDelegate.getClass(), this);
            onAfterRequestDeserialized(rpcRequest);
	      
	      
            // invoke the security check here at this level

            try
            {
                checkSecurity( localDelegate, rpcRequest.getMethod(), rpcRequest.getParameters());
            }
                catch( AccessException e)
            {
                return RPC.encodeResponseForFailure( rpcRequest.getMethod(),
                                               e,
                                               rpcRequest.getSerializationPolicy(),
                                               rpcRequest.getFlags());
            }

            return RPC.invokeAndEncodeResponse(localDelegate,
	    			  							  rpcRequest.getMethod(),
	    			  							  rpcRequest.getParameters(),
	    			  							  rpcRequest.getSerializationPolicy(),
	    			  							  rpcRequest.getFlags());

        }
        catch (IncompatibleRemoteServiceException ex)
        {
            log(
	          "An IncompatibleRemoteServiceException was thrown while processing this call.",
	          ex);

            return RPC.encodeResponseForFailure(null, ex);
	    }
	    catch (RpcTokenException tokenException)
        {
            log("An RpcTokenException was thrown while processing this call.",
	          tokenException);

            return RPC.encodeResponseForFailure(null, tokenException);
	    }
    }

	public HttpServletRequest getServletRequest()
    {
		return getThreadLocalRequest();
	}
	
	public HttpServletResponse getServletResponse()
    {
		return getThreadLocalResponse();
	}

}