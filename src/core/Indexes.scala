package mutatus

import scala.reflect.runtime.universe.WeakTypeTag
import language.experimental.macros

/** Order direction for given Index Proprty existing in schema */
sealed trait OrderDirection
object OrderDirection {
  case object Ascending extends OrderDirection
  case object Descending extends OrderDirection
}

/** Contains type defininitions of all Entity indexes */
trait Schema[+T <: Index[_, _]]
object Schema{
  implicit def simpleIndexShema[T]: Schema[Index[T, SimpleIndexDef]] = null
  def load(indexes: DatastoreIndex[_]*): Schema[_ <: Index[_, _]] =  macro IndexesMacros.loadIndexesImpl
}

/** Base type for each Index Property definition. 
 * IndexDef is used within QueryBuilder to check is query can be executed with predefined Datastore Indexes
 * Each IndexDef may be extended with Property
*/
sealed trait IndexDef
/** Represents index which needs defined Complex/Manual Index inside Datastore to be corectlly evaluated */
trait ComplexIndexDef extends IndexDef

/** Represents index which does not need manually created Index within Datastore. Such query is executed using built-in Indexes*/
trait SimpleIndexDef extends IndexDef

trait Index[Entity, +Properies <: IndexDef]

/** Property defines single Index entry for given path P, defined as literal singleton and OrderDirection in which it is indexed inside Datastore  */
trait Property[P <: Singleton, +OrderDirection]{
  _: IndexDef => 
}

/** Internal trait used to mark that given query property literal would be used within sort crtieria */
trait Sortable

/** Internal trait used to mark that given query property would be used within filter crtieria*/
trait Filtered
trait InequalityFiltered extends Filtered
trait EqualityFiltered extends Filtered

case class DatastoreIndex[T](properties: (String,OrderDirection)*)