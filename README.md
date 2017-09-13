# global-session

> simple distributed session solution

## How 

Quit simple. The key code of this project is the `GlobalSessionFilter`, it use a customized `WrappedRequest` replace the original 
`HttpServletRequest`. `WrappedRequest` override the method getSession, and returns a customized `WrappedSession`. In this way, when you
set or get attribute from session, the customized session set or get value from redis.
