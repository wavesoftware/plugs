# Plugs - Design & Ideas

## IT background as of 2019 (JVM)

Often developers come to a point when they would like to split bigger 
software solution to smaller, more maintainable parts. There are two main 
ways of how to achieve that splitage:

 * Use some kind of plugin system, for example:
 
   * Java EE (EE4J) deployments (war, ear) on Java EE servers (JBoss, 
     WildFly, WebSphere, WebLogic)
   * OSGi bundles on OSGi container like Felix, Equinox, or Karaf
   
 * Use separate processes and RPC protocols with various architectural styles:
   
   * Microservices with service discovery backed mostly by REST
   * SOA implemented with SOAP and ESB

Those methods above have many drawbacks and accusations, as well as a 
definitive advantages. General rule is that separate processes takes more 
system resources, but also brings classpath isolation, thus security. 

Summing up a few of the most popular opinions about approaches to architecture:

 * The use of Java EE-style deployments is standardized and should be easily
   adopted by programmers familiar with the Java EE standard. On the other 
   hand, they are difficult to maintain on a large scale, especially for 
   automating deployments, and most of the servers impose restrictions on the
   use of JAR dependencies, because the class path is often shared between 
   the server and package deployments.
 * Using OSGi bundles and servers provides a definite advantage to a 
   separate, secure class path, because each bundle has strict control over its 
   class path. However, they are often difficult to maintain because there 
   are no easy ways to automatically deploy bundles on OSGi servers. Developers 
   tend to create very small plugins that use other plugins that use other 
   plugins, which makes the whole system extremely complex.
 * SOA implemented with SOAP and ESB, brings standardization of inter-process 
   communication with SOAP protocol and automation of code generation and 
    most of ESB solutions can wire services with SOAP protocol and other RPCs.
   Separate processes of course can mean separated classpath, unless done 
   with some Java EE servers. Unfortunately SOA, SOAP, and especially ESB, 
   became bloated and are hard to maintain as configuration and deployment is
   mostly done manually, mostly in static files. Additionally SOAP isn't easy
   to implement in many programming languages (in JVM world it's not really a
   problem).
 * Microservices based on REST are currently mainly developed technology.
   They bring the promise of a small context of a related domain that should
   be easy to maintain for programmers. They also give the benefit of a 
   separate class path, because applications should be separate processes, 
   and most often containerized. Unfortunately, the solutions that programmers 
   most often create are extremely complex. They contain a huge variety of 
   technologies and connections, and it is impossible to understand how the 
   whole works. Unfortunately, often this approach will disappoint you in 
   creating software as a whole. Real success stories came mainly from 
   industry giants such as Google, Netlix etc. And this is not often the same
   use case as in the case of the most popular enterprise like projects. 
   
It must be said that there are many successful projects that uses any of 
above approaches. So, as with everything, it depends on a case. It should not
be taken as definitive statement.

## The Idea
 
A *Plugs* library become as an idea to create a safe, easy to use, plugin 
system for JVM world. It has been thought out to use the best parts of the 
technologies described above without, hopefully, any of drawbacks.

A *Plugs* library consists of two main parts. Server part, codenamed 
*"powerstrip"*, and plugin (client) part, named *"plug"*.

A Plugs library **promise** to bring a plugin system that have:

 * Isolated classpath using OSGi containers like: Felix, Equinox, etc. 
 * Easy integration of server part, as a drop-in library, for any technology 
   like Java EE Server, Spring, Guice, Dagger2, or any other.
 * Easy *Plug* module Maven generator (Gradle, SBT also), that takes all jar 
   dependencies and bundle them in Uberjar OSGi module. Provided scope 
   dependencies became OSGi imports.
 * Easy to use *Plugs* installers, that download, configure, and enable plugs, 
   all from code manifests at runtime. Installers are backed by Maven, YUM, and 
   other technologies.
 * Easy to use *Plugs* installers that download, configure, and run plugins, 
   all based on code manifest, also when operating the application on the 
   target environment. Installers are supported by Maven, YUM and other 
   technologies.

## The Architecture

![Plugs Architecture](https://g.gravizo.com/source/svg/%27architecture101?https%3A%2F%2Fraw.githubusercontent.com%2Fwavesoftware%2Fplugs%2Fdevelop%2Fdocs%2Farchitecture.puml)

Above diagram showcases a sample setup with *Plugs* library. 

In example ACME application there are tree *Plugs* components deployed:

 * **powerstrip-api** - an API for *Plugs* server (contains configuration, and 
   setup)
 * **powerstrip-felix** - a implementation of powerstrip-api with Apache Felix
 * **plugs-maven-installer** - a installer that searches, download and 
   install *Plug* modules, based on user code

In this example we use `plugs-maven-installer` so there is also a Maven 
repository deployed, with published *plugs* modules as artifacts (this 
repository can be remote or local). In this example there are two example 
modules:

 * coyote-module
 * bunny-module
 
Both modules can use any jar dependencies, as they like. It is possible even to 
use different versions of the same libraries, as each *Plug* module is 
separated in its own classpath.

Powerstrip API can be used to dynamically install, update, and remove Plug 
modules at application runtime.
