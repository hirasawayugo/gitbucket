package gitbucket.core.model

trait IssueCustomFieldComponent extends TemplateComponent { self: Profile =>
  import profile.api._
  import self._

  lazy val IssueCustomFields = TableQuery[IssueCustomFields]

  class IssueCustomFields(tag: Tag) extends Table[IssueCustomField](tag, "ISSUE_CUSTOM_FIELD") with IssueTemplate {
    val fieldId = column[Int]("FIELD_ID")
    val intValue = column[Option[Int]]("INT_VALUE")
    val stringValue = column[Option[String]]("STRING_VALUE")
    val booleanValue = column[Option[Boolean]]("BOOLEAN_VALUE")
    val dateValue = column[Option[java.util.Date]]("DATE_VALUE")
    def * =
      (userName, repositoryName, issueId, fieldId, intValue, stringValue, booleanValue, dateValue)
        .<>(IssueCustomField.tupled, IssueCustomField.unapply)

    def byPrimaryKey(owner: String, repository: String, issueId: Int, fieldId: Int) =
      byIssue(owner, repository, issueId) && (this.fieldId === fieldId.bind)
  }
}

case class IssueCustomField(
  userName: String,
  repositoryName: String,
  issueId: Int,
  fieldId: Int,
  intValue: Option[Int],
  stringValue: Option[String],
  booleanValue: Option[Boolean],
  dateValue: Option[java.util.Date]
)
