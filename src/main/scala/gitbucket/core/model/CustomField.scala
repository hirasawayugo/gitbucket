package gitbucket.core.model

trait CustomFieldComponent extends TemplateComponent { self: Profile =>
  import profile.api._

  lazy val CustomFields = TableQuery[CustomFields]

  class CustomFields(tag: Tag) extends Table[CustomField](tag, "CUSTOM_FIELD") with BasicTemplate {
    val fieldId = column[Int]("FIELD_ID", O AutoInc)
    val fieldName = column[String]("FIELD_NAME")
    val fieldType = column[String]("FIELD_TYPE")
    val enableForIssues = column[Boolean]("ENABLE_FOR_ISSUES")
    val enableForPullRequests = column[Boolean]("ENABLE_FOR_PULL_REQUESTS")
    def * =
      (userName, repositoryName, fieldId, fieldName, fieldType, enableForIssues, enableForPullRequests)
        .<>(CustomField.tupled, CustomField.unapply)

    def byPrimaryKey(userName: String, repositoryName: String, fieldId: Int) =
      (this.userName === userName.bind) && (this.repositoryName === repositoryName.bind) && (this.fieldId === fieldId.bind)
  }
}

case class CustomField(
  userName: String,
  repositoryName: String,
  fieldId: Int = 0,
  fieldName: String,
  fieldType: String, // number, string, boolean or date
  enableForIssues: Boolean,
  enableForPullRequests: Boolean
)
