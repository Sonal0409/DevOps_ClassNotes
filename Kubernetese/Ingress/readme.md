# Ingress

> An API object that manages external access to the services in a cluster, typically HTTP, Ingress can also provide load balancing, SSL termination and name-based virtual hosting

> Ingress exposes HTTP and HTTPS routes from outside the cluster to services within the cluster. Traffic routing is controlled by rules defined on the Ingress resource


## Deploy myshop application which has different micro services on k8s cluster

`

## Deploy Ingress Controller 


## Deploy Ingress Rule



## Get Ingress Controller Service NodePort 

``` 
get Ingress Controller Pod NodeIP & Ingress Controller Service NodePort

    kubectl get pods -o wide  ## Note the Node IP in which the Ingress Controller Pod is Running
    kubectl get svc ## Note the Ingress Servcie NodePort
```

## validate 

``` 
   From the above details noted in your browser hit as below
   
   NodeIp:NodePort/fashion
   NodeIp:NodePort/electronics
   NodeIp:NodePort/travel
   
   Example: 192.168.198.129:30528/fashion, 192.168.198.129:30528/electronics, 192.168.198.129:30528/travel
   
```
