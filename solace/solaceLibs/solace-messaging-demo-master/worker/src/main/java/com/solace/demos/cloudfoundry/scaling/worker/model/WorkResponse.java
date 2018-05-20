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

package com.solace.demos.cloudfoundry.scaling.worker.model;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkResponse {

	@NotEmpty
	@JsonSerialize
	@JsonProperty("id")
	private long id;
	
	@NotEmpty
	@JsonSerialize
	@JsonProperty("workDelay")
	private int workDelay;
	
	@NotEmpty
	@JsonSerialize
	@JsonProperty("sendTime")
	private long sendTime;
	
	@NotEmpty
	@JsonSerialize
	@JsonProperty("clientName")
	private String clientName;
	
	@NotEmpty
	@JsonSerialize
	@JsonProperty("jobId")
	private String jobId;
	
	public WorkResponse() {
	}

	public WorkResponse(long id, int workDelay, long sendTime, String clientName, String jobId) {
		this.id = id;
		this.workDelay = workDelay;
		this.sendTime = sendTime;
		this.clientName = clientName;
		this.jobId = jobId;
	}

	public long getId() {
		return id;
	}

	public int getWorkDelay() {
		return workDelay;
	}

	public long getSendTime() {
		return sendTime;
	}
	
	public String getClientName() {
		return clientName;
	}
	
	public String getJobId() {
		return jobId;
	}
}
