/*
 Purges expired xqueue sessions
 Vars consumed for this job:
    * NOTIFY_ON_FAILURE: alert@example.com
    * FOLDER_NAME: folder
*/

package devops.jobs
import static org.edx.jenkins.dsl.Constants.common_logrotator
import static org.edx.jenkins.dsl.Constants.common_wrappers

class DeleteXqueueSessions{
    public static def job = { dslFactory, extraVars ->
    extraVars.get('DEPLOYMENTS').each { deployment, configuration ->
            configuration.environments.each { environment ->
                dslFactory.job(extraVars.get("FOLDER_NAME","Monitoring") + "/delete-xqueue-sessions-${environment}-${deployment}") {

                    wrappers common_wrappers
                    logRotator common_logrotator

                    triggers{
                        cron("H 0 * * *")
                    }

                    steps {
                        shell('django-admin clearsessions')
                    }

                    if (extraVars.get('NOTIFY_ON_FAILURE')){
                        publishers {
                            mailer(extraVars.get('NOTIFY_ON_FAILURE'), false, false)
                        }
                    }

                }
            }
    }
   }
}
