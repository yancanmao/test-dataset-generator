Generator is from [yangjun.wang](https://github.com/wangyangjun), I use its API to develop a Samza Benchmark, but maybe not have same methods, if the final result wa great, I would push a commit to Mr.Wang's repo.

## workloads description

AdvClick: create two streams: Advertisement, AdvClick.

FileToStream: read from specific file and produce it to stream.

KMeansPoints: create one stream with two dimension's point, which is used to process KMeans App.

UniformWordCount: create one stream with words which followed uniform order.

## setup

### requirement

Kafka(see how to instsall in official website or use hello-samza install)

### package

run this, and don't need to tar.
<code>mvn clean package</code>


### run

<code>java -cp generator*.jar fi.aalto.dmg.generator.GeneratorClass (interval)</code>