
<pre>
   ###    ##    ## ##    ##    ###                       
  ## ##   ##   ##  ##   ##    ## ##                      
 ##   ##  ##  ##   ##  ##    ##   ##                     
##     ## #####    #####    ##     ##                    
######### ##  ##   ##  ##   #########                    
##     ## ##   ##  ##   ##  ##     ##                    
##     ## ##    ## ##    ## ##     ##                    
 ######  ######## ########  ########    ###    ##     ## 
##    ##    ##    ##     ## ##         ## ##   ###   ### 
##          ##    ##     ## ##        ##   ##  #### #### 
 ######     ##    ########  ######   ##     ## ## ### ## 
      ##    ##    ##   ##   ##       ######### ##     ## 
##    ##    ##    ##    ##  ##       ##     ## ##     ## 
 ######     ##    ##     ## ######## ##     ## ##     ## 
						  2.4.16
</pre>

---
## Plan

We are going to:
* Learn basics of **flows**.
* With this knowledge we will implement application repositories and routes.
* Caffe break.
* Learn basics of **graphs**.
* Implement together simple graph and scheduled processing.

---

## Application
Simple flashcard application which allows us to:
* Add flashcard
* Read all flashcards
* Pick questions
* Answer selected question

All application endpoints will be available via:
* Add flashcard
```
curl -X POST -H "Content-Type: application/json" -H "Cache-Control: no-cache" -d '[{"word": "czesc", "translation": "hi"}, {"word": "pa", "translation": "bye"}]' "http://localhost:8090/flashcard"
```
* Get flashcard
```
curl -X GET -H "Cache-Control: no-cache" "http://localhost:8090/flashcard"
```
* Get questions
```
curl -X GET -H "Cache-Control: no-cache" "http://localhost:8090/quiz/question"
```
* Answer
```
curl -X GET -H "Cache-Control: no-cache" "http://localhost:8090/quiz/answer?word=czesc&translation=hi"
```

---

## Streams are
* Designed for composition.
* Allows to abstract processing steps.
* Standarization of processing.
* Efficiently in case of heavy load.
* ???

---

## Are streams
* **Locking too complex?**
* **Hard to learn?**

Lets checkt that!

---
## Akka streams are
Akka Streams following the concept from RXScala but with different naming convention and with more complete and ready to use implementation and ecosystem.
* Building blocks: Source, Flow, Sink, Graph
* Additional features: Back-pressure, Failure, Error handling...
* Akka HTTP integration (Client and Server)
* Building block are immutable and can be shared.

---

## Design Principles behind Akka Streams

* No dead letter office
* Oriented to comparable components
* Interpretation with other Reactive Streams implementations
* More in [documentation](http://doc.akka.io/docs/akka/2.4.17/general/stream/stream-design.html)

---

## Stream ordering

_In Akka Streams almost all computation stages preserve input order of elements. This means that if inputs {IA1,IA2,...,IAn} "cause" outputs {OA1,OA2,...,OAk} and inputs {IB1,IB2,...,IBm} "cause" outputs {OB1,OB2,...,OBl} and all of IAi happened before all IBi then OAi happens before OBi._ [doc](http://doc.akka.io/docs/akka/2.4/scala/stream/stream-flows-and-basics.html#Stream_ordering)

---

## Flow

<pre>                                                                                    
######## ##        #######  ##      ## 
##       ##       ##     ## ##  ##  ## 
##       ##       ##     ## ##  ##  ## 
######   ##       ##     ## ##  ##  ## 
##       ##       ##     ## ##  ##  ## 
##       ##       ##     ## ##  ##  ## 
##       ########  #######   ###  ###      
</pre>

* Fluent interface.
* Looks like collections API.
* Best choice in most cases.
* Base abstractions.
* Easy to use.

_Let's skip error handling, back-pressure and many other for now and let's focus on basic blocks._

---

## Flow

```scala
implicit val system = ActorSystem("QuickStart")
implicit val materializer = ActorMaterializer()
```

* Streams are built **on top of akka** and need to have **actor system**.
* In order to **run any stream** akka.stream need to **materialize some number of actors**

---

## Flow

```scala
val source: Source[Int, NotUsed] =
	Source(1 to 100)
```

* Responsible for emitting values for future processing.
* Is inactive after declared.
* In order to get those numbers out we have to run it.
* Thanks of dedicated **Stream.from** methods can be built from **future**, **iterable**, **publisher**... .

_We will skip NotUsed in this presentation more details can be found in [documentation](http://doc.akka.io/docs/akka/2.4/scala/stream/stream-flows-and-basics.html#Stream_Materialization)_

---

## Flow reusable pieces

```scala
val flow: Flow[Int, Int, NotUsed] =
	Flow[Int].map(_ * 2)
```

* Basic building block.
* Can aggregate map, filter... .
* Designed to be reused.
* Can be seen as a part of service layer.

---

## Flow reusable pieces

```scala
val sink: Sink[Int, Future[Int]] =
    Sink.fold[Int, Int](0)(_ + _)
```

* Represents end of stream.
* Can represent some side effects.
* There is plenty of already implemented sinks. Like **Sink.ignore**, **Sink.last**...

---

## Flow reusable pieces

```scala
val runnableGraph: RunnableGraph[Future[Int]] =
	source.via(flow).toMat(sink)(Keep.right)

val result: Future[Int] = runnableGraph.run()
```

* We have many options to run stream. It allows us to design our flow with respect to out application architecture.
* Not always we need to care about stream value.

---

## Mob programming
<pre>

##     ##  #######  ########            
###   ### ##     ## ##     ##           
#### #### ##     ## ##     ##           
## ### ## ##     ## ########            
##     ## ##     ## ##     ##           
##     ## ##     ## ##     ##           
##     ##  #######  ########            
########  ########   #######   ######   
##     ## ##     ## ##     ## ##    ##  
##     ## ##     ## ##     ## ##        
########  ########  ##     ## ##   #### 
##        ##   ##   ##     ## ##    ##  
##        ##    ##  ##     ## ##    ##  
##        ##     ##  #######   ######   

</pre>

**git@github.com:mateuszjancy/intro-to-akka-stream.git**

**git checkout mob-prog**

---

## Source and Flow exercise.

* How to integrate async service (in example async DB client) with streams
* Example in: app.flashcard.repository.FlashcardRepository

---

## Source and Flow exercise.

* Is it ok that streams are visible in Repository layer?
* What if I need to use Repository.findAll in two places in one as a Source and in another one as a Flow?

---

## Akka.http integration exercise.

* How to interpret entity from request body as a source
* How to consume stream
* Example in: app.flashcard.route.FlashcardRoute
* ...

---

## Akka.http integration exercise.

* When go with Futures and when with Streams?
* ...

---

<pre>

##      ##    ###    ##    ## ########           
##  ##  ##   ## ##   ###   ##    ##              
##  ##  ##  ##   ##  ####  ##    ##              
##  ##  ## ##     ## ## ## ##    ##              
##  ##  ## ######### ##  ####    ##              
##  ##  ## ##     ## ##   ###    ##              
 ###  ###  ##     ## ##    ##    ##              
##     ##  #######  ########  ########  #######  
###   ### ##     ## ##     ## ##       ##     ## 
#### #### ##     ## ##     ## ##             ##  
## ### ## ##     ## ########  ######       ###   
##     ## ##     ## ##   ##   ##          ##     
##     ## ##     ## ##    ##  ##                 
##     ##  #######  ##     ## ########    ##     

</pre>

Read [Basics and working with Flows](http://doc.akka.io/docs/akka/2.4.17/scala/stream/stream-flows-and-basics.html)

---

## Graphs

<pre>

 ######   ########     ###    ########  ##     ## 
##    ##  ##     ##   ## ##   ##     ## ##     ## 
##        ##     ##  ##   ##  ##     ## ##     ## 
##   #### ########  ##     ## ########  ######### 
##    ##  ##   ##   ######### ##        ##     ## 
##    ##  ##    ##  ##     ## ##        ##     ## 
 ######   ##     ## ##     ## ##        ##     ## 

</pre>

* Designed for more complex processing requirements.
* Look too complex at the first time.

---

## Graphs common boilerplate

```scala
xxx.fromGraph(GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._
 	...
    xxxShape
  })
```

* **builder** allows us to build our fancy graphs.
* **GraphDSL.Implicits._** provides nice DSL like "~>" operator .
* xxx.fromGraph because graph can have shape of **Sources** and **Flows**.

---

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

* Closed stream. 
* Ready to run.

---

## Graph with SourceShape

```scala
Source.fromGraph(GraphDSL.create() { implicit b =>
  import GraphDSL.Implicits._
  ...
  SourceShape(out)
})
```

---

## Graph with FlowShape

```scala
Flow.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._
 	...
 	FlowShape(in, out)
  })
```

---

## Graph simplified API

There is [simplified API](http://doc.akka.io/docs/akka/2.4.17/scala/stream/stream-graphs.html#Combining_Sources_and_Sinks_with_simplified_API) which is nice and easy tool for combining

---

## Demo
<pre>

##     ##  #######  ########            
###   ### ##     ## ##     ##           
#### #### ##     ## ##     ##           
## ### ## ##     ## ########            
##     ## ##     ## ##     ##           
##     ## ##     ## ##     ##           
##     ##  #######  ########            
########  ########   #######   ######   
##     ## ##     ## ##     ## ##    ##  
##     ## ##     ## ##     ## ##        
########  ########  ##     ## ##   #### 
##        ##   ##   ##     ## ##    ##  
##        ##    ##  ##     ## ##    ##  
##        ##     ##  #######   ######   

</pre>

---

## Graph example

* How to implement simple graphs
* Example in: app.flashcard.service.UserService

---

## Graph example

* Nice way of representing abstraction.
* Simple to use.
* Are they easy to read?
* Is boilerplate code painful?
* For expression equivalent for complex processing.

```scala
 for {
   a <- callA
   b <- callB(a)
   c <- callC(a)
 } yield (b, c)
```

---

## Scheduled processing
How to approach scheduling in akka.streams.
Example in: app.flashcard.service.UserService

---

## Scheduled processing

* Super simple.
* Very expressive.

---

## Remarks +

* Super as a alternative for any scheduled activity
* Nice compassable blocks
* Easy to test
* Clear processing blocks
* Easy to use
* Nice integration with akka.http

---

## Remarks -

* Redundant complexity.
* Hard to design application with clear separation of logic and framework.
* Graph API can become overkill in simple cases.

---

<pre>

######## ##     ##    ###    ##    ## ##    ##  ######  
   ##    ##     ##   ## ##   ###   ## ##   ##  ##    ## 
   ##    ##     ##  ##   ##  ####  ## ##  ##   ##       
   ##    ######### ##     ## ## ## ## #####     ######  
   ##    ##     ## ######### ##  #### ##  ##         ## 
   ##    ##     ## ##     ## ##   ### ##   ##  ##    ## 
   ##    ##     ## ##     ## ##    ## ##    ##  ######  
                              			Mateusz
</pre>
