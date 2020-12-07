/*
 * Copyright [2020] [MaxKey of copyright http://www.maxkey.top]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 


package org.maxkey.authz.saml20.binding.impl;

import java.security.KeyStore;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.maxkey.authz.saml.common.TrustResolver;
import org.maxkey.authz.saml20.binding.ExtractBindingAdapter;
import org.maxkey.crypto.keystore.KeyStoreLoader;
import org.maxkey.domain.apps.AppsSAML20Details;
import org.opensaml.saml.common.binding.decoding.SAMLMessageDecoder;
import org.opensaml.security.credential.CredentialResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class ExtractPostBindingAdapter implements ExtractBindingAdapter, InitializingBean{
	private final static Logger logger = LoggerFactory.getLogger(ExtractPostBindingAdapter.class);
	
	static final String SAML_REQUEST_POST_PARAM_NAME = "SAMLRequest";
	static final String SAML_RESPONSE_POST_PARAM_NAME = "SAMLResponse";

	protected	SAMLMessageDecoder decoder;
	protected  String issuingEntityName;
	protected  SecurityPolicyResolver securityPolicyResolver;
	
	protected IssueInstantRule issueInstantRule;
	protected MessageReplayRule messageReplayRule;
	
	protected KeyStoreLoader keyStoreLoader;
	protected CredentialResolver credentialResolver;
	
	protected AppsSAML20Details  saml20Detail;
	
	
	public ExtractPostBindingAdapter() {
		
	}



	public ExtractPostBindingAdapter(SAMLMessageDecoder decoder) {
		super();
		this.decoder = decoder;
	}
	
	public ExtractPostBindingAdapter(SAMLMessageDecoder decoder,String issuingEntityName) {
		super();
		this.decoder = decoder;
		this.issuingEntityName = issuingEntityName;
	}
	
	public ExtractPostBindingAdapter(SAMLMessageDecoder decoder,String issuingEntityName, SecurityPolicyResolver securityPolicyResolver) {
		super();
		this.decoder = decoder;
		this.issuingEntityName = issuingEntityName;
		
		this.securityPolicyResolver = securityPolicyResolver;
	}

	

	
	@Override
	@SuppressWarnings("rawtypes")
	public SAMLMessageContext extractSAMLMessageContext(HttpServletRequest request) throws MessageDecodingException, SecurityException {
		
		BasicSAMLMessageContext messageContext = new BasicSAMLMessageContext();
		
		messageContext.setInboundMessageTransport(new HttpServletRequestAdapter(request));
		
		messageContext.setSecurityPolicyResolver(securityPolicyResolver);

		decoder.decode(messageContext);
		logger.debug("decode successed ");
		return	messageContext;

	}


	@Override
	public String extractSAMLMessage(HttpServletRequest request) {
	    
		if(StringUtils.isNotBlank(request.getParameter(SAML_REQUEST_POST_PARAM_NAME)))
			return request.getParameter(SAML_REQUEST_POST_PARAM_NAME);
		else
			return request.getParameter(SAML_RESPONSE_POST_PARAM_NAME);
		
	}
	

	public void buildSecurityPolicyResolver(KeyStore trustKeyStore) {

		TrustResolver trustResolver = new TrustResolver(trustKeyStore,
					keyStoreLoader.getEntityName(),
					keyStoreLoader.getKeystorePassword(), 
					issueInstantRule,
					messageReplayRule,"POST");
		credentialResolver = (CredentialResolver)trustResolver.getKeyStoreCredentialResolver();
		this.securityPolicyResolver = trustResolver.getStaticSecurityPolicyResolver();
	}
	
	
	/**
	 * @param securityPolicyResolver the securityPolicyResolver to set
	 */
	public void setSecurityPolicyResolver(
			SecurityPolicyResolver securityPolicyResolver) {
		this.securityPolicyResolver = securityPolicyResolver;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSaml20Detail(AppsSAML20Details saml20Detail) {
		this.saml20Detail=saml20Detail;
	}

	public AppsSAML20Details getSaml20Detail() {
		return saml20Detail;
	}

	public KeyStoreLoader getKeyStoreLoader() {
		return keyStoreLoader;
	}



	public void setKeyStoreLoader(KeyStoreLoader keyStoreLoader) {
		this.keyStoreLoader = keyStoreLoader;
	}



	@Override
	public CredentialResolver getCredentialResolver() {
		return this.credentialResolver;
	}



	public void setIssuingEntityName(String issuingEntityName) {
		this.issuingEntityName = issuingEntityName;
	}



	public void setIssueInstantRule(IssueInstantRule issueInstantRule) {
		this.issueInstantRule = issueInstantRule;
	}



	public void setMessageReplayRule(MessageReplayRule messageReplayRule) {
		this.messageReplayRule = messageReplayRule;
	}
	
	
}
