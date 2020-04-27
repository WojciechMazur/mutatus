<a href="https://furore.dev/propensive/mutatus"><img src="/doc/images/furore.png" style="vertical-align:middle" valign="middle"></a>&nbsp;&nbsp;<a href="https://furore.dev/propensive/mutatus">__Develop with Fury__ </a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="https://riot.im/app/#/room/#propensive.mutatus:matrix.org"><img src="/doc/images/riotim.png" style="vertical-arign:middle" valign="middle"></a>&nbsp;&nbsp;<a href="https://riot.im/app/#/room/#propensive.mutatus:matrix.org">__Discuss on Riot__</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="https://search.maven.org/search?q=g:com.propensive%20AND%20a:mutatus_2.12"><img src="/doc/images/mavencentral.png" style="vertical-arign:middle" valign="middle"></a>&nbsp;&nbsp;<a href="https://search.maven.org/search?q=g:com.propensive%20AND%20a:mutatus_2.12">__Download from Maven Central__</a>

<img src="/doc/images/github.png" valign="middle">

# Mutatus

Mutatus is an experimental library for working with cloud-based data stores. It currently provides an ORM layer for the Google Cloud Platform Datastore.

## Features

- lightweight API for storing and retriving data in the Google Cloud Platform Datastore
- typesafe querying
- safe exception handling
- automatic composite indexing for queries


## Getting Started

To use Mutatus, you should first set up a [GCP
account](https://cloud.google.com/), and grant appropriate access rights to a
service account, as [described
here](https://cloud.google.com/storage/docs/authentication). Mutatus uses
the Java API provided by Google to handle the underlying communication to the
GCP. This library expects to read the environment variable
`GOOGLE_APPLICATION_CREDENTIALS`, pointing to a local file containing those
credentials, as a JSON file (which is downloadable through the GCP user
interface).

Currently if any issues relating to credentials occur, an exception will be
thrown by the Google API.

If that has been completed succesfully, that is the most difficult part of the
instructions completed, and everything from now on is trivial.

To get started, first import the `mutatus` package,
```scala
import mutatus._
```
and create a case class you would like to store, for example,
```scala
case class Person(email: String, name: String, age: Int)
```
and use an implicit `Id` typeclass instance to choose a primary key for this
data type. Picking a unique field should be adequate, for example,
```scala
implicit val personId: Id[Person] = _.email
```
Note that the above concise syntax is only possible using SAM types in Scala 2.12.

You are now ready to save a value, which is as simple as calling `.save()` on a
case class instance, like so,
```scala
Person("jon.pretty@example.com", "Jon Pretty", 35).save()
```

If the save was successful, the value should now be in the GCP Datastore, with
the "kind" (in GCP terminology, though this is really a "type") `Person`.

We can fetch all `Person`s from the Datastore by calling,
```scala
Dao[Person].all()
```
which returns an `Iterator` of `Person`s.

## Serialization and Deserialization

Saving a case class value will use [Magnolia](http://magnolia.work/) to try to
generate an `Encoder` for that type. This will be possible only if all of its
parameters can be serialized. All Java primitive types and strings are
supported.

Additionally, any nested case class instances can also be serialized, provided
they meet the same conditions.

It will make most sense sometimes to compose datatypes directly inside others.
But other times, it will be most useful to refer to values stored elsewhere.
This can be done with `Ref`s, which can also be included in a case class
structure, and are serializable.

A `Ref` can only exist for a value which has already been saved into the
Datastore, and it is indeed the return type of the `save()` method.

A `Ref` is not the datatype itself; it is merely a reference to it, and can be
easily dereferenced by calling its' `apply()` method.

Here is an example using two case classes,
```scala
case class Company(id: String, name: String)
case class Person(id: String, name: String, company: Ref[Company])
```
where we may want to have several `Person`s all referring to the same
`Company`. As usual, we need to ensure both types have IDs,
```scala
implicit val companyId: Id[Company] = _.id
implicit val personId: Id[Person] = _.id
```
We can now create and link some data, like so,
```scala
val company: Company = Company("propensive", "Propensive Ltd")
val companyRef: Ref[Company] = company.save()
val person: Person = Person("jonpretty", "Jon Pretty", companyRef)
person.save()
```
and to access the `Company` value, via the `Person`, we would just call
`person.company()`.


## Availability

Mutatus&rsquo;s source is available on GitHub, and may be built with [Fury](https://github.com/propensive/fury) by
cloning the layer `propensive/mutatus`.
```
fury layer clone -i propensive/mutatus
```
or imported into an existing layer with,
```
fury layer import -i propensive/mutatus
```
A binary will be made available on Maven Central.

## Contributing

Contributors to Mutatus are welcome and encouraged. New contributors may like to look for issues marked
<a href="https://github.com/propensive/mutatus/labels/good%20first%20issue"><img alt="label: good first issue"
src="https://img.shields.io/badge/-good%20first%20issue-67b6d0.svg" valign="middle"></a>.

We suggest that all contributors read the [Contributing Guide](/contributing.md) to make the process of
contributing to Mutatus easier.

Please __do not__ contact project maintainers privately with questions, as other users cannot then benefit from
the answers.

## Author

Mutatus was designed and developed by [Jon Pretty](https://twitter.com/propensive), and commercial support and
training is available from [Propensive O&Uuml;](https://propensive.com/).



## License

Mutatus is copyright &copy; 2018-20 Jon Pretty & Propensive O&Uuml;, and is made available under the
[Apache 2.0 License](/license.md).
