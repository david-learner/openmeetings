/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
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
package org.apache.openmeetings.webservice;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("netTestWebService")
@Path("/networktest")
public class NetTestWebService {
	private static final Logger log = LoggerFactory.getLogger(UserWebService.class);
	enum TestType {
		UNKNOWN,
		PING,
		JITTER,
		DOWNLOAD_SPEED,
		UPLOAD_SPEED
	}

	private static final int PING_PACKET_SIZE = 64;
	private static final int JITTER_PACKET_SIZE = 1024;
	private static final int DOWNLOAD_PACKET_SIZE = 1024*1024;

	private final byte[] pingData;
	private final byte[] jitterData;
	private final byte[] downloadData;

	public NetTestWebService() {
		pingData = new byte[PING_PACKET_SIZE];
		jitterData = new byte[JITTER_PACKET_SIZE];
		downloadData = new byte[DOWNLOAD_PACKET_SIZE];

		Arrays.fill(pingData, (byte) '0');
		Arrays.fill(jitterData, (byte) '0');
		Arrays.fill(downloadData, (byte) '0');
	}

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/")
	public Response get(@QueryParam("module") String module, @QueryParam("size") long size) {
		TestType testType = getTypeByString(module);
		log.debug("Network test:: get");

		// choose data to send
		byte[] data = new byte[0];
		switch (testType) {
			case PING:
				data = pingData;
				break;
			case JITTER:
				data = jitterData;
				break;
			case DOWNLOAD_SPEED:
				data = downloadData;
				break;
			case UPLOAD_SPEED:
				break;
			default:
				break;
		}

		ResponseBuilder response = Response.ok().type(MediaType.APPLICATION_OCTET_STREAM).entity(new ByteArrayInputStream(data));
		response.header("Cache-Control", "no-cache, no-store, no-transform");
		response.header("Pragma", "no-cache");
		response.header("Content-Length", String.valueOf(data.length));
		return response.build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/")
	public void upload(
			@QueryParam("module") String module
			, @QueryParam("size") long size
			, InputStream stream) throws IOException
	{
		byte[] b = new byte[1024];
		while (stream.read(b) >= 0 ) {
			//no-op
		};
	}

	private static TestType getTypeByString(String typeString) {
		if ("ping".equals(typeString)) {
			return TestType.PING;
		} else if ("latency".equals(typeString)) {
			return TestType.JITTER;
		} else if ("download".equals(typeString)) {
			return TestType.DOWNLOAD_SPEED;
		} else if ("upload".equals(typeString)) {
			return TestType.UPLOAD_SPEED;
		}

		return TestType.UNKNOWN;
	}
}
