package models


case class Nodes[T](totalCount: Option[Int], nodes: List[T])

