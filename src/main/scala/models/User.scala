package models


case class User(

      name: Option[String],

      login: Option[String],

      email: Option[String],

      bio: Option[String],

      location: Option[String],

      websiteURL: Option[String],

      company: Option[String],

      isHireable: Option[Boolean],

      repositories: Nodes[Repository],

      contributedRepositories: Nodes[Repository]

 )
