/*
 * Copyright 2015.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tascape.qa.thr.rest;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

/**
 * REST Web Service
 *
 * @author linsong wang
 */
@Path("sr")
@RequestScoped
public class SuiteResult {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of SuiteResult
     */
    public SuiteResult() {
    }

    /**
     * Retrieves representation of an instance of com.tascape.qa.thr.rest.SuiteResult.
     *
     * @param srid suite result id
     *
     * @return an instance of String
     */
    @GET
    @Produces("application/json")
    public String getJson(@DefaultValue("") @QueryParam("srid") String srid) {
        return "{'srid': '" + srid + "'}";
    }

    /**
     * PUT method for updating or creating an instance of SuiteResult
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
}
