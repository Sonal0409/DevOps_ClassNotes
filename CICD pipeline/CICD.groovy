jenkins.model.Jenkins.theInstance.getProjects().each { job ->
    if (!job.name.contains('bootstrap') && !job.name.contains('Jenkins')) {
        job.delete()
    }
}

job('job-dsl-checkout') {
    
    scm {
        github('Sonal0409/DevOpsClassCodes', 'master')
    }
      
   publishers {
        downstream 'job-dsl-compile', 'SUCCESS'
    }
    
}

mavenJob('job-dsl-compile'){
   
  customWorkspace('/var/lib/jenkins/workspace/job-dsl-checkout')
  mavenInstallation('Maven 3.3.9')
  goals('compile')
    
  publishers {
        downstream 'job-dsl-package', 'SUCCESS'
   }
}

mavenJob('job-dsl-package'){
    customWorkspace('/var/lib/jenkins/workspace/job-dsl-checkout')
    mavenInstallation('Maven 3.3.9')
    goals('package')
    
  publishers {
        downstream 'job-dsl-deploy', 'SUCCESS'
  }
}

job('job-dsl-deploy') {
    description 'Deploy app to the demo server'
    
    steps{
             shell 'sshpass -p "123456" scp /var/lib/jenkins/workspace/job-dsl-checkout/target/addressbook.war release@10.12.108.11:/opt/tomcat/webapps/'
      }
}

listView('List View DSLs') {
    jobs {
        regex('job-dsl-.+')
    }
    columns {
        status()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
}

deliveryPipelineView('job-dsl delivery pipeline') {
    showAggregatedPipeline true
    enableManualTriggers true
    pipelineInstances 5
    pipelines {
        component('job-dsl delivery pipeline', 'job-dsl-checkout')
    }
}
