rootProject.name = "GeoGrind-Backend"
include("src")
include("src:main")
findProject("src:main")?.name = "main"
include("Chat-Microservice")
include("Chat-Microservice:chat-client-grpc")
findProject(":Chat-Microservice:chat-client-grpc")?.name = "chat-client-grpc"
include("Chat-Microservice:chat-server-grpc")
findProject(":Chat-Microservice:chat-server-grpc")?.name = "chat-server-grpc"
