package models

case class Organization(

      name: Option[String],

      members: Nodes[User],

      repositories: Nodes[Repository]

      //projects: Nodes[Project],

      //teams: Nodes[Team]

)
