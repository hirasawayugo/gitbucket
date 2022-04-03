package gitbucket.core.service

import gitbucket.core.model.CustomField
import gitbucket.core.model.Profile._
import gitbucket.core.model.Profile.profile.blockingApi._

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
}
