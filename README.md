# how to run this project

1. create an application-secrets.yml file
the yml config file should contain

aws:
    s3:
        access-key:
        secret-key:
    profile:
        bucket:
    region:
        static: ap-southeast-2

spring:
    security:
        oauth2:
            client:
                registration:
                    google:
                        client-secret:
                        client-id:
                        scope: profile,email

jjwt:
    key:SHA HMAC 256++ key



 # f-lab-springboot-jpa-project-template

refer
https://diligent-spy-288.notion.site/ENTITIES-5a67e895ecb745c99fd473035fe4a790


week 1 impl
![img.png](img.png)