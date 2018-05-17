/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.solacesystems.jms;

import java.util.List;

import com.solace.services.core.model.SolaceServiceCredentials;
import com.solace.spring.cloud.core.SolaceMessagingInfo;

/**
 * A Factory for {@link SolConnectionFactory} to Support Cloud Environments having
 * multiple solace-messaging services.
 */
public interface SpringSolJmsConnectionFactoryCloudFactory {
	/**
	 * Gets the first detected {@link SolaceServiceCredentials}.
	 *
	 * @return A Solace Messaging service
	 */
	SolaceServiceCredentials findFirstSolaceServiceCredentials();

	/**
	 * Lists All Cloud Environment detected Solace Messaging services.
	 *
	 * @return List of all Cloud Environment detected Solace Messaging services
	 */
	List<SolaceServiceCredentials> getSolaceServiceCredentials();

	/**
	 * Returns a {@link SolConnectionFactory} based on the first detected {@link SolaceServiceCredentials}.
	 *
	 * @return {@link SolConnectionFactory} based on the first detected {@link SolaceServiceCredentials}
	 */
	SolConnectionFactory getSolConnectionFactory();

	/**
	 * Returns a {@link SolConnectionFactory} based on the {@link SolaceServiceCredentials}
	 * identified by the given ID.
	 *
	 * @param id The Solace Messaging service's ID
	 * @return {@link SolConnectionFactory} with the given Solace Messaging service ID,
	 * otherwise null if the service cannot be found
	 */
	SolConnectionFactory getSolConnectionFactory(String id);

	/**
	 * Returns a {@link SolConnectionFactory} based on the given {@link SolaceServiceCredentials}.
	 *
	 * @param solaceServiceCredentials The credentials to an existing Solace Messaging service
	 * @return {@link SolConnectionFactory} based on the given {@link SolaceServiceCredentials},
	 * otherwise an application.properties based {@link SolConnectionFactory}
	 */
	SolConnectionFactory getSolConnectionFactory(SolaceServiceCredentials solaceServiceCredentials);

	/**
	 * Lists All Cloud Environment detected Solace Messaging services.
	 *
	 * @deprecated As of 1.1.0, usage of {@link SolaceMessagingInfo}
	 * was replaced by its interface, {@link SolaceServiceCredentials}.
	 * Use {@link #getSolaceServiceCredentials()} instead.
	 *
	 * @return If in a Cloud Foundry environment, list of all Cloud Environment detected Solace Messaging services,
	 * otherwise null
	 */
	@Deprecated
	List<SolaceMessagingInfo> getSolaceMessagingInfos();
}
