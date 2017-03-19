<pre>
     _    _    _               _                                
    / \  | | _| | ____ _   ___| |_ _ __ ___  __ _ _ __ ___  ___ 
   / _ \ | |/ / |/ / _` | / __| __| '__/ _ \/ _` | '_ ` _ \/ __|
  / ___ \|   <|   < (_| |_\__ \ |_| | |  __/ (_| | | | | | \__ \
 /_/   \_\_|\_\_|\_\__,_(_)___/\__|_|  \___|\__,_|_| |_| |_|___/
                                                                
</pre>
## Quick Start
Akka Streams following the concept from RXScala but with different naming convention and with more complete and ready to use implementation and ecosystem.
* Building blocks: Source, Flow, Sink, Graph
* Additional features: Back-pressure, Failure, Error handling...
* Akka HTTP integration (Client and Server)
* Building block are immutable and can be shared.

## Flow reusable pieces
```scala
implicit val system = ActorSystem("QuickStart")
implicit val materializer = ActorMaterializer()
```
* Streams are built **on top of akka** and need to have **actor system**.
* In order to **run any stream** akka.stream need to **materialize some number of actors**
* Api from time to time need to have **materializer** in **implicit** scope.

## Flow reusable pieces
```scala
val source: Source[Int, NotUsed] =
	Source(1 to 100)
```
* Responsible for emitting values for future processing.
* Is inactive after declared.
* In order to get those numbers out we have to run it.
* Can be built from **future**, **iterable**... and many more.

## Flow reusable pieces
```scala
val flow: Flow[Int, Int, NotUsed] =
	Flow[Int].map(_ * 2)
```
* Basic building block.
* Can aggregate map, filter... operation in named variable.
* Easy to reuse.
* Can be seen as a part of service layer.

## Flow reusable pieces
```scala
val sink: Sink[Int, Future[Int]] =
    Sink.fold[Int, Int](0)(_ + _)
```
* Represents end of stream.
* Can represent some side effects.
* There is plenty of already implemented sinks. Like **Sink.ignore**, **Sink.last**...

## Flow reusable pieces
```scala
val runnableGraph: RunnableGraph[Future[Int]] =
	source.via(flow).toMat(sink)(Keep.right)

val result: Future[Int] = runnableGraph.run()
```
* We have many options to run stream. It allows us to design our flow with respect to out application architecture.
* Not always we need to care about stream value.

## Source and Flow use case.
app.flashcard.repository.FlashcardRepository

## Akka.http integration use case.
app.flashcard.route.FlashcardRoute

## Flow reusable pieces
<pre>
 __    __            _                              ___ 
/ / /\ \ \__ _ _ __ | |_   _ __ ___   ___  _ __ ___/ _ \
\ \/  \/ / _` | '_ \| __| | '_ ` _ \ / _ \| '__/ _ \// /
 \  /\  / (_| | | | | |_  | | | | | | (_) | | |  __/ \/ 
  \/  \/ \__,_|_| |_|\__| |_| |_| |_|\___/|_|  \___| ()                                                   
</pre>

Read [Basics and working with Flows](http://doc.akka.io/docs/akka/2.4.17/scala/stream/stream-flows-and-basics.html)

## Design Principles behind Akka Streams
* No dead letter office
* Oriented to compassable components
* Interoperation with other Reactive Streams implementations
* More in [documentation](http://doc.akka.io/docs/akka/2.4.17/general/stream/stream-design.html)

## The difference between Error and Failure
* Error is accessible within the stream as a normal data element.
* Failure means that the stream itself has failed and is collapsing
* Good idea is to handle business errors by Either instead playing with akka.stream stuff.
* More in [documentation](http://doc.akka.io/docs/akka/2.4.17/general/stream/stream-design.html#The_difference_between_Error_and_Failure)

## Stream ordering
_In Akka Streams almost all computation stages preserve input order of elements. This means that if inputs {IA1,IA2,...,IAn} "cause" outputs {OA1,OA2,...,OAk} and inputs {IB1,IB2,...,IBm} "cause" outputs {OB1,OB2,...,OBl} and all of IAi happened before all IBi then OAi happens before OBi._ [doc](http://doc.akka.io/docs/akka/2.4/scala/stream/stream-flows-and-basics.html#Stream_ordering)

## Demo
<pre>
 ______   _______  __   __  _______ 
|      | |       ||  |_|  ||       |
|  _    ||    ___||       ||   _   |
| | |   ||   |___ |       ||  | |  |
| |_|   ||    ___||       ||  |_|  |
|       ||   |___ | ||_|| ||       |
|______| |_______||_|   |_||_______|
</pre>

## Graps common boilerplate
```scala
xxx.fromGraph(GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._
 	...
    xxxShape
  })
```
* **builder** allows us to build our funcy graphs.
* **GraphDSL.Implicits._** provides nice DSL.
* xxx.fromGraph because graph can by lifted into **Sources** and **Flows**.

## Graph with ClosedShape
```scala
val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
  import GraphDSL.Implicits._
  val in = Source(1 to 10)
  val out = Sink.ignore
 
  val bcast = builder.add(Broadcast[Int](2))
  val merge = builder.add(Merge[Int](2))
 
  val f1, f2, f3, f4 = Flow[Int].map(_ + 10)
 
  in ~> f1 ~> bcast ~> f2 ~> merge ~> f3 ~> out
  bcast ~> f4 ~> merge
  ClosedShape
})
```
* End to end stream which can be starded.

## Graph with SourceShape
```scala
Source.fromGraph(GraphDSL.create() { implicit b =>
  import GraphDSL.Implicits._
  ...
  SourceShape(out)
})
```

## Graph with FlowShape
```scala
Flow.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._
 	...
 	FlowShape(in, out)
  })
```

## Graph with FlowShape
```scala
Flow.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._
 	...
 	FlowShape(in, out)
  })
```

## Graph simplified API

There is [simplified API](http://doc.akka.io/docs/akka/2.4.17/scala/stream/stream-graphs.html#Combining_Sources_and_Sinks_with_simplified_API) which is nice and easy tool for combining 

## Demo
<pre>
 ______   _______  __   __  _______ 
|      | |       ||  |_|  ||       |
|  _    ||    ___||       ||   _   |
| | |   ||   |___ |       ||  | |  |
| |_|   ||    ___||       ||  |_|  |
|       ||   |___ | ||_|| ||       |
|______| |_______||_|   |_||_______|
</pre>

## Graph example
app.flashcard.service.UserService


## Scheduled processing
app.flashcard.service.UserService

## Remarks
* Super as a alternative for any scheduled activity
* Nice compassable blocks
* Easy to test
* Clear processing blocks
* In my opinion its wrong to say that most of problems can be rewritten with futures and for comprehension
* I general easy to use
* Nice integrations with akka.http

## Remarks
* Redundant complexity in some cases
* Hard to design application with clear separation of logic and framework (as it was in spring...)
* Graph API can be useful but in very complex processing



