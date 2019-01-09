# Plugs - Design & Ideas

## IT background as of 2019

Often developers came to a point when they would like to split bigger 
software solution to smaller, more maintainable parts. There are two main 
ways of how to achieve that:

 * Use some kind of plugin system, for example:
 
   * Java EE (EE4J) deployments (war, ear) on Java EE servers (JBoss, 
     WebSphere, WebLogic)
   * OSGi bundles on OSGi systems like Felix, Equinox, or Karaf
   * and many more
   
 * Use separate processes and RPC protocols with various architectural styles:
   
   * Microservices with service discovery backed mostly by REST
   * SOA implemented with SOAP and ESB
   * and many more

Those methods above have many drawbacks and accusations as well as a 
definitive advantages. General rule is that separate processes takes more 
system resources, but brings classpath isolation. Summarising couple of most 
popular of them:

 * Using Java EE style deployments are standardized and should be easy to 
   adopt by developers who know Java EE standard. On other hand they are hard
   to maintain at scale, especially to automate deployments, and most of them
   impose restrictions on usable jar dependencies as classpath is often 
   shared between server and deployments.
 * Using OSGi bundles and servers bring definitive advantage of separate, 
   safe classpath as every bundle has exact control on their classpath. But 
   they are also often hard to maintain, as there are no easy ways to deploy 
   bundles to servers. People tend to create very small plugins that use 
   other plugins, that use other plugins, making the whole system extremely 
   complex.
 * SOA implemented with SOAP and ESB, brings standardization of inter-process 
   communication with SOAP protocol and automation of code generation and 
   most of ESB solutions can wire services with SAOP protocol and other RPCs.
   Separate processes of course can mean separated classpath, unless done 
   with Java EE servers. Unfortunately SOAP, ESB, and SOA, became bloated and
   and are hard to maintain as configuration and deployment is mostly done 
   manually. Additionally SOAP isn't easy to implement in many programming 
   languages, but in JVM world it's not really a problem.
 * A REST based Microservices are actually mostly developed technology. It 
   bring a promise of small bound domain context that should be easy to 
   maintain for developers, separate classpath as applications should be 
   separate processes, most often containerized. Unfortunately, solutions that 
   people come up are extremely complex, including multiple technologies and 
   that is often hard not to fail to produce software as a whole. As real 
   success stories came mostly from industry giants like Google, Netlix etc. 
   and that is not often the same use case as most popular enterprise like 
   projects. 
   
It must be said that there are many successful projects that uses any of 
above approaches. So, as with everything, it depends on a case. It should not
be taken as definitive statement.

## The Idea
 
A Plugs library became as an idea to create a safe, easy to use, plugin 
system for JVM world. It has been thought out to take the best parts of the 
technologies described above without, hopefully, any of drawbacks.

A Plugs library consists of two main parts. Server part, codenamed 
*"powerstrip"*, and plugin (client) part, named *"plug"*.

A Plugs library **promise** to bring a plugin style system that have:

 * Isolated classpath using OSGi containers like: Felix, Equinox, etc. 
 * Easy integration of server part, as a drop-in library, for any technology 
   like Java EE Server, Spring, Guice, Dagger2, or any other.
 * Easy Plug module Maven generator (Gradle, SBT also), that takes all jar 
   dependencies and bundle them in Uberjar OSGi module. Provided scope 
   dependencies became OSGi imports.
 * Easy to use Plugs installers, that download, configure and enable plugs, 
   from code manifests at runtime. Installers are backed by Maven, YUM, and 
   other providers.

## The Architecture

![Plugs Architecture](https://g.gravizo.com/source/svg/%27architecture101?https%3A%2F%2Fraw.githubusercontent.com%2Fwavesoftware%2Fplugs%2Ffeature%2F14-design-doc%2Fdocs%2Farchitecture.puml)

Above diagram showcases a sample setup with Plugs library. In example ACME 
app there are tree Plugs components deployed:

 * **powerstrip-api** - an API for Plugs server (contains configuration, and 
   setup)
 * **powerstrip-felix** - a implementation of powerstrip-api with Apache Felix
 * **plugs-maven-installer** - a installer that searches, download and 
   install plug modules, based on user code

In this example we use `plugs-maven-installer` so there is also a Maven 
repository deployed, with published plugs modules as artifacts (this 
repository can be repote or local). In this example there are two modules:

 * coyote-module
 * bunny-module
 
Both modules can use any jar dependencies, they like. It is possible even to 
use 
different versions of the same libraries, as each Plug module is separated in 
its own classpath.

Powerstrip API can be used to dynamically install, update, and remove Plug 
modules at runtime.
