# Spring boot - Authentication

This repo serves as a demonstration of implementing authentication in Spring boot:
- Jwt authentication
- OAuth2 authentication (Google, Github, Linkedin, etc);

Every branch has an implementation:
- main -> basic JWT authentication
- basic-oauth2 -> integration simple github login using 0auth2-client
- complete-oauth2 -> full authentication flow using oauth2 for many provider

## Basic OAuth2

This leverages the following package with the following scenairo:


And the variations & customization are open!


However in the current implementation there're two important bottlenecks

1. we should've generated password and place it in the userdetails when successfully signing for the first time using Github
   1. Also checking if the user already existed, no need to create it
2. need to verify if the jwt service & jwt filter are working properly or not!
3. Will visit this implementation later!


## Complete OAuth2

In this implementation we somehow need a factory, to have a single endpoint responding to Oauth providers
as we could've more than +20 integrations, having single endpoint is a good solution, but not on many cases!

In this case the Spring security provide a standard way for most providers