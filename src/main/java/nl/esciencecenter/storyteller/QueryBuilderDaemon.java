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

import static spark.Spark.get;
import static spark.Spark.post;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.XenonFactory;
import nl.esciencecenter.xenon.files.Files;
import nl.esciencecenter.xenon.jobs.Job;
import nl.esciencecenter.xenon.jobs.JobDescription;
import nl.esciencecenter.xenon.jobs.JobStatus;
import nl.esciencecenter.xenon.jobs.Jobs;
import nl.esciencecenter.xenon.jobs.Scheduler;

/**
 * 
 */
public class QueryBuilderDaemon {

    // Xenon instances to use in this daemon
    private Xenon xenon;
    private Jobs jobs;
    private Files files;
    private Scheduler scheduler;
    private List<GenericJob> running;
    
    private AtomicInteger finishedQueries = new AtomicInteger(0);
    private AtomicInteger failedQueries = new AtomicInteger(0);

    private String scriptsBasePath = "/src/query-builder-daemon/scripts/";
    
    public QueryBuilderDaemon() throws XenonException, URISyntaxException { 
        
        // We create a new Xenon using the XenonFactory (providing some extra properties).
        Map <String, String> prop = new HashMap<>();
        prop.put("xenon.adaptors.local.queue.pollingDelay", "250");
        prop.put("xenon.adaptors.local.queue.multi.maxConcurrentJobs", "4");
        
        xenon = XenonFactory.newXenon(prop);

        // Next, we retrieve the Files and Jobs API
        files = xenon.files();
        jobs = xenon.jobs();
        
        running = Collections.synchronizedList(new LinkedList<>());
        
        scheduler = jobs.newScheduler("local", null, null, null);
        
        get("/status", (req, res) -> {
          return getStatus();  
        }); 
        
        post("/submit", (req, res) -> {
            
            String id = req.queryParams("id");
            String query = req.queryParams("query");

            if (id != null && query != null) { 
                return createAndSubmitJob(new QueryJob(id, query));
            } else { 
               return "INVALID";
            }
        });
        
        post("/clearall", (req, res) -> {            
            return createAndSubmitJob(new ClearAllJob());            
        });
        
        post("/clearuser", (req, res) -> {
            String username = req.queryParams("username");

            if (username != null) { 
                return createAndSubmitJob(new ClearUserJob(username));
            } else { 
               return "INVALID";
            }           
        });
        
        post("/rebuild", (req, res) -> {
            return createAndSubmitJob(new RebuildDatabaseJob());       
        });
    }

    private String getStatus() { 
        return "QueryBuilderDaemon:\nQueries in queue " + running.size() + "\nQueries processed " + finishedQueries.get() + 
                "\nQueries failed " + failedQueries.get();  
    }
    
    private JobDescription createQueryJobDescription(QueryJob job) { 
        System.out.println("Submitting query " + job.getID() + "\"" + job.getQuery() + "\"");

        // We can now create a JobDescription for the job we want to run.
        JobDescription description = new JobDescription();
        description.setQueueName("multi");
        description.setExecutable("/bin/bash");
        description.setArguments(scriptsBasePath+"storyteller.sh", job.getID(), job.getQuery());
        description.setStdout(job.getID() + "-stdout.txt");
        description.setStderr(job.getID() + "-stderr.txt");
       
        return description;
    }
    
    private JobDescription createClearAllJobDescription(ClearAllJob job) { 
        System.out.println("Clearing all jobs");

        // We can now create a JobDescription for the job we want to run.
        JobDescription description = new JobDescription();
        description.setQueueName("multi");
        description.setExecutable("/bin/bash");
        description.setArguments(scriptsBasePath+"clear_all_queries.sh");
        description.setStdout("clear_all_queries-stdout.txt");
        description.setStderr("clear_all_queries-stderr.txt");

        return description;
    }
    
    private JobDescription createClearUserJobDescription(ClearUserJob job) { 
        System.out.println("Clearing all jobs of user "+job.getUsername());

        // We can now create a JobDescription for the job we want to run.
        JobDescription description = new JobDescription();
        description.setQueueName("multi");
        description.setExecutable("/bin/bash");
        description.setArguments(scriptsBasePath+"clear_user_queries.sh", job.getUsername());
        description.setStdout("clear_user_queries-stdout.txt");
        description.setStderr("clear_user_queries-stderr.txt");
       
        return description;
    }
    
    private JobDescription createRebuildDatabaseJobDescription(RebuildDatabaseJob job) { 
        System.out.println("Rebuilding database");

        // We can now create a JobDescription for the job we want to run.
        JobDescription description = new JobDescription();
        description.setQueueName("multi");
        description.setExecutable("/bin/bash");
        description.setArguments(scriptsBasePath+"rebuild_database.sh");
        description.setStdout("rebuild_database-stdout.txt");
        description.setStderr("rebuild_database-stderr.txt");
               
        return description;
    }
    
    private String createAndSubmitJob(GenericJob newJob) {         
        JobDescription description = null;
        if (newJob instanceof QueryJob) {            
            description = createQueryJobDescription((QueryJob)newJob);
        } else if (newJob instanceof ClearAllJob) {            
            description = createClearAllJobDescription((ClearAllJob)newJob);
        } else if (newJob instanceof ClearUserJob) {            
            description = createClearUserJobDescription((ClearUserJob)newJob);
        } else if (newJob instanceof RebuildDatabaseJob) {            
            description = createRebuildDatabaseJobDescription((RebuildDatabaseJob)newJob);
        } else { 
            failedQuery(newJob, new Exception("Unknown Job type"));
            return "FAILED"; 
        }
        
        try {
            Job job = null;
            
            synchronized (this) {
                job = jobs.submitJob(scheduler, description);
            }
            
            newJob.setJob(job);
            running.add(newJob);
            return "OK";
        } catch (Exception e) {
            failedQuery(newJob, e);
            return "FAILED";
        }
    }

    private void queryDone(QueryJob query) {
        System.out.println("Finished job " + query.getID());
        running.remove(query);
        finishedQueries.incrementAndGet();
    }

    private void failedQuery(GenericJob query, Exception e) { 

        // System.out.println("Failed job " + query.getID() + " " + e);
        
        if (e != null) { 
            e.printStackTrace();
        }
        
        running.remove(query);
        failedQueries.incrementAndGet();
    }

    private void pollJobs() { 

        if (running.size() == 0) { 
            return;
        }

        QueryJob [] tmp;
        
        synchronized (running) {
            tmp = running.toArray(new QueryJob[running.size()]); 
        }
        
        for (int i=0;i<tmp.length;i++) {
            
            QueryJob q = tmp[i]; 
            
            if (q != null) { 
                
                try {
                
                    JobStatus status = jobs.getJobStatus(q.getJob());

                    if (status.isDone()) {
                        if (status.hasException()) {
                            failedQuery(q, status.getException());
                        } else {
                            queryDone(q);
                        }
                    }
                } catch (Exception e) {
                    failedQuery(q, e);
                }
            }
        } 
    }

    private void done() throws XenonException {
        
        // Close the scheduler
        jobs.close(scheduler);
        
        // Finally, we end Xenon to release all resources 
        XenonFactory.endXenon(xenon);
    }

    private boolean getDone() { 
        return false;
    }
    
    public void run() { 
        
        while (!getDone()) { 
            try { 
                Thread.sleep(1000);
            } catch (Exception e) {
                // ignored
            }
            
            pollJobs();
        }
    }
    
    public static void main(String [] args) { 

        try { 
            System.out.println("Starting daemon");
            
            QueryBuilderDaemon daemon = new QueryBuilderDaemon();
            daemon.run();
            
            System.out.println("Shutting down daemon");
            daemon.done();
        } catch (Exception e)  {
            System.out.println("QueryBuilderDaemon failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
