   
# kubectl create -f web.yml
   
You will need to use two terminal windows.

In the first terminal, use kubectl get to watch the creation of the StatefulSet's Pods.

# kubectl get pods -w -l app=nginx

In the second terminal, use kubectl create to create the headless Service and StatefulSet defined in web.yaml.

# kubectl create -f web.yaml

# kubectl get service nginx

# kubectl get statefulset web

# kubectl get pods -w -l app=nginx

 When Pods are being deployed, they are created sequentially, ordered from {0..n-1}
 Notice that the web-1 Pod is not launched until the web-0 Pod is Running and Ready

Pods in a StatefulSet
***********************
Examining the Pod's Ordinal Index

# kubectl get pods -l app=nginx


Note that, the Pods' names take the form <statefulset name>-<ordinal index>. Since the web StatefulSet has two replicas, it creates two Pods, web-0 and web-1.

Stable Network Identities 
*************************
  
Each Pod has a stable hostname based on its ordinal index.
  
for i in 0 1; do kubectl exec "web-$i" -- sh -c 'hostname'; done
  
*****************
  
Use kubectl run to execute a container that provides the nslookup command from the dnsutils package. Using nslookup on the Pods' hostnames, 
you can examine their in-cluster DNS addresses:
  
# kubectl run -i --tty --image busybox:1.28 dns-test --restart=Never --rm
  
above command starts a new shell. In that new shell, run:
  
  # Run this in the dns-test container shell
  
nslookup web-0.nginx
nslookup web-1.nginx

and now exit the container shell: exit

The CNAME of the headless service points to SRV records (one for each Pod that is Running and Ready). 
The SRV records point to A record entries that contain the Pods' IP addresses.
  
****************************
Delete the pods in statefulset
  
  # kubectl delete pod -l app=nginx
  
 Notice that StatefulSet will restart them, and both Pods will transition to Running and Ready:
  
  # kubectl get pod -w -l app=nginx
  
Use kubectl exec and kubectl run to view the Pods' hostnames and in-cluster DNS entries. First, view the Pods' hostnames:
  
 for i in 0 1; do kubectl exec web-$i -- sh -c 'hostname'; done
 
  Next run:
  
 kubectl run -i --tty --image busybox:1.28 dns-test --restart=Never --rm /bin/sh

  which starts a new shell.
In that new shell, run:
  
  nslookup web-0.nginx
  
  The output is similar to:

Server:    10.0.0.10
Address 1: 10.0.0.10 kube-dns.kube-system.svc.cluster.local

Name:      web-0.nginx
Address 1: 10.244.1.7

nslookup web-1.nginx
Server:    10.0.0.10
Address 1: 10.0.0.10 kube-dns.kube-system.svc.cluster.local

Name:      web-1.nginx
Address 1: 10.244.2.8

The Pods' ordinals, hostnames, SRV records, and A record names have not changed, but the IP addresses associated with the Pods may have changed. 
  
This is why it is important not to configure other applications to connect to Pods in a StatefulSet by IP address.
*************************************
  
  Stable storage and statefulset
*************************
  
  Get the PersistentVolumeClaims for web-0 and web-1:
  
  kubectl get pvc -l app=nginx
  
  The StatefulSet controller created two PersistentVolumeClaims that are bound to two PersistentVolumes.
  
  The NGINX webserver, by default, serves an index file from /usr/share/nginx/html/index.html. 
  The volumeMounts field in the StatefulSet's spec ensures that the /usr/share/nginx/html directory is backed by a PersistentVolume.
  
  Let's Write the Pods' hostnames to their index.html files :
  
  for i in 0 1; do kubectl exec "web-$i" -- sh -c 'echo "$(hostname)" > /usr/share/nginx/html/index.html'; done
  
  
  Lets verify that the NGINX webservers serve the hostnames:
  
  for i in 0 1; do kubectl exec -i -t "web-$i" -- curl http://localhost/; done
  
  **********************
  
  Delete the pods of the statefulset
  
  
  In one terminal, watch the StatefulSet's Pods:
  
 # kubectl get pod -w -l app=nginx
  
  In a second terminal, delete all of the StatefulSet's Pods:
  
  # kubectl delete pod -l app=nginx
  
  Examine the output of the kubectl get command in the first terminal, and wait for all of the Pods to transition to Running and Ready.
  
  
  # kubectl get pod -w -l app=nginx
  
  Verify the web servers continue to serve their hostnames:

for i in 0 1; do kubectl exec -i -t "web-$i" -- curl http://localhost/; done
  
  Even though web-0 and web-1 were rescheduled, they continue to serve their hostnames because the PersistentVolumes associated 
  with their PersistentVolumeClaims are remounted to their volumeMounts. No matter what node web-0and web-1 are scheduled on, 
  their PersistentVolumes will be mounted to the appropriate mount points.
  
  ***************************


  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  





