package gitbucket.core.service

import gitbucket.core.model.{CustomField, IssueCustomField}
import gitbucket.core.model.Profile._
import gitbucket.core.model.Profile.profile.blockingApi._

import java.text.SimpleDateFormat
import scala.util.Try

trait CustomFieldsService {

  def getCustomFields(owner: String, repository: String)(implicit s: Session): List[CustomField] =
    CustomFields.filter(_.byRepository(owner, repository)).sortBy(_.fieldId asc).list

  def getCustomField(owner: String, repository: String, fieldId: Int)(implicit s: Session): Option[CustomField] =
    CustomFields.filter(_.byPrimaryKey(owner, repository, fieldId)).firstOption

  def createCustomField(
    owner: String,
    repository: String,
    fieldName: String,
    fieldType: String,
    enableForIssues: Boolean,
    enableForPullRequests: Boolean
  )(implicit s: Session): Int = {
    CustomFields returning CustomFields.map(_.fieldId) insert CustomField(
      userName = owner,
      repositoryName = repository,
      fieldName = fieldName,
      fieldType = fieldType,
      enableForIssues = enableForIssues,
      enableForPullRequests = enableForPullRequests
    )
  }

  def updateCustomField(
    owner: String,
    repository: String,
    fieldId: Int,
    fieldName: String,
    fieldType: String,
    enableForIssues: Boolean,
    enableForPullRequests: Boolean
  )(
    implicit s: Session
  ): Unit =
    CustomFields
      .filter(_.byPrimaryKey(owner, repository, fieldId))
      .map(t => (t.fieldName, t.fieldType, t.enableForIssues, t.enableForPullRequests))
      .update((fieldName, fieldType, enableForIssues, enableForPullRequests))

  def deleteCustomField(owner: String, repository: String, fieldId: Int)(implicit s: Session): Unit = {
    IssueCustomFields
      .filter(t => t.userName === owner.bind && t.repositoryName === repository.bind && t.fieldId === fieldId.bind)
      .delete
    CustomFields.filter(_.byPrimaryKey(owner, repository, fieldId)).delete
  }

  def getCustomFieldValues(
    userName: String,
    repositoryName: String,
    issueId: Int,
  )(implicit s: Session): List[IssueCustomField] = {
    IssueCustomFields.filter(_.byIssue(userName, repositoryName, issueId)).list
  }

  def insertCustomFieldValue(
    field: CustomField,
    userName: String,
    repositoryName: String,
    issueId: Int,
    value: String
  )(implicit s: Session): Unit = {
    val customFieldValue = field.fieldType match {
      case "text" =>
        Some(
          IssueCustomField(
            userName = userName,
            repositoryName = repositoryName,
            issueId = issueId,
            fieldId = field.fieldId,
            stringValue = Some(value),
            booleanValue = None,
            intValue = None,
            dateValue = None
          )
        )
      case "int" =>
        Some(
          IssueCustomField(
            userName = userName,
            repositoryName = repositoryName,
            issueId = issueId,
            fieldId = field.fieldId,
            stringValue = None,
            booleanValue = None,
            intValue = value.toIntOption,
            dateValue = None
          )
        )
      case "boolean" =>
        Some(
          IssueCustomField(
            userName = userName,
            repositoryName = repositoryName,
            issueId = issueId,
            fieldId = field.fieldId,
            stringValue = None,
            booleanValue = value.toBooleanOption,
            intValue = None,
            dateValue = None
          )
        )
      case "date" =>
        Some(
          IssueCustomField(
            userName = userName,
            repositoryName = repositoryName,
            issueId = issueId,
            fieldId = field.fieldId,
            stringValue = None,
            booleanValue = None,
            intValue = None,
            dateValue = Try(new SimpleDateFormat("yyyy-MM-dd").parse(value)).toOption
          )
        )
      case _ => None
    }

    customFieldValue.foreach { value =>
      IssueCustomFields.insert(value)
    }
  }
}
