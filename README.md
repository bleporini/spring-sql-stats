# Spring SQL Stats
## Presentation
Spring SQL stats is a simple component to get information about JDBC usage inside your Spring application. It is manageable through JMX in order to easily activate it or deactivate it during runtime and to access to the collected metrics.
It is based on a standard Aspect which purpose is to intercept `getConnection` calls on the data source.

If the mertics collection is activated, then the component supplies a decorated `Connection`, it means that some methods, such as `Connection.createStatement()` for example, notify the statistics controller around the delegate method execution. Obviously, underlying JDBC components can be decorated as well.

If the metrics collection is not activated, then the original `Connection` is returned.
## Project status
It's a small project and it is just starting, features will be shortly added.
## Installation
Add the library in your application classpath and add the `classpath*:sql-stats-context.xml` to the Spring configuration locations.
## Usage
The data is not collected by default, so once your application has started, you have to start it through the JMX interface:

![Jconsole](http://i.imgur.com/lYjjd.png "JConsole")

The metrics are available in the attributes section. For query counting during a use case observation, it can be convenient to reset the current query count before, just invoke the operation.

--- 

Thanks to ej-technologies for the [![JProfiler](http://www.ej-technologies.com/images/headlines2/3453392302.png "JProfiler")](http://www.ej-technologies.com/products/jprofiler/overview.html) open source licence.