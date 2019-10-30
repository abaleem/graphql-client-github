package models

case class Repository(

      name: Option[String],

      description: Option[String],

      owner: Owner,

      diskUsage: Option[Int],

      isPrivate: Option[Boolean],

      updatedAt: Option[String],

      primaryLanguage: Option[Language],

      languages: Nodes[Language],

      isFork: Option[Boolean],

      parent: Option[Repository],

      forks: Nodes[Repository]
)
