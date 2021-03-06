/**
 * Copyright 2013 Netherlands eScience Center
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

package nl.esciencecenter.storyteller;

/**
 */
public class QueryJob extends GenericJob {
    
    private String id;
    private String query;
    private String mention_limit;
    
    public QueryJob(String id, String query, String mention_limit) {
        super();
        this.id = id;
        this.query = query;
        this.mention_limit = mention_limit;
    }
    
    public String getID() { 
        return id;
    }
    
    public String getQuery() { 
        return query;
    }
    
    public String getLimit() { 
        return mention_limit;
    }
}
